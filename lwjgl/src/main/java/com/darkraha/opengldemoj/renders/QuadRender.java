package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.*;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

/**
 * Render white quad. Demonstrate how to use VBO.
 * In OpenGL 3, VBO may not work standalone without VAO.
 * I decided to reject from shaders version 120 and just added VAO.
 * <p>
 * In this simple example you can setup
 */
public class QuadRender extends Render {
    private final FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);
    protected final Matrix4f matrix = new Matrix4f();
    private ShaderProgram prog;
    private int idVbo;
    private int idVao;
    private float[] solidColor = new float[]{1f, 1f, 1f, 1f};

    @Override
    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);

        prog = new ShaderProgramBuilder().matrix().solidColor().build();
        glUseProgram(prog.idProgram);

        //-----------------------------------------------
        // prepare data for VBO, positions of the quad vertices
        float[] data = new float[]{
                -1.0f, 1.0f,
                1.0f, 1.0f,
                -1.0f, -1.0f,
                1.0f, -1.0f};

        idVao = glGenVertexArrays();
        glBindVertexArray(idVao);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            idVbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, idVbo);

            FloatBuffer fb = stack.mallocFloat(data.length);
            fb.put(data).flip();
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
        }

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0,
                2, GL_FLOAT, false,
                0, 0);

        glBindVertexArray(0);
    }


    @Override
    public void onDrawFrame(AppOGL appOGL) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //--------------------------------------------------
        // in this simple example you can move the following piece of code
        // to the end of onSetup()
        glUseProgram(prog.idProgram);

        matrix.identity()
                .perspective(ALNGLE45, aspect, 0.01f, 100f)
                .translate(0f, 0f, -6f);

        // bind final transformation matrix to the shader matrix variable
        glUniformMatrix4fv(glGetUniformLocation(prog.idProgram, "m"),
                false, matrix.get(matrixBuffer));

        glUniform4fv(prog.solidColorLocation, solidColor);
        glBindVertexArray(idVao);
        // end
        //--------------------------------------------------

        //glBindBuffer(GL_VERTEX_ARRAY, idVbo); // for shader version 120
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
