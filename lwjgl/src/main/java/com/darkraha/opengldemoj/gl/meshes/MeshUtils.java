package com.darkraha.opengldemoj.gl.meshes;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public class MeshUtils {


    /**
     * @return array for 4 vertices with 5 components 2 coordinates, 3 color
     */
    public static float[] newColoredQuadArray() {
        return new float[]{
                // coord      color
                -1.0f, 1.0f, 1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
        };
    }

    /**
     * @return array for 4 vertices with 5 components 2 coordinates, 3 color
     */
    public static float[] newTexturedQuadArray() {
        return new float[]{
                // coord      uv
                -1.0f, 1.0f,   1.0f, 0.0f, 0.0f,
                1.0f, 1.0f,    0.0f, 1.0f, 0.0f,
                -1.0f, -1.0f,  0.0f, 0.0f, 1.0f,
                1.0f, -1.0f,   1.0f, 1.0f, 1.0f,
        };
    }



    /**
     * @return array for 8 vertices with 6 components, 3 coordinates, 3 color
     */
    public static float[] newColoredCubeArray() {
        return new float[]{
                -1.0f, -1.0f, 1.0f,  1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, 1.0f,   0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f,    0.0f, 0.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,   1.0f, 1.0f, 1.0f,
                // back
                -1.0f, -1.0f, -1.0f,  1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, -1.0f,   0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, -1.0f,    0.0f, 0.0f, 1.0f,
                -1.0f, 1.0f, -1.0f,   1.0f, 1.0f, 1.0f};
    }

    public static byte[] newCubeInd() {
        return new byte[]{
                // front
                0, 1, 2,
                2, 3, 0,
                // right
                1, 5, 6,
                6, 2, 1,
                // back
                7, 6, 5,
                5, 4, 7,
                // left
                4, 0, 3,
                3, 7, 4,
                // bottom
                4, 5, 1,
                1, 0, 4,
                // top
                3, 2, 6,
                6, 7, 3
        };
    }


    public static MeshI getColoredCube() {
        return loadColoredMeshI(newColoredCubeArray(), newCubeInd(), 8,
                3, 3, GL_TRIANGLES);

    }

    public static MeshI loadColoredMeshI(float[] vdata, byte[] indices, int numVertex,
                                         int numCoords,
                                         int numColors, int drawMode) {

        int idVbo;
        int idIbo;
        int idVao = glGenVertexArrays();
        glBindVertexArray(idVao);

        int stride = Float.BYTES * (numCoords + numColors);

        try (MemoryStack stack = MemoryStack.stackPush()) {

            FloatBuffer fb = stack.mallocFloat(vdata.length);
            fb.put(vdata).flip();

            idVbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, idVbo);
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0,
                    numCoords, GL_FLOAT, false,
                    stride, 0);

            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, numColors,
                    GL_FLOAT, false, stride,
                    Float.BYTES * numCoords);

            glBindVertexArray(0);

            ByteBuffer byteBuffer = stack.malloc(indices.length);
            byteBuffer.put(indices).flip();
            idIbo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, byteBuffer, GL_STATIC_DRAW);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        }

        return new MeshI(idVao, idVbo, idIbo, numVertex, indices.length, drawMode);
    }


    public static Mesh loadColoredMesh(float[] array, int numVertex, int numCoords, int numColors) {
        return loadColoredMesh(array, numVertex, numCoords, numColors, GL_TRIANGLE_STRIP);
    }

    public static Mesh loadColoredMesh(float[] array, int numVertex, int numCoords, int numColors, int drawMode) {
        int idVbo = 0;
        int idVao = glGenVertexArrays();
        glBindVertexArray(idVao);

        int stride = Float.BYTES * (numCoords + numColors);

        try (MemoryStack stack = MemoryStack.stackPush()) {

            FloatBuffer fb = stack.mallocFloat(array.length);
            fb.put(array).flip();

            idVbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, idVbo);
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0,
                    numCoords, GL_FLOAT, false,
                    stride, 0);

            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, numColors,
                    GL_FLOAT, true, stride,
                    Float.BYTES * numCoords);

            glBindVertexArray(0);
        }

        return new Mesh(idVao, idVbo, numVertex, drawMode);
    }


}
