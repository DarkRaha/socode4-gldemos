package com.darkraha.opengldemoj.gl;

import com.sun.prism.Texture;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Matrix4f;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;

public class GlUtils {


    public static final int A_LOCATION_COORDS = 0;
    public static final int A_LOCATION_COLORS = 1;
    public static final int A_LOCATION_NORMALS = 2;
    public static final int A_LOCATION_TEXCOORDS = 3;


    /**
     * Create 2D texture and upload data to it.
     *
     * @param idTex
     * @param imageData
     * @param width
     * @param height
     * @return
     */
    public static int createTexture(int idTex, ByteBuffer imageData,
                                    int width, int height) {
        int idTexture = (idTex == 0) ? glGenTextures() : idTex;

        glBindTexture(GL_TEXTURE_2D, idTexture);

        glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, imageData
        );

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        return idTexture;
    }

    public static int loadTex2DResDefault(int idTex, String resPath) {
        int idTexture = 0;
        try (MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            URL resource = Thread.currentThread().getClass().getResource(resPath);
            String filePath = new File(resource.toURI()).getAbsolutePath(); // encode spaces
            ByteBuffer buffer = STBImage.stbi_load(filePath, w, h, channels, 4);

            if (buffer == null) {
                throw new Exception("Can't load file " + filePath + " " + stbi_failure_reason());
            }

            idTexture = createTexture(idTex, buffer, w.get(), h.get());
            stbi_image_free(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idTexture;
    }


    public static int createVAO() {
        int idVao = glGenVertexArrays();
        glBindVertexArray(idVao);
        return idVao;
    }

    public static int createVBO(float[] data) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            int idVbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, idVbo);

            FloatBuffer fb = stack.mallocFloat(data.length);
            fb.put(data).flip();
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
            return idVbo;
        }
    }


    public static int createVBO(float[] data, int locations, int size) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            int idVbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, idVbo);

            FloatBuffer fb = stack.mallocFloat(data.length);
            fb.put(data).flip();
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);

            glEnableVertexAttribArray(locations);
            glVertexAttribPointer(locations,
                    size, GL_FLOAT, false,
                    0, 0);

            return idVbo;
        }
    }


    /**
     * @param coords    x,y,z
     * @param colors    r,g,b,a
     * @param texcoords s,t
     * @param indices
     * @return
     */
    public static int[] prepareVertexData(float[] coords, float[] colors, float[] texcoords, byte[] indices) {
        return prepareVertexData(coords, colors, texcoords, indices, null);
    }


    public static int[] prepareVertexData(float[] coords, float[] colors, float[] texcoords, byte[] indices, float[] normals) {

        int[] ret = new int[5];

        if (coords != null) {
            ret[0] = createVBO(coords);
            glEnableVertexAttribArray(A_LOCATION_COORDS);
            glVertexAttribPointer(A_LOCATION_COORDS,
                    3, GL_FLOAT, false,
                    0, 0);
        }

        if (colors != null) {
            ret[1] = createVBO(colors);
            glEnableVertexAttribArray(A_LOCATION_COLORS);
            glVertexAttribPointer(A_LOCATION_COLORS,
                    4, GL_FLOAT, false,
                    0, 0);
        }

        if (texcoords != null) {
            ret[2] = createVBO(texcoords);
            glEnableVertexAttribArray(A_LOCATION_TEXCOORDS);
            glVertexAttribPointer(A_LOCATION_TEXCOORDS,
                    2, GL_FLOAT, false,
                    0, 0);
        }

        if (indices != null) {
            ret[3] = createIBO(indices);
        }

        if (normals != null) {
            ret[4] = createVBO(normals);
            glEnableVertexAttribArray(A_LOCATION_NORMALS);
            glVertexAttribPointer(A_LOCATION_NORMALS,
                    3, GL_FLOAT, false,
                    0, 0);
        }
        return ret;
    }

    public static int createIBO(byte[] data) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer byteBuffer = stack.malloc(data.length);
            byteBuffer.put(data).flip();
            int idIbo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, byteBuffer, GL_STATIC_DRAW);
            return idIbo;
        }
    }


    public static int createIBO(int[] data) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer byteBuffer = stack.malloc(data.length * 4).asIntBuffer();
            byteBuffer.put(data).flip();
            int idIbo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, byteBuffer, GL_STATIC_DRAW);
            return idIbo;
        }
    }

    public static void bindAttributes(int idVBO, int numCoords, int numColorComponents, boolean texCoord) {

        glBindBuffer(GL_ARRAY_BUFFER, idVBO);

        // size of float is 4
        int stride = 4 * (numCoords + numColorComponents + ((texCoord) ? 2 : 0));

        if (numCoords > 0) {
            glEnableVertexAttribArray(A_LOCATION_COORDS);
            glVertexAttribPointer(A_LOCATION_COORDS,
                    numCoords, GL_FLOAT, false,
                    stride, 0);
        }

        if (numColorComponents > 0) {
            glEnableVertexAttribArray(A_LOCATION_COLORS);
            glVertexAttribPointer(A_LOCATION_COLORS,
                    numColorComponents, GL_FLOAT, true,
                    stride, 4L * numCoords);
        }

        if (texCoord) {
            glEnableVertexAttribArray(A_LOCATION_TEXCOORDS);
            glVertexAttribPointer(A_LOCATION_TEXCOORDS,
                    2, GL_FLOAT, false,
                    stride, 4L * (numCoords + numColorComponents));
        }
    }

}
