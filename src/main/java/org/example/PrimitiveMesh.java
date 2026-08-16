package org.example;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class PrimitiveMesh {

    private final int vao;
    private final int vertexCount;

    public PrimitiveMesh(
            float[] vertices,
            float[] normals,
            int[] indices
    ) {

        vertexCount = indices.length;

        vao = glGenVertexArrays();

        glBindVertexArray(vao);

        // VÉRTICES
     

        int vbo = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer vertexBuffer =
                MemoryUtil.memAllocFloat(vertices.length);

        vertexBuffer.put(vertices).flip();

        glBufferData(
                GL_ARRAY_BUFFER,
                vertexBuffer,
                GL_STATIC_DRAW
        );

        MemoryUtil.memFree(vertexBuffer);

        glVertexAttribPointer(
                0,
                3,
                GL_FLOAT,
                false,
                3 * Float.BYTES,
                0
        );

        glEnableVertexAttribArray(0);

   
        // NORMAIS
  

        int nbo = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, nbo);

        FloatBuffer normalBuffer =
                MemoryUtil.memAllocFloat(normals.length);

        normalBuffer.put(normals).flip();

        glBufferData(
                GL_ARRAY_BUFFER,
                normalBuffer,
                GL_STATIC_DRAW
        );

        MemoryUtil.memFree(normalBuffer);

        glVertexAttribPointer(
                1,
                3,
                GL_FLOAT,
                false,
                3 * Float.BYTES,
                0
        );

        glEnableVertexAttribArray(1);

    
        // ÍNDICES
    

        int ebo = glGenBuffers();

        glBindBuffer(
                GL_ELEMENT_ARRAY_BUFFER,
                ebo
        );

        IntBuffer indexBuffer =
                MemoryUtil.memAllocInt(indices.length);

        indexBuffer.put(indices).flip();

        glBufferData(
                GL_ELEMENT_ARRAY_BUFFER,
                indexBuffer,
                GL_STATIC_DRAW
        );

        MemoryUtil.memFree(indexBuffer);

        glBindVertexArray(0);
    }

    public void render() {

        glBindVertexArray(vao);

        glDrawElements(
                GL_TRIANGLES,
                vertexCount,
                GL_UNSIGNED_INT,
                0
        );

        glBindVertexArray(0);
    }

  
    // ESFERA


    public static PrimitiveMesh createSphere(
            int stacks,
            int slices
    ) {

        int vertexCount =
                (stacks + 1) * (slices + 1);

        float[] vertices =
                new float[vertexCount * 3];

        float[] normals =
                new float[vertexCount * 3];

        int[] indices =
                new int[stacks * slices * 6];

        int vertexIndex = 0;

        for (int i = 0; i <= stacks; i++) {

            float phi =
                    (float) Math.PI * i / stacks;

            float y =
                    (float) Math.cos(phi);

            float radius =
                    (float) Math.sin(phi);

            for (int j = 0; j <= slices; j++) {

                float theta =
                        2f * (float) Math.PI * j / slices;

                float x =
                        radius * (float) Math.cos(theta);

                float z =
                        radius * (float) Math.sin(theta);

                vertices[vertexIndex * 3] = x;
                vertices[vertexIndex * 3 + 1] = y;
                vertices[vertexIndex * 3 + 2] = z;

                normals[vertexIndex * 3] = x;
                normals[vertexIndex * 3 + 1] = y;
                normals[vertexIndex * 3 + 2] = z;

                vertexIndex++;
            }
        }

        int index = 0;

        for (int i = 0; i < stacks; i++) {

            for (int j = 0; j < slices; j++) {

                int first =
                        i * (slices + 1) + j;

                int second =
                        first + slices + 1;

                indices[index++] = first;
                indices[index++] = second;
                indices[index++] = first + 1;

                indices[index++] = second;
                indices[index++] = second + 1;
                indices[index++] = first + 1;
            }
        }

        return new PrimitiveMesh(
                vertices,
                normals,
                indices
        );
    }

   
    // CILINDRO
   

    public static PrimitiveMesh createCylinder(
            int segments
    ) {

        float radius = 0.08f;
        float height = 1.0f;

        int vertexCount =
                (segments + 1) * 2;

        float[] vertices =
                new float[vertexCount * 3];

        float[] normals =
                new float[vertexCount * 3];

        int[] indices =
                new int[segments * 6];

        int vertex = 0;

        for (int y = 0; y <= 1; y++) {

            float currentY =
                    y * height - height / 2f;

            for (int i = 0; i <= segments; i++) {

                float angle =
                        2f * (float) Math.PI
                                * i / segments;

                float x =
                        radius * (float) Math.cos(angle);

                float z =
                        radius * (float) Math.sin(angle);

                vertices[vertex * 3] = x;
                vertices[vertex * 3 + 1] =
                        currentY;
                vertices[vertex * 3 + 2] = z;

                normals[vertex * 3] =
                        (float) Math.cos(angle);

                normals[vertex * 3 + 1] = 0;

                normals[vertex * 3 + 2] =
                        (float) Math.sin(angle);

                vertex++;
            }
        }

        int index = 0;

        for (int i = 0; i < segments; i++) {

            int bottom = i;
            int nextBottom = i + 1;

            int top =
                    segments + 1 + i;

            int nextTop =
                    segments + 1 + i + 1;

            indices[index++] = bottom;
            indices[index++] = top;
            indices[index++] = nextBottom;

            indices[index++] = nextBottom;
            indices[index++] = top;
            indices[index++] = nextTop;
        }

        return new PrimitiveMesh(
                vertices,
                normals,
                indices
        );
    }
}