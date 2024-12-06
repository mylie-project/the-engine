package mylie.engine.graphics;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2i;
import org.joml.Vector2ic;

@Data
public class GraphicsContextSettings {
    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private GraphicsContext context;

    String id;
    String title;
    Resolution resolution;
    boolean vsync;

    public GraphicsContextSettings() {
        id = "GraphicsContext";
    }

    public void apply() {
        context.applySettings(this);
    }

    public interface Resolution {
        record FullScreenWindowed(Graphics.Display display) implements Resolution {}

        record Windowed(Graphics.Display display, Vector2ic size, Vector2ic position) implements Resolution {
            public static final Vector2ic Center = new Vector2i(-1, -1);
        }

        record Fullscreen(Graphics.Display display, Graphics.Display.VideoMode videoMode) implements Resolution {}
    }
}
