package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class FormigueiroRenderer {
   
     private final PrimitiveMesh montinho;
    private final PrimitiveMesh entrada;
    private final PrimitiveMesh detalheTerra;

    public FormigueiroRenderer() {

        // Montinho principal
        montinho = PrimitiveMesh.createSphere(24, 24);

        // Entrada do formigueiro
        entrada = PrimitiveMesh.createSphere(16, 16);

        // Pequenos detalhes para dar aparência de terra
        detalheTerra = PrimitiveMesh.createSphere(10, 10);
    }

    public void render(Formigueiro formigueiro, Shader shader) {

        Vector3f pos = formigueiro.getPosition();

        // =====================================================
        // 1. MONTINHO DE TERRA
        // =====================================================

        Matrix4f modelMontinho = new Matrix4f()
                .translate(pos.x, pos.y, pos.z)
                .scale(0.9f, 0.38f, 0.9f);

        shader.setUniform("model", modelMontinho);

        // Cor marrom da terra
        shader.setUniform(
                "objectColor",
                new float[]{0.28f, 0.12f, 0.035f}
        );

        montinho.render();


        // =====================================================
        // 2. ENTRADA ESCURA
        // =====================================================

        // A entrada fica um pouco acima do centro do montinho
        Matrix4f modelEntrada = new Matrix4f()
                .translate(
                        pos.x,
                        pos.y + 0.34f,
                        pos.z
                )
                .scale(0.25f, 0.08f, 0.25f);

        shader.setUniform("model", modelEntrada);

        // Quase preto para representar o interior
        shader.setUniform(
                "objectColor",
                new float[]{0.025f, 0.012f, 0.006f}
        );

        entrada.render();


        // =====================================================
        // 3. DETALHES DE TERRA
        // =====================================================

        // Detalhe 1
        Matrix4f terra1 = new Matrix4f()
                .translate(
                        pos.x - 0.35f,
                        pos.y + 0.08f,
                        pos.z + 0.15f
                )
                .scale(0.12f, 0.06f, 0.09f);

        shader.setUniform("model", terra1);
        shader.setUniform(
                "objectColor",
                new float[]{0.20f, 0.075f, 0.02f}
        );

        detalheTerra.render();


        // Detalhe 2
        Matrix4f terra2 = new Matrix4f()
                .translate(
                        pos.x + 0.30f,
                        pos.y + 0.10f,
                        pos.z - 0.20f
                )
                .scale(0.10f, 0.05f, 0.08f);

        shader.setUniform("model", terra2);
        shader.setUniform(
                "objectColor",
                new float[]{0.35f, 0.16f, 0.05f}
        );

        detalheTerra.render();


        // Detalhe 3
        Matrix4f terra3 = new Matrix4f()
                .translate(
                        pos.x - 0.10f,
                        pos.y + 0.12f,
                        pos.z - 0.35f
                )
                .scale(0.08f, 0.04f, 0.07f);

        shader.setUniform("model", terra3);
        shader.setUniform(
                "objectColor",
                new float[]{0.18f, 0.065f, 0.018f}
        );

        detalheTerra.render();
    }
}
