package mylie.engine.core.features.async.caches;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMapCache extends MapCache {
    public ConcurrentMapCache() {
        super(new ConcurrentHashMap<>());
    }
}
