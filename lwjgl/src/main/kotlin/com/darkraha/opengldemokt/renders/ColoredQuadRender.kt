package com.darkraha.opengldemokt.renders


import com.darkraha.opengldemokt.gl.*
import org.joml.Matrix4f

import org.lwjgl.opengl.GL33.*

class ColoredQuadRender : Render() {

    private lateinit var matrix: Matrix4f
    private lateinit var prog: ShaderProgram
    private var idVao = 0
    private var idVbo = 0

    override fun onSetup(appOGL: AppOGL) {
        setSurfaceSize(appOGL.width, appOGL.height)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        matrix = Matrix4f()
        prog = ShaderProgram()

        idVao = GlUtils.createVAO()
        idVbo = GlUtils.createVBO(
            floatArrayOf(
                // coord      color
                -1.0f, 1.0f,  /**/1.0f, 0.0f, 0.0f,
                1.0f, 1.0f,   /**/0.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, /**/0.0f, 0.0f, 1.0f,
                1.0f, -1.0f,  /**/1.0f, 1.0f, 1.0f
            )
        )

        GlUtils.bindAttributes(idVbo, 2, 3, false)
    }

    override fun onDrawFrame(appOGL: AppOGL) {
        super.onDrawFrame(appOGL)

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glUseProgram(prog.idProgram)
        glUniform1i(glGetUniformLocation(prog.idProgram, "withTexture"), 0)
        glUniform4f(glGetUniformLocation(prog.idProgram, "uColor"), -1f, -1f, -1f, -1f)

        matrix.identity().perspective(
            45 * TO_RAD,
            aspect,
            0.01f, 100f
        ).translate(0f, 0f, -6f)

        GlUtils.bindMatrix(prog.idProgram, matrix)

        glBindVertexArray(idVao)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
    }

    override fun onDispose(appOGL: AppOGL) {
        prog.dispose()
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteVertexArrays(idVao)
        glDeleteBuffers(idVbo)
    }
}
