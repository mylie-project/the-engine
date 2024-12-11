package mylie.engine.graphics;

import java.util.Map;
import mylie.util.Versioned;
import org.joml.Vector2ic;

public interface ContextProperties {
    ContextProperty<Vector2ic> Size = new ContextProperty<>();
    ContextProperty<Boolean> Focus = new ContextProperty<>();
    ContextProperty<Boolean> Maximized = new ContextProperty<>();
    ContextProperty<Vector2ic> Position = new ContextProperty<>();
    ContextProperty<Vector2ic> FrameBufferSize = new ContextProperty<>();

    @SuppressWarnings("unchecked")
    class ContextProperty<T> {
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
