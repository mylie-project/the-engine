package mylie.engine.core;

import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.application.ApplicationManager;
import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Scheduler;
import mylie.engine.core.features.async.schedulers.SchedulerSingleThreaded;
import mylie.engine.core.features.options.OptionsManager;
import mylie.engine.graphics.GraphicsModule;
import mylie.engine.input.InputModule;
import mylie.util.configuration.Configuration;
import mylie.util.configuration.Setting;

@Slf4j
public class Core {
    private final Configuration<Engine> engineConfiguration;
    private final FeatureManager featureManager;
    private final BlockingQueue<Runnable> engineTaskQueue;
    private Engine.ShutdownReason shutdownReason;
    private Scheduler scheduler;
    private boolean shutdownComplete = false;

    public Core(Configuration<Engine> engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
        this.featureManager = new FeatureManager(engineConfiguration);
        this.engineTaskQueue = new LinkedBlockingQueue<>();
    }

    public Engine.ShutdownReason onStart() {
        FeatureBarrier.initDefaults(featureManager);
        initModules();
        scheduler = featureManager.get(Scheduler.class);
        scheduler.registerTarget(Async.ENGINE, engineTaskQueue::add);
        if (scheduler instanceof SchedulerSingleThreaded) {
            updateLoop();
        } else {
            Thread updateLoopThread = new Thread(this::updateLoop, "UpdateLoop");
            updateLoopThread.setPriority(Thread.MAX_PRIORITY);
            updateLoopThread.start();
            while (!shutdownComplete) {
                try {
                    Runnable poll = engineTaskQueue.poll(10, TimeUnit.MILLISECONDS);
                    if (poll != null) {
                        poll.run();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return shutdownReason;
    }

    private void updateLoop() {

        while (shutdownReason == null) {
            log.debug("#### NEW FRAME ####");
            scheduler.clearCaches(0);
            featureManager.onUpdate();
        }
        featureManager.onShutdown();
        shutdownComplete = true;
    }

    private void initModules() {
        featureManager.add(new OptionsManager());
        featureManager.add(new Internal());
        initFeature(Engine.Settings.Scheduler);
        initFeature(Engine.Settings.Timer);
        featureManager.add(new InputModule());
        featureManager.add(new ApplicationManager());
        featureManager.add(new GraphicsModule());
    }

    private <F extends BaseFeature.Core, S extends Feature.Settings<F>> void initFeature(Setting<Engine, S> setting) {
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

    private class Internal implements EngineManager {

        @Override
        public void shutdown(Engine.ShutdownReason reason) {
            shutdownReason = reason;
        }
    }
}
