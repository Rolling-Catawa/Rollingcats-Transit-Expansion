package cn.jstxjf_.rte.api;

import cn.jstxjf_.rte.data.RailPresets;
import org.mtr.core.data.Rail;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public final class TrackConnectorRegistry {

    public interface TrackConnector {
        String id();

        String translationKey();

        RailPresets.PresetResult compute(Rail rail);
    }

    private static final Map<String, TrackConnector> CONNECTORS = new LinkedHashMap<>();

    static {
        for (final RailPresets.Preset preset : RailPresets.Preset.values()) {
            final String id = "rte:" + preset.name().toLowerCase(java.util.Locale.ROOT);
            CONNECTORS.put(id, new TrackConnector() {
                @Override
                public String id() {
                    return id;
                }

                @Override
                public String translationKey() {
                    return "gui.rte.rail_preset." + preset.name().toLowerCase(java.util.Locale.ROOT);
                }

                @Override
                public RailPresets.PresetResult compute(Rail rail) {
                    return RailPresets.compute(rail, preset);
                }
            });
        }
    }

    private TrackConnectorRegistry() {
    }

    public static synchronized void register(TrackConnector connector) {
        if (connector == null || connector.id() == null) {
            throw new IllegalArgumentException("connector and id must not be null");
        }
        CONNECTORS.put(connector.id(), connector);
    }

    public static synchronized List<TrackConnector> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(CONNECTORS.values()));
    }
}
