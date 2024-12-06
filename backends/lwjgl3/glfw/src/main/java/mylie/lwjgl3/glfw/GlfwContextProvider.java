package mylie.lwjgl3.glfw;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.Engine;
import mylie.engine.core.FeatureManager;
import mylie.engine.core.features.async.Scheduler;
import mylie.engine.graphics.ContextProvider;
import mylie.engine.graphics.Graphics;
import mylie.engine.graphics.GraphicsContextSettings;
import mylie.engine.input.InputManager;
import mylie.util.configuration.Configuration;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector4i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;

@Slf4j
@Getter(AccessLevel.PROTECTED)
public abstract class GlfwContextProvider extends ContextProvider implements GLFWErrorCallbackI {
    private Scheduler scheduler;
    private GlfwInputProvider inputProvider;

    public GlfwContextProvider() {
        inputProvider = new GlfwInputProvider();
    }

    @Override
    public List<Graphics.Display> onInitialize(
            FeatureManager featureManager, Configuration<Engine> engineConfiguration) {
        scheduler = featureManager.get(Scheduler.class);
        glfwSetErrorCallback(this);
        if (!glfwInit()) {
            throw new RuntimeException("Unable to initialize GLFW");
        }
        featureManager.get(InputManager.class).addInputProvider(inputProvider);
        return getDisplays();
    }

    private List<Graphics.Display> getDisplays() {
        List<Graphics.Display> displays = new ArrayList<>();
        PointerBuffer pointerBuffer = glfwGetMonitors();
        for (int i = 0; i < Objects.requireNonNull(pointerBuffer).capacity(); i++) {
            long handle = pointerBuffer.get(i);
            displays.add(getDisplay(handle));
        }
        return displays;
    }

    private Graphics.Display getDisplay(long handle) {
        List<DataTypes.GlfwVideoMode> videoModes = getVideoModes(handle);
        DataTypes.GlfwVideoMode defaultVideoMode =
                getVideoMode(handle, Objects.requireNonNull(glfwGetVideoMode(handle)));
        return new DataTypes.GlfwDisplay(handle, glfwGetPrimaryMonitor() == handle, defaultVideoMode, videoModes);
    }

    private List<DataTypes.GlfwVideoMode> getVideoModes(long handle) {
        List<DataTypes.GlfwVideoMode> videoModes = new ArrayList<>();
        GLFWVidMode.Buffer glfwVidModes = glfwGetVideoModes(handle);
        assert glfwVidModes != null;
        for (GLFWVidMode glfwVidMode : glfwVidModes) {
            videoModes.add(getVideoMode(handle, glfwVidMode));
        }
        return videoModes;
    }

    private DataTypes.GlfwVideoMode getVideoMode(long handle, GLFWVidMode glfwVidMode) {
        Vector2i resolution = new Vector2i(glfwVidMode.width(), glfwVidMode.height());
        int refreshRate = glfwVidMode.refreshRate();
        Vector4i bits = new Vector4i(glfwVidMode.redBits(), glfwVidMode.greenBits(), glfwVidMode.blueBits(), 0);
        DataTypes.GlfwVideoMode glfwVideoMode = new DataTypes.GlfwVideoMode(handle, resolution, refreshRate, bits);
        glfwVideoMode.displayHandle(handle);
        return glfwVideoMode;
    }

    protected void setupContext(GlfwContext context) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        if (context.settings() instanceof GlfwContextSettings settings) {
            glfwWindowHint(GLFW_RESIZABLE, settings.resizable() ? GLFW_TRUE : GLFW_FALSE);
            glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, settings.transparent() ? GLFW_TRUE : GLFW_FALSE);
            glfwWindowHint(GLFW_FLOATING, settings.alwaysOnTop() ? GLFW_TRUE : GLFW_FALSE);
            glfwWindowHint(GLFW_SRGB_CAPABLE, settings.srgb() ? GLFW_TRUE : GLFW_FALSE);
            glfwWindowHint(GLFW_SAMPLES, settings.samples());
            glfwWindowHint(GLFW_DECORATED, settings.decorated() ? GLFW_TRUE : GLFW_FALSE);
        }
        if (context.settings().resolution()
                instanceof GraphicsContextSettings.Resolution.FullScreenWindowed fullScreenWindowed) {
            glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        }
    }

    protected boolean createWindow(GlfwContext contexts) {
        log.trace("Creating window");
        GraphicsContextSettings settings = contexts.settings();
        GraphicsContextSettings.Resolution resolution = settings.resolution();
        Vector2ic size = new Vector2i(16, 16);
        long display = NULL;
        long parent = contexts.primaryContext == null ? NULL : contexts.primaryContext.handle;
        String title = settings.title() == null ? settings.id() : settings.title();
        boolean fullscreen = false;
        if (resolution instanceof GraphicsContextSettings.Resolution.Windowed windowed) {
            size = windowed.size();
            display = ((DataTypes.GlfwDisplay) windowed.display()).handle();
            fullscreen = false;
        } else if (resolution instanceof GraphicsContextSettings.Resolution.Fullscreen fullscreenResolution) {
            size = fullscreenResolution.videoMode().resolution();
            display = ((DataTypes.GlfwDisplay) fullscreenResolution.display()).handle();
            fullscreen = true;
        } else if (resolution
                instanceof GraphicsContextSettings.Resolution.FullScreenWindowed windowedFullscreenResolution) {
            size = windowedFullscreenResolution.display().defaultVideoMode().resolution();
            display = ((DataTypes.GlfwDisplay) windowedFullscreenResolution.display()).handle();
            fullscreen = false;
        }
        if (parent != NULL) {
            contexts.primaryContext.release().get();
            // glfwMakeContextCurrent(parent);
        }
        long window = glfwCreateWindow(size.x(), size.y(), title, fullscreen ? display : NULL, parent);
        if (parent != NULL) {
            // glfwMakeContextCurrent(NULL);
            contexts.primaryContext.makeCurrent().get();
        }
        if (resolution instanceof GraphicsContextSettings.Resolution.Windowed windowed) {
            if (windowed.position() == GraphicsContextSettings.Resolution.Windowed.Center) {
                GLFWVidMode vidmode = Objects.requireNonNull(glfwGetVideoMode(display));
                glfwSetWindowPos(window, (vidmode.width() - size.x()) / 2, (vidmode.height() - size.y()) / 2);
            }
        }
        contexts.handle = window;
        inputProvider.addContext(contexts);
        glfwShowWindow(window);
        return true;
    }

    @Override
    public void invoke(int i, long l) {
        String description = memUTF8(l);
        log.error("Glfw error: {} - {}", i, description);
    }

    public void destroyContext(GlfwContext glfwContext) {
        glfwHideWindow(glfwContext.handle);
        glfwDestroyWindow(glfwContext.handle);
    }
}
