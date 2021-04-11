package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.*;
import com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder;
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

        prog = new ShaderProgramBuilder().matrix().colors(false, true).build();
        glUseProgram(prog.idProgram);

        //-----------------------------------------------
        // prepare data for VBO, positions of the quad vertices
        float[] data = new float[]{
                -1.0f, 1.0f,
                1.0f, 1.0f,
                -1.0f, -1.0f,
                1.0f, -1.0f};

        //-----------------------------------------------
        // create VAO and activate it
        idVao = glGenVertexArrays();
        glBindVertexArray(idVao);

        //-----------------------------------------------
        // create VBO and upload data into it
        try (MemoryStack stack = MemoryStack.stackPush()) {
            idVbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, idVbo);

            FloatBuffer fb = stack.mallocFloat(data.length);
            fb.put(data).flip();
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
        }

        //-----------------------------------------------
        // specify locations of attribute in data

        // 0 means  attribute tightly packed in the array
        int stride = 0;

        // 0 because in data only one attribute packed
        int offset = 0;

        // we specified location of position attribute in shader to 0
        int posAttributeLocation = 0;

        // we use only x,y coordinates for vertex position (z=0 by default)
        int size = 2;

        glEnableVertexAttribArray(posAttributeLocation);
        glVertexAttribPointer(posAttributeLocation, size, GL_FLOAT, false,
                stride, offset);

        glBindVertexArray(0); // deactivate vao
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
