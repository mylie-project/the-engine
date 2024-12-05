package mylie.engine.input;

import mylie.engine.core.BaseFeature;
import mylie.engine.input.listeners.InputListener;

public interface InputManager extends BaseFeature.App {
    InputManager addInputListener(InputListener listener);

    InputManager removeInputListener(InputListener listener);

    InputManager addInputProvider(InputModule.Provider provider);

    InputManager removeInputProvider(InputModule.Provider provider);
}
