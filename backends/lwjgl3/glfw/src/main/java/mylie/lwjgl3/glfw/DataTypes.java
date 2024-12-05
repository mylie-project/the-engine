package mylie.lwjgl3.glfw;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import mylie.engine.graphics.Graphics;
import org.joml.Vector2ic;
import org.joml.Vector4ic;

public class DataTypes {
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    static class GlfwDisplay extends Graphics.Display {
        private long handle;

        public GlfwDisplay(boolean primary, GlfwVideoMode defaultVideoMode, List<GlfwVideoMode> videoModes) {
            super(primary, defaultVideoMode, videoModes);
        }
    }

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    static class GlfwVideoMode extends Graphics.Display.VideoMode {
        private long displayHandle;

        public GlfwVideoMode(Vector2ic resolution, int refreshRate, Vector4ic formatBits) {
            super(resolution, refreshRate, formatBits);
        }
    }
}
