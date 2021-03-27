package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.AppOGL;
import com.darkraha.opengldemoj.gl.GlUtils;
import com.darkraha.opengldemoj.gl.ShaderProgram;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL33.*;

/**
 * Render white quad using without VAO, may not work on OpenGL 3.
 */
public class QuadRender extends Render {

    protected Matrix4f matrix;
    private ShaderProgram prog;
    private int idVbo;

    @Override
    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        prog = new ShaderProgram(ShaderProgram.VERTEX_SHADER_120, ShaderProgram.FRAGMENT_SHADER_120);

        glUseProgram(prog.idProgram);

        idVbo = GlUtils.createVBO(new float[]{
                -1.0f, 1.0f,
                1.0f, 1.0f,
                -1.0f, -1.0f,
                1.0f, -1.0f});

        GlUtils.bindAttributes(idVbo, 2, 0, false);
    }


    @Override
    public void onDrawFrame(AppOGL appOGL) {
        super.onDrawFrame(appOGL);
        glUseProgram(prog.idProgram);

        GlUtils.bindMatrix(prog.idProgram,
                new Matrix4f()
                        .identity()
                        .perspective(ALNGLE45, aspect, 0.01f, 100f)
                        .translate(0f, 0f, -6f)
        );
        glBindBuffer(GL_ARRAY_BUFFER, idVbo);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4); // vertex count

    }

    @Override
    public void onDispose(AppOGL appOGL) {
        prog.dispose();
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(idVbo);
        super.onDispose(appOGL);
    }
}
