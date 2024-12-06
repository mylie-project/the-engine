package mylie.lwjgl3.opengl;

import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Cache;
import mylie.engine.core.features.async.Functions;
import mylie.engine.graphics.GraphicsContext;
import mylie.engine.graphics.GraphicsContextSettings;
import mylie.lwjgl3.glfw.GlfwContext;
import mylie.lwjgl3.glfw.GlfwContextProvider;

public class OpenglContextProvider extends GlfwContextProvider {
    @Override
    public GraphicsContext createContext(GraphicsContextSettings contextSettings, GraphicsContext primaryContext) {
        OpenGlContext glfwContext = new OpenGlContext(contextSettings, this, (GlfwContext) primaryContext);

        boolean success = Async.await(
                Async.async(Async.Mode.Async, Cache.Never, Async.ENGINE, -1, CreateContext, this, glfwContext));
        glfwContext.makeCurrent();
        glfwContext.createGlCapabilities();
        return glfwContext;
    }

    private void setupApi(GlfwContext glfwContext) {}

    private static final Functions.F1<Boolean, OpenglContextProvider, GlfwContext> CreateContext =
            new Functions.F1<Boolean, OpenglContextProvider, GlfwContext>("CreateContext") {
                @Override
                protected Boolean run(OpenglContextProvider o, GlfwContext glfwContext) {
                    o.setupContext(glfwContext);
                    o.setupApi(glfwContext);
                    boolean result = o.createWindow(glfwContext);
                    return result;
                }
            };
}
