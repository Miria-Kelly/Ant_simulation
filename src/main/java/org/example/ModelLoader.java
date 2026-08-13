package org.example;

import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {

    // cache: um mesmo .obj só é lido/parseado do disco uma vez,
    // mesmo que você crie várias instâncias (GameObject) dele depois.
    private static final Map<String, List<Mesh>> cache = new HashMap<>();

    public static List<Mesh> loadModel(String resourcePath) {

        if (cache.containsKey(resourcePath)) {
            return cache.get(resourcePath);
        }

        try {

            var resource =
                    ModelLoader.class
                            .getResource(resourcePath);

            if (resource == null) {
                throw new RuntimeException(
                        "Modelo não encontrado: " + resourcePath
                );
            }

            Path modelPath =
                    Path.of(resource.toURI());

            System.out.println(
                    "Carregando modelo de: "
                            + modelPath
            );

            AIScene scene = aiImportFile(
                    modelPath.toString(),
                    aiProcess_Triangulate |
                            aiProcess_FlipUVs
            );

            if (scene == null || scene.mNumMeshes() == 0) {

                throw new RuntimeException(
                        "Assimp não conseguiu carregar o modelo."
                );
            }

            List<Mesh> meshes =
                    new ArrayList<>();

            for (int i = 0;
                 i < scene.mNumMeshes();
                 i++) {

                AIMesh aiMesh =
                        AIMesh.create(
                                scene.mMeshes().get(i)
                        );

                System.out.println(
                        "Mesh " + i +
                                " | vertices: " +
                                aiMesh.mNumVertices() +
                                " | faces: " +
                                aiMesh.mNumFaces()
                );

                meshes.add(new Mesh(aiMesh, scene));
            }

            System.out.println(
                    "Modelo carregado com sucesso!"
            );

            System.out.println(
                    "Quantidade de meshes: "
                            + meshes.size()
            );

            cache.put(resourcePath, meshes);

            return meshes;

        } catch (URISyntaxException e) {

            throw new RuntimeException(
                    "Erro ao encontrar o modelo.",
                    e
            );
        }
    }
}