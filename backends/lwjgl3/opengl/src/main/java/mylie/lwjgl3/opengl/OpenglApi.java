package mylie.lwjgl3.opengl;

import mylie.engine.core.Engine;
import mylie.engine.core.FeatureManager;
import mylie.util.configuration.Configuration;

public class OpenglApi extends mylie.engine.graphics.apis.opengl.OpenglApi {
    @Override
    protected void onInitialize(FeatureManager featureManager, Configuration<Engine> engineConfiguration) {
        contextProvider(new OpenglContextProvider());
    }
}
