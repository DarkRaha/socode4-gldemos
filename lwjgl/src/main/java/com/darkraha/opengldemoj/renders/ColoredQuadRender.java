package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.*;
import com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

/**
 * Render colored quad. Demonstrate how to use VAO with single VBO that
 * contains multiple vertex attributes (in our case vertex positions and vertex colors).
 */
public class ColoredQuadRender extends Render {
    private final FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);
    private ShaderProgram prog;
    private int idVao;
    private int idVbo;

    @Override
    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        prog = new ShaderProgramBuilder()
                .colors(true,false)
                .build();

        glUseProgram(prog.idProgram);

        //-----------------------------------------------
        // bind final transformation matrix to the shader matrix variable
        Matrix4f matrix = new Matrix4f()
                .identity() // initially matrix identity, so you can skip it
                .perspective(45 * TO_RAD, aspect, 1f, 100f)
                .translate(0, 0f, -6f); // make quad before viewer

        glUniformMatrix4fv(glGetUniformLocation(prog.idProgram, ShaderProgramBuilder.U_MATRIX_NAMES[0]),
                false, matrix.get(matrixBuffer));


        //-----------------------------------------------
        // prepare data for VBO
        float[] data = new float[]{
                // x,y             rgb color
                -1.0f, 1.0f,/*   */ 1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, /*   */0.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, /*   */0.0f, 0.0f, 1.0f,
                1.0f, -1.0f, /*   */1.0f, 1.0f, 1.0f,
        };

        //-----------------------------------------------
        // create VAO
        idVao = glGenVertexArrays();
        glBindVertexArray(idVao);

        //-----------------------------------------------
        // create VBO and upload data into it. It will be attached to the current VAO.

        try (MemoryStack stack = MemoryStack.stackPush()) {
            idVbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, idVbo);

            FloatBuffer fb = stack.mallocFloat(data.length);
            fb.put(data).flip();
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
        }


        //-----------------------------------------------
        // specify locations of attributes in data
        int stride = 4 * (2 + 3); // 4 - size of float in bytes, 2 - x,y, 3 - r,g,b
        glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_POS);
        glVertexAttribPointer(ShaderProgramBuilder.A_LOCATION_VERTEX_POS,
                2, GL_FLOAT, false,
                stride, 0);

        int colorOffset = 4 * 2; // 4 - size of float in bytes, 2 - x,y
        glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR);
        glVertexAttribPointer(ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR,
                3, GL_FLOAT, false, stride, colorOffset);

        glBindVertexArray(0); // deactivate VAO
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
