package cn.jstxjf_.rte.data;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.chat.Component;

public interface ConfigResponder {

    String key();
    void init(Map<String, String> map);

    /**
     * Registers both ordinary responders and ANTE-style composite JavaScript
     * responders.  Composite responders initialise several child keys but the
     * old Cloth screen was the only code able to discover their nested widget
     * definitions.  RTE keeps those child values visible/editable using safe
     * text fallbacks instead of dropping the controls entirely.
     */
    static void registerInto(Map<String, String> configs, Map<String, ConfigResponder> responders,
                             ConfigResponder responder) {
        if (responder == null || configs == null || responders == null) {
            return;
        }
        final InitTrackingMap trackingMap = new InitTrackingMap(configs);
        responder.init(trackingMap);
        final String rootKey = responder.key();
        if (rootKey != null) {
            responders.put(rootKey, responder);
        }
        for (final String childKey : trackingMap.touchedKeys) {
            if (!Objects.equals(rootKey, childKey) && configs.containsKey(childKey)
                    && !responders.containsKey(childKey)) {
                responders.put(childKey,
                    new TextField(childKey, Component.literal(childKey), configs.get(childKey)));
            }
        }
    }

    /**
     * Records keys inspected while a responder installs defaults. This also
     * discovers composite child responders when their values were already
     * persisted, where comparing the map before/after init would miss them.
     */
    final class InitTrackingMap extends AbstractMap<String, String> {
        private final Map<String, String> delegate;
        private final Set<String> touchedKeys = new LinkedHashSet<>();

        private InitTrackingMap(Map<String, String> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean containsKey(Object key) {
            record(key);
            return delegate.containsKey(key);
        }

        @Override
        public String get(Object key) {
            record(key);
            return delegate.get(key);
        }

        @Override
        public String put(String key, String value) {
            touchedKeys.add(key);
            return delegate.put(key, value);
        }

        @Override
        public Set<Entry<String, String>> entrySet() {
            return delegate.entrySet();
        }

        private void record(Object key) {
            if (key instanceof String stringKey) {
                touchedKeys.add(stringKey);
            }
        }
    }

    static class TextField implements ConfigResponder {
        public String key;
        public Component name;
        public String defaultValue;
        public Function<String, String> transformer = str -> str;
        public Function<String, Optional<Component>> errorSupplier = str -> Optional.empty();
        public Consumer<String> saveConsumer = str -> {};
        public Function<String, Optional<Component[]>> tooltipSupplier = str -> Optional.empty();
        public boolean requireRestart = false;

        public TextField(String key, Component name, String defaultValue) {
            this.key = key;
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public TextField(String key, String defaultValue) {
            this(key, Component.literal(key), defaultValue);
        }

        public TextField(String key, Component name, String defaultValue, Function<String, String> transformer, Function<String, Optional<Component>> errorSupplier, Consumer<String> saveConsumer, Function<String, Optional<Component[]>> tooltipSupplier, boolean requireRestart) {
            this.key = key;
            setName(name);
            setDefaultValue(defaultValue);
            setTransformer(transformer);
            setErrorSupplier(errorSupplier);
            setSaveConsumer(saveConsumer);
            setTooltipSupplier(tooltipSupplier);
            setRequireRestart(requireRestart);
        }

        @Override
        public void init(Map<String, String> map) {
            if (!map.containsKey(key)) {
                map.put(key, defaultValue);
            }
        }

        @Override
        public String key() {
            return key;
        }


        public TextField setTransformer(Function<String, String> transformer) { this.transformer = transformer; return this; }
        public TextField setErrorSupplier(Function<String, Optional<Component>> errorSupplier) { this.errorSupplier = errorSupplier; return this; }
        public TextField setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; return this; }
        public TextField setName(Component name) { this.name = name; return this; }
        public TextField setSaveConsumer(Consumer<String> saveConsumer) { this.saveConsumer = saveConsumer; return this; }
        public TextField setTooltipSupplier(Function<String, Optional<Component[]>> tooltipSupplier) { this.tooltipSupplier = tooltipSupplier; return this; }
        public TextField setRequireRestart(boolean requireRestart) { this.requireRestart = requireRestart; return this; }
    }

    static class CycleToggle implements ConfigResponder {
        public String key;
        public Component name;
        public int defaultValue;
        public List<String> values;
        public Consumer<Integer> saveConsumer = ind -> {};
        public Function<Integer, Optional<Component[]>> tooltipSupplier = ind -> Optional.empty();
        public boolean requireRestart = false;

        public CycleToggle(String key, Component name, int defaultValue, List<String> values) {
            this.key = key;
            this.name = name;
            this.defaultValue = defaultValue;
            this.values = values;
        }

        public CycleToggle(String key, Component name, int defaultValue, List<String> values, Function<Integer, Optional<Component[]>> tooltipSupplier, Consumer<Integer> saveConsumer, boolean requireRestart) {
            this.key = key;
            setName(name);
            setDefaultValue(defaultValue);
            setValues(values);
            setTooltipSupplier(tooltipSupplier);
            setSaveConsumer(saveConsumer);
            setRequireRestart(requireRestart);
        }

