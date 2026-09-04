package org.example;

import org.joml.Vector3f;
import java.util.Random;

public class Formigueiro {
     private final Vector3f position;
    private final Random random = new Random();

    private static final float LIMITE = 9.0f;
    private static final float DISTANCIA_CHEGADA = 0.7f;

    public Formigueiro() {
        position = new Vector3f();
        mudarLocal();
    }

    public void mudarLocal() {

        position.x =
                -LIMITE + random.nextFloat() * (LIMITE * 2);

        position.y = 0.10f;

        position.z =
                -LIMITE + random.nextFloat() * (LIMITE * 2);
    }

    public boolean formigaChegou(Formiga formiga) {

        float dx =
                position.x - formiga.getPosition().x;

        float dz =
                position.z - formiga.getPosition().z;

        float distanciaQuadrada =
                dx * dx + dz * dz;

        return distanciaQuadrada <=
                DISTANCIA_CHEGADA * DISTANCIA_CHEGADA;
    }

    public Vector3f getPosition() {
        return position;
    }
}
