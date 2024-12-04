package mylie.examples.tests;

import lombok.extern.slf4j.Slf4j;
import mylie.engine.application.BaseApplication;
import mylie.engine.core.Engine;
import mylie.engine.core.EngineManager;
import mylie.engine.core.features.timer.Timer;
import mylie.engine.platform.PlatformDesktop;
import mylie.util.configuration.Configuration;

@Slf4j
public class A0_HelloEngine extends BaseApplication {

    public static void main(String[] args) {
        PlatformDesktop platform = new PlatformDesktop();
        Configuration<Engine> initialize = platform.initialize();
        initialize.set(Engine.Settings.Application, new A0_HelloEngine());
        Engine.ShutdownReason start = Engine.start(initialize, true, false);
    }

    @Override
    protected void onInit() {
        log.info("On init");
    }

    @Override
    public void onUpdate(Timer.Time time) {
        if (time.frameId() == 100) {
            getFeature(EngineManager.class).shutdown(new Engine.ShutdownReason.UserRequest("All OK"));
        }
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
}