        @Override
        public void init(Map<String, String> map) {
            if (!map.containsKey(key)) {
                map.put(key, values.get(0));
            }
        }

        @Override
        public String key() {
            return key;
        }


        public CycleToggle setDefaultValue(int defaultValue) { this.defaultValue = defaultValue; return this; }
        public CycleToggle setName(Component name) { this.name = name; return this; }
        public CycleToggle setSaveConsumer(Consumer<Integer> saveConsumer) { this.saveConsumer = saveConsumer; return this; }
        public CycleToggle setTooltipSupplier(Function<Integer, Optional<Component[]>> tooltipSupplier) { this.tooltipSupplier = tooltipSupplier; return this; }
        public CycleToggle setRequireRestart(boolean requireRestart) { this.requireRestart = requireRestart; return this; }
        public CycleToggle setValues(List<String> values) { this.values = values; return this; }
    }

    static class BooleanToggle implements ConfigResponder {
        public String key;
        public Component name;
        public boolean defaultValue;
        public Consumer<Boolean> saveConsumer = bool -> {};
        public Function<Boolean, Optional<Component[]>> tooltipSupplier = bool -> Optional.empty();
        public boolean requireRestart = false;

        public BooleanToggle(String key, Component name, boolean defaultValue) {
            this.key = key;
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public BooleanToggle(String key, boolean defaultValue) {
            this(key, Component.literal(key), defaultValue);
        }

        public BooleanToggle(String key, Component name, boolean defaultValue, Function<Boolean, Optional<Component[]>> tooltipSupplier, Consumer<Boolean> saveConsumer, boolean requireRestart) {
            this.key = key;
            setName(name);
            setDefaultValue(defaultValue);
            setTooltipSupplier(tooltipSupplier);
            setSaveConsumer(saveConsumer);
            setRequireRestart(requireRestart);
        }

        @Override
        public void init(Map<String, String> map) {
            if (!map.containsKey(key)) {
                map.put(key, defaultValue + "");
            }
        }

        @Override
        public String key() {
            return key;
        }


        public BooleanToggle setDefaultValue(boolean defaultValue) { this.defaultValue = defaultValue; return this; }
        public BooleanToggle setName(Component name) { this.name = name; return this; }
        public BooleanToggle setSaveConsumer(Consumer<Boolean> saveConsumer) { this.saveConsumer = saveConsumer; return this; }
        public BooleanToggle setTooltipSupplier(Function<Boolean, Optional<Component[]>> tooltipSupplier) { this.tooltipSupplier = tooltipSupplier; return this; }
        public BooleanToggle setRequireRestart(boolean requireRestart) { this.requireRestart = requireRestart; return this; }
    }

    static class DoubleSlider implements ConfigResponder {
        public String key;
        public Component name;
        public double defaultValue;
        public double min;
        public double max;
        public int step;
        public Function<Double, Component> textGetter = v -> Component.literal(String.format("%.2f", v));
        public Consumer<Double> saveConsumer = num -> {};
        public Function<Double, Optional<Component[]>> tooltipSupplier = value -> Optional.empty();
        public boolean requireRestart = false;

        public DoubleSlider(String key, Component name, double defaultValue, double min, double max, int step) {
            this.key = key;
            this.name = name;
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
            this.step = step;
        }

        public DoubleSlider(String key, double defaultValue, double min, double max) {
            this(key, Component.literal(key), defaultValue, min, max, 100);
        }

        public DoubleSlider(String key, Component name, double defaultValue, double min, double max, int step, Function<Double, Optional<Component[]>> tooltipSupplier, Consumer<Double> saveConsumer, boolean requireRestart) {
            this.key = key;
            setName(name);
            setDefaultValue(defaultValue);
            setMin(min);
            setMax(max);
            setStep(step);
            setTooltipSupplier(tooltipSupplier);
            setSaveConsumer(saveConsumer);
            setRequireRestart(requireRestart);
        }

        @Override
        public void init(Map<String, String> map) {
            if (!map.containsKey(key)) {
                map.put(key, defaultValue + "");
            }
        }

        @Override
        public String key() {
            return key;
        }

        private int getLevel(double value) {
            return (int) Math.round((value - min) / (max - min) * step);
        }

        public double getValue(int level) {
            return min + (max - min) * level / step;
        }


        public DoubleSlider setDefaultValue(double defaultValue) { this.defaultValue = defaultValue; return this; }
        public DoubleSlider setName(Component name) { this.name = name; return this; }
        public DoubleSlider setSaveConsumer(Consumer<Double> saveConsumer) { this.saveConsumer = saveConsumer; return this; }
        public DoubleSlider setTooltipSupplier(Function<Double, Optional<Component[]>> tooltipSupplier) { this.tooltipSupplier = tooltipSupplier; return this; }
        public DoubleSlider setTextGetter(Function<Double, Component> textGetter) { this.textGetter = textGetter; return this; }
        public DoubleSlider setRequireRestart(boolean requireRestart) { this.requireRestart = requireRestart; return this; }
        public DoubleSlider setMin(double min) { this.min = min; return this; }
        public DoubleSlider setMax(double max) { this.max = max; return this; }
        public DoubleSlider setStep(int step) { this.step = step; return this; }
    }


}

