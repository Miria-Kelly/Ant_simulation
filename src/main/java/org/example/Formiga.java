package org.example;

import org.joml.Vector3f;

public class Formiga {

    private final Vector3f position;

    private float rotationY;

    private float velocidade;
    private int vida;

    public Formiga(Vector3f position) {

        this.position = new Vector3f(position);
        this.rotationY = 0f;

        this.velocidade = 1.5f;
        this.vida = 100;
    }

  
    // MOVIMENTAÇÃO


    public void andar(float direcao, float deltaTime) {


        float x = (float) Math.sin(rotationY);
        float z = (float) -Math.cos(rotationY);

        position.x += x * velocidade * direcao * deltaTime;
        position.z += z * velocidade * direcao * deltaTime;
    }

    public void girar(float direcao, float deltaTime) {

        float velocidadeRotacao = 2.5f;

        rotationY +=
                direcao * velocidadeRotacao * deltaTime;
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