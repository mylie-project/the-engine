package mylie.engine.core.features.async;

import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Async {
    public static Target BACKGROUND = new Target("Background");

    @Setter(AccessLevel.PACKAGE)
    private static Scheduler scheduler;

    public record Target(String name) {}

    public enum Mode {
        Direct,
        Async
    }

    private Async() {}

    public static <R, O> Result<R> async(
            Mode mode, Cache cache, Target target, long frameId, Functions.F0<R, O> function, O object) {
        int hashCode = getHashCode(function, object);
        Result<R> result = cache.get(frameId, hashCode);
        log.trace(
                "Function<{}>({}): Hash={} Cache={}",
                function.name(),
                object.getClass().getSimpleName(),
                hashCode,
                result != null);
        if (result == null) {
            result = executeTask(new Tasks.T0<>(function, object), mode, hashCode, frameId, cache, target);
        }
        return result;
    }

    public static <R, O, P1> Result<R> async(
            Mode mode, Cache cache, Target target, long frameId, Functions.F1<R, O, P1> function, O object, P1 p1) {
        int hashCode = getHashCode(function, object);
        Result<R> result = cache.get(frameId, hashCode);
        log.trace(
                "Function<{}>({} ,{}): Hash={} Cache={}",
                function.name(),
                object.getClass().getSimpleName(),
                p1.getClass().getSimpleName(),
                hashCode,
                result != null);
        if (result == null) {
            result = executeTask(new Tasks.T1<>(function, object, p1), mode, hashCode, frameId, cache, target);
        }
        return result;
    }

    private static <R> Result<R> executeTask(
            Tasks<R> tasks, Mode mode, int hashCode, long frameId, Cache cache, Target target) {
        if (mode == Mode.Direct) {
            Result.FixedResult<R> result = new Result.FixedResult<>(hashCode, frameId, tasks.execute());
            cache.set(hashCode, result);
            return result;
        } else {
            return scheduler.executeTask(tasks, hashCode, frameId, cache, target);
        }
    }

    public static void await(Result<?> result) {
        result.get();
    }

    public static <T> void await(Iterable<Result<T>> results) {
        for (Result<T> result : results) {
            await(result);
        }
    }

    @SafeVarargs
    public static <T> void await(Result<T>... results) {
        for (Result<T> result : results) {
            await(result);
        }
    }

    private static int getHashCode(Functions.Function function, Object... objects) {
        return Objects.hash(function, Arrays.hashCode(objects));
    }
}
