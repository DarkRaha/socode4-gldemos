package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.*;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL33.*;

public class ColoredQuadRender extends Render {
    private ShaderProgram prog;
    private int idVao;
    private int idVbo;

    @Override
    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        prog = new ShaderProgram();

        glUseProgram(prog.idProgram);
        glUniform1i(glGetUniformLocation(prog.idProgram, "withTexture"),0);
        glUniform4f(glGetUniformLocation(prog.idProgram, "uColor"), -1,-1,-1,-1);
        // if you want solid color
        // glUniform4f(glGetUniformLocation(prog.idProgram, "uColor"), 1,0,1,1);

        GlUtils.bindMatrix(prog.idProgram,
                new Matrix4f()
                        .identity()
                        .perspective(ALNGLE45, aspect, 0.01f, 100f)
                        .translate(0f, 0f, -6f)
        );

        idVao = glGenVertexArrays();
        glBindVertexArray(idVao);

        idVbo = GlUtils.createVBO(
                new float[]{
                        // coord      color
                        -1.0f, 1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                        -1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
        }
        );

       GlUtils.bindAttributes(idVbo, 2,3,false);
    }


    @Override
    public void onDrawFrame(AppOGL appOGL) {
        super.onDrawFrame(appOGL);

        glUseProgram(prog.idProgram);
        glBindVertexArray(idVao);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glUseProgram(0);
    }

    @Override
    public void onDispose(AppOGL appOGL) {
        prog.dispose();
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteVertexArrays(idVao);
        glDeleteBuffers(idVbo);
        super.onDispose(appOGL);
    }
}
