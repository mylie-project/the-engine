package mylie.engine.core.features.async.caches;

import java.util.Map;
import mylie.engine.core.features.async.Cache;
import mylie.engine.core.features.async.Result;

public abstract class MapCache extends Cache.GlobalCache {
    private final Map<Integer, Result<?>> cache;

    public MapCache(Map<Integer, Result<?>> cache) {
        this.cache = cache;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Result<T> get(int hash) {
        return (Result<T>) cache.get(hash);
    }

    @Override
    public void remove(int hash) {
        cache.remove(hash);
    }

    @Override
    public <R> void set(int hash, Result<R> result) {
        cache.put(hash, result);
    }
}
