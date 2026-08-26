package org.example;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    // MOUSE

    private static float lastX = 400f;
    private static float lastY = 300f;

    private static boolean firstMouse = true;

    public static void main(String[] args) {

        // GLFW

        if (!GLFW.glfwInit()) {

            throw new IllegalStateException(
                    "Não foi possível inicializar o GLFW"
            );
        }

        long window =
                GLFW.glfwCreateWindow(
                        800,
                        600,
                        "Ant Simulation",
                        0,
                        0
                );

        if (window == 0) {

            throw new RuntimeException(
                    "Falha ao criar a janela"
            );
        }

        GLFW.glfwMakeContextCurrent(window);

        // OPENGL

        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);

        glDisable(GL_CULL_FACE);

        glClearColor(
                0.1f,
                0.1f,
                0.1f,
                1.0f
        );

        // SHADER

        Shader shader =
                new Shader(
                        "/shaders/vertex.glsl",
                        "/shaders/fragment.glsl"
                );

        shader.use();

        shader.setUniform(
                "lightPos",
                new Vector3f(
                        2f,
                        5f,
                        2f
                )
        );

        shader.setUniform(
                "lightColor",
                new Vector3f(
                        1f,
                        1f,
                        1f
                )
        );

        // CÂMERA

        Camera camera =
                new Camera();

        camera.setDistance(0.8f);
        camera.setHeight(0.3f);

        var projection =
                new org.joml.Matrix4f()
                        .perspective(
                                (float)
                                        Math.toRadians(70),
                                800f / 600f,
                                0.01f,
                                100f
                        );

        shader.setUniform(
                "projection",
                projection
        );

        // MOUSE

        glfwSetInputMode(
                window,
                GLFW_CURSOR,
                GLFW_CURSOR_DISABLED
        );

        glfwSetCursorPosCallback(
                window,
                (win, xpos, ypos) -> {

                    if (firstMouse) {

                        lastX =
                                (float) xpos;

                        lastY =
                                (float) ypos;

                        firstMouse = false;
                    }

                    float xOffset =
                            (float) xpos -
                            lastX;

                    float yOffset =
                            lastY -
                            (float) ypos;

                    lastX =
                            (float) xpos;

                    lastY =
                            (float) ypos;

                    camera.processMouseMovement(
                            xOffset,
                            yOffset
                    );
                }
        );

        // ESC

        glfwSetKeyCallback(
                window,
                (win, key, scancode, action, mods) -> {

                    if (
                            key == GLFW_KEY_ESCAPE
                                    && action == GLFW_PRESS
                    ) {

                        glfwSetWindowShouldClose(
                                win,
                                true
                        );
                    }
                }
        );

        // PEDRAS

        List<Mesh> rockMeshes =
                ModelLoader.loadModel(
                        "/models/rock_tallJ.obj"
                );

        List<GameObject> sceneObjects =
                List.of(

                        new GameObject(
                                rockMeshes,
                                new Vector3f(
                                        0f,
                                        0f,
                                        0f
                                ),
                                new Vector3f(0),
                                new Vector3f(1.5f)
                        ),

                        new GameObject(
                                rockMeshes,
                                new Vector3f(
                                        2.5f,
                                        0f,
                                        -1f
                                ),
                                new Vector3f(0),
                                new Vector3f(1f)
                        ),

                        new GameObject(
                                rockMeshes,
                                new Vector3f(
                                        -2f,
                                        0f,
                                        1f
                                ),
                                new Vector3f(0),
                                new Vector3f(0.8f)
                        )
                );

        // FORMIGA

        Formiga formiga =
                new Formiga(
                        new Vector3f(
                                0f,
                                0.10f,
                                3f
                        )
                );

        formiga.setVelocidade(1.0f);

        // RENDERIZADOR DA FORMIGA

        FormigaRenderer formigaRenderer =
                new FormigaRenderer();

        // LOOP

        glfwShowWindow(window);

        double lastFrameTime =
                glfwGetTime();

        while (
                !GLFW.glfwWindowShouldClose(window)
        ) {

            // DELTA TIME

            double currentFrameTime =
                    glfwGetTime();

            float deltaTime =
                    (float)
                            (
                                    currentFrameTime -
                                    lastFrameTime
                            );

            lastFrameTime =
                    currentFrameTime;

            if (deltaTime > 0.1f) {
                deltaTime = 0.1f;
            }

            // INPUT

            processInput(
                    window,
                    camera,
                    formiga,
                    deltaTime
            );

            // CÂMERA SEGUE A FORMIGA

            camera.follow(formiga);

            // LIMPA A TELA

            glClear(
                    GL_COLOR_BUFFER_BIT |
                    GL_DEPTH_BUFFER_BIT
            );

            // VIEW

            shader.setUniform(
                    "view",
                    camera.getViewMatrix()
            );

            shader.setUniform(
                    "viewPos",
                    camera.getPosition()
            );

            // PEDRAS

            for (GameObject obj : sceneObjects) {

                obj.render(shader);
            }

            // FORMIGA

            formigaRenderer.render(
                    formiga,
                    shader
            );

            // ATUALIZA

            glfwSwapBuffers(window);

            glfwPollEvents();
        }

        glfwDestroyWindow(window);

        glfwTerminate();
    }

    // CONTROLE DA FORMIGA

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

            lateral += 1f;
        }

        // D

        if (
                glfwGetKey(
                        window,
                        GLFW_KEY_D
                ) == GLFW_PRESS
        ) {

            lateral -= 1f;
        }

        // CALCULA DIREÇÃO DA CÂMERA

        Vector3f cameraForward =
                camera.getHorizontalFront();

        Vector3f cameraRight =
                camera.getRight();

        Vector3f direcao =
                new Vector3f();

        // W/S
        direcao.fma(
                frente,
                cameraForward
        );

        // A/D
        direcao.fma(
                lateral,
                cameraRight
        );

        // MOVIMENTA A FORMIGA

        if (direcao.lengthSquared() > 0.0001f) {

            direcao.normalize();

            formiga.mover(
                    direcao,
                    deltaTime
            );
        }
    }
}