package cn.jstxjf_.rte.gui.imgui;

import cn.jstxjf_.rte.mechanism.RailScriptExamples;

public final class RailScriptTemplates {

    public static final String SERVER = RailScriptExamples.read("turntable.server.js");
    public static final String CLIENT = "";
    public static final String META = RailScriptExamples.read("turntable.json");

    private RailScriptTemplates() {
    }
}
