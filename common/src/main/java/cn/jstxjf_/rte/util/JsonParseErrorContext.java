package cn.jstxjf_.rte.util;

import cn.jstxjf_.rte.RTE;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonParseErrorContext {
    private String context = "";
    
    public void setContext(String context) {
        this.context = context;
    }
    
    public String getContext() {
        return context;
    }
    
    public void logError(String message) {
        RTE.LOGGER.error("[RTE/JSON] Error in {}: {}", context, message);
    }
    
    public void logWarning(String message) {
        RTE.LOGGER.warn("[RTE/JSON] Warning in {}: {}", context, message);
    }
}
