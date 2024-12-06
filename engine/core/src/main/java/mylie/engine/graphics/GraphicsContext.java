package mylie.engine.graphics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import mylie.engine.core.features.async.*;

@Getter
@Setter(AccessLevel.PACKAGE)
public abstract class GraphicsContext {
    private final GraphicsContextSettings settings;
    private final Async.Target target;
    private final BlockingQueue<Runnable> queue;
    private final FeatureThread featureThread;

    public GraphicsContext(GraphicsContextSettings settings, Scheduler scheduler) {
        this.settings = settings;
        this.queue = new LinkedTransferQueue<>();
        this.target = new Async.Target("GraphicsContext<" + settings.id() + ">");
        scheduler.registerTarget(target(), queue()::add);
        featureThread = scheduler.createFeatureThread(target(), queue());
    }

    protected abstract void applySettings(GraphicsContextSettings graphicsContextSettings);

    protected abstract Result<Boolean> destroy();

    public abstract Result<Boolean> swapBuffers();
}
