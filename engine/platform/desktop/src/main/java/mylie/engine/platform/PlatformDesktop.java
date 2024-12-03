package mylie.engine.platform;

import mylie.engine.core.Engine;
import mylie.engine.core.Platform;
import mylie.engine.core.features.async.schedulers.VirtualThreadSchedulerSettings;
import mylie.util.configuration.Configuration;

public class PlatformDesktop extends Platform {
    @Override
    protected void initializePlatformConfigurations(Configuration<Engine> engineConfiguration) {
        engineConfiguration.setIfNotExists(Engine.Settings.Scheduler, new VirtualThreadSchedulerSettings());
    }
}
