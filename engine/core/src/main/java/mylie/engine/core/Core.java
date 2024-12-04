package mylie.engine.core;

import lombok.extern.slf4j.Slf4j;
import mylie.engine.application.ApplicationManager;
import mylie.engine.core.features.async.Scheduler;
import mylie.engine.input.InputModule;
import mylie.util.configuration.Configuration;
import mylie.util.configuration.Setting;

@Slf4j
public class Core implements EngineManager {
    private final Configuration<Engine> engineConfiguration;
    private final FeatureManager featureManager;
    private Engine.ShutdownReason shutdownReason;
    private Scheduler scheduler;

    public Core(Configuration<Engine> engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
        this.featureManager = new FeatureManager(engineConfiguration);
    }

    public Engine.ShutdownReason onStart() {
        FeatureBarrier.initDefaults(featureManager);
        initModules();
        updateLoop();
        return shutdownReason;
    }

    private void updateLoop() {
        this.scheduler = featureManager.get(Scheduler.class);
        while (shutdownReason == null) {
            log.debug("#### NEW FRAME ####");
            scheduler.clearCaches(0);
            featureManager.onUpdate();
        }
        featureManager.onShutdown();
    }

    private void initModules() {
        featureManager.add(this);
        initFeature(Engine.Settings.Scheduler);
        initFeature(Engine.Settings.Timer);
        featureManager.add(new InputModule());
        featureManager.add(new ApplicationManager());
    }

    private <F extends Feature.Core, S extends Feature.Settings<F>> void initFeature(Setting<Engine, S> setting) {
        S featureSettings = engineConfiguration.get(setting);
        F feature = featureSettings.build();
        if (feature instanceof BaseFeature baseFeature) {
            log.trace(
                    "Feature<{}> using {}",
                    baseFeature.featureType().getSimpleName(),
                    feature.getClass().getSimpleName());
            featureManager.add(feature);
        }
    }

    @Override
    public void shutdown(Engine.ShutdownReason reason) {
        this.shutdownReason = reason;
    }
}
