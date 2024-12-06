package mylie.engine.input;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.*;
import mylie.engine.core.features.async.*;
import mylie.engine.core.features.timer.Timer;
import mylie.engine.input.listeners.InputListener;
import mylie.util.configuration.Configuration;

@Slf4j
public class InputModule extends CoreFeature implements Lifecycle.Update.Timed {
    private final List<Provider> inputProviders;
    private final Queue<InputEvent> inputEvents;
    private final List<InputListener> inputListeners;
    private Scheduler scheduler;

    public InputModule() {
        super(InputModule.class);
        inputProviders = new CopyOnWriteArrayList<>();
        inputEvents = new LinkedList<>();
        inputListeners = new CopyOnWriteArrayList<>();
    }

    @Override
    protected void onSetup(FeatureManager featureManager, Configuration<Engine> engineConfiguration) {
        super.onSetup(featureManager, engineConfiguration);
        scheduler = get(Scheduler.class);
        runAfter(Engine.Barriers.FramePreparation);
        add(new Internal());
    }

    @Override
    public void onUpdate(Timer.Time time) {
        log.trace("Polling {} input providers", inputProviders.size());
        for (Provider inputProvider : inputProviders) {
            inputEvents.addAll(inputProvider.getEvents().get());
        }
        processInputEvents();
    }

    private void processInputEvents() {
        log.trace("Processing {} input events", inputEvents.size());
        while (!inputEvents.isEmpty()) {
            InputEvent event = inputEvents.poll();
            processInputEvent(event);
        }
    }

    private void processInputEvent(InputEvent event) {
        Timer.Time time = get(Timer.class).time();
        for (InputListener inputListener : inputListeners) {
            scheduler.submitRunnable(() -> inputListener.onEvent(event), Async.APPLICATION);
            // Async seems slightly slower currently 4.12.2024
            /*Async.async(
            Async.Mode.Async,
            Cache.Never,
            Async.APPLICATION,
            time.frameId(),
            notifyListener,
            inputListener,
            event);*/
        }
    }

    public interface Provider {
        Result<Collection<InputEvent>> getEvents();
    }

    class Internal implements InputManager {

        @Override
        public InputManager addInputListener(InputListener listener) {
            inputListeners.add(listener);
            return this;
        }

        @Override
        public InputManager removeInputListener(InputListener listener) {
            inputListeners.remove(listener);
            return this;
        }

        @Override
        public InputManager addInputProvider(Provider provider) {
            inputProviders.add(provider);
            return this;
        }

        @Override
        public InputManager removeInputProvider(Provider provider) {
            inputProviders.remove(provider);
            return this;
        }
    }

    private static final Functions.F1<Boolean, InputListener, InputEvent> notifyListener =
            new Functions.F1<>("NotifyListener") {
                @Override
                protected Boolean run(InputListener listener, InputEvent event) {
                    listener.onEvent(event);
                    return true;
                }
            };
}
