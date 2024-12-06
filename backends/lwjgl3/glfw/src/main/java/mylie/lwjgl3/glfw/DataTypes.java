package mylie.lwjgl3.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.util.List;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import mylie.engine.graphics.Graphics;
import mylie.engine.graphics.GraphicsContext;
import mylie.engine.input.Input;
import org.joml.Vector2ic;
import org.joml.Vector4ic;
import org.lwjgl.glfw.*;

public class DataTypes {
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    static class GlfwDisplay extends Graphics.Display {
        private long handle;

        public GlfwDisplay(
                long handle, boolean primary, GlfwVideoMode defaultVideoMode, List<GlfwVideoMode> videoModes) {
            super(primary, defaultVideoMode, videoModes);
            this.handle = handle;
        }
    }

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    static class GlfwVideoMode extends Graphics.Display.VideoMode {
        private long displayHandle;

        public GlfwVideoMode(long displayHandle, Vector2ic resolution, int refreshRate, Vector4ic formatBits) {
            super(resolution, refreshRate, formatBits);
            this.displayHandle = displayHandle;
        }
    }

    @Getter
    @Setter
    public static class GlfwCallbacks {
        GLFWKeyCallback keyCallback;
        GLFWMouseButtonCallback mouseButtonCallback;
        GLFWCharCallback charCallback;
        GLFWCursorPosCallback cursorPosCallback;
        GLFWCursorEnterCallback cursorEnterCallback;
        GLFWScrollCallback scrollCallback;

        public GlfwCallbacks(GlfwInputProvider glfwInputProvider, GlfwContext context) {
            keyCallback = new GLFWKeyCallback() {
                @Override
                public void invoke(long window, int key, int scancode, int action, int mods) {
                    glfwInputProvider.keyCallback(window, key, scancode, action, mods);
                }
            };
            mouseButtonCallback = new GLFWMouseButtonCallback() {
                @Override
                public void invoke(long window, int button, int action, int mods) {
                    glfwInputProvider.mouseButtonCallback(window, button, action, mods);
                }
            };
            charCallback = new GLFWCharCallback() {
                @Override
                public void invoke(long window, int codepoint) {
                    glfwInputProvider.charCallback(window, codepoint);
                }
            };
            cursorPosCallback = new GLFWCursorPosCallback() {
                @Override
                public void invoke(long window, double xpos, double ypos) {
                    glfwInputProvider.cursorPosCallback(window, xpos, ypos);
                }
            };
            cursorEnterCallback = new GLFWCursorEnterCallback() {
                @Override
                public void invoke(long window, boolean entered) {
                    glfwInputProvider.cursorEnterCallback(window, entered);
                }
            };
            scrollCallback = new GLFWScrollCallback() {
                @Override
                public void invoke(long window, double xoffset, double yoffset) {
                    glfwInputProvider.scrollCallback(window, xoffset, yoffset);
                }
            };
            GLFW.glfwSetKeyCallback(context.handle(), keyCallback);
            GLFW.glfwSetMouseButtonCallback(context.handle(), mouseButtonCallback);
            GLFW.glfwSetCharCallback(context.handle(), charCallback);
            GLFW.glfwSetCursorPosCallback(context.handle(), cursorPosCallback);
            GLFW.glfwSetCursorEnterCallback(context.handle(), cursorEnterCallback);
            GLFW.glfwSetScrollCallback(context.handle(), scrollCallback);
        }

        public void free() {
            keyCallback.free();
            mouseButtonCallback.free();
            charCallback.free();
            cursorPosCallback.free();
            cursorEnterCallback.free();
            scrollCallback.free();
        }
    }

