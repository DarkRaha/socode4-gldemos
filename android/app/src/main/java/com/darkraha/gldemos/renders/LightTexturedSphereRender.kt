package com.darkraha.gldemos.renders

import android.opengl.GLES30.*
import com.darkraha.gldemos.R
import com.darkraha.gldemos.gl.*
import com.darkraha.gldemos.gl.modelling.Models
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder
import org.joml.Matrix4f
import org.joml.Vector3f
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class LightTexturedSphereRender : Render() {
    private val rotY = 1.5f * GlCommon.TO_RAD
    private val rotX = GlCommon.TO_RAD
    private val matrices = Matrices()
    private lateinit var prog: ShaderProgram
    private val sphere = GlObject()

    val lightAmbient = Vector3f(0.6f, 0.6f, 0.6f)
    val lightDiffuse = Vector3f(1.0f, 1.0f, 1.0f)
    val lightDirection = Vector3f(0.85f, 0.8f, 0.75f).normalize()


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        prog = ShaderProgramBuilder()
            .lightDirectional(false)
            .texture2D()
            .build()

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)

        sphere.model = Models.sphere(
            1f,
            36, 36,
            1f, 0.5f, 0.5f,
            "sphere-0"
        ).toGlModel()
        sphere.transforms = Matrix4f()
        sphere.transforms!!.translate(0f, 0f, -6f)
        sphere.texture = GlTexture.newTexture2D(R.drawable.texture, "terra-0")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        matrices.projection.identity().perspective(GlCommon.ALNGLE45, aspect, 1f, 100f)
    }

    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        sphere.transforms!!.rotateAffineXYZ(rotX, rotY, 0f)
        matrices.applyModel(sphere.transforms!!)

        prog.use()
        prog.uniformMatrices(matrices)
        prog.uniformTexture(sphere.texture!!)
        prog.uniformDirectionalLight(lightAmbient, lightDiffuse, lightDirection)

        // glEnable(GL_CULL_FACE);
        sphere.model!!.draw()
    }

    override fun onDispose() {
       prog.dispose()
        sphere.model!!.dispose()
        sphere.texture!!.dispose()
    }
}