package mylie.engine.core.features.async;

import java.util.*;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Async {
    public static Target BACKGROUND = new Target("Background");
    public static Target ENGINE = new Target("Engine");
    public static Target APPLICATION = new Target("Application");

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

    public static <R, O, P1, P2> Result<R> async(
            Mode mode,
            Cache cache,
            Target target,
            long frameId,
            Functions.F2<R, O, P1, P2> function,
            O object,
            P1 p1,
            P2 p2) {
        int hashCode = getHashCode(function, object);
        Result<R> result = cache.get(frameId, hashCode);
        log.trace(
                "Function<{}>({},{},{}): Hash={} Cache={}",
                function.name(),
                object.getClass().getSimpleName(),
                p1.getClass().getSimpleName(),
                p2.getClass().getSimpleName(),
                hashCode,
                result != null);
        if (result == null) {
            result = executeTask(new Tasks.T2<>(function, object, p1, p2), mode, hashCode, frameId, cache, target);
        }
        return result;
    }

    public static <R, T> Iterable<Result<R>> async(
            Mode mode,
            Cache cache,
            Target target,
            int frameId,
            List<?> objects,
            Class<T> targetType,
            Functions.F0<R, T> function) {
        Set<Result<R>> results = new HashSet<>();
        Mode currentMode = mode;
        for (Object object : objects) {
            currentMode = mode == Mode.Direct && canExecuteDirect(target) ? Mode.Direct : Mode.Async;
            if (targetType.isAssignableFrom(object.getClass())) {
                T object1 = targetType.cast(object);
                results.add(async(currentMode, cache, target, frameId, function, object1));
            }
        }
        return results;
    }

    public static <R, T> Iterable<Result<R>> async(
            Mode mode,
            Cache cache,
            Function<T, Target> target,
            int frameId,
            List<?> objects,
            Class<T> targetType,
            Functions.F0<R, T> function) {
        Set<Result<R>> results = new HashSet<>();
        Mode currentMode = mode;
        Target currentTarget = null;
        for (Object object : objects) {
            if (targetType.isAssignableFrom(object.getClass())) {
                T object1 = targetType.cast(object);
                currentTarget = target.apply(object1);
                currentMode = mode == Mode.Direct && canExecuteDirect(currentTarget) ? Mode.Direct : Mode.Async;
                results.add(async(currentMode, cache, currentTarget, frameId, function, object1));
            }
        }
        return results;
    }

    private static <R> Result<R> executeTask(
            Tasks<R> tasks, Mode mode, int hashCode, long frameId, Cache cache, Target target) {
        if (mode == Mode.Direct) {
            // To avoid a dependency loop we have to set set the cache result before executing the task.
            // Not sure yet if this leads to possible null results..
            Result.FixedResult<R> result = new Result.FixedResult<>(hashCode, frameId, tasks.execute());
            cache.set(hashCode, result);
            // result.value = tasks.execute();
            return result;
        } else {
            return scheduler.executeTask(tasks, hashCode, frameId, cache, target);
        }
    }

    public static <T> T await(Result<T> result) {
        return result.get();
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

    private static boolean canExecuteDirect(Target target) {
        if (target == BACKGROUND) return true;
        return Thread.currentThread().getName().equals(target.name());
    }
}
