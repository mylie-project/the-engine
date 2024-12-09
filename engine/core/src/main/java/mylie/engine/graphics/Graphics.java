package mylie.engine.graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import mylie.util.Versioned;
import org.joml.Vector2ic;
import org.joml.Vector4ic;

/**
 * The Graphics class provides an abstract foundation for handling graphics-related operations
 * such as querying available displays and managing video modes. This class acts as a base
 * for specific implementations that manage how graphical content is rendered and displayed
 * across multiple screens.
 */
public abstract class Graphics {

    /**
     * Represents a display for rendering graphical content. A Display contains information
     * about whether it is the primary display, its default video mode, and the available video
     * modes it supports.
     */
    @ToString
    @Getter
    @AllArgsConstructor
    public static class Display {
        private boolean primary;
        private VideoMode defaultVideoMode;
        private List<? extends VideoMode> videoModes;

        /**
         * Represents a specific configuration of a video mode within a display. VideoMode holds the
         * essential properties that define the display resolution, refresh rate, and format bits.
         * It is used to manage and configure how images and graphics are rendered on a display.
         */
        @ToString
        @Getter
        @AllArgsConstructor
        public static class VideoMode {
            private Vector2ic resolution;
            private int refreshRate;
            private Vector4ic formatBits;
        }
    }

    public interface ContextProperties {
        ContextProperty<Vector2ic> Size = new ContextProperty<>();
        ContextProperty<Boolean> Focus = new ContextProperty<>();
        ContextProperty<Boolean> Maximized = new ContextProperty<>();
        ContextProperty<Vector2ic> Position = new ContextProperty<>();
        ContextProperty<Vector2ic> FrameBufferSize = new ContextProperty<>();
    }

    public abstract static class ContextCapabilities {
        public static ContextCapability<Integer> MaxTextureSize;
        public static ContextCapability<Integer> Max3dTextureSize;
    }

    @Slf4j
    public abstract static class ContextCapability<T> {
        @Getter(AccessLevel.PACKAGE)
        private static final List<ContextCapability<?>> allCapabilities = new ArrayList<>();

        private final String name;

        public ContextCapability(String name) {
            this.name = name;
            allCapabilities.add(this);
        }

        @SuppressWarnings("unchecked")
        public T get(GraphicsContext context) {
            return (T) context.capabilities().get(this);
        }

        protected abstract T init(GraphicsContext context);

        static void initAll(GraphicsContext context) {
            context.queue().add(new Runnable() {
                @Override
                public void run() {
                    for (ContextCapability<?> capability : allCapabilities) {
                        Object init = capability.init(context);
                        context.capabilities().put(capability, init);
                        log.trace("Capability<{}> = {}", capability.name, init);
                    }
                }
            });
        }
    }

    public static class ContextProperty<T> {
        public Versioned.Reference<T> get(GraphicsContext context) {
            Map<ContextProperty<?>, Versioned<?>> properties = context.properties();
            Versioned<?> versioned = properties.get(this);
            if (versioned == null) {
                return null;
            } else {
                return (Versioned.Reference<T>) versioned.newReference();
            }
        }

        public void set(GraphicsContext context, T value, long frameId) {
            Map<ContextProperty<?>, Versioned<?>> properties = context.properties();
            Versioned<T> objectVersioned = (Versioned<T>) properties.get(this);
            if (objectVersioned == null) {
                objectVersioned = new Versioned<>();
                properties.put(this, objectVersioned);
            }
            objectVersioned.set(value, frameId);
        }
    }
}
