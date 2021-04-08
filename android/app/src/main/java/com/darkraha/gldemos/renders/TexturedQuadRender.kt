package com.darkraha.gldemos.renders

import android.content.Context
import android.opengl.GLES30.*
import com.darkraha.gldemos.R
import com.darkraha.gldemos.gl.*
import org.joml.Matrix4f
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Render textured quad. Demonstrate how to apply 2D texture to the surface.
 *
 */
class TexturedQuadRender(val context: Context) : Render() {

    private lateinit var prog: ShaderProgram
    private lateinit var texture: GlTexture
    private lateinit var matrix: Matrix4f

    private var idVao = 0
    private var idVbo = 0

    private val rotY = 1.5f * GlCommon.TO_RAD
    private val rotX = GlCommon.TO_RAD

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        prog = ShaderProgramBuilder()
            .vertexAttributes(false, true, false)
            .build()

        matrix = Matrix4f()
            .perspective(GlCommon.ALNGLE45, aspect, 1f, 100f)
            .translate(0f, 0f, -6f)

        glUseProgram(prog.idProgram)
        texture = GlTexture.newTexture2D(R.drawable.t235, "test")
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


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        matrix = Matrix4f()
            .perspective(GlCommon.ALNGLE45, aspect, 1f, 100f)
            .translate(0f, 0f, -8f)
    }

    override fun onDrawFrame(arg0: GL10?) {
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

    override fun onDispose() {
        glUseProgram(0)
        prog.dispose()
        texture.dispose()
        glDeleteVertexArrays(1, intArrayOf(idVao),0)
        glDeleteBuffers(1, intArrayOf(idVbo),0)
    }
}