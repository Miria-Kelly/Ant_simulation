package org.example;

import org.joml.Vector3f;

public class Formiga {

    private final Vector3f position;

    private float rotationY;

    private float targetRotationY;

    private float velocidade = 1.0f;

    private int vida = 100;

    public Formiga(Vector3f position) {

        this.position = new Vector3f(position);

        this.rotationY = 0f;
        this.targetRotationY = 0f;
    }

    // MOVIMENTAÇÃO

    public void mover(Vector3f direcao, float deltaTime) {

        if (direcao.lengthSquared() < 0.0001f) {
            return;
        }

        Vector3f movimento =
                new Vector3f(
                        direcao.x,
                        0f,
                        direcao.z
                );

        if (movimento.lengthSquared() < 0.0001f) {
            return;
        }

        movimento.normalize();

        // MOVIMENTA

        position.x +=
                movimento.x *
                velocidade *
                deltaTime;

        position.z +=
                movimento.z *
                velocidade *
                deltaTime;

        // DIREÇÃO PARA ONDE A FORMIGA DEVE OLHAR

        targetRotationY =
                (float) Math.atan2(
                        movimento.x,
                        -movimento.z
                );

        // ROTAÇÃO SUAVE

        float diferenca =
                targetRotationY -
                rotationY;

        while (diferenca > Math.PI) {
            diferenca -=
                    2f * (float) Math.PI;
        }

        while (diferenca < -Math.PI) {
            diferenca +=
                    2f * (float) Math.PI;
        }

        float velocidadeRotacao = 10f;

        float fator =
                Math.min(
                        velocidadeRotacao * deltaTime,
                        1f
                );

        rotationY +=
                diferenca * fator;
    }

    // VIDA

    public void receberDano(int dano) {

        vida -= dano;

        if (vida < 0) {
            vida = 0;
        }
    }

    public boolean estaViva() {
        return vida > 0;
    }

    // GETTERS

    public Vector3f getPosition() {
        return position;
    }

    public float getRotationY() {
        return rotationY;
    }

    public float getVelocidade() {
        return velocidade;
    }

    public int getVida() {
        return vida;
    }

    // SETTERS

    public void setVelocidade(float velocidade) {
        this.velocidade = velocidade;
    }
}