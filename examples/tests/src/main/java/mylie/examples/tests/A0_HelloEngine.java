package mylie.examples.tests;

import lombok.extern.slf4j.Slf4j;
import mylie.engine.application.BaseApplication;
import mylie.engine.core.Engine;
import mylie.engine.core.features.async.schedulers.VirtualThreadSchedulerSettings;
import mylie.engine.core.features.timer.Timer;
import mylie.engine.input.InputEvent;
import mylie.engine.input.InputListener;
import mylie.engine.input.InputManager;
import mylie.engine.platform.PlatformDesktop;
import mylie.util.configuration.Configuration;

@Slf4j
public class A0_HelloEngine extends BaseApplication implements InputListener.Raw {

    public static void main(String[] args) {
        PlatformDesktop platform = new PlatformDesktop();
        Configuration<Engine> engineConfiguration = platform.initialize();
        engineConfiguration.set(Engine.Settings.Scheduler, new VirtualThreadSchedulerSettings());
        engineConfiguration.set(Engine.Settings.Application, new A0_HelloEngine());
        Engine.ShutdownReason start = Engine.start(engineConfiguration, true, false);
    }

    @Override
    protected void onInit() {
        log.info("On init");
        getFeature(InputManager.class).addInputListener(this);
    }

    @Override
    public void onUpdate(Timer.Time time) {
        // if (time.frameId() == 100) {
        //    getFeature(EngineManager.class).shutdown(new Engine.ShutdownReason.UserRequest("All OK"));
        // }
    }

    void limitFPS(int fps) {
        try {
            Thread.sleep(1000 / fps);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        log.info("On destroy");
    }

    @Override
    public void onEvent(InputEvent event) {
        log.trace(event.toString());
    }
}
