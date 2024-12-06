package mylie.engine.graphics;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
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
}
