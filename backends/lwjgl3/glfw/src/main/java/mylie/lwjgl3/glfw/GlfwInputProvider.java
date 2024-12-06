package mylie.lwjgl3.glfw;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Cache;
import mylie.engine.core.features.async.Functions;
import mylie.engine.core.features.async.Result;
import mylie.engine.input.Input;
import mylie.engine.input.InputDevice;
import mylie.engine.input.InputEvent;
import mylie.engine.input.InputModule;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

@Slf4j
public class GlfwInputProvider implements InputModule.Provider {
    private List<InputEvent> eventList;
    private final Map<Long, GlfwContext> contextMap;
    private int mods;
    private final InputDevice.Keyboard defaultKeyboard = new InputDevice.Keyboard() {};
    private final InputDevice.Mouse defaultMouse = new InputDevice.Mouse() {};

    public GlfwInputProvider() {
        contextMap = new HashMap<>();
    }

    @Override
    public Result<Collection<InputEvent>> getEvents() {
        return Async.async(Async.Mode.Async, Cache.OneFrame, Async.ENGINE, -1, PollEvents, this);
    }

    public void addContext(GlfwContext contexts) {
        DataTypes.GlfwCallbacks glfwCallbacks = new DataTypes.GlfwCallbacks(this, contexts);
        contexts.glfwCallbacks(glfwCallbacks);
        contextMap.put(contexts.handle, contexts);
    }

    public void keyCallback(long window, int keycode, int scancode, int action, int mods) {
        log.trace(
                "Key Callback: window={}, keycode={}, scancode={}, action={}, mods={}",
                window,
                keycode,
                scancode,
                action,
                mods);
        if (eventList == null) {
            log.warn("Event list is null");
            return;
        }
        Input.Key key = DataTypes.convertKeyScanCode(keycode, scancode);
        InputEvent.Keyboard.Key.Type engineType =
                switch (action) {
                    case GLFW.GLFW_PRESS -> InputEvent.Keyboard.Key.Type.PRESSED;
                    case GLFW.GLFW_RELEASE -> InputEvent.Keyboard.Key.Type.RELEASED;
                    case GLFW.GLFW_REPEAT -> InputEvent.Keyboard.Key.Type.LONG_PRESSED;
                    default -> throw new IllegalStateException("Unexpected value: " + action);
                };
        int modifiers = getModifiers(mods);
        this.mods = modifiers;
        eventList.add(new InputEvent.Keyboard.Key(getContext(window), defaultKeyboard, key, engineType, modifiers));
    }

    private static int getModifiers(int mods) {
        int modifiers = 0;
        if ((mods & GLFW.GLFW_MOD_SHIFT) != 0) {
            modifiers |= 1 << InputEvent.Keyboard.Key.Modifier.SHIFT.ordinal();
        }
        if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) {
            modifiers |= 1 << InputEvent.Keyboard.Key.Modifier.CONTROL.ordinal();
        }
        if ((mods & GLFW.GLFW_MOD_ALT) != 0) {
            modifiers |= 1 << InputEvent.Keyboard.Key.Modifier.ALT.ordinal();
        }
        if ((mods & GLFW.GLFW_MOD_SUPER) != 0) {
            modifiers |= 1 << InputEvent.Keyboard.Key.Modifier.SUPER.ordinal();
        }
        if ((mods & GLFW.GLFW_MOD_CAPS_LOCK) != 0) {
            modifiers |= 1 << InputEvent.Keyboard.Key.Modifier.CAPS_LOCK.ordinal();
        }
        if ((mods & GLFW.GLFW_MOD_NUM_LOCK) != 0) {
            modifiers |= 1 << InputEvent.Keyboard.Key.Modifier.NUM_LOCK.ordinal();
        }
        return modifiers;
    }

    public void mouseButtonCallback(long window, int button, int action, int mods) {
        log.trace("Mouse Button Callback: window={}, button={}, action={}, mods={}", window, button, action, mods);
        Input.MouseButton mouseButton = DataTypes.convertMouseButton(button);
        InputEvent.Mouse.Button.Type engineType =
                switch (action) {
                    case GLFW.GLFW_PRESS -> InputEvent.Mouse.Button.Type.PRESSED;
                    case GLFW.GLFW_RELEASE -> InputEvent.Mouse.Button.Type.RELEASED;
                    default -> throw new IllegalStateException("Unexpected value: " + action);
                };
        eventList.add(new InputEvent.Mouse.Button(getContext(window), defaultMouse, mouseButton, engineType, mods));
    }

    public void charCallback(long window, int codepoint) {
        log.trace("Char Callback: window={}, codepoint={}", window, codepoint);
    }

    public void cursorPosCallback(long window, double xpos, double ypos) {
        log.trace("Cursor Pos Callback: window={}, xpos={}, ypos={}", window, xpos, ypos);
        eventList.add(
                new InputEvent.Mouse.Cursor(getContext(window), defaultMouse, new Vector2i((int) xpos, (int) ypos)));
    }

    public void cursorEnterCallback(long window, boolean entered) {
        log.trace("Cursor Enter Callback: window={}, entered={}", window, entered);
    }

    public void scrollCallback(long window, double xoffset, double yoffset) {
        log.trace("Scroll Callback: window={}, xoffset={}, yoffset={}", window, xoffset, yoffset);
        InputEvent.Mouse.Wheel.WheelAxis axis =
                yoffset != 0 ? InputEvent.Mouse.Wheel.WheelAxis.Y : InputEvent.Mouse.Wheel.WheelAxis.X;
        eventList.add(new InputEvent.Mouse.Wheel(getContext(window), defaultMouse, axis, (int) yoffset));
    }

    private GlfwContext getContext(long window) {
        return contextMap.get(window);
    }

    private static final Functions.F0<Collection<InputEvent>, GlfwInputProvider> PollEvents =
            new Functions.F0<>("PollEvents") {
                @Override
                protected Collection<InputEvent> run(GlfwInputProvider inputProvider) {
                    inputProvider.eventList = new ArrayList<>();
                    GLFW.glfwPollEvents();
                    return inputProvider.eventList;
                }
            };
}
