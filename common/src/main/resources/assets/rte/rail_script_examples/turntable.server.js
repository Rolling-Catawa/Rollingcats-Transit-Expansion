function create(ctx, state, mech) {
  if (!state.queue) {
    state.queue = [];
  }
}

function enqueue(state, railId) {
  var id = String(railId);
  if (id.length > 0 && state.queue.indexOf(id) < 0) {
    state.queue.push(id);
  }
}

function onTrainApproach(ctx, state, mech, e) {
  if (String(e.requiredRail) != String(mech.activeRail())) {
    enqueue(state, e.requiredRail);
  }
}

function onUse(ctx, state, mech, e) {
  var list = mech.candidates();
  var index = 0;
  for (var i = 0; i < list.size(); i++) {
    if (String(list.get(i).id) == String(mech.activeRail())) {
      index = i;
    }
  }
  enqueue(state, list.get((index + 1) % list.size()).id);
}

function onRedstone(ctx, state, mech, e) {
  if (e.oldPower == 0 && e.newPower > 0) {
    onUse(ctx, state, mech, e);
  }
}

function tick(ctx, state, mech) {
  if (mech.isMoving() || state.queue.length == 0) {
    return;
  }
  var next = state.queue[0];
  if (next == String(mech.activeRail())) {
    state.queue.shift();
    return;
  }
  if (String(mech.rotateTo(next, 10)) == "ok") {
    state.queue.shift();
    ctx.sync(JSON.stringify({ target: next }));
  }
}
