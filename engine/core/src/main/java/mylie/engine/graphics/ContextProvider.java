package mylie.engine.graphics;

import java.util.List;
import mylie.engine.core.Engine;
import mylie.engine.core.FeatureManager;
import mylie.util.configuration.Configuration;

public abstract class ContextProvider {
    public abstract List<GraphicsContext.Display> onInitialize(
            FeatureManager featureManager, Configuration<Engine> engineConfiguration);

    public abstract GraphicsContext createContext(
            GraphicsContext.Configuration contextSettings, GraphicsContext primaryContext);
}
