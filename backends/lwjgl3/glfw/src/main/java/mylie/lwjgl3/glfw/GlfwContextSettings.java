package mylie.lwjgl3.glfw;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mylie.engine.graphics.GraphicsContextSettings;

@Data
@EqualsAndHashCode(callSuper = true)
public class GlfwContextSettings extends GraphicsContextSettings {
    boolean transparent;
    boolean resizable;
    boolean alwaysOnTop;
    boolean srgb;
    boolean decorated;
    int samples = -1;
}
