package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class FormigaRenderer {

    private final PrimitiveMesh sphere;
    private final PrimitiveMesh cylinder;

    private static final float FORMIGA_SCALE = 0.6f;

    public FormigaRenderer() {

        sphere =
                PrimitiveMesh.createSphere(16, 16);

        cylinder =
                PrimitiveMesh.createCylinder(12);
    }

    public void render(
            Formiga formiga,
            Shader shader
    ) {

        Vector3f pos =
                formiga.getPosition();

        float rotation =
                formiga.getRotationY();

        // CABEÇA

        drawSphere(
                shader,
                pos,
                rotation,

                new Vector3f(
                        0f,
                        0.09f * FORMIGA_SCALE,
                        -0.14f * FORMIGA_SCALE
                ),

                new Vector3f(
                        0.065f * FORMIGA_SCALE,
                        0.065f * FORMIGA_SCALE,
                        0.065f * FORMIGA_SCALE
                )
        );

        // TÓRAX

        drawSphere(
                shader,
                pos,
                rotation,

                new Vector3f(
                        0f,
                        0.08f * FORMIGA_SCALE,
                        0f
                ),

                new Vector3f(
                        0.08f * FORMIGA_SCALE,
                        0.08f * FORMIGA_SCALE,
                        0.10f * FORMIGA_SCALE
                )
        );

        // ABDÔMEN

        drawSphere(
                shader,
                pos,
                rotation,

                new Vector3f(
                        0f,
                        0.09f * FORMIGA_SCALE,
                        0.15f * FORMIGA_SCALE
                ),

                new Vector3f(
                        0.10f * FORMIGA_SCALE,
                        0.085f * FORMIGA_SCALE,
                        0.13f * FORMIGA_SCALE
                )
        );

        // PERNAS

        drawLeg(
                shader,
                pos,
                rotation,
                -1,
                -0.08f * FORMIGA_SCALE
        );

        drawLeg(
                shader,
                pos,
                rotation,
                -1,
                0f
        );

        drawLeg(
                shader,
                pos,
                rotation,
                -1,
                0.08f * FORMIGA_SCALE
        );

        drawLeg(
                shader,
                pos,
                rotation,
                1,
                -0.08f * FORMIGA_SCALE
        );

        drawLeg(
                shader,
                pos,
                rotation,
                1,
                0f
        );

        drawLeg(
                shader,
                pos,
                rotation,
                1,
                0.08f * FORMIGA_SCALE
        );

        // ANTENAS

        drawAntenna(
                shader,
                pos,
                rotation,
                -1
        );

        drawAntenna(
                shader,
                pos,
                rotation,
                1
        );
    }

    // CORPO

    private void drawSphere(
            Shader shader,
            Vector3f formigaPos,
            float rotation,
            Vector3f localPosition,
            Vector3f scale
    ) {

        Vector3f worldPosition =
                new Vector3f(localPosition);

        rotateXZ(
                worldPosition,
                rotation
        );

        worldPosition.add(formigaPos);

        Matrix4f model =
                new Matrix4f()
                        .translate(worldPosition)
                        .rotateY(rotation)
                        .scale(scale);

        shader.setUniform(
                "model",
                model
        );

        shader.setUniform(
                "objectColor",
                new float[]{
                        0.12f,
                        0.045f,
                        0.015f
                }
        );

        sphere.render();
    }

    // PERNAS

    private void drawLeg(
            Shader shader,
            Vector3f formigaPos,
            float rotation,
            int side,
            float z
    ) {

        Vector3f localPosition =
                new Vector3f(
                        side * 0.075f * FORMIGA_SCALE,
                        0.045f * FORMIGA_SCALE,
                        z
                );

        rotateXZ(
                localPosition,
                rotation
        );

        Vector3f worldPosition =
                new Vector3f(localPosition)
                        .add(formigaPos);

        Matrix4f model =
                new Matrix4f()
                        .translate(worldPosition)
                        .rotateY(rotation)
                        .rotateZ(
                                (float)
                                        Math.toRadians(
                                                side * 65
                                        )
                        )
                        .scale(
                                0.018f * FORMIGA_SCALE,
                                0.09f * FORMIGA_SCALE,
                                0.018f * FORMIGA_SCALE
                        );

        shader.setUniform(
                "model",
                model
        );

        shader.setUniform(
                "objectColor",
                new float[]{
                        0.07f,
                        0.025f,
                        0.008f
                }
        );

        cylinder.render();
    }

    // ANTENAS

    private void drawAntenna(
            Shader shader,
            Vector3f formigaPos,
            float rotation,
            int side
    ) {

        Vector3f localPosition =
                new Vector3f(
                        side * 0.035f * FORMIGA_SCALE,
                        0.14f * FORMIGA_SCALE,
                        -0.18f * FORMIGA_SCALE
                );

        rotateXZ(
                localPosition,
                rotation
        );

        Vector3f worldPosition =
                new Vector3f(localPosition)
                        .add(formigaPos);

        Matrix4f model =
                new Matrix4f()
                        .translate(worldPosition)
                        .rotateY(rotation)
                        .rotateZ(
                                (float)
                                        Math.toRadians(
                                                side * 20
                                        )
                        )
                        .scale(
                                0.01f * FORMIGA_SCALE,
                                0.08f * FORMIGA_SCALE,
                                0.01f * FORMIGA_SCALE
                        );

        shader.setUniform(
                "model",
                model
        );

        shader.setUniform(
                "objectColor",
                new float[]{
                        0.07f,
                        0.025f,
                        0.008f
                }
        );

        cylinder.render();
    }

    // ROTAÇÃO

    private void rotateXZ(
            Vector3f vector,
            float angle
    ) {

        float x = vector.x;
        float z = vector.z;

        float cos =
                (float) Math.cos(angle);

        float sin =
                (float) Math.sin(angle);

        vector.x =
                x * cos - z * sin;

        vector.z =
                x * sin + z * cos;
    }
}