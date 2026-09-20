package cn.jstxjf_.rte.scripting.engine;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.SandboxPolicy;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.FileSystem;
import org.graalvm.polyglot.io.IOAccess;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class GraalScriptBackend implements ScriptBackend {

    private static final Engine SHARED_ENGINE = Engine.newBuilder()
        .option("engine.WarnInterpreterOnly", "false")
        .option("log.file", "./logs/latest.log")
        .build();
    private final Context context;

    public GraalScriptBackend(Predicate<String> classFilter, Path scriptDataFolder) {
        context = Context.newBuilder("js")
            .engine(SHARED_ENGINE)
            .allowNativeAccess(false)
            .allowCreateThread(true)
            .allowCreateProcess(false)
            .allowHostClassLoading(true)
            .allowHostClassLookup(classFilter::test)
            .allowIO(IOAccess.newBuilder().fileSystem(new RestrictedFileSystem(scriptDataFolder)).build())
            .allowEnvironmentAccess(EnvironmentAccess.INHERIT)
            .allowExperimentalOptions(true)
            .allowInnerContextOptions(true)
            .sandbox(SandboxPolicy.TRUSTED)
            .allowHostAccess(
                HostAccess.newBuilder()
                    .allowPublicAccess(true)
                    .allowAllImplementations(true)
                    .allowAllClassImplementations(true)
                    .allowArrayAccess(true)
                    .allowListAccess(true)
                    .allowBufferAccess(true)
                    .allowIterableAccess(true)
                    .allowIteratorAccess(true)
                    .allowMapAccess(true)
                    .allowAccessInheritance(true)
                    .allowBigIntegerNumberAccess(true)
                    .targetTypeMapping(Double.class, String.class, value -> true,
                        value -> value == Math.floor(value) && !Double.isInfinite(value)
                            ? Long.toString((long) (double) value) : Double.toString(value),
                        HostAccess.TargetMappingPrecedence.LOW)
                    .targetTypeMapping(Integer.class, String.class, value -> true,
                        String::valueOf, HostAccess.TargetMappingPrecedence.LOW)
                    .targetTypeMapping(Long.class, String.class, value -> true,
                        String::valueOf, HostAccess.TargetMappingPrecedence.LOW)
                    .targetTypeMapping(Boolean.class, String.class, value -> true,
                        String::valueOf, HostAccess.TargetMappingPrecedence.LOW)
                    .targetTypeMapping(Double.class, Double.class, value -> true, value -> value, HostAccess.TargetMappingPrecedence.HIGHEST)
                    .targetTypeMapping(Double.class, Float.class, value -> true, value -> value.floatValue(), HostAccess.TargetMappingPrecedence.HIGH)
                    .targetTypeMapping(Double.class, Long.class, value -> true, value -> Math.round(value), HostAccess.TargetMappingPrecedence.LOW)
                    .targetTypeMapping(Double.class, Integer.class, value -> true, value -> (int) Math.round(value), HostAccess.TargetMappingPrecedence.LOW)
                    .targetTypeMapping(Double.class, Short.class, value -> true, value -> (short) Math.round(value), HostAccess.TargetMappingPrecedence.LOW)
                    .targetTypeMapping(Double.class, Byte.class, value -> true, value -> (byte) Math.round(value), HostAccess.TargetMappingPrecedence.LOW)
                    .build()
            )
            .option("js.nashorn-compat", "true")
            .option("js.ecmascript-version", "latest")
            .option("js.foreign-object-prototype", "true")
            .build();

        try {
            context.eval("js", "load(\"nashorn:mozilla_compat.js\");");
        } catch (Exception ignored) {
        }
    }

    @Override
    public void eval(String script, String sourceName) throws Exception {
        context.eval(Source.newBuilder("js", script, sourceName).build());
    }

    @Override
    public Object getFunction(String name) {
        final Value func = context.getBindings("js").getMember(name);
        return func != null && func.canExecute() ? func : null;
    }

    @Override
    public void invoke(List<Object> functions, Object[] args) {
        for (final Object function : functions) {
            if (cn.jstxjf_.rte.scripting.ScriptFailureIsolation.shouldSkip(function)) {
                continue;
            }
            try {
                ((Value) function).executeVoid(args);
                cn.jstxjf_.rte.scripting.ScriptFailureIsolation.noteSuccess(function);
            } catch (RuntimeException e) {
                cn.jstxjf_.rte.scripting.ScriptFailureIsolation.note(function, e);
            }
        }

    }

    @Override
    public void bindClass(String alias, Class<?> clazz) {
        context.eval("js", "var " + alias + " = Java.type('" + clazz.getName() + "');");
    }

    @Override
    public void bindStaticFunction(String alias, Class<?> clazz, String methodName) {
        context.eval("js", "var " + alias + " = Java.type('" + clazz.getName() + "')." + methodName + ";");
    }

    @Override
    public Object newStateObject() {
        return ProxyObject.fromMap(new HashMap<>());
    }

    @Override
    public void removeGlobal(String name) {
        context.eval("js", "delete " + name + ";");
    }

    @Override
    public String engineName() {
        return "graal";
    }

    @Override
    public void close() {
        try {
            context.close();
        } catch (Exception ignored) {
        }
    }

    private static final class RestrictedFileSystem implements FileSystem {

        private final FileSystem delegate = FileSystem.newDefaultFileSystem();
        private final Path root;

        private RestrictedFileSystem(Path root) {
            this.root = root;
        }

        private Path resolve(Path path) {
            final Path resolved = (path.isAbsolute() ? path : root.resolve(path)).normalize();
            if (!resolved.startsWith(root)) {
                throw new SecurityException("Path outside script data folder: " + path);
            }
            return resolved;
        }

        @Override
        public Path parsePath(URI uri) {
            return resolve(delegate.parsePath(uri));
        }

        @Override
        public Path parsePath(String path) {
            return resolve(delegate.parsePath(path));
        }

        @Override
        public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
            delegate.checkAccess(resolve(path), modes, linkOptions);
        }

        @Override
        public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
            delegate.createDirectory(resolve(dir), attrs);
        }

        @Override
        public void delete(Path path) throws IOException {
            delegate.delete(resolve(path));
        }

        @Override
        public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
            return delegate.newByteChannel(resolve(path), options, attrs);
        }

        @Override
        public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
            return delegate.newDirectoryStream(resolve(dir), filter);
        }

        @Override
        public Path toAbsolutePath(Path path) {
            return resolve(path);
        }

        @Override
        public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
            final Path realPath = delegate.toRealPath(resolve(path), linkOptions).normalize();
            if (!realPath.startsWith(root)) {
                throw new SecurityException("Path outside script data folder: " + path);
            }
            return realPath;
        }

        @Override
        public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
            return delegate.readAttributes(resolve(path), attributes, options);
        }

        @Override
        public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
            delegate.setAttribute(resolve(path), attribute, value, options);
        }

        @Override
        public void copy(Path source, Path target, CopyOption... options) throws IOException {
            delegate.copy(resolve(source), resolve(target), options);
        }

        @Override
        public void move(Path source, Path target, CopyOption... options) throws IOException {
            delegate.move(resolve(source), resolve(target), options);
        }

        @Override
        public Path getTempDirectory() {
            return root;
        }
    }
}
