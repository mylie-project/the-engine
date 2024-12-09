package mylie.engine.core;

import lombok.extern.slf4j.Slf4j;
import mylie.util.configuration.Configuration;

@Slf4j
public non-sealed class AppFeature extends BaseFeature implements BaseFeature.App {
    public AppFeature(Class<? extends Feature> featureType) {
        super(featureType);
    }

    protected <T extends App> T getFeature(Class<T> type) {
        return super.get(type);
    }

    protected <T extends App> BaseFeature addFeature(T feature) {
        return super.add(feature);
    }

    protected <T extends App> void removeFeature(T feature) {
        App toRemove = get(feature.getClass());
        if (toRemove instanceof CoreFeature) {
            log.warn("Not allowed to remove EngineFeature {}", toRemove.getClass());
            return;
        }
        super.remove(feature);
    }

    public abstract static class Sequential extends AppFeature {

        public Sequential(Class<? extends Feature> featureType) {
            super(featureType);
            executionMode(mylie.engine.core.features.async.Async.Mode.Direct);
        }
    }

    public abstract static class Async extends AppFeature {

        public Async(Class<? extends Feature> featureType) {
            super(featureType);
            executionMode(mylie.engine.core.features.async.Async.Mode.Async);
            executionTarget(mylie.engine.core.features.async.Async.BACKGROUND);
        }

        @Override
        protected void onSetup(FeatureManager featureManager, Configuration<Engine> engineConfiguration) {
            super.onSetup(featureManager, engineConfiguration);
            this.onSetupDependencies();
        }

        protected abstract void onSetupDependencies();

        @Override
        protected <T extends BaseFeature> void runAfter(Class<T> featureClass) {
            super.runAfter(featureClass);
        }

        @Override
        protected <T extends BaseFeature> void runBefore(Class<T> featureClass) {
            super.runBefore(featureClass);
        }
    }
}
