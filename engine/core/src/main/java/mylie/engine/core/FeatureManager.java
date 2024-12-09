package mylie.engine.core;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.features.async.*;
import mylie.engine.core.features.timer.Timer;
import mylie.util.configuration.Configuration;

@Slf4j
@Getter(AccessLevel.PACKAGE)
public class FeatureManager {
    private final Configuration<Engine> engineConfiguration;
    private final List<Feature> featureList;

    public FeatureManager(Configuration<Engine> engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
        featureList = new CopyOnWriteArrayList<>();
    }

    public <F extends Feature> void add(F feature) {
        featureList.add(feature);
        if (feature instanceof BaseFeature baseFeature) {
            baseFeature.onSetup(this, engineConfiguration);
        }
    }

    public <F extends Feature> F get(Class<F> type) {
        for (Feature feature : featureList) {
            if (type.isAssignableFrom(feature.getClass())) {
                return type.cast(feature);
            }
        }
        return null;
    }

    public <T extends Feature> void remove(T feature) {
        featureList.remove(feature);
    }

    public void onUpdate() {
        Set<Result<Boolean>> results = new HashSet<>();
        for (Feature feature : featureList) {
            if (feature instanceof FeatureBarrier baseFeature) {
                results.add(baseFeature.update());
            }
        }
        Async.await(results);
    }

    public void onShutdown() {
        Set<Result<Boolean>> results = new HashSet<>();
        for (Feature feature : featureList) {
            if (feature instanceof BaseFeature baseFeature) {
                results.add(baseFeature.destroy());
            }
        }
        List<Result<Boolean>> list = results.stream().filter(Objects::nonNull).toList();
        Async.await(list);
    }

    public Result<Boolean> updateFeature(Async.Mode mode, BaseFeature feature) {
        Timer.Time time = get(Timer.class).time();
        return feature.update();
        /*return Async.async(
        mode==null?feature.executionMode():mode,
        feature.executionCache(),
        feature.executionTarget(),
        time.frameId(),
        BaseFeature.updateFunction,
        feature,
        time);*/
    }
}
