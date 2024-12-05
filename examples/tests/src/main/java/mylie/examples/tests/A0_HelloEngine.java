package mylie.examples.tests;

import lombok.extern.slf4j.Slf4j;
import mylie.engine.application.BaseApplication;
import mylie.engine.core.Engine;
import mylie.engine.core.features.async.schedulers.VirtualThreadSchedulerSettings;
import mylie.engine.core.features.timer.Timer;
import mylie.engine.graphics.Graphics;
import mylie.engine.graphics.GraphicsManager;
import mylie.engine.input.InputManager;
import mylie.engine.input.events.InputEvent;
import mylie.engine.input.listeners.RawInputListener;
import mylie.engine.platform.PlatformDesktop;
import mylie.lwjgl3.opengl.OpenglSettings;
import mylie.util.configuration.Configuration;

@Slf4j
public class A0_HelloEngine extends BaseApplication implements RawInputListener {

    public static void main(String[] args) {
        PlatformDesktop platform = new PlatformDesktop();
        Configuration<Engine> engineConfiguration = platform.initialize();
        engineConfiguration.set(Engine.Settings.Scheduler, new VirtualThreadSchedulerSettings());
        engineConfiguration.set(Engine.Settings.Application, new A0_HelloEngine());
        engineConfiguration.set(Engine.Settings.GraphicsApi, new OpenglSettings());
        Engine.ShutdownReason start = Engine.start(engineConfiguration, true, false);
    }

    @Override
    protected void onInit() {
        log.info("On init");
        InputManager inputManager = getFeature(InputManager.class);
        inputManager.addInputListener(this);
        GraphicsManager graphicsManager = getFeature(GraphicsManager.class);
        Graphics.Display display = graphicsManager.primaryDisplay();
    }

    @Override
    public void onUpdate(Timer.Time time) {}

    @Override
    public void onDestroy() {
        log.info("On destroy");
    }

    @Override
    public void onEvent(InputEvent event) {
        log.trace(event.toString());
    }
}
