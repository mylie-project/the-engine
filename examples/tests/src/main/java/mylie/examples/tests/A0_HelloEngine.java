package mylie.examples.tests;

import lombok.extern.slf4j.Slf4j;
import mylie.engine.application.BaseApplication;
import mylie.engine.core.Engine;
import mylie.engine.core.EngineManager;
import mylie.engine.core.features.async.schedulers.SingleThreadSchedulerSettings;
import mylie.engine.core.features.timer.Timer;
import mylie.engine.graphics.Graphics;
import mylie.engine.graphics.GraphicsContext;
import mylie.engine.graphics.GraphicsContextSettings.Resolution;
import mylie.engine.graphics.GraphicsManager;
import mylie.engine.input.Input;
import mylie.engine.input.InputEvent;
import mylie.engine.input.InputManager;
import mylie.engine.input.listeners.RawInputListener;
import mylie.engine.platform.PlatformDesktop;
import mylie.lwjgl3.glfw.GlfwContextSettings;
import mylie.lwjgl3.opengl.OpenglSettings;
import mylie.util.configuration.Configuration;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

@Slf4j
public class A0_HelloEngine extends BaseApplication implements RawInputListener {
    GraphicsContext graphicsContext;
    GraphicsContext graphicsContext2;

    public static void main(String[] args) {
        PlatformDesktop platform = new PlatformDesktop();
        Configuration<Engine> engineConfiguration = platform.initialize();
        engineConfiguration.set(Engine.Settings.Scheduler, new SingleThreadSchedulerSettings());
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
        GlfwContextSettings contextSettings = setupContextSettings(display);

        graphicsContext = graphicsManager.createContext(contextSettings, true);
    }

    @NotNull
    private static GlfwContextSettings setupContextSettings(Graphics.Display display) {
        GlfwContextSettings contextSettings = new GlfwContextSettings();
        contextSettings.resolution(
                new Resolution.Windowed(display, new Vector2i(800, 600), Resolution.Windowed.Center));
        // new Resolution.Fullscreen(display,display.defaultVideoMode()));
        // new Resolution.FullScreenWindowed(display));
        contextSettings.id("1");
        contextSettings.transparent(false);
        contextSettings.srgb(true);
        contextSettings.samples(16);
        contextSettings.alwaysOnTop(true);
        contextSettings.transparent(false);
        contextSettings.decorated(true);
        return contextSettings;
    }

    @Override
    public void onUpdate(Timer.Time time) {

    }

    @Override
    public void onDestroy() {
        log.info("On destroy");
    }

    @Override
    public void onEvent(InputEvent event) {
        if (event instanceof InputEvent.Keyboard.Key keyEvent) {
            if (keyEvent.key().equals(Input.Key.ESCAPE)) {
                getFeature(EngineManager.class).shutdown(new Engine.ShutdownReason.UserRequest("Escape"));
            }
        }
    }
}
