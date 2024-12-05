package mylie.engine.input.events;

import mylie.engine.input.Input;

public abstract class InputEvent {
    public abstract static class Keyboard extends KeyboardEvent {
        public static class Key extends KeyEvent {
            public Key(Input.Key key, Type type, int mods) {
                super(key, type, mods);
            }
        }
    }

    public abstract static class Mouse extends MouseEvent {
        public static class Button extends MouseButtonEvent {
            public Button(Input.MouseButton button, Type type, int mods) {
                super(button, type, mods);
            }
        }

        public static class Cursor extends MouseCursorEvent {
            public Cursor(org.joml.Vector2ic position) {
                super(position);
            }
        }
    }
}
