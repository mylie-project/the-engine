package mylie.engine.core;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public sealed class AppFeature extends BaseFeature implements BaseFeature.App
        permits AppFeature.Sequential, AppFeature.Async {
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

    public static non-sealed class Sequential extends AppFeature {

        public Sequential(Class<? extends Feature> featureType) {
            super(featureType);
        }
    }

    public static non-sealed class Async extends AppFeature {

        public Async(Class<? extends Feature> featureType) {
            super(featureType);
            executionMode(mylie.engine.core.features.async.Async.Mode.Async);
        }

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
