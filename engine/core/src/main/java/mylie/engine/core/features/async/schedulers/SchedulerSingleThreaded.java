package mylie.engine.core.features.async.schedulers;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.FeatureManager;
import mylie.engine.core.features.async.Async;
import mylie.engine.core.features.async.Result;
import mylie.engine.core.features.async.Scheduler;
import mylie.engine.core.features.async.Tasks;
import mylie.util.configuration.Configuration;

@Slf4j
public class SchedulerSingleThreaded extends Scheduler implements Scheduler.TaskExecutor {
    public SchedulerSingleThreaded() {}

    @Override
    public void registerTarget(Async.Target target, Consumer<Runnable> consumer) {
        registerTarget(target, this);
    }

    @Override
    public <R> Result<R> execute(Tasks<R> task) {
        return new AsyncResult<>(task.execute());
    }

    @Override
    public void onSetup(FeatureManager featureManager, Configuration<mylie.engine.core.Engine> engineConfiguration) {
        super.onSetup(featureManager, engineConfiguration);
        registerTarget(Async.BACKGROUND, this);
    }

    private static class AsyncResult<R> extends Result<R> {
        public AsyncResult(R result) {
            super();
            this.result = result;
        }

        R result;

        @Override
        public R get() {
            return result;
        }

        @Override
        public boolean isDone() {
            return true;
        }
    }
}
