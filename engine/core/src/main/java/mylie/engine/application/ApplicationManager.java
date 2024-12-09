package mylie.engine.application;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.*;
import mylie.engine.core.Engine.Barriers;
import mylie.engine.core.features.async.*;
import mylie.engine.core.features.timer.Timer;
import mylie.engine.input.InputModule;
import mylie.util.configuration.Configuration;

@Slf4j
public class ApplicationManager extends CoreFeature implements Lifecycle.Update.Timed, Lifecycle.InitDestroy {
    private boolean applicationInitialized = false;
    private mylie.engine.application.Application application;
    private mylie.engine.application.Application.Manager appFeatureManager;
    private BlockingQueue<Runnable> applicationQueue;
    private FeatureThread featureThread;

    public ApplicationManager() {
        super(ApplicationManager.class);
    }

    @Override
    protected void onSetup(FeatureManager featureManager, Configuration<mylie.engine.core.Engine> engineConfiguration) {
        super.onSetup(featureManager, engineConfiguration);
        application = engineConfiguration.get(mylie.engine.core.Engine.Settings.Application);
        appFeatureManager = new mylie.engine.application.Application.Manager(featureManager);
        applicationQueue = new LinkedBlockingQueue<>();
        Scheduler scheduler = get(Scheduler.class);
        scheduler.registerTarget(Async.APPLICATION, applicationQueue::add);
        featureThread = scheduler.createFeatureThread(Async.APPLICATION, applicationQueue);
        featureThread.start();
        runBefore(Barriers.ApplicationLogic);
        runAfter(Barriers.FramePreparation);
        runAfter(InputModule.class);
    }

    @Override
    public void onUpdate(Timer.Time time) {
        if (!applicationInitialized) {
            applicationInitialized = true;
            Async.async(
                    Async.Mode.Async,
                    Cache.OneFrame,
                    Async.APPLICATION,
                    time.frameId(),
                    initApplication,
                    application,
                    appFeatureManager);
        }
        Async.await(Async.async(
                Async.Mode.Async,
                Cache.OneFrame,
                Async.APPLICATION,
                time.frameId(),
                updateApplication,
                this,
                application,
                time));
    }

    @Override
    public void onInit() {}

    @Override
    public void onDestroy() {
        Async.await(
                Async.async(Async.Mode.Async, Cache.OneFrame, Async.APPLICATION, -1, destroyApplication, application));
        featureThread.stop();
    }

    private static final Functions.F1<
                    Boolean, mylie.engine.application.Application, mylie.engine.application.Application.Manager>
            initApplication = new Functions.F1<>("InitApplication") {
                @Override
                protected Boolean run(
                        mylie.engine.application.Application application,
                        mylie.engine.application.Application.Manager featureManager) {
                    application.onInit(featureManager);
                    return true;
                }
            };

    private static final Functions.F0<Boolean, mylie.engine.application.Application> destroyApplication =
            new Functions.F0<>("DestroyApplication") {
                @Override
                protected Boolean run(mylie.engine.application.Application application) {
                    application.onDestroy();
                    return true;
                }
            };

    private static final Functions.F2<Boolean, ApplicationManager, mylie.engine.application.Application, Timer.Time>
            updateApplication = new Functions.F2<>("UpdateApplication") {
                @Override
                protected Boolean run(
                        ApplicationManager appManager,
                        mylie.engine.application.Application application,
                        Timer.Time time) {
                    for (Feature feature : appManager.features()) {
                        if (feature instanceof AppFeature.Sequential sequential) {
                            Async.await(appManager.featureManager().updateFeature(Async.Mode.Direct, sequential));
                        }
                    }
                    application.onUpdate(time);
                    return true;
                }
            };
}
