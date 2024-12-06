package mylie.lwjgl3.opengl;

import lombok.AccessLevel;
import lombok.Getter;
import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Cache;
import mylie.engine.core.features.async.Functions;
import mylie.engine.core.features.async.Result;
import mylie.lwjgl3.glfw.GlfwContext;
import mylie.lwjgl3.glfw.GlfwContextProvider;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

@Getter(AccessLevel.PACKAGE)
public class OpenGlContext extends GlfwContext {
    private GLCapabilities capabilities;

    public OpenGlContext(Configuration settings, GlfwContextProvider provider, GlfwContext primaryContext) {
        super(settings, provider, primaryContext);
    }

    public Result<Boolean> createGlCapabilities() {
        return Async.async(Async.Mode.Async, Cache.Never, target(), -1, CreateGlCapabilities, this);
    }

    private static final Functions.F0<Boolean, OpenGlContext> CreateGlCapabilities =
            new Functions.F0<>("CreateGlCapabilities") {
                @Override
                protected Boolean run(OpenGlContext o) {
                    o.capabilities = GL.createCapabilities();

                    return true;
                }
            };
}
