package mylie.lwjgl3.glfw;

import static org.lwjgl.system.MemoryUtil.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.Engine;
import mylie.engine.core.FeatureManager;
import mylie.engine.core.features.async.Scheduler;
import mylie.engine.core.features.timer.Timer;
import mylie.engine.graphics.ContextProvider;
import mylie.engine.graphics.Graphics;
import mylie.engine.graphics.GraphicsContext;
import mylie.engine.input.InputManager;
import mylie.util.configuration.Configuration;
import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.stb.STBImage;

@Slf4j
@Getter(AccessLevel.PROTECTED)
public abstract class GlfwContextProvider extends ContextProvider implements GLFWErrorCallbackI {
    private Timer timer;
    private Scheduler scheduler;
    private final GlfwInputProvider inputProvider;
    private Graphics.Display primaryDisplay;

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
        GraphicsContext.Parameters.VideoMode =
                new DataTypes.GlfwContextParameter<>(-1, this::setVideoModeWrapper, null);
        GraphicsContext.Parameters.Icons = new DataTypes.GlfwContextParameter<>(-1, this::setIconsWrapper, null);
    }

    private void setIconsWrapper(long window, GraphicsContext.Icons icons) {
        if (icons == null) {
            return;
        }
        IntBuffer w = memAllocInt(1);
        IntBuffer h = memAllocInt(1);
        IntBuffer comp = memAllocInt(1);
        try (GLFWImage.Buffer iconsBuffer = GLFWImage.malloc(icons.paths().length)) {
            ByteBuffer[] buffers = new ByteBuffer[icons.paths().length];
            ByteBuffer[] imageBuffers = new ByteBuffer[icons.paths().length];
            for (int i = 0; i < icons.paths().length; i++) {
                try {
                    buffers[i] = IOUtil.ioResourceToByteBuffer(icons.paths()[i], 10000);
                } catch (IOException e) {
                    log.error(e.getLocalizedMessage());
                }
                imageBuffers[i] = STBImage.stbi_load_from_memory(buffers[i], w, h, comp, 4);
                assert imageBuffers[i] != null;
                iconsBuffer.position(i).width(w.get(0)).height(h.get(0)).pixels(imageBuffers[i]);
            }
            iconsBuffer.position(0);
            GLFW.glfwSetWindowIcon(window, iconsBuffer);
            for (int i = 0; i < icons.paths().length; i++) {
                STBImage.stbi_image_free(imageBuffers[i]);
            }
        }
    }

    private void swapIntervalWrapper(long window, boolean vsync) {
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }

    private void setVideoModeWrapper(long window, GraphicsContext.VideoMode videoMode) {
        long display = NULL;
        Vector3ic size = new Vector3i();
        Vector2ic position = new Vector2i();
        if ((videoMode instanceof GraphicsContext.VideoMode.Fullscreen fullscreen)) {
            display = fullscreen.display() != null
                    ? ((DataTypes.GlfwDisplay) fullscreen.display()).handle()
                    : ((DataTypes.GlfwDisplay) primaryDisplay).handle();
            size = fullscreen.display() != null && fullscreen.videoMode() != null
                    ? new Vector3i(
                            fullscreen.videoMode().resolution(),
                            fullscreen.videoMode().refreshRate())
                    : new Vector3i(
                            primaryDisplay.defaultVideoMode().resolution(),
                            primaryDisplay.defaultVideoMode().refreshRate());
        } else if (videoMode instanceof GraphicsContext.VideoMode.Windowed windowed) {
            size = new Vector3i(windowed.size(), 0);
            if (windowed.position() == GraphicsContext.VideoMode.Windowed.Centered || windowed.position() == null) {
                DataTypes.GlfwDisplay tmpDisplay =
                        (DataTypes.GlfwDisplay) (windowed.display() != null ? windowed.display() : primaryDisplay);
                Graphics.Display.VideoMode tmpVideoMode = tmpDisplay.defaultVideoMode();
                position = new Vector2i(
                        (tmpVideoMode.resolution().x() - size.x()) / 2,
                        (tmpVideoMode.resolution().y() - size.y()) / 2);
            } else {
                position = windowed.position();
            }
        } else if (videoMode instanceof GraphicsContext.VideoMode.WindowedFullscreen windowedFullscreen) {
            DataTypes.GlfwDisplay tmpDisplay = (DataTypes.GlfwDisplay)
                    (windowedFullscreen.display() != null ? windowedFullscreen.display() : primaryDisplay);
            size = new Vector3i(
                    tmpDisplay.defaultVideoMode().resolution(),
                    tmpDisplay.defaultVideoMode().refreshRate());
            position = new Vector2i(0, 0);
            GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
        }
        GLFW.glfwSetWindowMonitor(window, display, position.x(), position.y(), size.x(), size.y(), size.z());
    }

    @Override
    public List<Graphics.Display> onInitialize(
            FeatureManager featureManager, Configuration<Engine> engineConfiguration) {
        scheduler = featureManager.get(Scheduler.class);
        timer=featureManager.get(Timer.class);
        inputProvider.timer(timer);
        GLFW.glfwSetErrorCallback(this);
        if (!GLFW.glfwInit()) {
            throw new RuntimeException("Unable to initialize GLFW");
        }
        featureManager.get(InputManager.class).addInputProvider(inputProvider);
        List<Graphics.Display> displays = getDisplays();
        for (Graphics.Display display : displays) {
            if (display.primary()) {
                primaryDisplay = display;
                break;
            }
        }
        return displays;
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
            if (windowedFullscreen.display() != null) {
                size = windowedFullscreen.display().defaultVideoMode().resolution();
            } else {
                size = primaryDisplay.defaultVideoMode().resolution();
            }
            Graphics.ContextProperties.Position.set(contexts,new Vector2i(0,0),timer.time().frameId());
        } else if (videoMode instanceof GraphicsContext.VideoMode.Fullscreen fullscreenMode) {
            fullscreen = true;
            if (fullscreenMode.display() != null) {
                display = ((DataTypes.GlfwDisplay) fullscreenMode.display()).handle();
                if (fullscreenMode.videoMode() != null) {
                    size = fullscreenMode.videoMode().resolution();
                } else {
                    size = fullscreenMode.display().defaultVideoMode().resolution();
                }
            } else {
                display = ((DataTypes.GlfwDisplay) primaryDisplay).handle();
                size = primaryDisplay.defaultVideoMode().resolution();
            }
            Graphics.ContextProperties.Position.set(contexts,new Vector2i(0,0),timer.time().frameId());
        }

        if (parent != NULL) {
            contexts.primaryContext.release().get();
        }
        long window = GLFW.glfwCreateWindow(size.x(), size.y(), title, fullscreen ? display : NULL, parent);
        if (parent != NULL) {
            contexts.primaryContext.makeCurrent().get();
        }
        if (videoMode instanceof GraphicsContext.VideoMode.Windowed windowed) {
            Vector2ic position;
            if (windowed.position() == GraphicsContext.VideoMode.Windowed.Centered || windowed.position() == null) {
                DataTypes.GlfwDisplay tmpDisplay =
                        (DataTypes.GlfwDisplay) (windowed.display() != null ? windowed.display() : primaryDisplay);
                Graphics.Display.VideoMode tmpVideoMode = tmpDisplay.defaultVideoMode();
                position = new Vector2i(
                        (tmpVideoMode.resolution().x() - size.x()) / 2,
                        (tmpVideoMode.resolution().y() - size.y()) / 2);
            } else {
                position = windowed.position();
            }
            GLFW.glfwSetWindowPos(window, position.x(), position.y());
            Graphics.ContextProperties.Position.set(contexts,position,timer.time().frameId());
        }
        contexts.handle = window;
        inputProvider.addContext(contexts);
        setIconsWrapper(window, configuration.get(GraphicsContext.Parameters.Icons));
        Graphics.ContextProperties.Size.set(contexts,size,timer.time().frameId());
        Graphics.ContextProperties.FrameBufferSize.set(contexts,size,timer.time().frameId());
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
