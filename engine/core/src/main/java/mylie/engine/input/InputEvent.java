package mylie.engine.input;

import lombok.*;
import org.joml.Vector2ic;

@SuppressWarnings("unused")
public abstract class InputEvent {
    public abstract static class KeyboardEvent extends InputEvent {
        @ToString
        @AllArgsConstructor
        @Getter
        @Setter(AccessLevel.PROTECTED)
        public static class KeyEvent extends KeyboardEvent {
            public enum Type {
                PRESSED,
                RELEASED,
                LONG_PRESSED,
            }

            public enum Modifier {
                SHIFT,
                CONTROL,
                ALT,
                SUPER,
                CAPS_LOCK,
                NUM_LOCK,
            }

            Input.Key key;
            Type type;

            @Getter(AccessLevel.NONE)
            int mods;
        }
    }

    public abstract static class MouseEvent extends InputEvent {
        @ToString
        @AllArgsConstructor
        @Getter
        @Setter(AccessLevel.PROTECTED)
        public static class ButtonEvent extends MouseEvent {
            public enum Type {
                PRESSED,
                RELEASED,
                CLICKED,
                DOUBLE_CLICKED,
            }

            Input.MouseButton button;
            Type type;
            int mods;
        }

        @ToString
        @AllArgsConstructor
        @Getter
        @Setter(AccessLevel.PROTECTED)
        public static class WheelEvent extends MouseEvent {
            public enum WheelAxis {
                X,
                Y,
            }

            WheelAxis axis;
            int amount;
        }

        @ToString
        @AllArgsConstructor
        @Getter
        @Setter(AccessLevel.PROTECTED)
        public static class CursorMotionEvent extends MouseEvent {
            Vector2ic position;
        }
    }
}
