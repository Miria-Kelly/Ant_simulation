package org.example;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class Garden {

    private int vaoId;
    private int vboId;
    private int vertexCount;
    private final Vector3f position;
    
    private final float[] color = {0.2f, 0.55f, 0.2f}; // Verde vivo

    public Garden(float width, float depth) {
        this.position = new Vector3f(0, 0, 0);

        int gridX = 40;
        int gridZ = 40;

        int numTriangles = gridX * gridZ * 2;
        vertexCount = numTriangles * 3;

        // AGORA SÃO 8 NÚMEROS: 3(Posição) + 3(Luz) + 2(Textura Fantasma)
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertexCount * 8);

        float startX = -width / 2f;
        float startZ = -depth / 2f;
        float stepX = width / gridX;
        float stepZ = depth / gridZ;

        float[][] heights = new float[gridX + 1][gridZ + 1];
        for (int x = 0; x <= gridX; x++) {
            for (int z = 0; z <= gridZ; z++) {
                heights[x][z] = (float) (Math.random() * 0.2f); 
            }
        }

        for (int z = 0; z < gridZ; z++) {
            for (int x = 0; x < gridX; x++) {
                float x0 = startX + x * stepX;
                float z0 = startZ + z * stepZ;
                float x1 = x0 + stepX;
                float z1 = z0 + stepZ;

                float y00 = heights[x][z];
                float y10 = heights[x + 1][z];
                float y01 = heights[x][z + 1];
                float y11 = heights[x + 1][z + 1];

                Vector3f p00 = new Vector3f(x0, y00, z0);
                Vector3f p10 = new Vector3f(x1, y10, z0);
                Vector3f p01 = new Vector3f(x0, y01, z1);
                Vector3f p11 = new Vector3f(x1, y11, z1);

                Vector3f normal1 = calcularIluminacao(p00, p01, p10);
                adicionarVertice(verticesBuffer, p00, normal1);
                adicionarVertice(verticesBuffer, p01, normal1);
                adicionarVertice(verticesBuffer, p10, normal1);

                Vector3f normal2 = calcularIluminacao(p10, p01, p11);
                adicionarVertice(verticesBuffer, p10, normal2);
                adicionarVertice(verticesBuffer, p01, normal2);
                adicionarVertice(verticesBuffer, p11, normal2);
            }
        }
        verticesBuffer.flip();

        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        vboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, verticesBuffer, GL30.GL_STATIC_DRAW);

        // Atributo 0: Posição (X, Y, Z)
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 8 * Float.BYTES, 0);
        GL30.glEnableVertexAttribArray(0);

        // Atributo 1: Normal da Luz
        GL30.glVertexAttribPointer(1, 3, GL30.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        GL30.glEnableVertexAttribArray(1);

        // Atributo 2: A textura fantasma que evita o crash!
        GL30.glVertexAttribPointer(2, 2, GL30.GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        GL30.glEnableVertexAttribArray(2);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        MemoryUtil.memFree(verticesBuffer);
    }

    private Vector3f calcularIluminacao(Vector3f p1, Vector3f p2, Vector3f p3) {
        Vector3f v1 = new Vector3f(p2).sub(p1);
        Vector3f v2 = new Vector3f(p3).sub(p1);
        return v1.cross(v2).normalize();
    }

    private void adicionarVertice(FloatBuffer buffer, Vector3f pos, Vector3f normal) {
        buffer.put(pos.x).put(pos.y).put(pos.z);
        buffer.put(normal.x).put(normal.y).put(normal.z);
        // Colocando 2 zeros para enganar o shader
        buffer.put(0f).put(0f);
    }

    public void render(Shader shader) {
        Matrix4f model = new Matrix4f()
                .identity()
                .translate(position);

        shader.setUniform("model", model);
        shader.setUniform("objectColor", color);

        GL30.glBindVertexArray(vaoId);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, vertexCount);
        GL30.glBindVertexArray(0);
    }

    public Vector3f getPosition() {
        return position;
    }
}