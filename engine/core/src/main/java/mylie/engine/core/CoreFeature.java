package mylie.engine.core;

import mylie.engine.core.features.async.Async;

public non-sealed class CoreFeature extends BaseFeature implements BaseFeature.Core {
    public CoreFeature(Class<? extends Feature> featureType) {
        super(featureType);
    }

    @Override
    protected <T extends BaseFeature> void runAfter(Class<T> featureClass) {
        super.runAfter(featureClass);
    }

    @Override
    protected <T extends BaseFeature> void runBefore(Class<T> featureClass) {
        super.runBefore(featureClass);
    }

    @Override
    public CoreFeature executionMode(Async.Mode mode) {
        super.executionMode(mode);
        return this;
    }

    @Override
    public CoreFeature executionTarget(Async.Target target) {
        super.executionTarget(target);
        return this;
    }
}
