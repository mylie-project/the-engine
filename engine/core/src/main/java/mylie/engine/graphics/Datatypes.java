package mylie.engine.graphics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.nio.ByteBuffer;
import java.util.Iterator;

public class Datatypes {

    public static final GpuDataType<Integer> Integer = new PrimitiveDataType<>(java.lang.Integer.BYTES, 1, ByteBuffer::getInt, (value, buffer) -> buffer.putInt(value));
    public static final GpuDataType<Float> Float = new PrimitiveDataType<>(java.lang.Float.BYTES, 1, ByteBuffer::getFloat, (value, buffer) -> buffer.putFloat(value));
    public static final GpuDataType<Matrix4fc> Matrix4f = new PrimitiveDataType<>(Float.size, 16, null, Matrix4fc::get);
    public static final GpuDataType<Matrix3fc> Matrix3f = new PrimitiveDataType<>(Float.size, 9, null, Matrix3fc::get);
    public static final GpuDataType<Matrix2fc> Matrix2f = new PrimitiveDataType<>(Float.size, 4, null, Matrix2fc::get);
    public static final GpuDataType<Vector4fc> Vector4f = new PrimitiveDataType<>(Float.size, 4, org.joml.Vector4f::new, Vector4fc::get);
    public static final GpuDataType<Vector3fc> Vector3f = new PrimitiveDataType<>(Float.size, 3, org.joml.Vector3f::new, Vector3fc::get);
    public static final GpuDataType<Vector2fc> Vector2f = new PrimitiveDataType<>(Float.size, 2, org.joml.Vector2f::new, Vector2fc::get);
    public static final GpuDataType<Vector3ic> Vector3i = new PrimitiveDataType<>(Integer.size, 3, org.joml.Vector3i::new, Vector3ic::get);
    public static final GpuDataType<Vector2ic> Vector2i = new PrimitiveDataType<>(Integer.size, 2, org.joml.Vector2i::new, Vector2ic::get);



    @AllArgsConstructor
    @Getter
    public static class GpuDataType<T> {
        final int size;
        final BufferReader<T> reader;
        final BufferWriter<T> writer;
    }

    @Getter
    static class PrimitiveDataType<T> extends GpuDataType<T> {
        final int componentByteSize;
        final int components;

        public PrimitiveDataType(int componentByteSize, int components, BufferReader<T> reader, BufferWriter<T> writer) {
            super(componentByteSize * components, reader, writer);
            this.componentByteSize = componentByteSize;
            this.components = components;
        }
    }

    public static class StructDataType<T extends Struct> extends GpuDataType<T> {

        public StructDataType(Class<T> structClass) {
            super(128, null, null);
        }
    }

    public interface Struct {

    }

    public interface DataBuffer<T> extends Iterable<T>, BufferReader<DataBuffer<T>>, BufferWriter<DataBuffer<T>> {
        void set(int index, T value);

        T get(int index);
    }

    public static class ArrayDataBuffer<T> implements DataBuffer<T> {
        final GpuDataType<T> dataType;
        T[] data;


        @SuppressWarnings("unchecked")
        public ArrayDataBuffer(GpuDataType<T> dataType, int size) {
            this.dataType = dataType;
            data = (T[]) new Object[size];
        }


        @Override
        public void set(int index, T value) {
            data[index] = value;
        }

        @Override
        public T get(int index) {
            return data[index];
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new Iterator<>() {
                int index = 0;

                @Override
                public boolean hasNext() {
                    return index != data.length;
                }

                @Override
                public T next() {
                    return data[index++];
                }
            };
        }

        @Override
        public DataBuffer<T> readFromBuffer(ByteBuffer buffer) {
            int bufferIndex = buffer.position();
            int index=0;
            while (buffer.limit()>=bufferIndex+dataType.size) {
                data[index]= dataType.reader.readFromBuffer(buffer);
                buffer.position(bufferIndex + dataType.size);
                index++;
            }
            return this;
        }

        @Override
        public void writeToBuffer(DataBuffer<T> dataBuffer, ByteBuffer buffer) {
            int bufferIndex = buffer.position();
            for (int i = 0; i < data.length; i++) {
                dataType.writer.writeToBuffer(data[i], buffer);
                buffer.position(bufferIndex + i * dataType.size);
            }
        }
    }

    interface BufferWriter<T> {
        void writeToBuffer(T value, ByteBuffer buffer);
    }

    interface BufferReader<T> {
        T readFromBuffer(ByteBuffer buffer);
    }

}
