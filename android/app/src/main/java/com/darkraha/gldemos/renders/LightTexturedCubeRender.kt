package com.darkraha.gldemos.renders

import android.content.Context
import org.joml.Matrix4f
import android.opengl.GLES30.*
import com.darkraha.gldemos.R
import com.darkraha.gldemos.gl.*
import com.darkraha.gldemos.gl.modelling.Models
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder
import org.joml.Vector3f

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Render textured cube with ambient light and single remote directional light (like sun).
 * Light direction and model normals must be normalized.
 */
class LightTexturedCubeRender(private val context: Context) : Render() {

    private val matrices = Matrices()
    private lateinit var prog: ShaderProgram
    private val cube = GlObject()

    private val rotY = 1.5f * GlCommon.TO_RAD
    private val rotX = GlCommon.TO_RAD

    val lightAmbient = Vector3f(0.6f, 0.6f, 0.6f)
    val lightDiffuse = Vector3f(1.0f, 1.0f, 1.0f)
    val lightDirection = Vector3f(0.85f, 0.8f, 0.75f).normalize()


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        prog = ShaderProgramBuilder()
            .lightDirectional(false)
            .texture2D()
            .build()
        glUseProgram(prog.idProgram)
        cube.model = Models.cube(1f, 1f, 1f, "cube-0").toGlModel()
        cube.texture = GlTexture.newTexture2D(R.drawable.t235, "name")
        cube.transforms = Matrix4f().translate(0f, 0f, -6f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        matrices.projection.identity().perspective(GlCommon.ALNGLE45, aspect, 1f, 100f)
    }


    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glUseProgram(prog.idProgram)

        cube.transforms!!.rotateAffineXYZ(rotX, rotY, 0f)
        matrices.applyModel(cube.transforms!!)
        prog.uniformMatrices(matrices)
        prog.uniformTexture(cube.texture!!)
        prog.uniformDirectionalLight(lightAmbient, lightDiffuse, lightDirection)
        cube.model!!.draw()
    }

    override fun onDispose() {
        prog.dispose()
        cube.model!!.dispose()
        cube.texture!!.dispose()
    }
}