    public static Input.Key convertKeyScanCode(int key, int scancode) {
        Input.Key engineKey =
                switch (key) {
                    case GLFW_KEY_SPACE -> Input.Key.SPACE;
                    case GLFW_KEY_APOSTROPHE -> Input.Key.APOSTROPHE;
                    case GLFW_KEY_COMMA -> Input.Key.COMMA;
                    case GLFW_KEY_MINUS -> Input.Key.MINUS;
                    case GLFW_KEY_PERIOD -> Input.Key.PERIOD;
                    case GLFW_KEY_SLASH -> Input.Key.SLASH;
                    case GLFW_KEY_0 -> Input.Key.NUM_0;
                    case GLFW_KEY_1 -> Input.Key.NUM_1;
                    case GLFW_KEY_2 -> Input.Key.NUM_2;
                    case GLFW_KEY_3 -> Input.Key.NUM_3;
                    case GLFW_KEY_4 -> Input.Key.NUM_4;
                    case GLFW_KEY_5 -> Input.Key.NUM_5;
                    case GLFW_KEY_6 -> Input.Key.NUM_6;
                    case GLFW_KEY_7 -> Input.Key.NUM_7;
                    case GLFW_KEY_8 -> Input.Key.NUM_8;
                    case GLFW_KEY_9 -> Input.Key.NUM_9;
                    case GLFW_KEY_SEMICOLON -> Input.Key.SEMICOLON;
                    case GLFW_KEY_EQUAL -> Input.Key.EQUAL;
                    case GLFW_KEY_A -> Input.Key.A;
                    case GLFW_KEY_B -> Input.Key.B;
                    case GLFW_KEY_C -> Input.Key.C;
                    case GLFW_KEY_D -> Input.Key.D;
                    case GLFW_KEY_E -> Input.Key.E;
                    case GLFW_KEY_F -> Input.Key.F;
                    case GLFW_KEY_G -> Input.Key.G;
                    case GLFW_KEY_H -> Input.Key.H;
                    case GLFW_KEY_I -> Input.Key.I;
                    case GLFW_KEY_J -> Input.Key.J;
                    case GLFW_KEY_K -> Input.Key.K;
                    case GLFW_KEY_L -> Input.Key.L;
                    case GLFW_KEY_M -> Input.Key.M;
                    case GLFW_KEY_N -> Input.Key.N;
                    case GLFW_KEY_O -> Input.Key.O;
                    case GLFW_KEY_P -> Input.Key.P;
                    case GLFW_KEY_Q -> Input.Key.Q;
                    case GLFW_KEY_R -> Input.Key.R;
                    case GLFW_KEY_S -> Input.Key.S;
                    case GLFW_KEY_T -> Input.Key.T;
                    case GLFW_KEY_U -> Input.Key.U;
                    case GLFW_KEY_V -> Input.Key.V;
                    case GLFW_KEY_W -> Input.Key.W;
                    case GLFW_KEY_X -> Input.Key.X;
                    case GLFW_KEY_Y -> Input.Key.Y;
                    case GLFW_KEY_Z -> Input.Key.Z;
                    case GLFW_KEY_LEFT_BRACKET -> Input.Key.LEFT_BRACKET;
                    case GLFW_KEY_BACKSLASH -> Input.Key.BACKSLASH;
                    case GLFW_KEY_RIGHT_BRACKET -> Input.Key.RIGHT_BRACKET;
                    case GLFW_KEY_GRAVE_ACCENT -> Input.Key.GRAVE_ACCENT;
                    case GLFW_KEY_WORLD_1 -> Input.Key.WORLD_1;
                    case GLFW_KEY_WORLD_2 -> Input.Key.WORLD_2;
                    case GLFW_KEY_ESCAPE -> Input.Key.ESCAPE;
                    case GLFW_KEY_ENTER -> Input.Key.ENTER;
                    case GLFW_KEY_TAB -> Input.Key.TAB;
                    case GLFW_KEY_BACKSPACE -> Input.Key.BACKSPACE;
                    case GLFW_KEY_INSERT -> Input.Key.INSERT;
                    case GLFW_KEY_DELETE -> Input.Key.DELETE;
                    case GLFW_KEY_RIGHT -> Input.Key.RIGHT;
                    case GLFW_KEY_LEFT -> Input.Key.LEFT;
                    case GLFW_KEY_DOWN -> Input.Key.DOWN;
                    case GLFW_KEY_UP -> Input.Key.UP;
                    case GLFW_KEY_PAGE_UP -> Input.Key.PAGE_UP;
                    case GLFW_KEY_PAGE_DOWN -> Input.Key.PAGE_DOWN;
                    case GLFW_KEY_HOME -> Input.Key.HOME;
                    case GLFW_KEY_END -> Input.Key.END;
                    case GLFW_KEY_CAPS_LOCK -> Input.Key.CAPS_LOCK;
                    case GLFW_KEY_SCROLL_LOCK -> Input.Key.SCROLL_LOCK;
                    case GLFW_KEY_NUM_LOCK -> Input.Key.NUM_LOCK;
                    case GLFW_KEY_PRINT_SCREEN -> Input.Key.PRINT_SCREEN;
                    case GLFW_KEY_PAUSE -> Input.Key.PAUSE;
                    case GLFW_KEY_F1 -> Input.Key.F1;
                    case GLFW_KEY_F2 -> Input.Key.F2;
                    case GLFW_KEY_F3 -> Input.Key.F3;
                    case GLFW_KEY_F4 -> Input.Key.F4;
                    case GLFW_KEY_F5 -> Input.Key.F5;
                    case GLFW_KEY_F6 -> Input.Key.F6;
                    case GLFW_KEY_F7 -> Input.Key.F7;
                    case GLFW_KEY_F8 -> Input.Key.F8;
                    case GLFW_KEY_F9 -> Input.Key.F9;
                    case GLFW_KEY_F10 -> Input.Key.F10;
                    case GLFW_KEY_F11 -> Input.Key.F11;
                    case GLFW_KEY_F12 -> Input.Key.F12;
                    case 320 -> Input.Key.NUMPAD_0;
                    case 321 -> Input.Key.NUMPAD_1;
                    case 322 -> Input.Key.NUMPAD_2;
                    case 323 -> Input.Key.NUMPAD_3;
                    case 324 -> Input.Key.NUMPAD_4;
                    case 325 -> Input.Key.NUMPAD_5;
                    case 326 -> Input.Key.NUMPAD_6;
                    case 327 -> Input.Key.NUMPAD_7;
                    case 328 -> Input.Key.NUMPAD_8;
                    case 329 -> Input.Key.NUMPAD_9;
                    case 335 -> Input.Key.NUMPAD_ENTER;
                    case 334 -> Input.Key.NUMPAD_PLUS;
                    case 333 -> Input.Key.NUMPAD_MINUS;
                    case 332 -> Input.Key.NUMPAD_TIMES;
                    case 331 -> Input.Key.NUMPAD_DIVIDE;
                    case 330 -> Input.Key.NUMPAD_DELETE;
                    default -> Input.Key.UNKNOWN;
                };
        if (engineKey == Input.Key.UNKNOWN) {
            engineKey = switch (scancode) {
                case 0x1D -> Input.Key.LEFT_CONTROL;
                case 0x2A -> Input.Key.LEFT_SHIFT;
                case 0x38 -> Input.Key.LEFT_ALT;
                case 347 -> Input.Key.LEFT_SUPER;
                case 0x36 -> Input.Key.RIGHT_SHIFT;
                case 0x1C -> Input.Key.ENTER;
                case 285 -> Input.Key.RIGHT_CONTROL;
                case 312 -> Input.Key.RIGHT_ALT;
                case 349 -> Input.Key.RIGHT_SUPER;
                case 0x3A -> Input.Key.CAPS_LOCK;
                default -> Input.Key.UNKNOWN;};
        }
        return engineKey;
    }

