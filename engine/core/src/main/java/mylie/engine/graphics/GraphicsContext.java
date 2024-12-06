package mylie.engine.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import lombok.*;
import mylie.engine.core.features.async.*;
import org.joml.Vector2i;
import org.joml.Vector2ic;

@Getter
@Setter(AccessLevel.PACKAGE)
public abstract class GraphicsContext {
    private static int counter = 0;
    private final Configuration configuration;
    private final Async.Target target;
    private final BlockingQueue<Runnable> queue;
    private final FeatureThread featureThread;

    public GraphicsContext(Configuration configuration, Scheduler scheduler) {
        this.configuration = configuration;
        configuration.context = this;
        this.queue = new LinkedTransferQueue<>();
        this.target = new Async.Target("GraphicsContext<" + (counter++) + ">");
        scheduler.registerTarget(target(), queue()::add);
        featureThread = scheduler.createFeatureThread(target(), queue());
    }

    protected abstract void applySettings();

    protected abstract Result<Boolean> destroy();

    public abstract Result<Boolean> swapBuffers();

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public abstract static class Parameters {
        public static Configuration.Parameter<Boolean> Resizable;
        public static Configuration.Parameter<Boolean> VSync;
        public static Configuration.Parameter<Boolean> Transparent;
        public static Configuration.Parameter<Boolean> AlwaysOnTop;
        public static Configuration.Parameter<Boolean> Srgb;
        public static Configuration.Parameter<Boolean> Decorated;
        public static Configuration.Parameter<Integer> Multisampling;
        public static Configuration.Parameter<String> Title;
        public static Configuration.Parameter<VideoMode> VideoMode;
    }

    public static class Configuration {
        private final Map<Parameter<?>, Object> parameters;
        GraphicsContext context;

        public Configuration() {
            parameters = new HashMap<>();
        }

        public <T> void set(Parameter<T> parameter, T value) {
            parameters.put(parameter, value);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(Parameter<T> parameter) {
            Object value = parameters.get(parameter);
            if (value == null) {
                return parameter.defaultValue();
            }
            return (T) value;
        }

        public Set<Parameter<?>> getParameters() {
            return parameters.keySet();
        }

        @Getter(AccessLevel.PACKAGE)
        public static class Parameter<T> {
            protected T defaultValue;
        }
    }

    public interface VideoMode {

        record Fullscreen(Graphics.Display display, Graphics.Display.VideoMode videoMode) implements VideoMode {}

        record Windowed(Graphics.Display display, Vector2ic size,Vector2ic position) implements VideoMode {
            public static final Vector2ic Centered = new Vector2i(0, 0);
        }

        record WindowedFullscreen(Graphics.Display display) implements VideoMode {}
    }
}
