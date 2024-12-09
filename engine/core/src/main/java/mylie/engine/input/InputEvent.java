package mylie.engine.input;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import mylie.engine.graphics.GraphicsContext;
import org.joml.Vector2ic;

@Getter
@ToString
public abstract class InputEvent {
    final GraphicsContext graphicsContext;

    protected InputEvent(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    @Getter
    @ToString
    public abstract static class Window extends InputEvent {
        public Window(GraphicsContext graphicsContext) {
            super(graphicsContext);
        }

        @Getter
        @ToString
        public static class Size extends Window {
            final Vector2ic size;

            public Size(GraphicsContext graphicsContext, Vector2ic size) {
                super(graphicsContext);
                this.size = size;
            }
        }

        @Getter
        @ToString
        public static class FramebufferSize extends Window {
            final Vector2ic size;

            public FramebufferSize(GraphicsContext graphicsContext, Vector2ic size) {
                super(graphicsContext);
                this.size = size;
            }
        }

        @Getter
        @ToString
        public static class Close extends Window {
            public Close(GraphicsContext graphicsContext) {
                super(graphicsContext);
            }
        }

        @Getter
        @ToString
        public static class Focus extends Window {
            final boolean focused;

            public Focus(GraphicsContext graphicsContext, boolean focused) {
                super(graphicsContext);
                this.focused = focused;
            }
        }

        @Getter
        @ToString
        public static class Position extends Window {
            final Vector2ic position;

            public Position(GraphicsContext graphicsContext, Vector2ic position) {
                super(graphicsContext);
                this.position = position;
            }
        }

        @Getter
        @ToString
        public static class Maximized extends Window {
            final boolean maximized;

            public Maximized(GraphicsContext graphicsContext, boolean maximized) {
                super(graphicsContext);
                this.maximized = maximized;
            }
        }
    }

    @Getter
    @ToString
    public abstract static class Keyboard extends InputEvent {
        final InputDevice.Keyboard keyboard;

        public Keyboard(GraphicsContext graphicsContext, InputDevice.Keyboard keyboard) {
            super(graphicsContext);
            this.keyboard = keyboard;
        }

        @SuppressWarnings("CanBeFinal")
        @Getter
        @ToString
        public static class Key extends Keyboard {
            public Key(
                    GraphicsContext graphicsContext,
                    InputDevice.Keyboard keyboard,
                    Input.Key key,
                    Type type,
                    int mods) {
                super(graphicsContext, keyboard);
                this.key = key;
                this.type = type;
                this.mods = mods;
            }

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

            private Input.Key key;
            private Type type;

            @Getter(AccessLevel.NONE)
            int mods;
        }

        @Getter
        @ToString
        public static class Text extends InputEvent {
            private final char text;

            public Text(GraphicsContext context, char text) {
                super(context);
                this.text = text;
            }
        }
    }

    @Getter
    @ToString
    public abstract static class Gamepad extends InputEvent {
        final InputDevice.Gamepad gamepad;

        public Gamepad(GraphicsContext graphicsContext, InputDevice.Gamepad gamepad) {
            super(graphicsContext);
            this.gamepad = gamepad;
        }

        @Getter
        @ToString
        public static class Button extends Gamepad {
            public enum Type {
                PRESSED,
                RELEASED
            }

            final Input.GamepadButton button;
            final Type type;

            public Button(
                    GraphicsContext graphicsContext,
                    InputDevice.Gamepad gamepad,
                    Input.GamepadButton button,
                    Type type) {
                super(graphicsContext, gamepad);
                this.button = button;
                this.type = type;
            }
        }

        @Getter
        @ToString
        public static class Axis extends Gamepad {
            final Input.GamepadAxis axis;
            final float value;

            public Axis(
                    GraphicsContext graphicsContext, InputDevice.Gamepad gamepad, Input.GamepadAxis axis, float value) {
                super(graphicsContext, gamepad);
                this.axis = axis;
                this.value = value;
            }
        }
    }

    @Getter
    @ToString
    public abstract static class Mouse extends InputEvent {
        final InputDevice.Mouse mouse;

        public Mouse(GraphicsContext graphicsContext, InputDevice.Mouse mouse) {
            super(graphicsContext);
            this.mouse = mouse;
        }

        @SuppressWarnings("CanBeFinal")
        @Getter
        @ToString
        public static class Button extends Mouse {
            public enum Type {
                PRESSED,
                RELEASED,
                CLICKED,
                DOUBLE_CLICKED,
            }

            public Button(
                    GraphicsContext graphicsContext,
                    InputDevice.Mouse mouse,
                    Input.MouseButton button,
                    Type type,
                    int mods) {
                super(graphicsContext, mouse);
                this.button = button;
                this.type = type;
                this.mods = mods;
            }

            private Input.MouseButton button;
            private Type type;
            private int mods;
        }

        @SuppressWarnings("CanBeFinal")
        @Getter
        @ToString
        public static class Cursor extends Mouse {
            private Vector2ic position;

            public Cursor(GraphicsContext graphicsContext, InputDevice.Mouse mouse, Vector2ic position) {
                super(graphicsContext, mouse);
                this.position = position;
            }
        }

        @SuppressWarnings("CanBeFinal")
        @Getter
        @ToString
        public static class Wheel extends Mouse {
            public enum WheelAxis {
                X,
                Y,
            }

            private WheelAxis axis;
            private int amount;

            public Wheel(GraphicsContext graphicsContext, InputDevice.Mouse mouse, WheelAxis axis, int amount) {
                super(graphicsContext, mouse);
                this.axis = axis;
                this.amount = amount;
            }
        }
    }
}
