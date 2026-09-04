package cn.jstxjf_.rte.scripting.engine;

import java.util.List;

public interface ScriptBackend extends AutoCloseable {

    void eval(String script, String sourceName) throws Exception;

    Object getFunction(String name);

    void invoke(List<Object> functions, Object[] args) throws Exception;

    void bindClass(String alias, Class<?> clazz) throws Exception;

    void bindStaticFunction(String alias, Class<?> clazz, String methodName) throws Exception;

    Object newStateObject();

    void removeGlobal(String name) throws Exception;

    String engineName();

    @Override
    void close();
}
