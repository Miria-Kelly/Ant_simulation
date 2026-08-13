package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private final int programId;

    public Shader(String vertexResourcePath, String fragmentResourcePath) {

        String vertexSource = readResource(vertexResourcePath);
        String fragmentSource = readResource(fragmentResourcePath);

        int vertexShader = compile(GL_VERTEX_SHADER, vertexSource, vertexResourcePath);
        int fragmentShader = compile(GL_FRAGMENT_SHADER, fragmentSource, fragmentResourcePath);

        programId = glCreateProgram();
        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException(
                    "Erro ao linkar shader program: " + glGetProgramInfoLog(programId)
            );
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private int compile(int type, String source, String path) {

        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException(
                    "Erro ao compilar shader (" + path + "): " + glGetShaderInfoLog(shader)
            );
        }

        return shader;
    }

    private String readResource(String resourcePath) {

        try (InputStream in = Shader.class.getResourceAsStream(resourcePath)) {

            if (in == null) {
                throw new RuntimeException("Shader não encontrado: " + resourcePath);
            }

            return new String(in.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler shader: " + resourcePath, e);
        }
    }

    public void use() {
        glUseProgram(programId);
    }

    private int location(String name) {
        return glGetUniformLocation(programId, name);
    }

    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(location(name), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String name, Vector3f value) {
        glUniform3f(location(name), value.x, value.y, value.z);
    }

    public void setUniform(String name, float[] rgb) {
        glUniform3f(location(name), rgb[0], rgb[1], rgb[2]);
    }
}
