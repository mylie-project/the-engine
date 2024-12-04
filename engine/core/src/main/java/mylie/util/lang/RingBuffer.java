package mylie.util.lang;

import java.util.Iterator;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class RingBuffer<T> implements Iterable<T> {
    final int size;
    final Supplier<T> factory;
    final T[] buffer;

    @Setter(AccessLevel.PUBLIC)
    long index = 0;

    public RingBuffer(int size, Supplier<T> factory) {
        this.size = size;
        this.factory = factory;
        buffer = (T[]) new Object[size];
        for (int i = 0; i < size; i++) {
            buffer[i] = factory.get();
        }
    }

    public T get() {
        return buffer[(int) (index % size)];
    }

    @NotNull @Override
    public Iterator<T> iterator() {
        return new IteratorImpl<>();
    }

    private static class IteratorImpl<T> implements Iterator<T> {
        private int startIndex = 0;

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            return null;
        }
    }
}
