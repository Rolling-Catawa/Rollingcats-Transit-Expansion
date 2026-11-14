function create(ctx, state, mech) {
  state.open = !!state.open;
}

function hinge(mech) {
  var rail = mech.candidates().get(0);
  return { x: rail.x1, y: rail.y1, z: rail.z1, yaw: rail.yaw };
}

function onRedstone(ctx, state, mech, e) {
  var open = e.newPower > 0;
  if (open == state.open) {
    return;
  }
  var h = hinge(mech);
  var result = open
    ? mech.animateAround(h.x, h.y, h.z, 0, 70, 0, 0, 0, 0, 12, 1)
    : mech.animateAround(h.x, h.y, h.z, 0, 0, 0, 0, 0, 0, 12, 1);
  if (String(result) == "ok") {
    state.open = open;
    ctx.sync(JSON.stringify({ open: open }));
  }
}
