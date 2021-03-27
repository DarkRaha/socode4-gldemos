package com.darkraha.gldemos.renders

import android.opengl.GLES30.*

import com.darkraha.gldemos.gl.GlUtils
import com.darkraha.gldemos.gl.ShaderProgram

import org.joml.Matrix4f
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class ColoredQuadRender : Render() {

    private lateinit var matrix: Matrix4f
    private lateinit var prog: ShaderProgram
    private var idVbo = 0
    private var idVao = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

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

    override fun onDrawFrame(arg0: GL10?) {
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
}