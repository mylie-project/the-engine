package mylie.engine.core.features.timer;

import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.*;
import mylie.util.configuration.Configuration;

@Slf4j
@Getter(AccessLevel.PROTECTED)
public abstract class Timer extends CoreFeature implements BaseFeature.Core, Lifecycle.Update {
    protected static final double NANOSECONDS_IN_SECOND = TimeUnit.SECONDS.toNanos(1);

    @Getter(AccessLevel.PUBLIC)
    private Time time;

    private final Settings settings;
    private float logInterval;
    private long count;

    protected Timer(Settings settings) {
        super(Timer.class);
        this.settings = settings;
    }

    @Override
    protected void onSetup(FeatureManager featureManager, Configuration<mylie.engine.core.Engine> engineConfiguration) {
        super.onSetup(featureManager, engineConfiguration);
        time = new DefaultTime(-1, 0, 0);
        runBefore(mylie.engine.core.Engine.Barriers.FramePreparation);
    }

    public void setAppTimeModifier(double modifier) {
        this.settings.appTimeModifier(modifier);
    }

    @Override
    public void onUpdate() {
        time = getNewTime();
        logInterval += (float) time.delta();
        count++;
        if (logInterval > settings().fpsLogInterval()) {
            log.info("FPS: {}", count);
            count = 0;
            logInterval = 0;
        }
    }

    protected abstract Time getNewTime();

    public interface Time {
        long frameId();

        double delta();

        double deltaApp();
    }

    record DefaultTime(long frameId, double delta, double deltaApp) implements Time {}

    @Data
    public abstract static class Settings implements Feature.Settings<Timer> {
        private double appTimeModifier = 1;
        private float fpsLogInterval = 1;
    }
}
