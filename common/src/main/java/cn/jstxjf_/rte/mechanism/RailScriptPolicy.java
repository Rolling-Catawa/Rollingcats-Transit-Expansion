package cn.jstxjf_.rte.mechanism;

import java.nio.charset.StandardCharsets;

public final class RailScriptPolicy {

    public static final String OK = "ok";
    public static final String DENIED = "denied";
    public static final String BAD_ID = "bad_id";
    public static final String TOO_LARGE = "too_large";
    public static final int MAX_GROUP_LENGTH = 64;

    private RailScriptPolicy() {
    }

    public static String decideSave(boolean op, boolean singleplayerOwner, String id, String text) {
        if (!op && !singleplayerOwner) {
            return DENIED;
        }
        if (!RailScriptStore.isValidId(id)) {
            return BAD_ID;
        }
        if (text != null && text.getBytes(StandardCharsets.UTF_8).length > RailScriptStore.MAX_BYTES) {
            return TOO_LARGE;
        }
        return OK;
    }

    public static String decideAssign(boolean op, boolean creative, boolean nearby, String group, String scriptId) {
        if (!op && !(creative && nearby)) {
            return DENIED;
        }
        if (group == null || group.length() > MAX_GROUP_LENGTH || (scriptId != null && !scriptId.isEmpty() && !RailScriptStore.isValidId(scriptId))) {
            return BAD_ID;
        }
        return OK;
    }
}
