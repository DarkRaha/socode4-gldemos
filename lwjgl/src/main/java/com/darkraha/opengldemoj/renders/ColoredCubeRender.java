package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.AppOGL;
import com.darkraha.opengldemoj.gl.ShaderProgram;
import com.darkraha.opengldemoj.gl.ShaderProgramBuilder;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;


import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

/**
 * Render colored cube. Demonstrate how to use VAO with single VBO and IBO.
 */
public class ColoredCubeRender extends Render {

    private final FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);
    private Matrix4f matrix;
    private ShaderProgram prog;
    private float rotY = 1.5f * TO_RAD;
    private float rotX = TO_RAD;
    private int idVao;
    private int idVbo;
    private int idIbo;

    @Override
    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);

        prog = new ShaderProgramBuilder()
                .vertexAttributes(true, false, false)
                .build();

        glUseProgram(prog.idProgram);

        matrix = new Matrix4f()
                .perspective(45 * TO_RAD, aspect, 1f, 100f)
                .translate(0, 0f, -6f);

        float[] data = new float[]{
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
                -1.0f, 1.0f, -1.0f,  /*  */ 1.0f, 1.0f, 1.0f};

        byte[] indices = new byte[]{
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
        int stride = 4 * (3 + 3); // 4 - size of float in bytes, 3 - x,y,z, 3 - r,g,b
        glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_POS);
        glVertexAttribPointer(ShaderProgramBuilder.A_LOCATION_VERTEX_POS,
                3, GL_FLOAT, false,
                stride, 0);

        int colorOffset = 4 * 3; // 4 - size of float in bytes, 3 - x,y,z
        glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR);
        glVertexAttribPointer(ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR,
                3, GL_FLOAT, false, stride, colorOffset);


        //-----------------------------------------------
        // create IBO and upload data into it.
        // It will be attached to the current VAO.
        // IBO does not restricted by byte indices.
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer byteBuffer = stack.malloc(indices.length);
            byteBuffer.put(indices).flip();
            idIbo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, byteBuffer, GL_STATIC_DRAW);
        }

        glBindVertexArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }


    @Override
    public void onDrawFrame(AppOGL appOGL) {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // add rotation to the our cube
        matrix.rotateAffineXYZ(rotX, rotY, 0);

        glUseProgram(prog.idProgram);
        glUniformMatrix4fv(glGetUniformLocation(prog.idProgram, ShaderProgramBuilder.U_MATRIX_NAMES[0]),
                false, matrix.get(matrixBuffer));

        glBindVertexArray(idVao);

        // be careful, we used byte indices, so we draw with GL_UNSIGNED_BYTE
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
