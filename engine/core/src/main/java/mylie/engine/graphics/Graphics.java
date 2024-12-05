package mylie.engine.graphics;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.joml.Vector2ic;
import org.joml.Vector4ic;

public abstract class Graphics {

    @ToString
    @Getter
    @AllArgsConstructor
    public static class Display {
        private boolean primary;
        private VideoMode defaultVideoMode;
        private List<? extends VideoMode> videoModes;

        @ToString
        @Getter
        @AllArgsConstructor
        public static class VideoMode {
            private Vector2ic resolution;
            private int refreshRate;
            private Vector4ic formatBits;
        }
    }
}
