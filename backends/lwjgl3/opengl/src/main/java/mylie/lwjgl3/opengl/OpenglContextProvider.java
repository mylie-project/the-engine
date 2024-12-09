package mylie.lwjgl3.opengl;

import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Cache;
import mylie.engine.core.features.async.Functions;
import mylie.engine.graphics.GraphicsContext;
import mylie.lwjgl3.glfw.GlfwContext;
import mylie.lwjgl3.glfw.GlfwContextProvider;
import mylie.lwjgl3.opengl.api.GlBase;

public class OpenglContextProvider extends GlfwContextProvider {
    public OpenglContextProvider() {}

    @Override
    public GraphicsContext createContext(
            GraphicsContext.Configuration contextSettings, GraphicsContext primaryContext) {
        OpenGlContext glfwContext = new OpenGlContext(contextSettings, this, (GlfwContext) primaryContext);

        boolean success = Async.await(
                Async.async(Async.Mode.Async, Cache.Never, Async.ENGINE, -1, CreateContext, this, glfwContext));
        glfwContext.makeCurrent();
        glfwContext.createGlCapabilities();
        addBaseApiFeatures(glfwContext);
        return glfwContext;
    }

    private void addBaseApiFeatures(OpenGlContext glfwContext) {
        glfwContext.addApiFeature(new GlBase());
    }

    private void setupApi(GlfwContext glfwContext) {
        // todo: opengl profile selection.
    }

    private static final Functions.F1<Boolean, OpenglContextProvider, GlfwContext> CreateContext =
            new Functions.F1<>("CreateContext") {
                @Override
                protected Boolean run(OpenglContextProvider o, GlfwContext glfwContext) {
                    o.setupContext(glfwContext);
                    o.setupApi(glfwContext);
                    return o.createWindow(glfwContext);
                }
            };
}
