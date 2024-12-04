package mylie.engine.application;

import lombok.experimental.Delegate;

public abstract class BaseApplication implements Application {
    @Delegate
    private Manager featureManager;

    @Override
    public void onInit(Manager featureManager) {
        this.featureManager = featureManager;
        this.onInit();
    }

    protected abstract void onInit();
}
