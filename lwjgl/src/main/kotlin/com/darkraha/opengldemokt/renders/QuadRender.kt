package com.darkraha.opengldemokt.renders


import com.darkraha.opengldemokt.gl.AppOGL
import com.darkraha.opengldemokt.gl.GlUtils
import com.darkraha.opengldemokt.gl.ShaderProgram
import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*


class QuadRender : Render() {
    protected lateinit var matrix: Matrix4f
    private lateinit var prog: ShaderProgram
    private var idVbo = 0

    override fun onSetup(appOGL: AppOGL) {
        setSurfaceSize(appOGL.width, appOGL.height)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        prog = ShaderProgram(
            ShaderProgram.VERTEX_SHADER_120,
            ShaderProgram.FRAGMENT_SHADER_120)

        glUseProgram(prog.idProgram)
        GlUtils.bindMatrix(
            prog.idProgram,
            Matrix4f()
                .identity()
                .perspective(ALNGLE45, aspect, 0.01f, 100f)
                .translate(0f, 0f, -6f)
        )

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

    override fun onDrawFrame(appOGL: AppOGL) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glUseProgram(prog.idProgram)
        GlUtils.bindMatrix(
            prog.idProgram,
            Matrix4f()
                .identity()
                .perspective(com.darkraha.opengldemoj.renders.Render.ALNGLE45, aspect, 0.01f, 100f)
                .translate(0f, 0f, -6f)
        )
        glBindBuffer(GL_ARRAY_BUFFER, idVbo)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4) // vertex count
    }

    override fun onDispose(appOGL: AppOGL) {
        prog.dispose()
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(idVbo)
        super.onDispose(appOGL)
    }
}
