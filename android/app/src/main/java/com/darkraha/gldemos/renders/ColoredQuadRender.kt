package com.darkraha.gldemos.renders

import android.opengl.GLES30.*
import com.darkraha.gldemos.gl.GlCommon

import com.darkraha.gldemos.gl.GlUtils
import com.darkraha.gldemos.gl.ShaderProgram
import com.darkraha.gldemos.gl.ShaderProgramBuilder
import com.darkraha.gldemos.gl.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_COLOR
import com.darkraha.gldemos.gl.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_POS

import org.joml.Matrix4f
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Render colored quad. Demonstrate how to use VAO with single VBO that
 * contains multiple vertex attributes (in our case vertex positions and vertex colors).
 */
class ColoredQuadRender : Render() {

    private val matrixBuffer = ByteBuffer.allocateDirect(16 * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer()

    private lateinit var  matrix: Matrix4f;
    private lateinit var prog: ShaderProgram

    private var idVao = 0
    private var idVbo = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        prog = ShaderProgramBuilder()
            .vertexAttributes(true, false, false)
            .build()

        glUseProgram(prog.idProgram)

        //-----------------------------------------------
        // bind final transformation matrix to the shader matrix variable
         matrix = Matrix4f()
            .identity() // initially matrix identity, so you can skip it
            .perspective(GlCommon.ALNGLE45, aspect, 1f, 100f)
            .translate(0f, 0f, -6f) // make quad before viewer



        //-----------------------------------------------
        // prepare data for VBO
        val data = floatArrayOf( // x,y             rgb color
            -1.0f, 1.0f,  /*   */1.0f, 0.0f, 0.0f,
            1.0f, 1.0f,  /*   */0.0f, 1.0f, 0.0f,
            -1.0f, -1.0f,  /*   */0.0f, 0.0f, 1.0f,
            1.0f, -1.0f,  /*   */1.0f, 1.0f, 1.0f
        )

        //-----------------------------------------------
        // create VAO
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
        // specify locations of attributes in data
        val stride = 4 * (2 + 3) // 4 - size of float in bytes, 2 - x,y, 3 - r,g,b

        glEnableVertexAttribArray(A_LOCATION_VERTEX_POS)
        glVertexAttribPointer(A_LOCATION_VERTEX_POS, 2, GL_FLOAT, false, stride, 0)

        val colorOffset = 4 * 2 // 4 - size of float in bytes, 2 - x,y
        glEnableVertexAttribArray(A_LOCATION_VERTEX_COLOR)
        glVertexAttribPointer(A_LOCATION_VERTEX_COLOR, 3, GL_FLOAT, false, stride, colorOffset)

        glBindVertexArray(0) // deactivate VAO
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)

        matrix.identity()
            .perspective(GlCommon.ALNGLE45, aspect, 0.01f, 100f)
            .translate(0f, 0f, -6f)
    }

    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glUseProgram(prog.idProgram)

        glUniformMatrix4fv(
            glGetUniformLocation(
                prog.idProgram,
                ShaderProgramBuilder.U_MATRIX_NAMES[0]
            ),
            1,false, matrix[matrixBuffer]
        )

        glBindVertexArray(idVao)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        glUseProgram(0)
    }
}