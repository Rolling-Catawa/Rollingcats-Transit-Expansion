package cn.jstxjf_.rte.scripting.engine;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.List;
import java.util.function.Predicate;

public class RhinoScriptBackend implements ScriptBackend {

    private final ContextFactory contextFactory;
    private final ScriptableObject scope;

    private static boolean isObfuscatedDefaultPackageName(String className) {
        return className.indexOf('.') < 0 && !className.isEmpty() && Character.isLowerCase(className.charAt(0));
    }

    public RhinoScriptBackend(Predicate<String> classFilter) {
        final Predicate<String> shutterFilter = className -> !isObfuscatedDefaultPackageName(className) && (
            classFilter.test(className)
                || className.startsWith("cn.jstxjf_.rte.")
                || className.startsWith("org.mtr.")
                || className.startsWith("net.minecraft.")
                || className.startsWith("com.mojang.")
                || className.startsWith("org.joml.")
                || className.startsWith("org.mozilla.javascript.")
                || className.startsWith("sun.java2d.")
                || className.startsWith("sun.awt.")
                || className.startsWith("sun.font.")
                || className.startsWith("javax.imageio."));
        contextFactory = new ContextFactory() {
            @Override
            protected Context makeContext() {
                final Context cx = super.makeContext();
                cx.setLanguageVersion(Context.VERSION_ES6);
                cx.setOptimizationLevel(-1);
                cx.setClassShutter(shutterFilter::test);
                cx.setWrapFactory(new org.mozilla.javascript.WrapFactory() {
                    @Override
                    public org.mozilla.javascript.Scriptable wrapAsJavaObject(
                        Context context, org.mozilla.javascript.Scriptable staticScope,
                        Object javaObject, Class<?> staticType) {
                        if (javaObject instanceof java.util.List) {
                            return new org.mozilla.javascript.NativeJavaList(staticScope, javaObject);
                        }
                        if (javaObject instanceof java.util.Map) {
                            return new org.mozilla.javascript.NativeJavaMap(staticScope, javaObject);
                        }
                        return super.wrapAsJavaObject(context, staticScope, javaObject, staticType);
                    }
                });
                return cx;
            }
        };
        final Context cx = contextFactory.enterContext();
        try {
            scope = new ImporterTopLevel(cx, false);
            cx.evaluateString(scope,
                "var Java = { type: function(name) { return eval('Packages.' + name); } };",
                "rte:java_type_shim", 1, null);
        } finally {
            Context.exit();
        }
    }

    @Override
    public void eval(String script, String sourceName) throws Exception {
        final Context cx = contextFactory.enterContext();
        try {
            cx.evaluateString(scope, script, sourceName, 1, null);
        } finally {
            Context.exit();
        }
    }

    @Override
    public Object getFunction(String name) {
        contextFactory.enterContext();
        try {
            final Object member = ScriptableObject.getProperty(scope, name);
            return member instanceof Function ? member : null;
        } finally {
            Context.exit();
        }
    }

    @Override
    public void invoke(List<Object> functions, Object[] args) throws Exception {
        final Context cx = contextFactory.enterContext();
        try {
            final Object[] wrapped = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                wrapped[i] = Context.javaToJS(args[i], scope);
            }
            for (final Object function : functions) {
                if (cn.jstxjf_.rte.scripting.ScriptFailureIsolation.shouldSkip(function)) {
                    continue;
                }
                try {
                    ((Function) function).call(cx, scope, scope, wrapped);
                    cn.jstxjf_.rte.scripting.ScriptFailureIsolation.noteSuccess(function);
                } catch (Exception e) {
                    cn.jstxjf_.rte.scripting.ScriptFailureIsolation.note(function, e);
                }
            }

        } finally {
            Context.exit();
        }
    }

    @Override
    public void bindClass(String alias, Class<?> clazz) {
        final Context cx = contextFactory.enterContext();
        try {
            ScriptableObject.putProperty(scope, alias, new NativeJavaClass(scope, clazz));
        } finally {
            Context.exit();
        }
    }

    @Override
    public void bindStaticFunction(String alias, Class<?> clazz, String methodName) throws Exception {
        final Context cx = contextFactory.enterContext();
        try {
            ScriptableObject.putProperty(scope, "__rteBindTmp", new NativeJavaClass(scope, clazz));
            cx.evaluateString(scope, "var " + alias + " = __rteBindTmp." + methodName + ";", "rte:bind", 1, null);
            scope.delete("__rteBindTmp");
        } finally {
            Context.exit();
        }
    }

    @Override
    public Object newStateObject() {
        final Context cx = contextFactory.enterContext();
        try {
            return cx.newObject(scope);
        } finally {
            Context.exit();
        }
    }

    @Override
    public void removeGlobal(String name) {
        contextFactory.enterContext();
        try {
            scope.delete(name);
        } finally {
            Context.exit();
        }
    }

    @Override
    public String engineName() {
        return "rhino";
    }

    @Override
    public void close() {
    }

    public Scriptable topScope() {
        return scope;
    }
}
