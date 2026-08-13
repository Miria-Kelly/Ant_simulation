package org.example;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    // estado do mouse (precisa ser acessível dentro do callback)
    private static float lastX = 400;
    private static float lastY = 300;
    private static boolean firstMouse = true;

    public static void main(String[] args) {

        // =========================
        // GLFW
        // =========================

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Não foi possível inicializar o GLFW");
        }

        long window = GLFW.glfwCreateWindow(800, 600, "Ant Simulation", 0, 0);

        if (window == 0) {
            throw new RuntimeException("Falha ao criar a janela");
        }

        GLFW.glfwMakeContextCurrent(window);

        // =========================
        // OPENGL
        // =========================

        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // =========================
        // SHADER
        // =========================

        Shader shader = new Shader("/shaders/vertex.glsl", "/shaders/fragment.glsl");
        shader.use();

        shader.setUniform("lightPos", new Vector3f(2f, 5f, 2f));
        shader.setUniform("lightColor", new Vector3f(1f, 1f, 1f));

        // =========================
        // CÂMERA
        // =========================

        Camera camera = new Camera();

        // FOV bem mais largo que o humano (60°) — simula a visão panorâmica
        // dos olhos compostos de um inseto. Também reduzimos o "near plane"
        // pra 0.01, já que na escala de formiga tudo fica muito perto da câmera.
        var projection = new org.joml.Matrix4f()
                .perspective((float) Math.toRadians(100), 800f / 600f, 0.01f, 100f);

        shader.setUniform("projection", projection);

        // =========================
        // INPUT: MOUSE (olhar em volta)
        // =========================

        // esconde o cursor e trava ele no centro da janela (padrão FPS)
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {

            if (firstMouse) {
                lastX = (float) xpos;
                lastY = (float) ypos;
                firstMouse = false;
            }

            float xOffset = (float) xpos - lastX;
            // invertido: mouse pra cima deve olhar pra cima
            float yOffset = lastY - (float) ypos;

            lastX = (float) xpos;
            lastY = (float) ypos;

            camera.processMouseMovement(xOffset, yOffset);
        });

        // ESC fecha a janela
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(win, true);
            }
        });

        // =========================
        // CENA (modelos + instâncias)
        // =========================

        List<Mesh> rockMeshes = ModelLoader.loadModel("/models/rock_tallJ.obj");

        List<GameObject> sceneObjects = List.of(
                new GameObject(rockMeshes, new Vector3f(0f, 0f, 0f), new Vector3f(0), new Vector3f(1.5f)),
                new GameObject(rockMeshes, new Vector3f(2.5f, 0f, -1f), new Vector3f(0), new Vector3f(1f)),
                new GameObject(rockMeshes, new Vector3f(-2f, 0f, 1f), new Vector3f(0), new Vector3f(0.8f))
        );

        // =========================
        // LOOP
        // =========================

        glfwShowWindow(window);

        double lastFrameTime = glfwGetTime();

        while (!GLFW.glfwWindowShouldClose(window)) {

            // ---- delta time (independe de FPS) ----
            double currentFrameTime = glfwGetTime();
            float deltaTime = (float) (currentFrameTime - lastFrameTime);
            lastFrameTime = currentFrameTime;

            // ---- input de teclado (movimentação) ----
            processInput(window, camera, deltaTime);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            shader.setUniform("view", camera.getViewMatrix());
            shader.setUniform("viewPos", camera.getPosition());

            for (GameObject obj : sceneObjects) {
                obj.render(shader);
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private static void processInput(long window, Camera camera, float deltaTime) {

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.moveForward(deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.moveBackward(deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.moveLeft(deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.moveRight(deltaTime);
        }
        // sem Espaço/Shift: formiga anda no chão, não voa
    }
}