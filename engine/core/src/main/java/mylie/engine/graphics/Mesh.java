package mylie.engine.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

public class Mesh {
    final VertexDataLayouts.VertexDataLayout vertexDataLayout;
    final Map<VertexDataPoints.VertexDataPoint<?>, Datatypes.DataBuffer<?>> vertexDataBuffers;
    final Datatypes.DataBuffer<Integer> indices;
    final List<Mesh.Lod> lodLevels;

    private Mesh(VertexDataLayouts.VertexDataLayout vertexDataLayout) {
        this.vertexDataLayout = vertexDataLayout;
        this.vertexDataBuffers = new HashMap<>();
        this.lodLevels = new ArrayList<>();
        this.indices = new Datatypes.ListDataBuffer<>(Datatypes.Integer);
    }

    public Mesh(VertexDataLayouts.VertexDataLayout vertexDataLayout, int size) {
        this(vertexDataLayout);
        for (VertexDataPoints.VertexDataPoint<?> dataPoint : vertexDataLayout.dataPoints()) {
            vertexDataBuffers.put(dataPoint, new Datatypes.ArrayDataBuffer<>(dataPoint.dataType(), size));
        }
    }

    @SuppressWarnings("unchecked")
    public final <T> void vertexData(VertexDataPoints.VertexDataPoint<T> point, int index, T data) {
        Datatypes.DataBuffer<T> dataBuffer = (Datatypes.DataBuffer<T>) vertexDataBuffers.get(point);
        dataBuffer.set(index, data);
    }

    @SafeVarargs
    public final <T> void vertexData(VertexDataPoints.VertexDataPoint<T> point, T... data) {
        for (int i = 0; i < data.length; i++) {
            vertexData(point, i, data[i]);
        }
    }

    @SuppressWarnings("unchecked")
    public final <T> T vertexData(VertexDataPoints.VertexDataPoint<T> point, int index) {
        Datatypes.DataBuffer<T> dataBuffer = (Datatypes.DataBuffer<T>) vertexDataBuffers.get(point);
        return dataBuffer.get(index);
    }

    public final void lod(Mesh.Lod lod) {
        lodLevels.add(lod);
        lod.offset = indices.count();
        lod.count = lod.indices.size();
        int count = 0;
        for (Integer index : lod.indices) {
            indices.set(lod.offset + count, index);
            count++;
        }
    }

    @SuppressWarnings("unused")
    @Getter
    public static class Lod {
        public enum RenderMode {
            Triangles,
            TriangleStrip,
            TriangleFan,
            TriangleAdjacency,
            TriangleStripAdjacency,
            Lines,
            LineStrip,
            LineLoop,
            LineStripAdjacency,
            LinesAdjacency,
            Points,
            Patches
        }

        int offset, count;
        Mesh.Lod.RenderMode renderMode;
        int patchCount;
        List<Integer> indices;

        public Lod(Mesh.Lod.RenderMode renderMode, int patchCount) {
            indices = new ArrayList<>();
            this.patchCount = patchCount;
        }

        public Lod(Mesh.Lod.RenderMode renderMode, List<Integer> indices) {
            this(renderMode, 0);
            this.indices.addAll(indices);
        }

        public Lod(Mesh.Lod.RenderMode renderMode) {
            this(renderMode, 0);
        }
    }
}
