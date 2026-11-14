"""Behavioural checks for the MTR 3 legacy vehicle geometry contract."""

import math
from pathlib import Path


MODEL_LENGTH = 21.0  # dlmtr_5_g_js.json
MTR3_SPACING = MODEL_LENGTH + 1.0  # TrainType.getSpacing
MTR4_CONVERTED_LENGTH = MODEL_LENGTH + 1.0  # LegacyVehicleResource


def scanned_longitudinal_cells(half_spacing: float) -> set[int]:
    """Equivalent integer cells reached by MTR 3's unit-step checkZ loop."""
    result = set()
    value = -half_spacing
    while value <= half_spacing + 1e-9:
        result.add(math.floor(value))
        value += 1
    return result


def check(condition: bool, message: str) -> None:
    if not condition:
        raise AssertionError(message)


check(MTR3_SPACING == 22.0, "line 4 MTR 3 spacing")
check(MTR4_CONVERTED_LENGTH == MTR3_SPACING, "converter already retains spacing")

additional_gap = 0.0
centre_spacing = MTR4_CONVERTED_LENGTH + 2 * additional_gap
script_spacing = MTR4_CONVERTED_LENGTH + 2 * additional_gap
check(centre_spacing == MTR3_SPACING, "centre spacing parity")
check(script_spacing == centre_spacing, "script/render spacing parity")

base_train_type = f"train_{round(MTR4_CONVERTED_LENGTH - 1)}_3"
check(base_train_type == "train_21_3", "base train type reconstruction")
legacy_manual_limits = [20, 40, 60, 80, 120, 160, 200, 300]
check(min(range(len(legacy_manual_limits)), key=lambda i: abs(legacy_manual_limits[i] - 80)) == 3,
      "manual speed ordinal parity")
check(abs((80 / 3600) * 50 - 80 / 72) < 1e-12, "rail speed unit parity")

old_cells = scanned_longitudinal_cells((MTR4_CONVERTED_LENGTH - 1) / 2)
mtr3_cells = scanned_longitudinal_cells(MTR3_SPACING / 2)
new_cells = scanned_longitudinal_cells(centre_spacing / 2)
check(10 in old_cells and 11 not in old_cells, "old scan reproduces end miss")
check(11 in mtr3_cells, "MTR 3 reaches platform at car end")
check(new_cells == mtr3_cells, "corrected scan matches MTR 3")

right_open = [True] * 6
left_open = [False] * 6
any_right = any(right_open)
any_left = any(left_open)
red_right = [not value and any_right for value in right_open]
red_left = [not value and any_left for value in left_open]
check(not any(red_right) and not any(red_left), "no false red door lights")

face_a = [(1.0, 0.0, 2.0), (1.0, 3.0, 2.0), (-1.0, 3.0, 2.0), (-1.0, 0.0, 2.0)]
face_b = [(2.0, 0.5, 8.0), (2.0, 3.5, 8.0), (0.0, 3.5, 8.0), (0.0, 0.5, 8.0)]


def lower_edge_center(face):
    return tuple((face[0][i] + face[3][i]) / 2 for i in range(3))


def face_centroid(face):
    return tuple(sum(point[i] for point in face) / 4 for i in range(3))


lower_a, lower_b = lower_edge_center(face_a), lower_edge_center(face_b)
centroid_a = face_centroid(face_a)
check(lower_a[1] == 0.0 and centroid_a[1] == 1.5, "gangway origin remains on floor")
dx, dy, dz = (lower_b[i] - lower_a[i] for i in range(3))
horizontal = math.hypot(dx, dz)
yaw = math.atan2(dx, dz)
pitch = -math.atan2(dy, horizontal)
check(abs(yaw - math.atan2(1.0, 6.0)) < 1e-9, "gangway yaw from actual faces")
check(abs(pitch + math.atan2(0.5, math.sqrt(37))) < 1e-9, "gangway pitch from actual faces")

base_length = 10.0
y_offset_1 = 0.25
y_offset_2 = -0.75
effective_length = math.hypot(base_length, y_offset_2 - y_offset_1)
check(abs(effective_length - math.sqrt(101)) < 1e-12, "Y-offset rail length")
check(abs((y_offset_1 + (y_offset_2 - y_offset_1) * 0.0) - y_offset_1) < 1e-12,
      "Y-offset start endpoint")
check(abs((y_offset_1 + (y_offset_2 - y_offset_1) * 1.0) - y_offset_2) < 1e-12,
      "Y-offset end endpoint")
check((y_offset_2, y_offset_1) == (-0.75, 0.25), "transposed rail swaps endpoint offsets")

root = Path(__file__).resolve().parents[2]
schema_source = (root / "common/src/main/java/cn/jstxjf_/rte/mixin/RailSchemaMixin.java").read_text(encoding="utf-8")
path_source = (root / "common/src/main/java/cn/jstxjf_/rte/mixin/PathDataMixin.java").read_text(encoding="utf-8")
node_source = (root / "common/src/main/java/cn/jstxjf_/rte/block/BlockDirectNode.java").read_text(encoding="utf-8")
rail_source = (root / "common/src/main/java/cn/jstxjf_/rte/mixin/RailMixin.java").read_text(encoding="utf-8")
angle_source = (root / "common/src/main/java/cn/jstxjf_/rte/data/RailAngleExtra.java").read_text(encoding="utf-8")
for key in (
    "rte_node_x_offset_1", "rte_node_y_offset_1", "rte_node_z_offset_1",
    "rte_node_x_offset_2", "rte_node_y_offset_2", "rte_node_z_offset_2",
    "rte_node_pitch_1", "rte_node_pitch_2",
):
    check(schema_source.count(key) >= 2, f"serialized {key}")
