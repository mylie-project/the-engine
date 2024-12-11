package mylie.engine.graphics;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;

public interface VertexDataLayouts {
    VertexDataLayout Unshaded = new VertexDataLayout(VertexDataPoints.Position, VertexDataPoints.TextureCoordinates0);
    VertexDataLayout Shaded = new VertexDataLayout(
            VertexDataPoints.Position, VertexDataPoints.TextureCoordinates0, VertexDataPoints.Normal);

    @Getter(AccessLevel.PUBLIC)
    class VertexDataLayout {
        Set<VertexDataPoints.VertexDataPoint<?>> dataPoints;

        public VertexDataLayout() {
            dataPoints = new HashSet<>();
        }

        public VertexDataLayout(VertexDataPoints.VertexDataPoint<?>... dataPoints) {
            this();
            Collections.addAll(this.dataPoints, dataPoints);
        }

        public boolean contains(VertexDataPoints.VertexDataPoint<?> dataPoint) {
            return dataPoints.contains(dataPoint);
        }
    }
}
