package mylie.engine.graphics;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.*;
import mylie.engine.core.features.async.*;
import mylie.util.Versioned;
import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * The GraphicsContext class serves as an abstract base for implementing various
 * types of graphics contexts in a graphical application. It manages the setup
 * and configuration necessary for rendering graphical content, utilizing features
 * such as configuration management, threading, and asynchronous operations.
 * <p>
 * This class keeps track of a global counter for instances, helps in managing
 * a task queue, and utilizes a feature thread to handle graphical operations.
 * Subclasses must provide concrete implementations for the resource management
 * and rendering operations specified by abstract methods in this class.
 */
@Getter
@Setter(AccessLevel.PACKAGE)
public abstract class GraphicsContext {
    private final List<ApiFeature> apis = new ArrayList<>();
    private static int counter = 0;
    private final Configuration configuration;
    private final Async.Target target;
    private final BlockingQueue<Runnable> queue;
    private final FeatureThread featureThread;
    private final Map<Graphics.ContextProperty<?>, Versioned<?>> properties;
    protected GraphicsContext(Configuration configuration, Scheduler scheduler) {
        this.configuration = configuration;
        configuration.context = this;
        this.queue = new LinkedBlockingQueue<>();
        this.target = new Async.Target("GraphicsContext<" + (counter++) + ">");
        this.properties=new HashMap<>();
        scheduler.registerTarget(target(), queue()::add);
        featureThread = scheduler.createFeatureThread(target(), queue());
    }

    /**
     * Invoked when a specific setting in the configuration has been changed.
     * Subclasses should implement this method to handle the updates necessary
     * when a configuration parameter has been modified.
     *
     * @param <T> the type of the parameter value
     * @param parameter the configuration parameter that has changed
     * @param value the new value of the configuration parameter
     */
    protected abstract <T> void onSettingChanged(Configuration.Parameter<T> parameter, T value);

    /**
     * Destroys the current graphics context, releasing any associated resources
     * and performing necessary cleanup operations. This abstract method should
     * be implemented by subclasses to define specific destruction logic for a
     * particular graphics context implementation.
     *
     * @return a Result<Boolean> indicating the success or failure of the destroy operation.
     */
    protected abstract Result<Boolean> destroy();

    /**
     * Swaps the rendering buffers to display the current drawing frame.
     * This operation typically involves flipping the back buffer to become the visible front buffer
     * and vice versa. This method is intended to be implemented by subclasses to
     * provide specific logic for buffer swapping based on the rendering context being used.
     *
     * @return a Result<Boolean> indicating the success or failure of the buffer swap operation.
     */
    public abstract Result<Boolean> swapBuffers();

    <T extends ApiFeature> T getApi(Class<T> apiClass) {
        for (ApiFeature api : apis) {
            if (apiClass.isAssignableFrom(api.getClass())) {
                return apiClass.cast(api);
            }
        }
        return null;
    }

