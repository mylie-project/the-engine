package mylie.engine.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Cache;
import mylie.engine.core.features.async.Functions;
import mylie.engine.core.features.async.Result;
import mylie.engine.core.features.timer.Timer;
import mylie.util.configuration.Configuration;

import java.util.Objects;

@Slf4j
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
public abstract class BaseFeature implements Feature {
    private final Class<? extends Feature> featureType;
    private boolean initialized = false, requestEnabled = false, alreadyEnabled = false;
    private FeatureManager featureManager;
    private Async.Target executionTarget;
    private Async.Mode executionMode;
    private Cache executionCache;

    public BaseFeature(Class<? extends Feature> featureType) {
        this.featureType = featureType;
        executionTarget(Async.BACKGROUND);
        executionMode(Async.Mode.Direct);
        executionCache(Cache.OneFrame);
    }

    Result<Boolean> update() {
        Timer.Time time = get(Timer.class).time();
        return Async.async(executionMode, executionCache, executionTarget, time.frameId(), updateFunction, this, time);
    }

    protected void onSetup(FeatureManager featureManager, Configuration<mylie.engine.core.Engine> engineConfiguration) {
        featureManager(featureManager);
    }

    protected <T extends Feature> T get(Class<T> type) {
        return featureManager().get(type);
    }

    private static Functions.F1<Boolean, BaseFeature, Timer.Time> updateFunction =
            new Functions.F1<>("FeatureUpdateFunction") {
                @Override
                public Boolean run(BaseFeature feature, Timer.Time time) {
                    if (!feature.initialized()) {
                        feature.initialized(true);
                        if (feature instanceof Feature.Lifecycle.InitDestroy initDestroyFeature) {
                            log.trace(
                                    "Feature<{}>.onInit()",
                                    feature.featureType().getSimpleName());
                            initDestroyFeature.onInit();
                        }
                    }
                    // waitForDependencies(baseFeature, time);
                    if (feature instanceof Feature.Lifecycle.EnableDisable enableDisableFeature) {
                        if (feature.requestEnabled() != feature.alreadyEnabled()) {
                            if (feature.requestEnabled()) {
                                log.trace(
                                        "Feature<{}>.onEnable()",
                                        feature.featureType().getSimpleName());
                                enableDisableFeature.onEnable();
                                feature.alreadyEnabled(true);
                            } else {
                                log.trace(
                                        "Feature<{}>.onDisable()",
                                        feature.featureType().getSimpleName());
                                enableDisableFeature.onDisable();
                                feature.alreadyEnabled(false);
                            }
                            // Skip update if disabled
                            if (!feature.requestEnabled()) {
                                return true;
                            }
                        }
                    }

                    if (feature instanceof Feature.Lifecycle.Update updatableFeature) {
                        log.trace(
                                "Feature<{}>.onUpdate()", feature.featureType().getSimpleName());
                        updatableFeature.onUpdate();
                    } else if (feature instanceof Lifecycle.Update.Timed timedUpdatableFeature) {
                        log.trace(
                                "Feature<{}>.onUpdate(time)",
                                feature.featureType().getSimpleName());
                        timedUpdatableFeature.onUpdate(time);
                    }
                    return true;
                }
            };


}
