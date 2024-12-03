package mylie.engine.core;

import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.features.async.Scheduler;
import mylie.util.configuration.Configuration;
import mylie.util.configuration.Setting;

@Slf4j
public class Core {
    private final Configuration<Engine> engineConfiguration;
    private final FeatureManager featureManager;
    private Engine.ShutdownReason shutdownReason;
    private Scheduler scheduler;

    public Core(Configuration<Engine> engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
        this.featureManager = new FeatureManager(engineConfiguration);
    }

    public Engine.ShutdownReason onStart() {
        initModules();
        updateLoop();
        return Engine.Shutdown;
    }

    private void updateLoop() {
        this.scheduler = featureManager.get(Scheduler.class);
        while (shutdownReason == null) {
            scheduler.clearCaches(0);
            featureManager.onUpdate();
        }
    }

    private void initModules() {
        initFeature(Engine.Settings.Scheduler);
        initFeature(Engine.Settings.Timer);
    }

    private <F extends Feature.Engine, S extends Feature.Settings<F>> void initFeature(Setting<Engine, S> setting) {
        S featureSettings = engineConfiguration.get(setting);
        F feature = featureSettings.build();
        log.trace(
                "Feature<{}> using {}",
                feature.featureType().getSimpleName(),
                feature.getClass().getSimpleName());
        featureManager.add(feature);
    }
}
