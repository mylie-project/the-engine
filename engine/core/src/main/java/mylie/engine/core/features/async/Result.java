package mylie.engine.core.features.async;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
public abstract class Result<R> {
    private int hash;
    private long frameId;

    public abstract R get();

    public abstract boolean isDone();

    @Setter(AccessLevel.PACKAGE)
    static class FixedResult<R> extends Result<R> {
        R value;

        public FixedResult(int hashCode, long frameId, R value) {
            this.value = value;
            hash(hashCode);
            frameId(frameId);
        }

        @Override
        public R get() {
            if (value == null) {
                throw new RuntimeException("See Async.executeTask");
            }
            return value;
        }

        @Override
        public boolean isDone() {
            return true;
        }
    }
}
