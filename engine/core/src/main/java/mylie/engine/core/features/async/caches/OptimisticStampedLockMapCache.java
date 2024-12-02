package mylie.engine.core.features.async.caches;

import java.util.HashMap;
import java.util.concurrent.locks.StampedLock;
import mylie.engine.core.features.async.Result;

@SuppressWarnings("unused")
public class OptimisticStampedLockMapCache extends MapCache {
    private final StampedLock lock = new StampedLock();

    public OptimisticStampedLockMapCache() {
        super(new HashMap<>());
    }

    @Override
    public <T> Result<T> get(int hash) {
        long stamp = lock.tryOptimisticRead();
        Result<T> objectResult = super.get(hash);
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                // noinspection ReassignedVariable
                objectResult = super.get(hash);
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return objectResult;
    }

    @Override
    public <T> void set(int hash, Result<T> result) {
        long stamp = lock.writeLock();
        super.set(hash, result);
        lock.unlockWrite(stamp);
    }

    @Override
    public void remove(int hash) {
        long stamp = lock.writeLock();
        super.remove(hash);
        lock.unlockWrite(stamp);
    }
}
