package mylie.engine.graphics;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2i;

@Data
public class GraphicsContextSettings {
    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private GraphicsContext context;

    String title;
    Resolution resolution;
    boolean vsync;

    public void apply() {
        context.applySettings(this);
    }

    public interface Resolution {
        record FullScreenWindowed(Graphics.Display display) implements Resolution {}

        record Windowed(Graphics.Display display, Vector2i size, Vector2i position) implements Resolution {}

        record Fullscreen(Graphics.Display display, Graphics.Display.VideoMode videoMode) implements Resolution {}
    }
}
