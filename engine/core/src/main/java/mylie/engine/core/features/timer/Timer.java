package mylie.engine.core.features.timer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.Feature;

import java.util.concurrent.TimeUnit;

@Slf4j
@Getter(AccessLevel.PROTECTED)
public abstract class Timer implements Feature.Engine,Feature.Lifecycle.Update {
    protected final static double NANOSECONDS_IN_SECOND = TimeUnit.SECONDS.toNanos(1);
    private Time time;
    private final Settings settings;
    private float logInterval;
    private long count;
    protected Timer(Settings settings) {
        this.settings = settings;
    }

    public void setAppTimeModifier(double modifier){
        this.settings.appTimeModifier(modifier);
    }

    @Override
    public void onUpdate() {
        time = getNewTime();
        logInterval+= (float) time.delta();
        count++;
        if(logInterval>settings().fpsLogInterval()){

            log.info("FPS: {}",count);
            count=0;
            logInterval=0;
        }
    }

    @Override
    public Class<? extends Feature> featureType(){
        return Timer.class;
    }

    protected abstract Time getNewTime();

    public interface Time{
        long frameId();
        double delta();
        double deltaApp();
    }

    record DefaultTime(long frameId,double delta,double deltaApp) implements Time{}

    @Data
    public abstract static class Settings implements Feature.Settings<Timer> {
        private double appTimeModifier=1;
        private float fpsLogInterval=1;
    }
}
