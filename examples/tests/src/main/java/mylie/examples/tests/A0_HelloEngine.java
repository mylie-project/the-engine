package mylie.examples.tests;

import lombok.extern.slf4j.Slf4j;
import mylie.engine.application.BaseApplication;
import mylie.engine.core.Engine;
import mylie.engine.core.EngineManager;
import mylie.engine.core.features.timer.Timer;
import mylie.engine.graphics.GraphicsContext;
import mylie.engine.graphics.GraphicsManager;
import mylie.engine.input.Input;
import mylie.engine.input.InputEvent;
import mylie.engine.input.InputManager;
import mylie.engine.input.listeners.RawInputListener;
import mylie.engine.platform.PlatformDesktop;
import mylie.examples.utils.IconFactory;
import mylie.lwjgl3.opengl.OpenglSettings;
import mylie.util.configuration.Configuration;
import org.joml.Vector2i;

@Slf4j
public class A0_HelloEngine extends BaseApplication implements RawInputListener {
    GraphicsContext graphicsContext;

    GraphicsContext.VideoMode windowed = new GraphicsContext.VideoMode.Windowed(
            null, new Vector2i(1280, 600), GraphicsContext.VideoMode.Windowed.Centered);
    GraphicsContext.VideoMode fullscreen = new GraphicsContext.VideoMode.Fullscreen(null, null);
    boolean tmp = false;

    public static void main(String[] args) {
        PlatformDesktop platform = new PlatformDesktop();
        Configuration<Engine> engineConfiguration = platform.initialize();
        engineConfiguration.set(Engine.Settings.Application, new A0_HelloEngine());
        engineConfiguration.set(Engine.Settings.GraphicsApi, new OpenglSettings());
        Engine.ShutdownReason start = Engine.start(engineConfiguration, true, false);
        log.info(start.toString());
    }

    @Override
    protected void onInit() {
        log.info("On init");
        InputManager inputManager = getFeature(InputManager.class);
        inputManager.addInputListener(this);
        GraphicsManager graphicsManager = getFeature(GraphicsManager.class);
        GraphicsContext.Configuration configuration = new GraphicsContext.Configuration();
        GraphicsContext.VideoMode videoMode = windowed;
        configuration.set(GraphicsContext.Parameters.AlwaysOnTop, true);
        configuration.set(GraphicsContext.Parameters.Title, "Hello Engine");
        configuration.set(GraphicsContext.Parameters.VideoMode, videoMode);
        configuration.set(GraphicsContext.Parameters.VSync, true);
        configuration.set(GraphicsContext.Parameters.Icons, IconFactory.getDefaultIcons());
        graphicsContext = graphicsManager.createContext(configuration, true);
    }

    @Override
    public void onUpdate(Timer.Time time) {}

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
            if (keyEvent.key().equals(Input.Key.ENTER) && keyEvent.type() == InputEvent.Keyboard.Key.Type.PRESSED) {
                log.trace("VSync");
                graphicsContext
                        .configuration()
                        .set(
                                GraphicsContext.Parameters.VSync,
                                !graphicsContext.configuration().get(GraphicsContext.Parameters.VSync));
            }
            if (keyEvent.key().equals(Input.Key.F12) && keyEvent.type() == InputEvent.Keyboard.Key.Type.PRESSED) {
                tmp = !tmp;
                if (tmp) {
                    graphicsContext.configuration().set(GraphicsContext.Parameters.VideoMode, fullscreen);
                } else {
                    graphicsContext.configuration().set(GraphicsContext.Parameters.VideoMode, windowed);
                }
            }
        }
    }
}
