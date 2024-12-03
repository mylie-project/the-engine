package mylie.engine.core.features.async;

import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class Cache {
    @Getter
    private final String name;

    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PROTECTED)
    private GlobalCache globalCache;

    protected Cache(String name) {
        this.name = name;
    }

    public static final Cache Never = new Cache("Never") {
        @Override
        <R> Result<R> get(long frameId, int hash) {
            return null;
        }

        @Override
        <R> void set(int hash, Result<R> result) {}

        @Override
        void update(long frameId) {}
    };
    public static final Cache OneFrame = new Cache("OneFrame") {
        private final Set<Integer> hashes = new HashSet<>();

        @Override
        <R> Result<R> get(long frameId, int hash) {
            return globalCache().get(hash);
        }

        @Override
        <R> void set(int hash, Result<R> result) {
            globalCache().set(hash, result);
            hashes.add(hash);
        }

        @Override
        void update(long frameId) {
            for (Integer hash : hashes) {
                globalCache().remove(hash);
            }
        }
    };
    public static final Cache Forever = new Cache("Forever") {
        @Override
        <R> Result<R> get(long frameId, int hash) {
            return globalCache().get(hash);
        }

        @Override
        <R> void set(int hash, Result<R> result) {
            globalCache().set(hash, result);
        }

        @Override
        void update(long frameId) {}
    };
    public static final Cache FrameId = new Cache("FrameId") {
        @Override
        <R> Result<R> get(long frameId, int hash) {
            Result<R> result = globalCache().get(hash);
            if (result != null) {
                if (result.frameId() < frameId) {
                    globalCache().remove(hash);
                    result = null;
                }
            }
            return result;
        }

        @Override
        <R> void set(int hash, Result<R> result) {
            globalCache().set(hash, result);
        }

        @Override
        void update(long frameId) {}
    };

    abstract <R> Result<R> get(long frameId, int hash);

    abstract <R> void set(int hash, Result<R> result);

    abstract void update(long frameId);

    public abstract static class GlobalCache {
        public abstract <R> Result<R> get(int hash);

        public abstract <R> void set(int hash, Result<R> result);

        public abstract void remove(int hash);
    }
}
