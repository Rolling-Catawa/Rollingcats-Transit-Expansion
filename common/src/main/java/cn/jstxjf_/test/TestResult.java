package cn.jstxjf_.test;

import java.util.ArrayList;
import java.util.List;

public final class TestResult {

    public enum Status { PASS, FAIL, INFO, SKIP }

    public final String name;
    public final Status status;
    public final String detail;

    private TestResult(String name, Status status, String detail) {
        this.name = name;
        this.status = status;
        this.detail = detail;
    }

    public static TestResult pass(String name, String detail) {
        return new TestResult(name, Status.PASS, detail);
    }

    public static TestResult fail(String name, String detail) {
        return new TestResult(name, Status.FAIL, detail);
    }

    public static TestResult info(String name, String detail) {
        return new TestResult(name, Status.INFO, detail);
    }

    public static TestResult skip(String name, String detail) {
        return new TestResult(name, Status.SKIP, detail);
    }

    public static TestResult check(String name, boolean ok, String detail) {
        return ok ? pass(name, detail) : fail(name, detail);
    }

    @Override
    public String toString() {
        final String mark = switch (status) {
            case PASS -> "PASS";
            case FAIL -> "FAIL";
            case INFO -> "INFO";
            case SKIP -> "SKIP";
        };
        return String.format("[%-4s] %-34s %s", mark, name, detail);
    }

    public static List<TestResult> newList() {
        return new ArrayList<>();
    }
}