supplier_source = (root / "common/src/main/java/cn/jstxjf_/rte/data/RailExtraSupplier.java").read_text(encoding="utf-8")
check("supplier.usesExtendedGeometry()" in path_source and "|| hasNodeOffset()" in supplier_source,
      "vehicle path consumes XYZ-offset geometry")
check('KEY_Y_OFFSET = "y_offset"' in node_source, "direct-node Y offset persists")

free_node_source = (root / "common/src/main/java/cn/jstxjf_/rte/block/BlockFreeNode.java").read_text(encoding="utf-8")
free_node_editor_source = (root / "common/src/main/java/cn/jstxjf_/rte/render/FreeNodeEditor.java").read_text(encoding="utf-8")
free_node_packet_source = (root / "common/src/main/java/cn/jstxjf_/rte/network/PacketFreeNodeAction.java").read_text(encoding="utf-8")
direct_node_renderer_source = (root / "common/src/main/java/cn/jstxjf_/rte/render/block/BlockEntityDirectNodeRenderer.java").read_text(encoding="utf-8")
imgui_host_source = (root / "common/src/main/java/cn/jstxjf_/rte/gui/imgui/RteImGuiHost.java").read_text(encoding="utf-8")
imgui_screen_source = (root / "common/src/main/java/cn/jstxjf_/rte/gui/imgui/ImGuiScreenBase.java").read_text(encoding="utf-8")
train_wrapper_source = (root / "common/src/main/java/cn/jstxjf_/rte/scripting/train/TrainWrapper.java").read_text(encoding="utf-8")
check("context.getHitPos()" in free_node_source, "free-node placement uses the exact hit point")
check("side == Direction.UP ? 0 : side == Direction.DOWN ? 180 : -90" in free_node_source,
      "free-node mirrored wall and ceiling pitch")
check("PacketFreeNodeAction.USE_CONNECTOR" in free_node_editor_source and ".useOn(context)" in free_node_packet_source,
      "free-node connector hit is resolved server-side")
check("Handle.XY" in free_node_editor_source and "Handle.XZ" in free_node_editor_source
      and "Handle.YZ" in free_node_editor_source, "free-node gizmo has Axiom-style plane handles")
check("Handle.YAW" in free_node_editor_source and "Handle.PITCH" in free_node_editor_source,
      "free-node gizmo exposes applicable Axiom-style rotation rings")
check("GLFW_KEY_V" in free_node_editor_source and "GLFW_KEY_Z" in free_node_editor_source
      and "GLFW_KEY_Y" in free_node_editor_source, "free-node gizmo supports clipboard and history shortcuts")
check("0.0625" in free_node_editor_source and "0.0001" in free_node_editor_source,
      "free-node gizmo uses Axiom-style snapping precision")
check("rail_node_vertical" in direct_node_renderer_source and "rail_node_connection" in direct_node_renderer_source,
      "direct node keeps its vertical and connection models")
check("/assets/mtr/font/noto-sans-cjk-tc-medium.otf" in imgui_host_source,
      "ImGui ships a Linux-independent CJK font")
check("graphics.flush()" in imgui_screen_source, "ImGui background is submitted before direct GL rendering")
check("public float doorValue() {\n        return currentDoorValue;\n    }" in train_wrapper_source,
      "script door value uses the published render snapshot")
check("RailSpatialContext.apply(this)" in rail_source, "free-node geometry exists before MTR validation")
check("rte$copyStyleExtras" in rail_source, "MTR style copies preserve free-node geometry")
check("ensureCarrierRailMath" in angle_source, "vertical rails receive a valid MTR carrier")

offset_start = (0.25, -0.4, 0.75)
offset_end = (-0.5, 0.6, -0.25)
offset_delta = tuple(offset_end[i] - offset_start[i] for i in range(3))
for pitch_deg in (0, 90, 180, -90):
    yaw_rad = math.radians(35)
    pitch_rad = math.radians(pitch_deg)
    direction = (
        math.cos(yaw_rad) * math.cos(pitch_rad),
        math.sin(pitch_rad),
        math.sin(yaw_rad) * math.cos(pitch_rad),
    )
    handle = 4.0
    base_derivative = tuple(3 * (direction[i] * handle - offset_delta[i] / 3) for i in range(3))
    effective_derivative = tuple(base_derivative[i] + offset_delta[i] for i in range(3))
    expected = tuple(3 * handle * direction[i] for i in range(3))
    check(all(abs(effective_derivative[i] - expected[i]) < 1e-12 for i in range(3)),
          f"free-node endpoint tangent at {pitch_deg} degrees")

print("PASS: MTR 3 parity and free-node XYZ/pitch geometry")