    public static Input.MouseButton convertMouseButton(int button) {
        return switch (button) {
            case GLFW_MOUSE_BUTTON_LEFT -> Input.MouseButton.LEFT;
            case GLFW_MOUSE_BUTTON_RIGHT -> Input.MouseButton.RIGHT;
            case GLFW_MOUSE_BUTTON_MIDDLE -> Input.MouseButton.MIDDLE;
            case GLFW_MOUSE_BUTTON_4 -> Input.MouseButton.BUTTON_4;
            case GLFW_MOUSE_BUTTON_5 -> Input.MouseButton.BUTTON_5;
            case GLFW_MOUSE_BUTTON_6 -> Input.MouseButton.BUTTON_6;
            case GLFW_MOUSE_BUTTON_7 -> Input.MouseButton.BUTTON_7;
            case GLFW_MOUSE_BUTTON_8 -> Input.MouseButton.BUTTON_8;
            default -> Input.MouseButton.UNKNOWN;
        };
    }

    @Getter(AccessLevel.PACKAGE)
    public static class GlfwContextParameter<T> extends GraphicsContext.Configuration.Parameter<T> {
        final int windowHint;
        final BiConsumer<Long, T> consumer;

        public GlfwContextParameter(int windowHint, BiConsumer<Long, T> consumer, T defaultValue) {
            this.windowHint = windowHint;
            this.consumer = consumer;
            this.defaultValue = defaultValue;
        }
    }
}
