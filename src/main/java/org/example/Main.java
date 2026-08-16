package org.example;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

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

        // INPUT

        glfwSetInputMode(
                window,
                GLFW_CURSOR,
                GLFW_CURSOR_DISABLED
        );


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
                                0.15f,
                                3f
                        )
                );

        FormigaRenderer formigaRenderer =
                new FormigaRenderer();

        // LOOP PRINCIPAL

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
                                    currentFrameTime
                                            - lastFrameTime
                            );

            lastFrameTime =
                    currentFrameTime;

            // INPUT

            processInput(
                    window,
                    formiga,
                    deltaTime
            );

            // CÂMERA

            camera.follow(formiga);

            // LIMPA A TELA

            glClear(
                    GL_COLOR_BUFFER_BIT
                            | GL_DEPTH_BUFFER_BIT
            );

            // MATRIZES DA CÂMERA

            shader.setUniform(
                    "view",
                    camera.getViewMatrix()
            );

            shader.setUniform(
                    "viewPos",
                    camera.getPosition()
            );

            // RENDERIZA PEDRAS

            for (GameObject obj : sceneObjects) {

                obj.render(shader);
            }

            // RENDERIZA FORMIGA

            formigaRenderer.render(
                    formiga,
                    shader
            );

            // ATUALIZA TELA

            glfwSwapBuffers(window);

            glfwPollEvents();
        }

        // ENCERRA

        glfwDestroyWindow(window);

        glfwTerminate();
    }

    // CONTROLES DA FORMIGA

    private static void processInput(
            long window,
            Formiga formiga,
            float deltaTime
    ) {


        if (
                glfwGetKey(
                        window,
                        GLFW_KEY_W
                ) == GLFW_PRESS
        ) {

            formiga.andar(
                    1f,
                    deltaTime
            );
        }


        if (
                glfwGetKey(
                        window,
                        GLFW_KEY_S
                ) == GLFW_PRESS
        ) {

            formiga.andar(
                    -1f,
                    deltaTime
            );
        }


        if (
                glfwGetKey(
                        window,
                        GLFW_KEY_A
                ) == GLFW_PRESS
        ) {

            formiga.girar(
                    -1f,
                    deltaTime
            );
        }


        if (
                glfwGetKey(
                        window,
                        GLFW_KEY_D
                ) == GLFW_PRESS
        ) {

            formiga.girar(
                    1f,
                    deltaTime
            );
        }
    }
}