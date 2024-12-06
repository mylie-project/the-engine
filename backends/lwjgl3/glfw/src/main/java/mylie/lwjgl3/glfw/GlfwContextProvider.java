package mylie.lwjgl3.glfw;

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
import mylie.engine.graphics.GraphicsContext;
import mylie.engine.input.InputManager;
import mylie.util.configuration.Configuration;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector4i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;

@Slf4j
@Getter(AccessLevel.PROTECTED)
public abstract class GlfwContextProvider extends ContextProvider implements GLFWErrorCallbackI {
    private Scheduler scheduler;
    private final GlfwInputProvider inputProvider;

    public GlfwContextProvider() {
        inputProvider = new GlfwInputProvider();
        GraphicsContext.Parameters.Resizable = new DataTypes.GlfwContextParameter<>(GLFW.GLFW_RESIZABLE, null, false);
        GraphicsContext.Parameters.Transparent =
                new DataTypes.GlfwContextParameter<>(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, null, false);
        GraphicsContext.Parameters.AlwaysOnTop = new DataTypes.GlfwContextParameter<>(GLFW.GLFW_FLOATING, null, false);
        GraphicsContext.Parameters.Title = new DataTypes.GlfwContextParameter<>(-1, GLFW::glfwSetWindowTitle, "Mylie");

        GraphicsContext.Parameters.VSync = new DataTypes.GlfwContextParameter<>(-1, this::swapIntervalWrapper, true);
        GraphicsContext.Parameters.Decorated = new DataTypes.GlfwContextParameter<>(GLFW.GLFW_DECORATED, null, true);
        GraphicsContext.Parameters.Multisampling = new DataTypes.GlfwContextParameter<>(GLFW.GLFW_SAMPLES, null, 0);
        GraphicsContext.Parameters.Srgb = new DataTypes.GlfwContextParameter<>(GLFW.GLFW_SRGB_CAPABLE, null, false);
    }

    private void swapIntervalWrapper(long window, boolean vsync) {
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }

    @Override
    public List<Graphics.Display> onInitialize(
            FeatureManager featureManager, Configuration<Engine> engineConfiguration) {
        scheduler = featureManager.get(Scheduler.class);
        GLFW.glfwSetErrorCallback(this);
        if (!GLFW.glfwInit()) {
            throw new RuntimeException("Unable to initialize GLFW");
        }
        featureManager.get(InputManager.class).addInputProvider(inputProvider);
        return getDisplays();
    }

    private List<Graphics.Display> getDisplays() {
        List<Graphics.Display> displays = new ArrayList<>();
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        for (int i = 0; i < Objects.requireNonNull(pointerBuffer).capacity(); i++) {
            long handle = pointerBuffer.get(i);
            displays.add(getDisplay(handle));
        }
        return displays;
    }

    private Graphics.Display getDisplay(long handle) {
        List<DataTypes.GlfwVideoMode> videoModes = getVideoModes(handle);
        DataTypes.GlfwVideoMode defaultVideoMode =
                getVideoMode(handle, Objects.requireNonNull(GLFW.glfwGetVideoMode(handle)));
        return new DataTypes.GlfwDisplay(handle, GLFW.glfwGetPrimaryMonitor() == handle, defaultVideoMode, videoModes);
    }

    private List<DataTypes.GlfwVideoMode> getVideoModes(long handle) {
        List<DataTypes.GlfwVideoMode> videoModes = new ArrayList<>();
        GLFWVidMode.Buffer glfwVidModes = GLFW.glfwGetVideoModes(handle);
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
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GraphicsContext.Configuration configuration = context.configuration();
        for (GraphicsContext.Configuration.Parameter<?> parameter : configuration.getParameters()) {
            if (parameter instanceof DataTypes.GlfwContextParameter<?> glfwParameter) {
                if (glfwParameter.windowHint() != -1) {
                    Object o = configuration.get(parameter);
                    int value = 0;
                    if (o instanceof Boolean) {
                        value = ((Boolean) o) ? 1 : 0;
                    } else if (o instanceof Integer) {
                        value = (Integer) o;
                    }
                    GLFW.glfwWindowHint(glfwParameter.windowHint(), value);
                }
            }
        }
        GraphicsContext.VideoMode videoMode = configuration.get(GraphicsContext.Parameters.VideoMode);
        if (videoMode instanceof GraphicsContext.VideoMode.WindowedFullscreen) {
            GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
        }
    }

    protected boolean createWindow(GlfwContext contexts) {
        log.trace("Creating window");
        GraphicsContext.Configuration configuration = contexts.configuration();
        GraphicsContext.VideoMode videoMode = configuration.get(GraphicsContext.Parameters.VideoMode);
        Vector2ic size = new Vector2i(16, 16);
        long display = NULL;
        long parent = contexts.primaryContext == null ? NULL : contexts.primaryContext.handle;
        String title = configuration.get(GraphicsContext.Parameters.Title);
        boolean fullscreen = false;
        if (videoMode instanceof GraphicsContext.VideoMode.Windowed windowed) {
            size = windowed.size();
        } else if (videoMode instanceof GraphicsContext.VideoMode.WindowedFullscreen windowedFullscreen) {
            size = windowedFullscreen.display().defaultVideoMode().resolution();
        } else if (videoMode instanceof GraphicsContext.VideoMode.Fullscreen fullscreenMode) {
            fullscreen = true;
            size = fullscreenMode.videoMode().resolution();
            display = ((DataTypes.GlfwDisplay) fullscreenMode.display()).handle();
        }

        if (parent != NULL) {
            contexts.primaryContext.release().get();
        }
        long window = GLFW.glfwCreateWindow(size.x(), size.y(), title, fullscreen ? display : NULL, parent);
        if (parent != NULL) {
            contexts.primaryContext.makeCurrent().get();
        }
        contexts.handle = window;
        inputProvider.addContext(contexts);
        GLFW.glfwShowWindow(window);
        return true;
    }

    @Override
    public void invoke(int i, long l) {
        String description = memUTF8(l);
        log.error("Glfw error: {} - {}", i, description);
    }

    public void destroyContext(GlfwContext glfwContext) {
        GLFW.glfwHideWindow(glfwContext.handle);
        GLFW.glfwDestroyWindow(glfwContext.handle);
    }
}
