package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;
    private final Vector3f front;
    private final Vector3f up;
    private final Vector3f right;
    private final Vector3f worldUp;

    // ângulos de orientação (em graus)
    private float yaw = -90f;   // -90 pra começar olhando pro -Z (padrão OpenGL)
    private float pitch = 0f;

    private float movementSpeed = 0.8f;      // unidades por segundo (formiga é lenta em escala)
    private float mouseSensitivity = 0.1f;

    // altura fixa dos "olhos" da formiga — travada, não muda ao andar nem olhar pra cima/baixo
    private final float eyeHeight;

    public Camera() {
        this(0.12f); // ~escala de formiga (pedra tem ~0.9 de altura, formiga fica bem menor)
    }

    public Camera(float eyeHeight) {
        this.eyeHeight = eyeHeight;
        position = new Vector3f(0, eyeHeight, 6);
        worldUp = new Vector3f(0, 1, 0);
        front = new Vector3f(0, 0, -1);
        up = new Vector3f(worldUp);
        right = new Vector3f();
        updateVectors();
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f()
                .lookAt(
                        position,
                        new Vector3f(position).add(front),
                        up
                );
    }

    // ---- movimentação (WASD) — travada no plano horizontal (chão) ----

    public void moveForward(float deltaTime) {
        Vector3f flatFront = horizontalFront();
        position.fma(movementSpeed * deltaTime, flatFront);
        position.y = eyeHeight; // trava a altura, mesmo com erro de ponto flutuante
    }

    public void moveBackward(float deltaTime) {
        Vector3f flatFront = horizontalFront();
        position.fma(-movementSpeed * deltaTime, flatFront);
        position.y = eyeHeight;
    }

    public void moveLeft(float deltaTime) {
        // "right" já é sempre horizontal (right = front x worldUp tem y=0 por construção)
        position.fma(-movementSpeed * deltaTime, right);
        position.y = eyeHeight;
    }

    public void moveRight(float deltaTime) {
        position.fma(movementSpeed * deltaTime, right);
        position.y = eyeHeight;
    }

    // pega o "front" mas zera a componente Y e renormaliza,
    // assim W/S nunca fazem a formiga "subir voando" quando você olha pra cima
    private Vector3f horizontalFront() {
        Vector3f flat = new Vector3f(front.x, 0f, front.z);
        if (flat.lengthSquared() > 0.0001f) {
            flat.normalize();
        }
        return flat;
    }

    // ---- olhar em volta (mouse) ----

    public void processMouseMovement(float xOffset, float yOffset) {

        yaw += xOffset * mouseSensitivity;
        pitch += yOffset * mouseSensitivity;

        // trava o pitch pra não "virar de cabeça pra baixo"
        if (pitch > 89f) pitch = 89f;
        if (pitch < -89f) pitch = -89f;

        updateVectors();
    }

    private void updateVectors() {

        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        Vector3f newFront = new Vector3f(
                (float) (Math.cos(yawRad) * Math.cos(pitchRad)),
                (float) Math.sin(pitchRad),
                (float) (Math.sin(yawRad) * Math.cos(pitchRad))
        );

        front.set(newFront.normalize());
        right.set(front).cross(worldUp, right).normalize();
        up.set(right).cross(front, up).normalize();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setMovementSpeed(float speed) {
        this.movementSpeed = speed;
    }
}