package mylie.engine.graphics.meshes;

import mylie.engine.graphics.Mesh;
import mylie.engine.graphics.VertexDataLayouts;
import mylie.engine.graphics.VertexDataPoints;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Cube extends Mesh {
    static Vector3fc[] Positions = {
        // Front face
        new Vector3f(-0.5f, -0.5f, 0.5f), // Bottom-left
        new Vector3f(0.5f, -0.5f, 0.5f), // Bottom-right
        new Vector3f(0.5f, 0.5f, 0.5f), // Top-right
        new Vector3f(-0.5f, 0.5f, 0.5f), // Top-left

        // Back face
        new Vector3f(0.5f, -0.5f, -0.5f), // Bottom-left
        new Vector3f(-0.5f, -0.5f, -0.5f), // Bottom-right
        new Vector3f(-0.5f, 0.5f, -0.5f), // Top-right
        new Vector3f(0.5f, 0.5f, -0.5f), // Top-left

        // Top face
        new Vector3f(-0.5f, 0.5f, -0.5f), // Bottom-left
        new Vector3f(0.5f, 0.5f, -0.5f), // Bottom-right
        new Vector3f(0.5f, 0.5f, 0.5f), // Top-right
        new Vector3f(-0.5f, 0.5f, 0.5f), // Top-left

        // Bottom face
        new Vector3f(-0.5f, -0.5f, 0.5f), // Bottom-left
        new Vector3f(0.5f, -0.5f, 0.5f), // Bottom-right
        new Vector3f(0.5f, -0.5f, -0.5f), // Top-right
        new Vector3f(-0.5f, -0.5f, -0.5f), // Top-left

        // Right face
        new Vector3f(0.5f, -0.5f, 0.5f), // Bottom-left
        new Vector3f(0.5f, -0.5f, -0.5f), // Bottom-right
        new Vector3f(0.5f, 0.5f, -0.5f), // Top-right
        new Vector3f(0.5f, 0.5f, 0.5f), // Top-left

        // Left face
        new Vector3f(-0.5f, -0.5f, -0.5f), // Bottom-left
        new Vector3f(-0.5f, -0.5f, 0.5f), // Bottom-right
        new Vector3f(-0.5f, 0.5f, 0.5f), // Top-right
        new Vector3f(-0.5f, 0.5f, -0.5f) // Top-left
    };

    // Define normal vectors for each face of the cube
    static Vector3fc[] Normals = {
        // Front face normals
        new Vector3f(0, 0, 1), new Vector3f(0, 0, 1), new Vector3f(0, 0, 1), new Vector3f(0, 0, 1),
        // Back face normals
        new Vector3f(0, 0, -1), new Vector3f(0, 0, -1), new Vector3f(0, 0, -1), new Vector3f(0, 0, -1),
        // Top face normals
        new Vector3f(0, 1, 0), new Vector3f(0, 1, 0), new Vector3f(0, 1, 0), new Vector3f(0, 1, 0),
        // Bottom face normals
        new Vector3f(0, -1, 0), new Vector3f(0, -1, 0), new Vector3f(0, -1, 0), new Vector3f(0, -1, 0),
        // Right face normals
        new Vector3f(1, 0, 0), new Vector3f(1, 0, 0), new Vector3f(1, 0, 0), new Vector3f(1, 0, 0),
        // Left face normals
        new Vector3f(-1, 0, 0), new Vector3f(-1, 0, 0), new Vector3f(-1, 0, 0), new Vector3f(-1, 0, 0)
    };

    // Define texture coordinates for each vertex
    static Vector2fc[] TextureCoordinates = {
        // Front face
        new Vector2f(0f, 0f), new Vector2f(1f, 0f), new Vector2f(1f, 1f), new Vector2f(0f, 1f),
        // Back face
        new Vector2f(0f, 0f), new Vector2f(1f, 0f), new Vector2f(1f, 1f), new Vector2f(0f, 1f),
        // Top face
        new Vector2f(0f, 0f), new Vector2f(1f, 0f), new Vector2f(1f, 1f), new Vector2f(0f, 1f),
        // Bottom face
        new Vector2f(0f, 0f), new Vector2f(1f, 0f), new Vector2f(1f, 1f), new Vector2f(0f, 1f),
        // Right face
        new Vector2f(0f, 0f), new Vector2f(1f, 0f), new Vector2f(1f, 1f), new Vector2f(0f, 1f),
        // Left face
        new Vector2f(0f, 0f), new Vector2f(1f, 0f), new Vector2f(1f, 1f), new Vector2f(0f, 1f)
    };

    // Define the order of indices to form triangles from vertices
    int[] indices = {
        0, 1, 2, 2, 3, 0, // Front face
        4, 5, 6, 6, 7, 4, // Back face
        8, 9, 10, 10, 11, 8, // Top face
        12, 13, 14, 14, 15, 12, // Bottom face
        16, 17, 18, 18, 19, 16, // Right face
        20, 21, 22, 22, 23, 20 // Left face
    };

    public Cube(VertexDataLayouts.VertexDataLayout vertexDataLayout) {
        super(vertexDataLayout, 24);
        vertexData(VertexDataPoints.Position, Positions);
        if (vertexDataLayout.contains(VertexDataPoints.TextureCoordinates0)) {
            vertexData(VertexDataPoints.TextureCoordinates0, TextureCoordinates);
        }
        if (vertexDataLayout.contains(VertexDataPoints.Normal)) {
            vertexData(VertexDataPoints.Normal, Normals);
        }
    }
}