    /**
     * The `Parameters` class defines a collection of configuration parameters
     * used for setting various graphics context properties. Each parameter
     * is represented as a static field belonging to a nested `Configuration.Parameter`
     * class, allowing the configuration of different properties like window
     * resizability, vertical synchronization, transparency, window decorations,
     * multisampling, and more.
     * <p>
     * These parameters are meant to be used in tandem with a `Configuration` object
     * that manages their values and notifies any associated `GraphicsContext`
     * when a parameter value changes.
     * <p>
     * Fields:
     * - `Resizable`: Specifies if the window can be resized by the user.
     * - `VSync`: Enables or disables vertical synchronization.
     * - `Transparent`: Sets whether the window should support transparency.
     * - `AlwaysOnTop`: Indicates if the window should always be displayed on top of other windows.
     * - `Srgb`: Determines if the SRGB color space is used.
     * - `Decorated`: Controls whether the window has decorations (title bar, borders).
     * - `Multisampling`: Configures the level of multisampling for antialiasing purposes.
     * - `Title`: Sets the title of the window.
     * - `VideoMode`: Configures the video mode, such as fullscreen or windowed.
     * - `Icons`: Sets the icons used for the window.
     * <p>
     * This abstract class serves as a central place for defining and accessing
     * configuration parameters within the graphics system.
     */
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
        public static Configuration.Parameter<Icons> Icons;
    }

    /**
     * The Configuration class manages a collection of configuration parameters,
     * allowing for the setting and retrieval of parameter values. It is designed
     * to be used in conjunction with a GraphicsContext, notifying it when
     * configuration settings change.
     * <p>
     * This class is useful for managing settings that may affect the behavior
     * of a graphics system, and could be extended for other systems requiring
     * dynamic configuration capabilities.
     */
    public static class Configuration {
        private final Map<Parameter<?>, Object> parameters;
        GraphicsContext context;

        public Configuration() {
            parameters = new HashMap<>();
        }

        /**
         * Sets the value of a given configuration parameter. If the new value differs
         * from the current value, it updates the configuration and notifies the
         * graphics context of the change if the context is available.
         *
         * @param <T> the type of the parameter value
         * @param parameter the configuration parameter to be set
         * @param value the new value to assign to the parameter
         */
        public <T> void set(Parameter<T> parameter, T value) {
            boolean changed = !value.equals(parameters.get(parameter));
            parameters.put(parameter, value);
            if (context != null && changed) {
                context.onSettingChanged(parameter, value);
            }
        }

        /**
         * Retrieves the value associated with the specified configuration parameter.
         * If the parameter has not been explicitly set, it returns the default value
         * defined in the parameter.
         *
         * @param <T> the type of the parameter value
         * @param parameter the configuration parameter whose value is to be retrieved
         * @return the current value of the specified parameter, or its default value if not set
         */
        @SuppressWarnings("unchecked")
        public <T> T get(Parameter<T> parameter) {
            Object value = parameters.get(parameter);
            if (value == null) {
                return parameter.defaultValue();
            }
            return (T) value;
        }

        /**
         * Retrieves a set of parameters currently stored in the configuration.
         *
         * @return a set of Parameter objects representing the keys of the configuration map.
         */
        public Set<Parameter<?>> getParameters() {
            return parameters.keySet();
        }

        /**
         * Represents a configuration parameter with a default value. This class is
         * designed to be extended for different types of parameters within a configuration
         * system, typically associated with graphics or system settings.
         *
         * @param <T> the type of the parameter value
         */
        @Getter(AccessLevel.PACKAGE)
        public static class Parameter<T> {
            protected T defaultValue;
        }
    }

    /**
     * The VideoMode interface represents different display modes that can be used
     * within a graphical application. It provides several implementations for
     * specifying how the content should be displayed on the screen.
     */
    public interface VideoMode {

        /**
         * The Fullscreen record represents a full-screen video mode within a display,
         * implementing the VideoMode interface. It encompasses a specific display and
         * its particular video mode settings, which dictate how the graphical content is
         * rendered when in full-screen mode.
         *
         * @param display The display on which the full-screen mode is to be set.
         * @param videoMode The specific video mode configuration to use for full-screen rendering.
         */
        record Fullscreen(Graphics.Display display, Graphics.Display.VideoMode videoMode) implements VideoMode {}

        /**
         * The Windowed record represents a windowed video mode within a graphical
         * application, offering flexibility in terms of window size and position on
         * the screen, while implementing the VideoMode interface.
         *
         * @param display The display on which the windowed mode is to be set.
         * @param size The dimensions of the window, specified by a Vector2ic object.
         * @param position The position of the window on the screen, specified by
         *                 a Vector2ic object. The static field Centered provides a
         *                 default positioning at the screen center.
         */
        record Windowed(Graphics.Display display, Vector2ic size, Vector2ic position) implements VideoMode {
            public static final Vector2ic Centered = new Vector2i(0, 0);
        }

        /**
         * The WindowedFullscreen record represents a hybrid video mode within a graphical
         * application. It combines aspects of both windowed and fullscreen modes, allowing
         * content to be displayed in what is effectively fullscreen, but within windowed
         * constraints. This mode is useful for applications that require the immersion of
         * fullscreen while retaining some aspects of windowed operation, such as window
         * controls or task switching.
         *
         * @param display The display on which the windowed fullscreen mode is to be set.
         */
        record WindowedFullscreen(Graphics.Display display) implements VideoMode {}
    }

    public record Icons(String... paths) {}
}
