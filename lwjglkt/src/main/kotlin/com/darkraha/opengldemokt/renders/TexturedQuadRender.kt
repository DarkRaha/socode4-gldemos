package com.darkraha.opengldemokt.renders

import com.darkraha.opengldemokt.gl.*

import org.joml.Matrix4f
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL33.*

/**
 * Render textured quad. Demonstrate how to apply 2D texture to the surface.
 * It uses class GlTexture, that has methods for loading graphic resources via STBI library.
 */
class TexturedQuadRender : Render() {
    private lateinit var prog: ShaderProgram
    private lateinit var texture: GlTexture
    private lateinit var matrix: Matrix4f

    private var idVao = 0
    private var idVbo = 0

    private val rotY = 1.5f * TO_RAD
    private val rotX = TO_RAD

    override fun onSetup(appOGL: AppOGL) {
        setSurfaceSize(appOGL.width, appOGL.height)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        prog = ShaderProgramBuilder()
            .vertexAttributes(false, true, false)
            .build()

        matrix = Matrix4f()
            .perspective(ALNGLE45, aspect, 1f, 100f)
            .translate(0f, 0f, -6f)

        glUseProgram(prog.idProgram)
        texture = GlTexture.newTexture2D("/textures/235.jpg", "test")
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
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glUseProgram(prog.idProgram)

        //-----------------------------------------------
        // activate our texture
        // you can use replace it by prog.uniformTexture(texture);
        val samplerLocation = glGetUniformLocation(prog.idProgram, ShaderProgramBuilder.U_SAMPLER_NAME)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texture.idTexture)
        glUniform1i(samplerLocation, 0)

        matrix.rotateAffineXYZ(rotX, rotY, 0f)
        prog.uniformMatrix(matrix)

        glBindVertexArray(idVao)
        // glEnable(GL_CULL_FACE);
        glDrawArrays(GL_TRIANGLES, 0, 6) // vertex count
    }

    override fun onDispose(appOGL: AppOGL) {
        glUseProgram(0)
        prog.dispose()
        texture.dispose()
        glDeleteVertexArrays(idVao)
        glDeleteBuffers(idVbo)
    }
}
