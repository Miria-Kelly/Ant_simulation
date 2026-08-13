package org.example;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;

import java.nio.IntBuffer;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private final int vao;
    private final int vbo;
    private final int nbo; // normal buffer
    private final int ebo;
    private final int vertexCount;

    private float[] color = {1f, 1f, 1f}; // fallback: branco

    public Mesh(AIMesh mesh, AIScene scene) {

        float[] vertices = new float[mesh.mNumVertices() * 3];
        float[] normals = new float[mesh.mNumVertices() * 3];

        boolean hasNormals = mesh.mNormals() != null;

        for (int i = 0; i < mesh.mNumVertices(); i++) {

            vertices[i * 3]     = mesh.mVertices().get(i).x();
            vertices[i * 3 + 1] = mesh.mVertices().get(i).y();
            vertices[i * 3 + 2] = mesh.mVertices().get(i).z();

            if (hasNormals) {
                normals[i * 3]     = mesh.mNormals().get(i).x();
                normals[i * 3 + 1] = mesh.mNormals().get(i).y();
                normals[i * 3 + 2] = mesh.mNormals().get(i).z();
            }
        }

        int[] indices = new int[mesh.mNumFaces() * 3];

        for (int i = 0; i < mesh.mNumFaces(); i++) {
            IntBuffer face = mesh.mFaces().get(i).mIndices();
            indices[i * 3]     = face.get(0);
            indices[i * 3 + 1] = face.get(1);
            indices[i * 3 + 2] = face.get(2);
        }

        vertexCount = indices.length;

        // ---- cor do material ----
        int matIndex = mesh.mMaterialIndex();
        if (scene != null && matIndex >= 0 && matIndex < scene.mNumMaterials()) {
            PointerBuffer materials = scene.mMaterials();
            AIMaterial material = AIMaterial.create(materials.get(matIndex));

            try (AIColor4D colorOut = AIColor4D.create()) {
                int result = aiGetMaterialColor(
                        material,
                        AI_MATKEY_COLOR_DIFFUSE,
                        aiTextureType_NONE,
                        0,
                        colorOut
                );

                if (result == 0) { // aiReturn_SUCCESS == 0
                    color = new float[]{colorOut.r(), colorOut.g(), colorOut.b()};
                }
            }
        }

        // ---- VAO / VBOs / EBO ----
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        nbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    public float[] getColor() {
        return color;
    }

    public void render() {
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }
}