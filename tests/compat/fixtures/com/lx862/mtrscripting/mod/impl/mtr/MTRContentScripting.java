package com.lx862.mtrscripting.mod.impl.mtr;
import java.util.LinkedHashMap;
import java.util.Map;
public class MTRContentScripting {
    public static final Manager manager = new Manager();
    public static boolean fail;
    public static int calls;
    public static Manager getScriptManager() {
        calls++;
        if (fail) throw new IllegalStateException("fixture failure");
        return manager;
    }
    public static class Manager {
        public Instances getInstanceManager() { return instances; }
        public final Instances instances = new Instances();
    }
    public static class Instances {
        public final Map<Object, Object> values = new LinkedHashMap<>();
        public Map<Object, Object> getInstances() { return values; }
    }
    public static class Key {
        private final Object[] objs;
        public Key(Object... values) { objs = values; }
    }
    public static class Script {
        public boolean cooling;
        public Object error;
        public boolean duringFailCooldown() { return cooling; }
        public Object getCapturedScriptException() { return error; }
    }
    public static class Instance {
        public Script script = new Script();
        public Script getScript() { return script; }
    }
    public static class OtherInstance {
        public Script script = new Script();
        public Script getScript() { return script; }
    }
    public static class BrokenInstance {
        public boolean fail = true;
        public Script getScript() {
            if (fail) throw new IllegalStateException("health failure");
            return null;
        }
    }
}
