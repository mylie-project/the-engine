package mylie.engine.core.features.timer;

import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.FeatureManager;
import mylie.util.configuration.Configuration;

@Slf4j
public class NanoTimer extends Timer {
    long t1, t2;
    long duration;
    long frameId;

    public NanoTimer(Settings settings) {
        super(settings);
    }

    @Override
    public void onSetup(FeatureManager featureManager, Configuration<mylie.engine.core.Engine> engineConfiguration) {
        t2 = System.nanoTime();
    }

    @Override
    protected Time getNewTime() {
        t1 = t2;
        t2 = System.nanoTime();
        duration = t2 - t1;
        frameId++;
        return new DefaultTime(
                frameId,
                duration / NANOSECONDS_IN_SECOND,
                (duration / NANOSECONDS_IN_SECOND) * settings().appTimeModifier());
    }

    public static class Settings extends Timer.Settings {

        @Override
        public Timer build() {
            return new NanoTimer(this);
        }
    }
}
