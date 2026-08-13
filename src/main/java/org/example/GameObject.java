package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class GameObject {

    private final List<Mesh> meshes;

    private final Vector3f position;
    private final Vector3f rotation; // em radianos, eixos x/y/z
    private final Vector3f scale;

    public GameObject(List<Mesh> meshes) {
        this(meshes, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
    }

    public GameObject(List<Mesh> meshes, Vector3f position) {
        this(meshes, position, new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
    }

    public GameObject(List<Mesh> meshes, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.meshes = meshes;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f()
                .identity()
                .translate(position)
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .rotateZ(rotation.z)
                .scale(scale);
    }

    public void render(Shader shader) {

        shader.setUniform("model", getModelMatrix());

        for (Mesh mesh : meshes) {
            shader.setUniform("objectColor", mesh.getColor());
            mesh.render();
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }
}
