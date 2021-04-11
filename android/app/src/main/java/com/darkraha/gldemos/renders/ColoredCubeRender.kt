package com.darkraha.gldemos.renders

import android.opengl.GLES30.*
import com.darkraha.gldemos.gl.GlCommon
import com.darkraha.gldemos.gl.GlUtils
import com.darkraha.gldemos.gl.ShaderProgram

import com.darkraha.gldemos.gl.modelling.Model
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_COLOR
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder.Companion.A_LOCATION_VERTEX_POS

import org.joml.Matrix4f
import java.nio.ByteBuffer
import java.nio.ByteOrder

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Render colored cube. Demonstrate how to use VAO with single VBO and IBO.
 */
class ColoredCubeRender : Render() {

    private val matrixBuffer = ByteBuffer.allocateDirect(16 * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer()

    private lateinit var matrix: Matrix4f
    private lateinit var prog: ShaderProgram
    private val rotY = 1.5f * GlCommon.TO_RAD
    private val rotX = GlCommon.TO_RAD
    private var idVao = 0
    private var idVbo = 0
    private var idIbo = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        prog = ShaderProgramBuilder()
            .colors(true, false)
            .build()

        glUseProgram(prog.idProgram)

        matrix = Matrix4f()
            .perspective(GlCommon.ALNGLE45, aspect, 1f, 100f)
            .translate(0f, 0f, -6f)

        val data = floatArrayOf( // coords             colors
            // front
            -1.0f, -1.0f, 1.0f,  /*  */1.0f, 0.0f, 0.0f,
            1.0f, -1.0f, 1.0f,  /*  */0.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f,  /*  */0.0f, 0.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,  /*  */1.0f, 1.0f, 1.0f,  // back
            -1.0f, -1.0f, -1.0f,  /*  */1.0f, 0.0f, 0.0f,
            1.0f, -1.0f, -1.0f,  /*  */0.0f, 1.0f, 0.0f,
            1.0f, 1.0f, -1.0f,  /*  */0.0f, 0.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,  /*  */1.0f, 1.0f, 1.0f
        )

        val indices = byteArrayOf( // front
            0, 1, 2,
            2, 3, 0,  // right
            1, 5, 6,
            6, 2, 1,  // back
            7, 6, 5,
            5, 4, 7,  // left
            4, 0, 3,
            3, 7, 4,  // bottom
            4, 5, 1,
            1, 0, 4,  // top
            3, 2, 6,
            6, 7, 3
        )

        val idArray = intArrayOf(0)

        glGenVertexArrays(1, idArray, 0)
        glBindVertexArray(idArray[0])

        idVao = idArray[0]

        val buffer = ByteBuffer.allocateDirect(data.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()

        glGenBuffers(1, idArray, 0)
        idVbo = idArray[0]
        glBindBuffer(GL_ARRAY_BUFFER, idVbo)

        buffer.put(data).position(0)
        glBufferData(GL_ARRAY_BUFFER, data.size*4, buffer, GL_STATIC_DRAW)


        //-----------------------------------------------
        // specify locations of attributes in data

        val stride = 4 * (3 + 3) // 4 - size of float in bytes, 3 - x,y,z, 3 - r,g,b

        glEnableVertexAttribArray(A_LOCATION_VERTEX_POS)
        glVertexAttribPointer(A_LOCATION_VERTEX_POS, 3, GL_FLOAT, false, stride, 0)

        val colorOffset = 4 * 3 // 4 - size of float in bytes, 3 - x,y,z

        glEnableVertexAttribArray(A_LOCATION_VERTEX_COLOR)
        glVertexAttribPointer(A_LOCATION_VERTEX_COLOR, 3,
            GL_FLOAT, false, stride, colorOffset)

        //--------------------------------------------------
        // create IBO and upload data into it
        val indicesBuffer = ByteBuffer.allocateDirect(indices.size)
        indicesBuffer.put(indices).position(0)

        glGenBuffers(1, idArray, 0)
        idIbo = idArray[0]
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.size, indicesBuffer, GL_STATIC_DRAW)

        glBindVertexArray(0)
        //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        matrix.identity()
            .perspective(GlCommon.ALNGLE45, aspect, 1f, 100f)
            .translate(0f,0f,-8f)
    }


    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        // add rotation to the our cube
        matrix.rotateAffineXYZ(rotX, rotY, 0f)

        glUseProgram(prog.idProgram)

        glUniformMatrix4fv(
            glGetUniformLocation(prog.idProgram, ShaderProgramBuilder.U_MATRIX_NAMES[0]),
            1,false, matrix[matrixBuffer])

        glBindVertexArray(idVao)

        // be careful, we used byte indices, so we draw with GL_UNSIGNED_BYTE
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, 0)
    }
}