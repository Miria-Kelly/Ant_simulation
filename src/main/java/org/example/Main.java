package org.example;

import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public class Main {

    private static float lastX = 400f;
    private static float lastY = 300f;
    private static boolean firstMouse = true;
    private static boolean cursorPreso = true;

    private static GLFWErrorCallback errorCallback;
    private static GLFWCursorPosCallback mouseCallback;
    private static GLFWKeyCallback keyCallback;

    public static void main(String[] args) {
        errorCallback = GLFWErrorCallback.createPrint(System.err);
        errorCallback.set();

        try {
            if (!GLFW.glfwInit()) {
                throw new IllegalStateException("Não foi possível inicializar o GLFW");
            }

            // Descobre o tamanho real do seu monitor
            long monitor = glfwGetPrimaryMonitor();
            org.lwjgl.glfw.GLFWVidMode vidMode = glfwGetVideoMode(monitor);
            int width = vidMode != null ? vidMode.width() : 1280;
            int height = vidMode != null ? vidMode.height() : 720;

            // Configura para a janela iniciar maximizada
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

            // Cria a janela ocupando a tela toda
            long window = GLFW.glfwCreateWindow(width, height, "Ant Simulation", 0, 0);
            if (window == 0) {
                throw new RuntimeException("Falha ao criar a janela");
            }

            GLFW.glfwMakeContextCurrent(window);
            GL.createCapabilities();
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_CULL_FACE);
            glClearColor(0.3f, 0.5f, 0.8f, 1.0f);

            Shader shader = new Shader("/shaders/vertex.glsl", "/shaders/fragment.glsl");
            shader.use();
            shader.setUniform("lightPos", new Vector3f(2f, 5f, 2f));
            shader.setUniform("lightColor", new Vector3f(1f, 1f, 1f));

            Camera camera = new Camera();
            camera.setDistance(0.8f);
            camera.setHeight(0.3f);

            var projection = new org.joml.Matrix4f().perspective((float) Math.toRadians(70), (float) width / height, 0.01f, 100f);
            shader.setUniform("projection", projection);

            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            
            mouseCallback = GLFWCursorPosCallback.create((win, xpos, ypos) -> {
                if (!cursorPreso) return;
                if (firstMouse) { lastX = (float) xpos; lastY = (float) ypos; firstMouse = false; }
                float xOffset = (float) xpos - lastX;
                float yOffset = lastY - (float) ypos;
                lastX = (float) xpos; lastY = (float) ypos;
                camera.processMouseMovement(xOffset, yOffset);
            });
            glfwSetCursorPosCallback(window, mouseCallback);

            keyCallback = GLFWKeyCallback.create((win, key, scancode, action, mods) -> {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                    glfwSetWindowShouldClose(win, true);
                }
                // Pressione TAB para soltar/prender a seta do mouse
                if (key == GLFW_KEY_TAB && action == GLFW_PRESS) {
                    cursorPreso = !cursorPreso;
                    if (cursorPreso) {
                        glfwSetInputMode(win, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                        firstMouse = true;
                    } else {
                        glfwSetInputMode(win, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                    }
                }
            });
            glfwSetKeyCallback(window, keyCallback);

            System.out.println("Carregando modelos 3D do Jardim...");

            List<Mesh> rockMeshes = ModelLoader.loadModel("/models/rock_tallJ.obj");
            List<Mesh> treeMeshes = ModelLoader.loadModel("/models/tree_default.obj");
            List<Mesh> flowerMeshes = ModelLoader.loadModel("/models/flower_redA.obj");
            List<Mesh> mushroomMeshes = ModelLoader.loadModel("/models/mushroom_red.obj");
            List<Mesh> logMeshes = ModelLoader.loadModel("/models/log.obj");

            List<GameObject> sceneObjects = new java.util.ArrayList<>();
            java.util.Random rand = new java.util.Random();

            for (int i = 0; i < 3; i++) {
                float x = -9.5f + rand.nextFloat() * 19f;
                float z = -9.5f + rand.nextFloat() * 19f;
                sceneObjects.add(new GameObject(rockMeshes, new Vector3f(x, 0f, z), new Vector3f(0f, rand.nextFloat() * 360f, 0f), new Vector3f(4.0f)));
            }

            for (int i = 0; i < 12; i++) {
                float x = -9.5f + rand.nextFloat() * 19f;
                float z = -9.5f + rand.nextFloat() * 19f;
                sceneObjects.add(new GameObject(treeMeshes, new Vector3f(x, 0f, z), new Vector3f(0f, rand.nextFloat() * 360f, 0f), new Vector3f(5.0f + rand.nextFloat() * 2f)));
            }

            for (int i = 0; i < 20; i++) {
                float x = -9.5f + rand.nextFloat() * 19f;
                float z = -9.5f + rand.nextFloat() * 19f;
                sceneObjects.add(new GameObject(flowerMeshes, new Vector3f(x, 0f, z), new Vector3f(0f, rand.nextFloat() * 360f, 0f), new Vector3f(3.0f + rand.nextFloat() * 1.5f)));
            }

            for (int i = 0; i < 10; i++) {
                float x = -9.5f + rand.nextFloat() * 19f;
                float z = -9.5f + rand.nextFloat() * 19f;
                sceneObjects.add(new GameObject(mushroomMeshes, new Vector3f(x, 0f, z), new Vector3f(0f, rand.nextFloat() * 360f, 0f), new Vector3f(2.5f + rand.nextFloat() * 1.5f)));
            }

            for (int i = 0; i < 6; i++) {
                float x = -9.5f + rand.nextFloat() * 19f;
                float z = -9.5f + rand.nextFloat() * 19f;
                sceneObjects.add(new GameObject(logMeshes, new Vector3f(x, 0f, z), new Vector3f(0f, rand.nextFloat() * 360f, 0f), new Vector3f(3.5f + rand.nextFloat() * 1.5f)));
            }

            Garden garden = new Garden(20f, 20f);
            Formiga formiga = new Formiga(new Vector3f(0f, 0.10f, 3f));
            formiga.setVelocidade(1.0f);
            FormigaRenderer formigaRenderer = new FormigaRenderer();
            Formigueiro formigueiro = new Formigueiro();
            FormigueiroRenderer formigueiroRenderer = new FormigueiroRenderer();

            glfwShowWindow(window);
            double lastFrameTime = glfwGetTime();


            while (!GLFW.glfwWindowShouldClose(window)) {
                double currentFrameTime = glfwGetTime();
                float deltaTime = (float) (currentFrameTime - lastFrameTime);
                lastFrameTime = currentFrameTime;

                if (deltaTime > 0.1f) deltaTime = 0.1f;

                if (cursorPreso) {
                    processInput(window, camera, formiga, deltaTime);
                    camera.follow(formiga);

                    if (formigueiro.formigaChegou(formiga)) {
                        formigueiro.mudarLocal();
                    }
                }

                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                shader.setUniform("view", camera.getViewMatrix());
                shader.setUniform("viewPos", camera.getPosition());

                garden.render(shader);

                for (GameObject obj : sceneObjects) {
                    obj.render(shader);
                }

                formigueiroRenderer.render(formigueiro, shader);

                formigaRenderer.render(formiga, shader);

                glfwSwapBuffers(window);
                glfwPollEvents();
            }
            glfwDestroyWindow(window);

        } catch (Throwable t) {
            System.err.println("\n--- CRASH FATAL! ---");
            t.printStackTrace();
        } finally {
            glfwTerminate();
            if (errorCallback != null) errorCallback.free();
            if (mouseCallback != null) mouseCallback.free();
            if (keyCallback != null) keyCallback.free();
        }
    }

    private static void processInput(
            long window,
            Camera camera,
            Formiga formiga,
            float deltaTime
    ) {

        float frente = 0f;
        float lateral = 0f;

        // W

        if (
                glfwGetKey(
                        window,
                        GLFW_KEY_W
                ) == GLFW_PRESS
        ) {

            frente += 1f;
        }

        // S

        if (
                glfwGetKey(
                        window,
                        GLFW_KEY_S
                ) == GLFW_PRESS
        ) {

            frente -= 1f;
        }

        // A

        if (
                glfwGetKey(
                        window,
                        GLFW_KEY_A
                ) == GLFW_PRESS
        ) {

            lateral -= 1f;
        }

        // D

        if (
                glfwGetKey(
                        window,
                        GLFW_KEY_D
                ) == GLFW_PRESS
        ) {

            lateral += 1f;
        }

        // CALCULA DIREÇÃO DA CÂMERA

        Vector3f cameraForward = camera.getHorizontalFront();
        Vector3f cameraRight = camera.getRight();
        Vector3f direcao = new Vector3f();
        direcao.fma(frente, cameraForward);
        direcao.fma(lateral, cameraRight);

        if (direcao.lengthSquared() > 0.0001f) {
            direcao.normalize();
            formiga.mover(direcao, deltaTime);
        }
    }
}