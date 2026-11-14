package cn.jstxjf_.rte.scripting;

public class ScriptHolderServer extends ScriptHolderBase {

    public ScriptHolderServer(cn.jstxjf_.rte.scripting.engine.ScriptEngineType engineType) {
        super("server", engineType);
    }

    @Override
    protected void appendImporter() {
        super.appendImporter();
        inject(cn.jstxjf_.rte.mechanism.MechanismTransform.class, "MechanismTransform");
    }
}
