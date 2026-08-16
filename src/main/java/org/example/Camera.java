package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;
    private final Vector3f front;
    private final Vector3f up;
    private final Vector3f right;
    private final Vector3f worldUp;

    private float yaw = 0f;
    private float pitch = 25f;

    private float distance = 3.0f;
    private float height = 1.5f;

    public Camera() {

        position =
                new Vector3f();

        front =
                new Vector3f();

        up =
                new Vector3f();

        right =
                new Vector3f();

        worldUp =
                new Vector3f(0, 1, 0);

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

    // SEGUE A FORMIGA

    public void follow(Formiga formiga) {

        Vector3f antPosition =
                formiga.getPosition();

        float rotation =
                formiga.getRotationY();

        float dirX =
                (float) Math.sin(rotation);

        float dirZ =
                (float) -Math.cos(rotation);

        position.x =
                antPosition.x - dirX * distance;

        position.y =
                antPosition.y + height;

        position.z =
                antPosition.z - dirZ * distance;

        Vector3f target =
                new Vector3f(
                        antPosition.x,
                        antPosition.y + 0.15f,
                        antPosition.z
                );

        front
                .set(target)
                .sub(position)
                .normalize();

        right
                .set(front)
                .cross(worldUp)
                .normalize();

        up
                .set(right)
                .cross(front)
                .normalize();
    }

    private void updateVectors() {

        float yawRad =
                (float) Math.toRadians(yaw);

        float pitchRad =
                (float) Math.toRadians(pitch);

        front.set(
                (float)
                        (Math.cos(yawRad)
                                * Math.cos(pitchRad)),

                (float)
                        Math.sin(pitchRad),

                (float)
                        (Math.sin(yawRad)
                                * Math.cos(pitchRad))
        );

        front.normalize();

        right
                .set(front)
                .cross(worldUp)
                .normalize();

        up
                .set(right)
                .cross(front)
                .normalize();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}