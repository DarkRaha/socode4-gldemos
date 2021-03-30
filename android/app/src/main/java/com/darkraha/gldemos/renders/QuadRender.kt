package com.darkraha.gldemos.renders


import android.opengl.GLES30.*
import com.darkraha.gldemos.gl.GlUtils
import com.darkraha.gldemos.gl.ShaderProgram
import org.joml.Matrix4f

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class QuadRender : Render() {

    private lateinit var prog: ShaderProgram
    private var idVbo = 0
    private val matrix = Matrix4f()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        prog = ShaderProgram()
        glUseProgram(prog.idProgram)
        glUniform1i(glGetUniformLocation(prog.idProgram, "withTexture"), 0)
        glUniform4f(glGetUniformLocation(prog.idProgram, "uColor"), 1f, 1f, 1f, 1f)

        idVbo = GlUtils.createVBO(
            floatArrayOf(
                -1.0f, 1.0f,
                1.0f, 1.0f,
                -1.0f, -1.0f,
                1.0f, -1.0f
            )
        )
        GlUtils.bindAttributes(idVbo, 2, 0, false)
    }

    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glUseProgram(prog.idProgram)

        GlUtils.bindMatrix(
            prog.idProgram,
            matrix
                .identity()
                .perspective(ALNGLE45, aspect, 0.01f, 100f)
                .translate(0f, 0f, -6f)
        )

        glBindBuffer(GL_ARRAY_BUFFER, idVbo)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
    }

}