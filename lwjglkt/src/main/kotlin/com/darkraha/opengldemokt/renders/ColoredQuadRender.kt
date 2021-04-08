package com.darkraha.opengldemokt.renders

import com.darkraha.opengldemokt.gl.AppOGL
import com.darkraha.opengldemokt.gl.ShaderProgram
import com.darkraha.opengldemokt.gl.ShaderProgramBuilder
import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

/**
 * Render colored quad. Demonstrate how to use VAO with single VBO that
 * contains multiple vertex attributes (in our case vertex positions and vertex colors).
 */
class ColoredQuadRender : Render() {
    private val matrixBuffer = MemoryUtil.memAllocFloat(16)
    private lateinit var prog: ShaderProgram
    private var idVao = 0
    private var idVbo = 0

    override fun onSetup(appOGL: AppOGL) {
        setSurfaceSize(appOGL.width, appOGL.height)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        prog = ShaderProgramBuilder()
            .vertexAttributes(true, false, false)
            .build()
        glUseProgram(prog.idProgram)

        //-----------------------------------------------
        // bind final transformation matrix to the shader matrix variable
        val matrix = Matrix4f()
            .identity() // initially matrix identity, so you can skip it
            .perspective(45 * TO_RAD, aspect, 1f, 100f)
            .translate(0f, 0f, -6f) // make quad before viewer

        glUniformMatrix4fv(
            glGetUniformLocation(
                prog.idProgram,
                ShaderProgramBuilder.U_MATRIX_NAMES[0]
            ),
            false, matrix[matrixBuffer]
        )


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
        val stride = 4 * (2 + 3) // 4 - size of float in bytes, 2 - x,y, 3 - r,g,b

        glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_POS)
        glVertexAttribPointer(
            ShaderProgramBuilder.A_LOCATION_VERTEX_POS,
            2, GL_FLOAT, false,
            stride, 0
        )

        val colorOffset = 4 * 2 // 4 - size of float in bytes, 2 - x,y
        glEnableVertexAttribArray(ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR)
        glVertexAttribPointer(
            ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR,
            3, GL_FLOAT, false, stride, colorOffset.toLong()
        )

        glBindVertexArray(0) // deactivate VAO
    }

    override fun onDrawFrame(appOGL: AppOGL) {
        super.onDrawFrame(appOGL)
        glUseProgram(prog.idProgram)
        glBindVertexArray(idVao)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        glUseProgram(0)
    }

    override fun onDispose(appOGL: AppOGL) {
        prog.dispose()
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteVertexArrays(idVao)
        glDeleteBuffers(idVbo)
        super.onDispose(appOGL)
    }
}
