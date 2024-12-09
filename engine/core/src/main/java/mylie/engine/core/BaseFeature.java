package mylie.engine.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
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

@Slf4j
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
public abstract sealed class BaseFeature implements Feature permits CoreFeature, AppFeature {
    private final Class<? extends Feature> featureType;
    private boolean initialized = false, requestEnabled = false, alreadyEnabled = false;
    private List<BaseFeature> dependencies;
    private FeatureManager featureManager;
    private Async.Target executionTarget;
    private Async.Mode executionMode;
    private Cache executionCache;

    public BaseFeature(Class<? extends Feature> featureType) {
        this.featureType = featureType;
        dependencies = new CopyOnWriteArrayList<>();
        executionTarget(Async.BACKGROUND);
        executionMode(null);
        executionCache(Cache.OneFrame);
    }

    protected void onSetup(FeatureManager featureManager, Configuration<mylie.engine.core.Engine> engineConfiguration) {
        featureManager(featureManager);
    }

    Result<Boolean> update() {
        Timer.Time time = get(Timer.class).time();
        return Async.async(executionMode, executionCache, executionTarget, time.frameId(), updateFunction, this, time);
    }

    public Result<Boolean> destroy() {
        Timer.Time time = get(Timer.class).time();
        if (this instanceof Lifecycle.InitDestroy initDestroyFeature) {
            return Async.async(executionMode, Cache.Never, executionTarget, time.frameId(), destroyFunction, this);
        }
        return null;
    }

    <T extends BaseFeature> void runAfter(Class<T> featureClass) {
        T feature = get(featureClass);
        dependencies.add(feature);
    }

    <T extends BaseFeature> void runBefore(Class<T> featureClass) {
        T feature = get(featureClass);
        feature.dependencies().add(this);
    }

    protected <T extends Feature> T get(Class<T> type) {
        return featureManager().get(type);
    }

    protected <T extends Feature> BaseFeature add(T feature) {
        featureManager.add(feature);
        return this;
    }

    protected <T extends Feature> void remove(T feature) {
        featureManager.remove(feature);
    }

    public interface Core extends Feature {}

    public interface App extends Feature {}

    private static void waitForDependencies(BaseFeature baseFeature, Timer.Time time) {
        Set<Result<Boolean>> results = new HashSet<>();
        for (BaseFeature dependency : baseFeature.dependencies()) {
            results.add(dependency.update());
        }
        Async.await(results);
    }

    private static Functions.F0<Boolean, BaseFeature> destroyFunction = new Functions.F0<>("FeatureDestroyFunction") {
        @Override
        protected Boolean run(BaseFeature feature) {
            if (feature instanceof Lifecycle.InitDestroy initDestroyFeature) {
                log.trace("Feature<{}>.onDestroy", feature.featureType().getSimpleName());
                initDestroyFeature.onDestroy();
            }
            return true;
        }
    };

    static Functions.F1<Boolean, BaseFeature, Timer.Time> updateFunction = new Functions.F1<>("FeatureUpdateFunction") {
        @Override
        public Boolean run(BaseFeature feature, Timer.Time time) {
            if (!feature.initialized()) {
                feature.initialized(true);
                if (feature instanceof Lifecycle.InitDestroy initDestroyFeature) {
                    log.trace("Feature<{}>.onInit()", feature.featureType().getSimpleName());
                    initDestroyFeature.onInit();
                }
            }
            waitForDependencies(feature, time);
            if (feature instanceof Lifecycle.EnableDisable enableDisableFeature) {
                if (feature.requestEnabled() != feature.alreadyEnabled()) {
                    if (feature.requestEnabled()) {
                        log.trace(
                                "Feature<{}>.onEnable()", feature.featureType().getSimpleName());
                        enableDisableFeature.onEnable();
                        feature.alreadyEnabled(true);
                    } else {
                        log.trace(
                                "Feature<{}>.onDisable()", feature.featureType().getSimpleName());
                        enableDisableFeature.onDisable();
                        feature.alreadyEnabled(false);
                    }
                    // Skip update if disabled
                    if (!feature.requestEnabled()) {
                        return true;
                    }
                }
            }

            if (feature instanceof Lifecycle.Update updatableFeature) {
                log.trace("Feature<{}>.onUpdate()", feature.featureType().getSimpleName());
                updatableFeature.onUpdate();
            } else if (feature instanceof Lifecycle.Update.Timed timedUpdatableFeature) {
                log.trace(
                        "Feature<{}>.onUpdate({})",
                        feature.featureType().getSimpleName(),
                        time.getClass().getSimpleName());
                timedUpdatableFeature.onUpdate(time);
            }
            return true;
        }
    };
}
