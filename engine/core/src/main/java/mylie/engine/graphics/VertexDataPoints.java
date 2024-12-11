package mylie.engine.graphics;

import lombok.Value;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

public interface VertexDataPoints {
    VertexDataPoint<Vector3fc> Position = new VertexDataPoint<>("vertexPosition", Datatypes.Vector3f);
    VertexDataPoint<Vector3fc> Normal = new VertexDataPoint<>("vertexNormal", Datatypes.Vector3f);
    VertexDataPoint<Vector2fc> TextureCoordinates0 =
            new VertexDataPoint<>("vertexTextureCoordinates0", Datatypes.Vector2f);

    @Value
    class VertexDataPoint<T> {
        String name;
        Datatypes.PrimitiveDataType<T> dataType;
    }
}
