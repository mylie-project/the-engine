package mylie.engine.core.features.async;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.core.features.async.caches.ConcurrentMapCache;
import mylie.engine.core.Feature;
import mylie.engine.core.FeatureManager;
import mylie.util.configuration.Configuration;

@Slf4j
public abstract class Scheduler implements Feature.Engine {
    private final Cache.GlobalCache globalCache;
    private final Map<Async.Target, TaskExecutor> targets;
    private final Set<Cache> caches;
    @Getter
    private final Class<? extends Feature> featureType = Scheduler.class;
    public Scheduler() {
        this.targets = new HashMap<>();
        this.globalCache = new ConcurrentMapCache();
        this.caches = new HashSet<>();
    }

    <R> Result<R> executeTask(Tasks<R> task, int hashCode, long frameId, Cache cache, Async.Target target) {
        TaskExecutor executor = targets.get(target);
        Result<R> result;
        if (executor != null) {
            result = executor.execute(task);
            if (result != null) {
                result.frameId(frameId);
                cache.set(hashCode, result);
                return result;
            }
            log.error("Task factory registered for Target<{}> failed", target.name());
        }
        log.warn("Target<{}> not registered", target.name());
        return null;
    }

    public void clearCaches(long frameId) {
        for (Cache cache : caches) {
            cache.update(frameId);
        }
    }

    protected void registerTarget(Async.Target target, TaskExecutor executor) {
        log.trace("Target<{}> registered", target.name());
        this.targets.put(target, executor);
    }

    public abstract void registerTarget(Async.Target target, Consumer<Runnable> consumer);

    public void registerCaches(Cache... caches) {
        for (Cache cache : caches) {
            registerCache(cache);
        }
    }

    public void registerCache(Cache cache) {
        log.trace("Cache<{}> registered", cache.name());
        cache.globalCache(globalCache);
        this.caches.add(cache);
    }

    @Override
    public void onSetup(FeatureManager featureManager, Configuration<mylie.engine.core.Engine> engineConfiguration) {
        registerCaches(Cache.Never, Cache.Forever, Cache.OneFrame, Cache.FrameId);
        Async.scheduler(this);
    }

    public interface TaskExecutor {
        <R> Result<R> execute(Tasks<R> task);
    }
}
