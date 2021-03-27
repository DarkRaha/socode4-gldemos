package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.AppOGL;
import com.darkraha.opengldemoj.gl.GlUtils;
import com.darkraha.opengldemoj.gl.ShaderProgram;


import org.joml.Matrix4f;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;

import static org.lwjgl.opengl.GL33.*;


public class TexturedQuadRender extends Render {

    private int idTexture;
    private int idVao;
    private int idVbo;
    protected Matrix4f matrix;
    private ShaderProgram prog;
    private float rotY = 0;
    private float rotX = 0;

    @Override
    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);


        prog = new ShaderProgram();
        glUseProgram(prog.idProgram);

        matrix = new Matrix4f();

        idTexture = GlUtils.loadTex2DResDefault("/textures/235.jpg");
        idVao = GlUtils.createVAO();
        idVbo = GlUtils.createVBO(new float[]{
                // coords         texture coords
                -1.0f, -1.0f, /**/ 0.0f, 1.0f,
                1.0f, -1.0f,  /**/1.0f, 1.0f,
                1.0f, 1.0f,   /**/1.0f, 0.0f,

                1.0f, 1.0f,   /**/1.0f, 0.0f,
                -1.0f, 1.0f,  /**/0.0f, 0.0f,
                -1.0f, -1.0f, /**/0.0f, 1.0f
        });
        GlUtils.bindAttributes(idVbo, 2, 0, true);
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

        glUseProgram(prog.idProgram);
        glUniform1i(glGetUniformLocation(prog.idProgram, "withTexture"), 1);
        glUniform4f(glGetUniformLocation(prog.idProgram, "uColor"), -1, -1, -1, -1);


        int samplerLocation = glGetUniformLocation(prog.idProgram, "texSampler");
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, idTexture);
        glUniform1i(samplerLocation, 0);

        matrix.identity()
                .perspective(ALNGLE45, aspect, 1f, 100f)
                .translate(0f, 0f, -6f)
                .rotateAffineXYZ(rotX, rotY, 0);

        GlUtils.bindMatrix(prog.idProgram, matrix);
        glBindVertexArray(idVao);

        glEnable(GL_CULL_FACE);
        glDrawArrays(GL_TRIANGLES, 0, 6); // vertex count
    }

    @Override
    public void onDispose(AppOGL appOGL) {
        glUseProgram(0);
        prog.dispose();
        glDeleteTextures(idTexture);
        glDeleteVertexArrays(idVao);
        glDeleteBuffers(idVbo);
    }
}
