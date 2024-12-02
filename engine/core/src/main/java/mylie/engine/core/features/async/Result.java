package mylie.engine.core.features.async;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
public abstract class Result<R> {
    private int hash;
    private long frameId;

    protected abstract R get();

    protected abstract boolean isDone();

    static class FixedResult<R> extends Result<R> {
        final R value;

        public FixedResult(int hashCode, long frameId, R value) {
            this.value = value;
            hash(hashCode);
            frameId(frameId);
        }

        @Override
        protected R get() {
            return value;
        }

        @Override
        protected boolean isDone() {
            return true;
        }
    }
}
