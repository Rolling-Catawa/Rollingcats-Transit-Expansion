package cn.jstxjf_.rte.mechanism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class TrainProximityTracker {

    public record TrainSample(long id, String railOnPath, double distanceToMechanism, boolean headOn, boolean tailOn, boolean stopped) {
    }

    private static final class Seen {
        boolean approached;
        boolean on;
        boolean stopped;
    }

    private final Map<Long, Seen> seen = new HashMap<>();

    public List<MechanismEvent> update(Iterable<TrainSample> samples, double approachRange) {
        final List<MechanismEvent> events = new ArrayList<>();
        final Set<Long> present = new HashSet<>();
        for (final TrainSample sample : samples) {
            present.add(sample.id());
            final Seen state = seen.computeIfAbsent(sample.id(), ignored -> new Seen());
            final boolean on = sample.headOn() || sample.tailOn();
            if (!on && !state.on && sample.distanceToMechanism() >= 0 && sample.distanceToMechanism() <= approachRange && !state.approached) {
                state.approached = true;
                events.add(new MechanismEvent(MechanismEvent.Type.APPROACH, sample.id(), sample.distanceToMechanism(), sample.railOnPath()));
            }
            if (on && !state.on) {
                state.on = true;
                state.approached = true;
                events.add(new MechanismEvent(MechanismEvent.Type.ENTER, sample.id(), 0, sample.railOnPath()));
            } else if (!on && state.on) {
                state.on = false;
                state.approached = false;
                state.stopped = false;
                events.add(new MechanismEvent(MechanismEvent.Type.LEAVE, sample.id(), 0, sample.railOnPath()));
            }
            if (on && sample.stopped() && !state.stopped) {
                state.stopped = true;
                events.add(new MechanismEvent(MechanismEvent.Type.STOP, sample.id(), 0, sample.railOnPath()));
            } else if (!sample.stopped()) {
                state.stopped = false;
            }
            if (!on && sample.distanceToMechanism() > approachRange) {
                state.approached = false;
            }
        }
        for (final Map.Entry<Long, Seen> entry : new ArrayList<>(seen.entrySet())) {
            if (!present.contains(entry.getKey())) {
                if (entry.getValue().on) {
                    events.add(new MechanismEvent(MechanismEvent.Type.LEAVE, entry.getKey(), 0, ""));
                }
                seen.remove(entry.getKey());
            }
        }
        return events;
    }

    public Set<Long> trainsOn() {
        final Set<Long> result = new HashSet<>();
        seen.forEach((id, state) -> {
            if (state.on) {
                result.add(id);
            }
        });
        return result;
    }
}
