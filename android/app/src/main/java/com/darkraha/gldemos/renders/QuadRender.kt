package com.darkraha.gldemos.renders


import android.opengl.GLES30.*
import com.darkraha.gldemos.gl.GlCommon
import com.darkraha.gldemos.gl.ShaderProgram
import com.darkraha.gldemos.gl.ShaderProgramBuilder
import org.joml.Matrix4f
import java.nio.ByteBuffer
import java.nio.ByteOrder

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Render white quad. Demonstrate how to use VBO.
 * In OpenGL 3, VBO may not work standalone without VAO.
 * I decided to reject from shaders version 120 and just added VAO.
 *
 */
class QuadRender : Render() {
    private val matrixBuffer = ByteBuffer.allocateDirect(16 * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer()

    protected val matrix = Matrix4f()
    private lateinit var prog: ShaderProgram
    private var idVbo = 0
    private var idVao = 0
    private val solidColor = floatArrayOf(1f, 1f, 1f, 1f)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        prog = ShaderProgramBuilder().matrix().solidColor().build()
        glUseProgram(prog.idProgram)

        //-----------------------------------------------
        // prepare data for VBO, positions of the quad vertices
        val data = floatArrayOf(
            -1.0f, 1.0f,
            1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f
        )

        //-----------------------------------------------
        // create VAO and activate it
        val idArray = GlCommon.idArray

        glGenVertexArrays(1, idArray, 0)
        glBindVertexArray(idArray[0])
        idVao = idArray[0]

        //-----------------------------------------------
        // create VBO and upload data into it
        val buffer = ByteBuffer.allocateDirect(data.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()

        glGenBuffers(1, idArray, 0)
         idVbo = idArray[0]
        glBindBuffer(GL_ARRAY_BUFFER, idVbo)
        buffer.put(data).position(0)
        glBufferData(GL_ARRAY_BUFFER, data.size * 4, buffer, GL_STATIC_DRAW)

        //-----------------------------------------------
        // specify locations of attribute in data

        // 0 means  attribute tightly packed in the array
        val stride = 0

        // 0 because in data only one attribute packed
        val offset = 0L

        // we specified location of position attribute in shader to 0
        val posAttributeLocation = 0 // ShaderProgramBuilder.A_LOCATION_VERTEX_POS

        // we use only x,y coordinates for vertex position (z=0 by default)
        val size = 2

        glEnableVertexAttribArray(posAttributeLocation)
        glVertexAttribPointer(posAttributeLocation, size, GL_FLOAT, false, stride, 0)

        glBindVertexArray(0) // deactivate vao

    }

    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        //--------------------------------------------------
        // in this simple example you can move the following piece of code
        // to the end of onSetup()
        glUseProgram(prog.idProgram)
        matrix.identity()
            .perspective(GlCommon.ALNGLE45, aspect, 0.01f, 100f)
            .translate(0f, 0f, -6f)

        // bind final transformation matrix to the shader matrix variable
        glUniformMatrix4fv(
            glGetUniformLocation(prog.idProgram, "m"),1,
            false, matrix[matrixBuffer]
        )
        glUniform4fv(prog.solidColorLocation, 1, solidColor,0)
        glBindVertexArray(idVao)
        // end
        //--------------------------------------------------

        //glBindBuffer(GL_VERTEX_ARRAY, idVbo); // for shader version 120
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4) // 4 - vertex count
    }

    override fun onDispose() {
        prog.dispose()
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteVertexArrays(1, intArrayOf(idVao), 0)
        glDeleteBuffers(1,intArrayOf(idVbo),0)
    }
}