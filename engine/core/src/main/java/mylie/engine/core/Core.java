package mylie.engine.core;

import lombok.extern.slf4j.Slf4j;
import mylie.util.configuration.Configuration;
import mylie.util.configuration.Setting;
@Slf4j
public class Core {
    private final Configuration<Engine> engineConfiguration;
    private final FeatureManager featureManager;
    public Core(Configuration<Engine> engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
        this.featureManager = new FeatureManager(engineConfiguration);
    }

    public Engine.ShutdownReason onStart() {
        initModules();
        return Engine.Shutdown;
    }

    private void initModules() {
        initFeature(Engine.Settings.Scheduler);
        initFeature(Engine.Settings.Timer);
    }

    private <F extends Feature.Engine,S extends Feature.Settings<F>> void initFeature(Setting<Engine,S> setting){
        S featureSettings = engineConfiguration.get(setting);
        F feature = featureSettings.build();
        log.trace("Feature<{}> using {}", feature.featureType().getSimpleName(),feature.getClass().getSimpleName());
        featureManager.add(feature);
    }

}
