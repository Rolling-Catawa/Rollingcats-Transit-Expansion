package cn.jstxjf_.rte;
public class RTE {
    public static final Log LOGGER = new Log();
    public static class Log {
        public int warnings;
        public void info(String text, Object... args) {}
        public void warn(String text, Object... args) { warnings++; }
    }
}
