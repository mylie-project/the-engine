package mylie.engine.graphics;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.*;
import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Result;
import mylie.util.configuration.Configuration;

@Slf4j
public class GraphicsModule extends CoreFeature implements Lifecycle.Update, Lifecycle.InitDestroy {
    @Getter(AccessLevel.PUBLIC)
    private List<Graphics.Display> availableDisplays;

    private final List<GraphicsContext> activeContexts;
    private final List<GraphicsContext> syncedContexts;
    private final List<Result<Boolean>> swapBufferQueue;
    private ContextProvider contextProvider;
    private GraphicsApi graphicsApi;
    private GraphicsContext primaryContext;

    public GraphicsModule() {
        super(GraphicsModule.class);
        activeContexts = new CopyOnWriteArrayList<>();
        syncedContexts = new CopyOnWriteArrayList<>();
        swapBufferQueue = new LinkedList<>();
    }

    @Override
    protected void onSetup(FeatureManager featureManager, Configuration<Engine> engineConfiguration) {
        super.onSetup(featureManager, engineConfiguration);
        runAfter(FeatureBarrier.AppRendering.class);
        runBefore(FeatureBarrier.FrameComplete.class);
        GraphicsApiSettings graphicsApiSettings = engineConfiguration.get(Engine.Settings.GraphicsApi);
        if (graphicsApiSettings == null) {
            log.error("No GraphicsApi specified in configuration");
            return;
        }
        graphicsApi = graphicsApiSettings.build();
        graphicsApi.onInitialize(featureManager, engineConfiguration);
        contextProvider = graphicsApi.contextProvider();
        availableDisplays = contextProvider.onInitialize(featureManager, engineConfiguration);
        add(new Internal());
    }

    public void lastFrameComplete() {
        while (!swapBufferQueue.isEmpty()) {
            swapBufferQueue.removeFirst().get();
        }
    }

    @Override
    public void onUpdate() {
        if (syncedContexts.isEmpty()) return;

        for (GraphicsContext syncedContext : syncedContexts) {
            swapBufferQueue.add(syncedContext.swapBuffers());
        }
    }

    @Override
    public void onInit() {}

    @Override
    public void onDestroy() {
        if (activeContexts.isEmpty()) return;
        Set<Result<Boolean>> results = new HashSet<>();
        for (GraphicsContext context : activeContexts) {
            results.add(get(GraphicsManager.class).destroyContext(context));
        }
        Async.await(results);
    }

    private class Internal implements GraphicsManager {

        @Override
        public List<Graphics.Display> availableDisplays() {
            return availableDisplays;
        }

        @Override
        public GraphicsContext createContext(GraphicsContext.Configuration contextSettings, boolean synced) {
            if (contextSettings.context != null) {
                log.warn("Context setting already bound to a different context");
                return null;
            }
            GraphicsContext context = contextProvider.createContext(contextSettings, primaryContext);
            Graphics.ContextCapability.initAll(context);
            context.featureThread().start();
            if (synced) {
                syncedContexts.add(context);
            }
            if (primaryContext == null) {
                primaryContext = context;
            }
            activeContexts.add(context);
            return context;
        }

        @Override
        public Result<Boolean> destroyContext(GraphicsContext context) {
            activeContexts.remove(context);
            syncedContexts.remove(context);
            return context.destroy();
        }
    }
}
