package mylie.engine.core;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.application.Application;
import mylie.engine.core.features.async.schedulers.SchedulerSettings;
import mylie.engine.core.features.timer.NanoTimer;
import mylie.engine.core.features.timer.Timer;
import mylie.engine.graphics.GraphicsApiSettings;
import mylie.util.BuildInfo;
import mylie.util.configuration.Configuration;
import mylie.util.configuration.Setting;

@Slf4j
public class Engine {
    public static final BuildInfo buildInfo = new BuildInfo();
    public static final ShutdownReason Restart = new ShutdownReason.UserRequest("Restart");

    public interface Barriers {
        Class<? extends FeatureBarrier> FramePreparation = FeatureBarrier.FramePreparation.class;
        Class<? extends FeatureBarrier> ApplicationLogic = FeatureBarrier.AppLogic.class;
    }

    public interface Settings {
        Setting<Engine, SchedulerSettings> Scheduler = new Setting<>("Scheduler", SchedulerSettings.class, true, null);
        Setting<Engine, Timer.Settings> Timer =
                new Setting<>("Timer", Timer.Settings.class, true, new NanoTimer.Settings());
        Setting<Engine, Application> Application = new Setting<>("Application", Application.class, false, null);
        Setting<Engine, GraphicsApiSettings> GraphicsApi =
                new Setting<>("GraphicsApi", GraphicsApiSettings.class, true, null);
    }

    public static ShutdownReason start(
            Configuration<Engine> engineConfiguration, boolean handleRestart, boolean storeSettings) {
        buildInfo.logBuildInfo(log);
        log.info("Starting the engine, handle restarts: {}, store settings: {}", handleRestart, storeSettings);
        Core core;
        boolean restart;
        ShutdownReason shutdownReason;
        do {
            core = new Core(engineConfiguration);
            restart = false;
            shutdownReason = core.onStart();
            if (shutdownReason instanceof ShutdownReason.UserRequest) {
                break;
            }
            if (shutdownReason == Restart && handleRestart) {
                restart = true;
                log.info("Restarting the engine as requested");
            }
        } while (restart);
        if (shutdownReason instanceof ShutdownReason.UserRequest) {
            log.info("Engine shutdown complete, reason: {}", shutdownReason.message);
        }
        if (shutdownReason instanceof ShutdownReason.Error) {
            log.error("Engine shutdown because of {}", shutdownReason.message);
        }
        return shutdownReason;
    }

    @ToString
    public abstract static class ShutdownReason {
        final String message;

        protected ShutdownReason(String message) {
            this.message = message;
        }

        public static class UserRequest extends ShutdownReason {
            public UserRequest(String message) {
                super(message);
            }
        }

        public static class Error extends ShutdownReason {
            final Throwable throwable;

            public Error(Throwable throwable) {
                super(throwable.getMessage());
                this.throwable = throwable;
            }
        }
    }
}
