package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;
    private final Vector3f front;
    private final Vector3f up;
    private final Vector3f worldUp;

    private float yaw = -90f;

    private float pitch = 20f;

    private float distance = 1.8f;

    private float height = 0.7f;

    private float mouseSensitivity = 0.12f;

    public Camera() {

        position =
                new Vector3f();

        front =
                new Vector3f(
                        0f,
                        0f,
                        -1f
                );

        up =
                new Vector3f(
                        0f,
                        1f,
                        0f
                );

        worldUp =
                new Vector3f(
                        0f,
                        1f,
                        0f
                );

        updateVectors();
    }


    public Matrix4f getViewMatrix() {

        return new Matrix4f()
                .lookAt(
                        position,
                        new Vector3f(position)
                                .add(front),
                        up
                );
    }

    // MOUSE

    public void processMouseMovement(
            float xOffset,
            float yOffset
    ) {

        yaw +=
                xOffset *
                mouseSensitivity;

        pitch +=
                yOffset *
                mouseSensitivity;

        if (pitch > 75f) {
            pitch = 75f;
        }

        if (pitch < -20f) {
            pitch = -20f;
        }

        updateVectors();
    }

    // ATUALIZA DIREÇÃO DA CÂMERA

    private void updateVectors() {

        float yawRad =
                (float) Math.toRadians(yaw);

        float pitchRad =
                (float) Math.toRadians(pitch);

        float x =
                (float)
                        (
                                Math.cos(yawRad) *
                                Math.cos(pitchRad)
                        );

        float y =
                (float)
                        Math.sin(pitchRad);

        float z =
                (float)
                        (
                                Math.sin(yawRad) *
                                Math.cos(pitchRad)
                        );

        front.set(
                x,
                y,
                z
        ).normalize();

        Vector3f right =
                new Vector3f(front)
                        .cross(worldUp)
                        .normalize();

        up.set(right)
                .cross(front)
                .normalize();
    }

    // SEGUE A FORMIGA

    public void follow(Formiga formiga) {

        Vector3f antPosition =
                formiga.getPosition();

        // DIREÇÃO HORIZONTAL DA CÂMERA

        float yawRad =
                (float) Math.toRadians(yaw);

        float forwardX =
                (float) Math.cos(yawRad);

        float forwardZ =
                (float) Math.sin(yawRad);

        // COLOCA A CÂMERA ATRÁS DA FORMIGA

        position.x =
                antPosition.x -
                forwardX * distance;

        position.y =
                antPosition.y +
                height;

        position.z =
                antPosition.z -
                forwardZ * distance;

        // A CÂMERA OLHA PARA A FORMIGA

        Vector3f target =
                new Vector3f(
                        antPosition.x,
                        antPosition.y + 0.08f,
                        antPosition.z
                );

        front.set(
                target
        ).sub(position)
                .normalize();

        // UP

        Vector3f right =
                new Vector3f(front)
                        .cross(worldUp)
                        .normalize();

        up.set(right)
                .cross(front)
                .normalize();
    }

    // DIREÇÃO PARA FRENTE DA CÂMERA

    public Vector3f getHorizontalFront() {

        float yawRad =
                (float) Math.toRadians(yaw);

        return new Vector3f(
                (float) Math.cos(yawRad),
                0f,
                (float) Math.sin(yawRad)
        ).normalize();
    }

    // DIREÇÃO PARA A DIREITA DA CÂMERA

    public Vector3f getRight() {

        Vector3f front =
                getHorizontalFront();

        return new Vector3f(
                front.z,
                0f,
                -front.x
        ).normalize();
    }

    // GETTERS

    public Vector3f getPosition() {
        return position;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}