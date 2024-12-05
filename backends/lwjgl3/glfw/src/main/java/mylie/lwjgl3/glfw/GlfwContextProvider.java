package mylie.lwjgl3.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.Engine;
import mylie.engine.core.FeatureManager;
import mylie.engine.graphics.ContextProvider;
import mylie.engine.graphics.Graphics;
import mylie.util.configuration.Configuration;
import org.joml.Vector2i;
import org.joml.Vector4i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;

@Slf4j
public abstract class GlfwContextProvider extends ContextProvider implements GLFWErrorCallbackI {
    @Override
    public List<Graphics.Display> onInitialize(
            FeatureManager featureManager, Configuration<Engine> engineConfiguration) {
        glfwSetErrorCallback(this);
        if (!glfwInit()) {
            throw new RuntimeException("Unable to initialize GLFW");
        }

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
        return new DataTypes.GlfwDisplay(glfwGetPrimaryMonitor() == handle, defaultVideoMode, videoModes);
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
        DataTypes.GlfwVideoMode glfwVideoMode = new DataTypes.GlfwVideoMode(resolution, refreshRate, bits);
        glfwVideoMode.setDisplayHandle(handle);
        return glfwVideoMode;
    }

    @Override
    public void invoke(int i, long l) {
        log.error("Glfw error: {} - {}", i, l);
    }
}
