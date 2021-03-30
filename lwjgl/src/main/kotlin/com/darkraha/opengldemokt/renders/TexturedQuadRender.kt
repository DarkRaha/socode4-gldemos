package com.darkraha.opengldemokt.renders


import com.darkraha.opengldemokt.gl.AppOGL
import com.darkraha.opengldemokt.gl.GlUtils
import com.darkraha.opengldemokt.gl.ShaderProgram
import org.joml.Matrix4f
import org.lwjgl.opengl.GL33.*

class TexturedQuadRender : Render() {
    private var idTexture = 0
    private var idVao = 0
    private var idVbo = 0
    private lateinit var matrix: Matrix4f
    private lateinit var prog: ShaderProgram
    private var rotY = 0f
    private var rotX = 0f

    override fun onSetup(appOGL: AppOGL) {
        setSurfaceSize(appOGL.width, appOGL.height)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        prog = ShaderProgram()
        glUseProgram(prog.idProgram)

        matrix = Matrix4f()

        idTexture = GlUtils.loadTex2DResDefault(0, "/textures/235.jpg")

        idVao = GlUtils.createVAO()
        idVbo = GlUtils.createVBO(
            floatArrayOf( // coords         texture coords
                -1.0f, -1.0f,  /**/0.0f, 1.0f,
                1.0f, -1.0f,  /**/1.0f, 1.0f,
                1.0f, 1.0f,  /**/1.0f, 0.0f,
                1.0f, 1.0f,  /**/1.0f, 0.0f,
                -1.0f, 1.0f,  /**/0.0f, 0.0f,
                -1.0f, -1.0f,  /**/0.0f, 1.0f
            )
        )
        GlUtils.bindAttributes(idVbo, 2, 0, true)
    }

    override fun onDrawFrame(appOGL: AppOGL) {
        rotY += 1.5f * TO_RAD
        if (rotY > 2 * PI_F) {
            rotY = rotY - 2 * PI_F
        }
        rotX += 1f * TO_RAD
        if (rotX > 2 * PI_F) {
            rotX = rotX - 2 * PI_F
        }
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glUseProgram(prog.idProgram)
        glUniform1i(glGetUniformLocation(prog.idProgram, "withTexture"), 1)
        glUniform4f(glGetUniformLocation(prog.idProgram, "uColor"), -1f, -1f, -1f, -1f)

        val samplerLocation = glGetUniformLocation(prog.idProgram, "texSampler")
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, idTexture)
        glUniform1i(samplerLocation, 0)

        matrix.identity()
            .perspective(ALNGLE45, aspect, 1f, 100f)
            .translate(0f, 0f, -6f)
            .rotateAffineXYZ(rotX, rotY, 0f)
        GlUtils.bindMatrix(prog.idProgram, matrix)

        glBindVertexArray(idVao)
        // glEnable(GL_CULL_FACE);
        glDrawArrays(GL_TRIANGLES, 0, 6) // vertex count
    }

    override fun onDispose(appOGL: AppOGL) {
        glUseProgram(0)
        prog.dispose()
        glDeleteTextures(idTexture)
        glDeleteVertexArrays(idVao)
        glDeleteBuffers(idVbo)
    }
}