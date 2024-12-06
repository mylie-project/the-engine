package mylie.engine.graphics;

import java.util.HashSet;
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

    private List<GraphicsContext> activeContexts;
    private List<GraphicsContext> syncedContexts;
    private ContextProvider contextProvider;
    private GraphicsApi graphicsApi;
    private GraphicsContext primaryContext;

    public GraphicsModule() {
        super(GraphicsModule.class);
        activeContexts = new CopyOnWriteArrayList<>();
        syncedContexts = new CopyOnWriteArrayList<>();
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

    @Override
    public void onUpdate() {
        if (syncedContexts.isEmpty()) return;
        Set<Result<Boolean>> results = new HashSet<>();
        for (GraphicsContext syncedContext : syncedContexts) {
            results.add(syncedContext.swapBuffers());
        }
        Async.await(results);
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
        public GraphicsContext createContext(GraphicsContextSettings contextSettings, boolean synced) {
            GraphicsContext context = contextProvider.createContext(contextSettings, primaryContext);

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
