package com.darkraha.opengldemokt.renders

import com.darkraha.opengldemokt.gl.AppOGL
import com.darkraha.opengldemokt.gl.ShaderProgram
import com.darkraha.opengldemokt.gl.ShaderProgramBuilder
import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

/**
 * Render colored cube. Demonstrate how to use VAO with single VBO and IBO.
 */
class ColoredCubeRender : Render() {
    private val matrixBuffer = MemoryUtil.memAllocFloat(16)
    private lateinit var matrix: Matrix4f
    private lateinit var prog: ShaderProgram
    private val rotY = 1.5f * TO_RAD
    private val rotX = TO_RAD
    private var idVao = 0
    private var idVbo = 0
    private var idIbo = 0

    override fun onSetup(appOGL: AppOGL) {
        setSurfaceSize(appOGL.width, appOGL.height)

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        prog = ShaderProgramBuilder()
            .vertexAttributes(true, false, false)
            .build()

        glUseProgram(prog.idProgram)

        matrix = Matrix4f()
            .perspective(45 * TO_RAD, aspect, 1f, 100f)
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

        idVao = glGenVertexArrays()
        glBindVertexArray(idVao)

        MemoryStack.stackPush().use { stack ->
            idVbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, idVbo)
            val fb = stack.mallocFloat(data.size)
            fb.put(data).flip()
            glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW)
        }

        //-----------------------------------------------
        // specify locations of attributes in data

        val stride = 4 * (3 + 3) // 4 - size of float in bytes, 3 - x,y,z, 3 - r,g,b

        glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_POS)
        glVertexAttribPointer(
            ShaderProgramBuilder.A_LOCATION_VERTEX_POS,
            3, GL_FLOAT, false,
            stride, 0
        )

        val colorOffset = 4 * 3 // 4 - size of float in bytes, 3 - x,y,z
        glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR)
        glVertexAttribPointer(
            ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR,
            3, GL_FLOAT, false, stride, colorOffset.toLong()
        )

        MemoryStack.stackPush().use { stack ->
            val byteBuffer = stack.malloc(indices.size)
            byteBuffer.put(indices).flip()
            idIbo = glGenBuffers()
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, byteBuffer, GL_STATIC_DRAW)
        }

        glBindVertexArray(0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    override fun onDrawFrame(appOGL: AppOGL) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        // add rotation to the our cube
        matrix.rotateAffineXYZ(rotX, rotY, 0f)

        glUseProgram(prog.idProgram)

        glUniformMatrix4fv(
            glGetUniformLocation(
                prog.idProgram,
                ShaderProgramBuilder.U_MATRIX_NAMES[0]
            ),
            false, matrix[matrixBuffer]
        )
        glBindVertexArray(idVao)

        // be careful, we used byte indices, so we draw with GL_UNSIGNED_BYTE
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, 0)
    }

    override fun onDispose(appOGL: AppOGL) {
        prog.dispose()
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        glDeleteVertexArrays(idVao)
        glDeleteBuffers(idVbo)
        glDeleteBuffers(idIbo)
    }
}
