package mylie.engine.application;

import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.BaseFeature;
import mylie.engine.core.CoreFeature;
import mylie.engine.core.FeatureManager;
import mylie.engine.core.features.timer.Timer;

public interface Application {
    void onInit(Manager featureManager);

    void onUpdate(Timer.Time time);

    void onDestroy();

    @Slf4j
    class Manager {
        private final FeatureManager featureManager;

        public Manager(FeatureManager featureManager) {
            this.featureManager = featureManager;
        }

        public <T extends BaseFeature.App> T getFeature(Class<T> type) {
            return featureManager.get(type);
        }

        public <T extends BaseFeature.App> Manager addFeature(T feature) {
            featureManager.add(feature);
            return this;
        }

        public <T extends BaseFeature.App> void removeFeature(T feature) {
            BaseFeature.App toRemove = featureManager.get(feature.getClass());
            if (toRemove instanceof CoreFeature) {
                log.warn("Not allowed to remove EngineFeature {}", toRemove.getClass());
                return;
            }
            featureManager.remove(feature);
        }
    }
}
