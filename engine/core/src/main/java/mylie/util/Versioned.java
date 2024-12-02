package mylie.util;

import lombok.Getter;

@Getter
public class Versioned<T> {
    private long version;
    private T value;

    public Reference<T> newReference() {
        return new Reference<>(this);
    }

    public void set(T value, long version) {
        this.value = value;
        this.version = version;
    }

    @Getter
    public static class Reference<T> {
        private final Versioned<T> versioned;
        private long version;
        private T value;

        public Reference(Versioned<T> versioned) {
            this.versioned = versioned;
            this.version = versioned.version;
            this.value = versioned.value;
        }

        public T get() {
            if (versioned.version > version) {
                this.value = versioned.value;
                this.version = versioned.version;
                return value;
            }
            return value;
        }

        public boolean isActual() {
            return !(versioned.version > version);
        }
    }
}
