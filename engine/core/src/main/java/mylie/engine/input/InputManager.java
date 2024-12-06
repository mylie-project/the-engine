package mylie.engine.input;

import mylie.engine.core.BaseFeature;
import mylie.engine.input.listeners.InputListener;

/**
 * The InputManager interface provides methods for managing input listeners and input providers.
 * It extends the BaseFeature.App interface, indicating that it is a type of feature within an application.
 * The primary purpose of the InputManager is to register and deregister input listeners and input providers,
 * allowing the application to handle input events and manage input sources dynamically.
 */
public interface InputManager extends BaseFeature.App {

    /**
     * Registers a new input listener to the input management system.
     *
     * @param listener the InputListener to be added. It receives input events from the input sources.
     * @return the InputManager instance to allow method chaining.
     */
    InputManager addInputListener(InputListener listener);

    /**
     * Removes an existing input listener from the input management system.
     *
     * @param listener the InputListener to be removed. It will no longer receive input events from the input sources.
     * @return the InputManager instance to allow method chaining.
     */
    InputManager removeInputListener(InputListener listener);

    /**
     * Registers a new input provider to the input management system.
     *
     * @param provider the InputModule.Provider to be added. It serves as a source for input events that are polled and processed by the system.
     * @return the InputManager instance to allow method chaining.
     */
    InputManager addInputProvider(InputModule.Provider provider);

    /**
     * Removes an existing input provider from the input management system.
     *
     * @param provider the InputModule.Provider to be removed. It will no longer serve as a source for input events.
     * @return the InputManager instance to allow method chaining.
     */
    InputManager removeInputProvider(InputModule.Provider provider);
}
