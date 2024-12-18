package mylie.engine.core.features.async;

import static mylie.engine.core.features.async.Async.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import mylie.engine.core.features.async.schedulers.SchedulerSingleThreaded;
import mylie.engine.core.features.async.schedulers.VirtualThreadScheduler;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class SchedulerTest {

    static Stream<Scheduler> schedulerProvider() {

        return Stream.of(init(new SchedulerSingleThreaded()), init(new VirtualThreadScheduler()));
    }

    public static Scheduler init(Scheduler scheduler) {
        scheduler.onSetup(null, null);
        return scheduler;
    }

    @ParameterizedTest
    @MethodSource("schedulerProvider")
    void shouldExecuteTaskWithoutCache(Scheduler scheduler) {
        scheduler(scheduler);
        AtomicInteger integer = new AtomicInteger(0);

        Async.await(
                async(Mode.Async, Cache.Never, BACKGROUND, 0, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.Never, BACKGROUND, 0, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.Never, BACKGROUND, 0, atomicIntegerIncrease, integer));
        assertEquals(3, integer.get());
        Async.await(async(Mode.Async, Cache.Never, BACKGROUND, 0, atomicIntegerDecrease, integer));
        assertEquals(2, integer.get());
    }

    @ParameterizedTest
    @MethodSource("schedulerProvider")
    void shouldExecuteTaskWithCache(Scheduler scheduler) {
        scheduler(scheduler);
        AtomicInteger integer = new AtomicInteger(0);
        Async.await(
                async(Mode.Async, Cache.OneFrame, BACKGROUND, 0, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.OneFrame, BACKGROUND, 0, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.OneFrame, BACKGROUND, 0, atomicIntegerIncrease, integer));
        assertEquals(1, integer.get());
    }

    @ParameterizedTest
    @MethodSource("schedulerProvider")
    void shouldExecuteMixedCachePolicies(Scheduler scheduler) {
        scheduler(scheduler);
        AtomicInteger integer = new AtomicInteger(0);
        Async.await(
                async(Mode.Async, Cache.Never, BACKGROUND, 0, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.OneFrame, BACKGROUND, 0, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.Never, BACKGROUND, 0, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.OneFrame, BACKGROUND, 0, atomicIntegerIncrease, integer));
        assertEquals(3, integer.get());
    }

    @ParameterizedTest
    @MethodSource("schedulerProvider")
    void shouldExecuteTaskWithDynamicCachingAndFrameId(Scheduler scheduler) {
        scheduler(scheduler);
        AtomicInteger integer = new AtomicInteger(0);
        Async.await(
                async(Mode.Async, Cache.FrameId, BACKGROUND, 1, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.FrameId, BACKGROUND, 1, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.FrameId, BACKGROUND, 2, atomicIntegerIncrease, integer));
        assertEquals(2, integer.get());
    }

    @ParameterizedTest
    @MethodSource("schedulerProvider")
    void shouldExecuteTaskWithDynamicCachingAndFrameId2(Scheduler scheduler) {
        scheduler(scheduler);
        AtomicInteger integer = new AtomicInteger(0);
        Async.await(
                async(Mode.Async, Cache.FrameId, BACKGROUND, 1, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.FrameId, BACKGROUND, 2, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.FrameId, BACKGROUND, 2, atomicIntegerIncrease, integer));
        assertEquals(2, integer.get());
    }

    @ParameterizedTest
    @MethodSource("schedulerProvider")
    void shouldExecuteTaskWithDynamicCachingAndFrameId3(Scheduler scheduler) {
        scheduler(scheduler);
        AtomicInteger integer = new AtomicInteger(0);
        Async.await(
                async(Mode.Async, Cache.FrameId, BACKGROUND, 2, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.FrameId, BACKGROUND, 1, atomicIntegerIncrease, integer),
                async(Mode.Async, Cache.FrameId, BACKGROUND, 2, atomicIntegerIncrease, integer));
        assertEquals(1, integer.get());
    }

    private static final Target q1 = new Target("1");
    private static final Target q2 = new Target("2");
    private static final Queue<Runnable> queue1 = new LinkedBlockingQueue<>();
    private static final Queue<Runnable> queue2 = new LinkedBlockingQueue<>();

    private static void setupQueues(Scheduler scheduler) {
        scheduler.registerTarget(q1, queue1::add);
        scheduler.registerTarget(q2, queue2::add);
        queue1.clear();
        queue2.clear();
    }

    private static final Functions.F0<Boolean, AtomicInteger> atomicIntegerIncrease =
            new Functions.F0<>("AtomicIntegerIncrease") {
                @Override
                public Boolean run(AtomicInteger o) {
                    o.incrementAndGet();
                    return true;
                }
            };

    private static final Functions.F0<Boolean, AtomicInteger> atomicIntegerDecrease =
            new Functions.F0<>("AtomicIntegerDecrease") {
                @Override
                public Boolean run(AtomicInteger o) {
                    o.decrementAndGet();
                    return true;
                }
            };
}
