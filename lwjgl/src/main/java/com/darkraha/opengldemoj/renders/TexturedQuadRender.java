package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.*;
import com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL33.*;

/**
 * Render textured quad. Demonstrate how to apply 2D texture to the surface.
 * It uses class GlTexture, that has methods for loading graphic resources via STBI library.
 */
public class TexturedQuadRender extends Render {

    private GlTexture texture;
    private int idVao;
    private int idVbo;
    protected Matrix4f matrix;
    private ShaderProgram prog;
    private float rotY = 1.5f * TO_RAD;
    private float rotX = TO_RAD;

    @Override
    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);

        prog = new ShaderProgramBuilder()
                .texture2D()
                .matrix()
                .build();

        matrix = new Matrix4f()
                .perspective(ALNGLE45, aspect, 1f, 100f)
                .translate(0f, 0f, -6f);

        glUseProgram(prog.idProgram);

        texture = GlTexture.newTexture2D( "/textures/235.jpg", "test");

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
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUseProgram(prog.idProgram);

        //-----------------------------------------------
        // activate our texture
        // you can use replace it by prog.uniformTexture(texture);
        int samplerLocation = glGetUniformLocation(prog.idProgram, ShaderProgramBuilder.U_SAMPLER_NAME);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.idTexture);
        glUniform1i(samplerLocation, 0);

        matrix.rotateAffineXYZ(rotX, rotY, 0);

        prog.uniformMatrix(matrix);
        glBindVertexArray(idVao);

        // glEnable(GL_CULL_FACE);
        glDrawArrays(GL_TRIANGLES, 0, 6); // vertex count
    }

    @Override
    public void onDispose(AppOGL appOGL) {
        glUseProgram(0);
        prog.dispose();
        texture.dispose();
        glDeleteVertexArrays(idVao);
        glDeleteBuffers(idVbo);
    }
}
