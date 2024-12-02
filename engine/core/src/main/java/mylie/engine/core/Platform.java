package mylie.engine.core;

import mylie.util.configuration.Configuration;

public abstract class Platform {
    public Configuration<Engine> initialize() {
        Configuration<Engine> engineConfiguration = new Configuration<>();
        initializePlatformConfigurations(engineConfiguration);
        return engineConfiguration;
    }

    protected abstract void initializePlatformConfigurations(Configuration<Engine> engineConfiguration);
}
