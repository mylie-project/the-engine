package mylie.engine.graphics;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import mylie.engine.core.Engine;
import mylie.engine.core.FeatureManager;
import mylie.util.configuration.Configuration;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PROTECTED)
public abstract class GraphicsApi {
    private ContextProvider contextProvider;

    protected GraphicsApi() {}

    protected abstract void onInitialize(FeatureManager featureManager, Configuration<Engine> engineConfiguration);
}
