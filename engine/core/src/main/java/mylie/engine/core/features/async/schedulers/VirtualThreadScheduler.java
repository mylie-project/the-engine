package mylie.engine.core.features.async.schedulers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.Feature;
import mylie.engine.core.FeatureManager;
import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Result;
import mylie.engine.core.features.async.Scheduler;
import mylie.engine.core.features.async.Tasks;
import mylie.util.configuration.Configuration;

@Slf4j
public class VirtualThreadScheduler extends SchedulerMultiThreaded
        implements Scheduler.TaskExecutor, Feature.Lifecycle.InitDestroy {
    final ExecutorService executorService;

    public VirtualThreadScheduler() {
        executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public void onSetup(FeatureManager featureManager, Configuration<mylie.engine.core.Engine> engineConfiguration) {
        super.onSetup(featureManager, engineConfiguration);
        registerTarget(Async.BACKGROUND, this);
    }

    @Override
    public <R> Result<R> execute(Tasks<R> task) {
        Future<R> future = executorService.submit(task::execute);
        return new FutureResult<>(future);
    }

    @Override
    public void onInit() {}

    @Override
    public void onDestroy() {
        executorService.shutdown();
    }

    @RequiredArgsConstructor
    static class FutureResult<R> extends Result<R> {
        final Future<R> future;

        @Override
        public R get() {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isDone() {
            return future.isDone();
        }
    }
}
