package mylie.lwjgl3.glfw;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Cache;
import mylie.engine.core.features.async.Functions;
import mylie.engine.core.features.async.Result;
import mylie.engine.graphics.GraphicsContext;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

@Slf4j
@Getter(AccessLevel.PACKAGE)
public class GlfwContext extends GraphicsContext {
    final GlfwContextProvider provider;
    final GlfwContext primaryContext;

    @Setter(AccessLevel.PACKAGE)
    private DataTypes.GlfwCallbacks glfwCallbacks;

    long handle;

    public GlfwContext(Configuration configuration, GlfwContextProvider provider, GlfwContext primaryContext) {
        super(configuration, provider.scheduler());
        this.provider = provider;
        this.primaryContext = primaryContext;
    }

    @Override
    protected void applySettings() {}

    protected Result<Boolean> destroy() {
        return Async.async(Async.Mode.Async, Cache.Never, Async.ENGINE, -1, ShutdownContext, this);
    }

    private static Functions.F0<Boolean, GlfwContext> ShutdownContext = new Functions.F0<>("ShutdownContext") {
        @Override
        protected Boolean run(GlfwContext o) {
            o.provider.destroyContext(o);
            o.featureThread().stop();
            return true;
        }
    };

    public Result<Boolean> makeCurrent() {
        return Async.async(Async.Mode.Async, Cache.Never, target(), -1, MakeCurrent, this);
    }

    @Override
    public Result<Boolean> swapBuffers() {
        return Async.async(Async.Mode.Async, Cache.Never, target(), -1, SwapBuffers, this);
    }

    public Result<Boolean> release() {
        return Async.async(Async.Mode.Async, Cache.Never, target(), -1, Release, this);
    }

    public static Functions.F0<Boolean, GlfwContext> ApplySettings = new Functions.F0<>("ApplySettings") {

        @Override
        protected Boolean run(GlfwContext o) {
            o.provider.applySettings(o);
            return true;
        }
    };

    public static Functions.F0<Boolean, GlfwContext> MakeCurrent = new Functions.F0<>("MakeCurrent") {

        @Override
        protected Boolean run(GlfwContext o) {
            GLFW.glfwMakeContextCurrent(o.handle);
            GLFW.glfwSwapInterval(0);
            return true;
        }
    };

    public static Functions.F0<Boolean, GlfwContext> Release = new Functions.F0<>("Release") {

        @Override
        protected Boolean run(GlfwContext o) {
            GLFW.glfwMakeContextCurrent(MemoryUtil.NULL);
            return true;
        }
    };

    public static Functions.F0<Boolean, GlfwContext> SwapBuffers = new Functions.F0<>("SwapBuffers") {
        @Override
        protected Boolean run(GlfwContext o) {
            // long t1=System.nanoTime();

            GLFW.glfwSwapBuffers(o.handle);
            // log.info("SwapBuffers {}-{}",t1,System.nanoTime());
            return true;
        }
    };
}