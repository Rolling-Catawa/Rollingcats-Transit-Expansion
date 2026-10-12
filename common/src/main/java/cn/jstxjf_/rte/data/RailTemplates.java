package cn.jstxjf_.rte.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cn.jstxjf_.rte.RTE;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class RailTemplates {

    private static final Path TEMPLATE_DIR = Path.of("./rte/templates/").toAbsolutePath().normalize();

    private RailTemplates() {
    }

    public static List<String> list() {
        final List<String> names = new ArrayList<>();
        try {
            if (Files.isDirectory(TEMPLATE_DIR)) {
                try (Stream<Path> stream = Files.list(TEMPLATE_DIR)) {
                    stream.filter(path -> path.getFileName().toString().endsWith(".json"))
                        .sorted()
                        .forEach(path -> names.add(path.getFileName().toString().replaceFirst("\\.json$", "")));
                }
            }
        } catch (Exception e) {
            RTE.LOGGER.warn("[RailTemplates] list failed", e);
        }
        return names;
    }

    public static boolean export(Rail rail, String name) {
        try {
            if (rail == null || name == null || name.isBlank() || !(((Object) rail) instanceof RailExtraSupplier extra)) {
                return false;
            }
            final List<BezierCurve.Vec3> points = controlPointsOf(rail, extra);
            if (points == null || points.size() < 4 || (points.size() - 1) % 3 != 0) {
                return false;
            }

            final BezierCurve.Vec3 first = points.get(0);
            final BezierCurve.Vec3 last = points.get(points.size() - 1);
            final double chordX = last.x - first.x;
            final double chordZ = last.z - first.z;
            final double scale = Math.hypot(chordX, chordZ);
            if (scale < 1) {
                return false;
            }
            final double cos = chordX / scale;
            final double sin = chordZ / scale;
            final double slope = (last.y - first.y) / scale;

            final JsonArray pointsArray = new JsonArray();
            for (final BezierCurve.Vec3 point : points) {
                final double relX = point.x - first.x;
                final double relY = point.y - first.y;
                final double relZ = point.z - first.z;
                final double localX = (relX * cos + relZ * sin) / scale;
                final double localZ = (-relX * sin + relZ * cos) / scale;
                final double localY = relY / scale - slope * localX;
                final JsonArray tuple = new JsonArray();
                tuple.add(round(localX));
                tuple.add(round(localY));
                tuple.add(round(localZ));
                pointsArray.add(tuple);
            }

            final JsonObject root = new JsonObject();
            root.addProperty("format", 1);
            root.addProperty("name", name);
            root.add("points", pointsArray);

            Files.createDirectories(TEMPLATE_DIR);
            Files.writeString(TEMPLATE_DIR.resolve(sanitize(name) + ".json"),
                new GsonBuilder().setPrettyPrinting().create().toJson(root));
            return true;
        } catch (Exception e) {
            RTE.LOGGER.warn("[RailTemplates] export failed", e);
            return false;
        }
    }

    public static RailPresets.PresetResult load(Rail rail, String name) {
        try {
            if (rail == null || !(((Object) rail) instanceof RailExtraSupplier extra)) {
                return null;
            }
            final Path file = TEMPLATE_DIR.resolve(sanitize(name) + ".json");
            if (!Files.exists(file)) {
                return null;
            }
            final JsonObject root = RTE.JSON_PARSER.parse(Files.readString(file)).getAsJsonObject();
            final JsonArray pointsArray = root.getAsJsonArray("points");
            if (pointsArray == null || pointsArray.size() < 4 || (pointsArray.size() - 1) % 3 != 0) {
                return null;
            }

            final Position p1 = extra.getExtraPosition1();
            final Position p2 = extra.getExtraPosition2();
            if (p1 == null || p2 == null) {
                return null;
            }
            final double startX = p1.getX();
            final double startY = p1.getY();
            final double startZ = p1.getZ();
            final double chordX = p2.getX() - startX;
            final double chordZ = p2.getZ() - startZ;
            final double scale = Math.hypot(chordX, chordZ);
            if (scale < 1) {
                return null;
            }
            final double cos = chordX / scale;
            final double sin = chordZ / scale;
            final double slope = (p2.getY() - startY) / scale;

            final List<BezierCurve.Vec3> points = new ArrayList<>();
            for (final JsonElement element : pointsArray) {
                final JsonArray tuple = element.getAsJsonArray();
                final double localX = tuple.get(0).getAsDouble();
                final double localY = tuple.get(1).getAsDouble();
                final double localZ = tuple.get(2).getAsDouble();
                final double worldX = startX + (localX * cos - localZ * sin) * scale;
                final double worldZ = startZ + (localX * sin + localZ * cos) * scale;
                final double worldY = startY + (localY + slope * localX) * scale;
                points.add(new BezierCurve.Vec3(worldX, worldY, worldZ));
            }

            final BezierCurve curve = new BezierCurve(0.1, points);
            if (!Double.isFinite(curve.getLength()) || curve.getLength() < 1) {
                return null;
            }
            final BezierCurve.Vec3 c0 = points.get(0);
            final BezierCurve.Vec3 c1 = points.get(1);
            final BezierCurve.Vec3 cn1 = points.get(points.size() - 2);
            final BezierCurve.Vec3 cn = points.get(points.size() - 1);
            final double angleStart = Math.atan2(c1.z - c0.z, c1.x - c0.x);
            final double angleEnd = Math.atan2(cn.z - cn1.z, cn.x - cn1.x);
            return new RailPresets.PresetResult(curve, angleStart, angleEnd);
        } catch (Exception e) {
            RTE.LOGGER.warn("[RailTemplates] load failed", e);
            return null;
        }
    }

    public static boolean delete(String name) {
        try {
            return Files.deleteIfExists(TEMPLATE_DIR.resolve(sanitize(name) + ".json"));
        } catch (Exception e) {
            return false;
        }
    }

    private static List<BezierCurve.Vec3> controlPointsOf(Rail rail, RailExtraSupplier extra) {
        final BezierCurve curve = extra.getBezierCurve();
        if (curve != null && curve.getPoints() != null && curve.getPoints().size() >= 4) {
            return new ArrayList<>(curve.getPoints());
        }
        final Position p1 = extra.getExtraPosition1();
        final Position p2 = extra.getExtraPosition2();
        if (p1 == null || p2 == null) {
            return null;
        }
        final double dx = p2.getX() - p1.getX();
        final double dz = p2.getZ() - p1.getZ();
        final double dist = Math.hypot(dx, dz);
        final double handle = Math.max(1, dist / 3.0);
        final double a1 = extra.getExtraAngle1() != null ? extra.getExtraAngle1().angleRadians : Math.atan2(dz, dx);
        final double a2 = extra.getExtraAngle2() != null ? extra.getExtraAngle2().angleRadians : Math.atan2(-dz, -dx);
        final List<BezierCurve.Vec3> points = new ArrayList<>();
        points.add(new BezierCurve.Vec3(p1.getX(), p1.getY(), p1.getZ()));
        points.add(new BezierCurve.Vec3(p1.getX() + Math.cos(a1) * handle, p1.getY() + (p2.getY() - p1.getY()) / 3.0, p1.getZ() + Math.sin(a1) * handle));
        points.add(new BezierCurve.Vec3(p2.getX() + Math.cos(a2) * handle, p2.getY() - (p2.getY() - p1.getY()) / 3.0, p2.getZ() + Math.sin(a2) * handle));
        points.add(new BezierCurve.Vec3(p2.getX(), p2.getY(), p2.getZ()));
        return points;
    }

    private static String sanitize(String name) {
        return name.trim().replaceAll("[^\\w\\u4e00-\\u9fff-]", "_").toLowerCase(Locale.ROOT);
    }

    private static double round(double value) {
        return Math.round(value * 100000D) / 100000D;
    }
}
