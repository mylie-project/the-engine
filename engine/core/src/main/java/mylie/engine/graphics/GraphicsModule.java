package mylie.engine.graphics;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.*;
import mylie.util.configuration.Configuration;

@Slf4j
public class GraphicsModule extends CoreFeature implements Lifecycle.Update {
    @Getter(AccessLevel.PUBLIC)
    private List<Graphics.Display> availableDisplays;

    private ContextProvider contextProvider;
    private GraphicsApi graphicsApi;
    private GraphicsContext primaryContext;

    public GraphicsModule() {
        super(GraphicsModule.class);
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
    public void onUpdate() {}

    private class Internal implements GraphicsManager {

        @Override
        public List<Graphics.Display> availableDisplays() {
            return availableDisplays;
        }
    }
}
