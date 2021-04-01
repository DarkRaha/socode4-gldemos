package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.AppOGL;
import com.darkraha.opengldemoj.gl.GlUtils;
import com.darkraha.opengldemoj.gl.ShaderProgram;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;

import static org.lwjgl.opengl.GL33.*;

public class TexturedCubeRender extends Render {
    private Matrix4f matrix;
    private ShaderProgram prog;
    private float rotY = 0;
    private float rotX = 0;
    private int idVao;
    private int[] ids;
    private int idTexture;

    @Override
    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);

        prog = new ShaderProgram();
        glUseProgram(prog.idProgram);

        matrix = new Matrix4f();

        float[] coords = new float[]{
                // Front face
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,

                // Back face
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,

                // Top face
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,

                // Bottom face
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,

                // Right face
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,

                // Left face
                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, -1.0f};

        float[] texCoords = new float[]{
                // Front
                0.0f, 0.0f, //
                1.0f, 0.0f, //
                1.0f, 1.0f, //
                0.0f, 1.0f, //
                // Back
                0.0f, 0.0f, //
                1.0f, 0.0f, //
                1.0f, 1.0f, //
                0.0f, 1.0f, //
                // Right
                0.0f, 0.0f, //
                1.0f, 0.0f, //
                1.0f, 1.0f, //
                0.0f, 1.0f, //
                // Left
                0.0f, 0.0f, //
                1.0f, 0.0f, //
                1.0f, 1.0f, //
                0.0f, 1.0f, //
                // Top
                0.0f, 0.0f, //
                1.0f, 0.0f, //
                1.0f, 1.0f, //
                0.0f, 1.0f, //
                // Bottom
                0.0f, 0.0f, //
                1.0f, 0.0f, //
                1.0f, 1.0f, //
                0.0f, 1.0f, //
        };

        byte[] indices = new byte[]{
                0, 1, 2,/*        */ 0, 2, 3,    // front
                4, 5, 6,/*        */ 4, 6, 7,    // back
                8, 9, 10,/*       */8, 10, 11,   // top
                12, 13, 14,/*     */12, 14, 15,   // bottom
                16, 17, 18,/*     */16, 18, 19,   // right
                20, 21, 22,/*     */20, 22, 23,   // left
        };

        idTexture = GlUtils.loadTex2DResDefault(0, "/textures/235.jpg");
        glGenerateMipmap(idTexture);
        idVao = glGenVertexArrays();
        glBindVertexArray(idVao);
        ids = GlUtils.prepareVertexData(coords, null, texCoords, indices);
    }


    @Override
    public void onDrawFrame(AppOGL appOGL) {
        rotY += 1.5f * TO_RAD;
        if (rotY > 2 * PI_F) {
            rotY = rotY - 2 * PI_F;
        }

        rotX += 1f * TO_RAD;
        if (rotX > 2 * PI_F) {
            rotX = rotX - 2 * PI_F;
        }

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        int samplerLocation = glGetUniformLocation(prog.idProgram, "texSampler");
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, idTexture);
        glUniform1i(samplerLocation, 0);

        GlUtils.bindMatrix(prog.idProgram, matrix.identity()
                .perspective(45 * TO_RAD,
                        aspect,
                        1f, 100f)
                .translate(0, 0f, -6f)
                .rotateAffineXYZ(rotX, rotY, 0));

        glUseProgram(prog.idProgram);
        glUniform1i(glGetUniformLocation(prog.idProgram, "withTexture"), 1);
        glUniform4f(glGetUniformLocation(prog.idProgram, "uColor"), -1, -1, -1, -1);

        glBindVertexArray(idVao);
        glEnable(GL_CULL_FACE);
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE,0);

        // for demo
        //glBindVertexArray(idVao);
        //glEnable(GL_CULL_FACE);
        //glDrawElements(GL_TRIANGLES, 18, GL_UNSIGNED_BYTE,0);
    }

    @Override
    public void onDispose(AppOGL appOGL) {
        prog.dispose();
        glDeleteTextures(idTexture);
        glDeleteVertexArrays(idVao);
        glDeleteBuffers(ids);
    }
}
