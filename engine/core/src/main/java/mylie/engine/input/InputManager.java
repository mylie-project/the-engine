package mylie.engine.input;

import mylie.engine.core.Feature;

public interface InputManager extends Feature.App {
    InputManager addInputListener(InputListener listener);

    InputManager removeInputListener(InputListener listener);

    InputManager addInputProvider(InputModule.Provider provider);

    InputManager removeInputProvider(InputModule.Provider provider);
}
