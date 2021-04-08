package com.darkraha.gldemos.renders

import android.content.Context
import android.opengl.GLES30.*
import com.darkraha.gldemos.R
import com.darkraha.gldemos.gl.GlCommon
import com.darkraha.gldemos.gl.GlUtils
import com.darkraha.gldemos.gl.ShaderProgram
import com.darkraha.gldemos.gl.ShaderProgramBuilder
import com.darkraha.gldemos.gl.modelling.GlModel
import com.darkraha.gldemos.gl.modelling.Models
import org.joml.Matrix4f
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TexturedCubeRender(private val context: Context) : Render() {

    private lateinit var matrix: Matrix4f
    private lateinit var prog: ShaderProgram
    private val rotY = 1.5f * GlCommon.TO_RAD
    private val rotX = GlCommon.TO_RAD
    private lateinit var cube: GlModel

    private var idTexture = 0


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        prog = ShaderProgramBuilder()
            .vertexAttributes(false, true, false)
            .matrix(false)
            .build()

        glUseProgram(prog.idProgram)

        matrix = Matrix4f()

        matrix.identity()
            .perspective(GlCommon.ALNGLE45, aspect, 1f, 100f)
            .translate(0f, 0f, -6f)

        cube = Models.cube(1f, 1f, 1f, "cube-0").toGlModel()



        idTexture = GlUtils.loadTex2DResDefault(0, context, R.drawable.t235)
        glGenerateMipmap(idTexture)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        matrix.identity()
            .perspective(GlCommon.ALNGLE45, aspect, 1f, 100f)
            .translate(0f, 0f, -6f)
    }

    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glUseProgram(prog.idProgram)
        matrix.rotateAffineXYZ(rotX, rotY, 0f)
        prog.uniformTexture(idTexture)
        prog.uniformMatrix(matrix)
        // glEnable(GL_CULL_FACE)
        cube.draw()
    }

    override fun onDispose() {
        prog.dispose()
        glDeleteTextures(1, intArrayOf(idTexture), 0)
        cube.dispose()
    }


}