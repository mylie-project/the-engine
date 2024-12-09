package mylie.engine.core.features.async.schedulers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import mylie.engine.core.features.async.*;

public class SchedulerMultiThreaded extends Scheduler {

    @Override
    public void registerTarget(Async.Target target, Consumer<Runnable> consumer) {
        registerTarget(target, new CallableExecutor(consumer, target));
    }

    @Override
    public FeatureThread createFeatureThread(Async.Target target, BlockingQueue<Runnable> queue) {
        return new BaseFeatureThread(target, queue);
    }

    @Override
    public void submitRunnable(Runnable runnable, Async.Target target) {
        TaskExecutor taskExecutor = targets().get(target);
        if (taskExecutor instanceof CallableExecutor callableExecutor) {
            callableExecutor.consumer().accept(runnable);
        }
    }

    record CallableExecutor(Consumer<Runnable> consumer, Async.Target target) implements TaskExecutor {
        @Override
        public <R> Result<R> execute(Tasks<R> task) {
            CompletableFutureResult<R> result = new CompletableFutureResult<>(task, target);
            consumer.accept(result::execute);
            return result;
        }
    }

    static class CompletableFutureResult<R> extends Result<R> {
        final Tasks<R> task;
        final CompletableFuture<R> future;
        final AtomicBoolean running;
        final Async.Target target;

        CompletableFutureResult(Tasks<R> task, Async.Target target) {
            this.task = task;
            this.future = new CompletableFuture<>();
            this.target = target;
            this.running = new AtomicBoolean(false);
        }

        private void execute() {
            future.complete(task.execute());
        }

        @Override
        public R get() {
            try {

                if (!isDone() && task != null) {
                    if (Thread.currentThread().getName().equals(target.name())) {
                        future.complete(task.execute());
                    }
                }
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

    static class BaseFeatureThread implements FeatureThread {
        final Async.Target target;
        final BlockingQueue<Runnable> taskQueue;
        final Thread thread;
        volatile boolean running;

        BaseFeatureThread(Async.Target target, BlockingQueue<Runnable> taskQueue) {
            this.target = target;
            this.taskQueue = taskQueue;
            this.thread = new Thread(this::loop, target.name());
        }

        private void loop() {
            while (running) {
                try {
                    Runnable poll = taskQueue.poll(10, TimeUnit.MILLISECONDS);
                    if (poll != null) {
                        poll.run();
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void start() {
            running = true;
            thread.start();
        }

        @Override
        public void stop() {
            running = false;
        }
    }
}
