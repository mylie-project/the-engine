package mylie.engine.graphics.meshes;

import java.util.List;
import mylie.engine.core.Constants;
import mylie.engine.graphics.Mesh;
import mylie.engine.graphics.VertexDataLayouts;
import mylie.engine.graphics.VertexDataPoints;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Quad extends Mesh {

    static Vector3fc[] positions = { // Define vertices of a quad (two triangles making up a rectangle)
        new Vector3f(-1.0f, -1.0f, 0.0f), // Bottom-left
        new Vector3f(1.0f, -1.0f, 0.0f), // Bottom-right
        new Vector3f(1.0f, 1.0f, 0.0f), // Top-right
        new Vector3f(-1.0f, 1.0f, 0.0f) // Top-left
    };
    static Vector2fc[] texCoordinates = { // Define vertices of a quad (two triangles making up a rectangle)
        new Vector2f(0.0f, 0.0f), // Bottom-left
        new Vector2f(1.0f, 0.0f), // Bottom-right
        new Vector2f(1.0f, 1.0f), // Top-right
        new Vector2f(0.0f, 1.0f) // Top-left
    };
    static Vector3fc normal = Constants.UnitZ;

    public Quad(VertexDataLayouts.VertexDataLayout vertexLayout) {
        super(vertexLayout, 4);

        vertexData(VertexDataPoints.Position, positions); // Set vertex data
        if (vertexLayout.contains(VertexDataPoints.Position)) {
            vertexData(VertexDataPoints.TextureCoordinates0, texCoordinates);
        }
        if (vertexLayout.contains(VertexDataPoints.Normal)) {
            vertexData(VertexDataPoints.Normal, normal, normal, normal, normal);
        }
        List<Integer> indices = List.of(0, 1, 2, 2, 3, 0); // Define indices (two triangles to form a quad)
        Lod lod = new Lod(Lod.RenderMode.Triangles, indices); // Create LOD with indices
        lod(lod); // Add LOD to this Mesh
    }
}
