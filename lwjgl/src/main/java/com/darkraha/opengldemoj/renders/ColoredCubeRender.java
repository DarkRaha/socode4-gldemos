package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.AppOGL;
import com.darkraha.opengldemoj.gl.GlUtils;
import com.darkraha.opengldemoj.gl.ShaderProgram;
import org.joml.Matrix4f;


import static org.lwjgl.opengl.GL33.*;


public class ColoredCubeRender extends Render {


    private Matrix4f matrix;
    private ShaderProgram prog;
    private float rotY = 0;
    private float rotX = 0;
    private int idVao;
    private int idVbo;
    private int idIbo;

    @Override
    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);

        prog = new ShaderProgram();
        glUseProgram(prog.idProgram);

        matrix = new Matrix4f();

        idVao = glGenVertexArrays();
        glBindVertexArray(idVao);

        idVbo = GlUtils.createVBO(new float[]{
                // coords             colors
                // front
                -1.0f, -1.0f, 1.0f, /*  */ 1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, 1.0f,  /*  */ 0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f,   /*  */ 0.0f, 0.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,  /*  */ 1.0f, 1.0f, 1.0f,
                // back
                -1.0f, -1.0f, -1.0f, /*  */ 1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, -1.0f,  /*  */ 0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, -1.0f,   /*  */ 0.0f, 0.0f, 1.0f,
                -1.0f, 1.0f, -1.0f,  /*  */ 1.0f, 1.0f, 1.0f});

        GlUtils.bindAttributes(idVbo, 3, 3, false);

        idIbo = GlUtils.createIBO(new byte[]{
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
        });

        glBindVertexArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
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

        GlUtils.bindMatrix(prog.idProgram, matrix.identity()
                .perspective(45 * TO_RAD,
                        aspect,
                        1f, 100f)
                .translate(0, 0f, -6f)
                .rotateAffineXYZ(rotX, rotY, 0));

        glUseProgram(prog.idProgram);
        glUniform1i(glGetUniformLocation(prog.idProgram, "withTexture"), 0);
        glUniform4f(glGetUniformLocation(prog.idProgram, "uColor"), -1, -1, -1, -1);

        glBindVertexArray(idVao);
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, 0);
    }

    @Override
    public void onDispose(AppOGL appOGL) {
        prog.dispose();
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDeleteVertexArrays(idVao);
        glDeleteBuffers(idVbo);
        glDeleteBuffers(idIbo);
    }
}
