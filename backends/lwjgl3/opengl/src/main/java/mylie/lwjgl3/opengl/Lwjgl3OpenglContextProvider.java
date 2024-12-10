package mylie.lwjgl3.opengl;

import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Cache;
import mylie.engine.core.features.async.Functions;
import mylie.engine.graphics.GraphicsContext;
import mylie.lwjgl3.glfw.GlfwContext;
import mylie.lwjgl3.glfw.GlfwContextProvider;
import mylie.lwjgl3.opengl.api.Lwjgl3GlBase;
import org.lwjgl.glfw.GLFW;

public class Lwjgl3OpenglContextProvider extends GlfwContextProvider {
    public Lwjgl3OpenglContextProvider() {}

    @Override
    public GraphicsContext createContext(
            GraphicsContext.Configuration contextSettings, GraphicsContext primaryContext) {
        Lwjgl3OpenGlContext glfwContext = new Lwjgl3OpenGlContext(contextSettings, this, (GlfwContext) primaryContext);

        boolean success = Async.await(
                Async.async(Async.Mode.Async, Cache.Never, Async.ENGINE, -1, CreateContext, this, glfwContext));
        glfwContext.makeCurrent();
        glfwContext.createGlCapabilities();
        addBaseApiFeatures(glfwContext);
        return glfwContext;
    }

    private void addBaseApiFeatures(Lwjgl3OpenGlContext glfwContext) {
        glfwContext.addApiFeature(new Lwjgl3GlBase());
    }

    private void setupApi(GlfwContext glfwContext) {
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        // todo: opengl profile selection.
    }

    private static final Functions.F1<Boolean, Lwjgl3OpenglContextProvider, GlfwContext> CreateContext =
            new Functions.F1<>("CreateContext") {
                @Override
                protected Boolean run(Lwjgl3OpenglContextProvider o, GlfwContext glfwContext) {
                    o.setupContext(glfwContext);
                    o.setupApi(glfwContext);
                    return o.createWindow(glfwContext);
                }
            };
}
