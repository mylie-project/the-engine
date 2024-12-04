package mylie.engine.input;

import mylie.engine.core.Feature;
import mylie.engine.input.listeners.InputListener;

public interface InputManager extends Feature.App {
    InputManager addInputListener(InputListener listener);

    InputManager removeInputListener(InputListener listener);

    InputManager addInputProvider(InputModule.Provider provider);

    InputManager removeInputProvider(InputModule.Provider provider);
}
