package com.darkraha.gldemos.renders

import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder
import org.joml.Matrix4f
import org.joml.Vector3f
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES30.*
import com.darkraha.gldemos.R
import com.darkraha.gldemos.gl.*
import com.darkraha.gldemos.gl.GlCommon.TO_RAD
import com.darkraha.gldemos.gl.modelling.Models
import javax.microedition.khronos.egl.EGLConfig

class BumpingSphereRender : Render() {
    private val rotY = 1.5f * TO_RAD
    private val rotX = TO_RAD
    private val matrices: Matrices = Matrices()
    private lateinit var prog: ShaderProgram
    private val sphere: GlObject = GlObject()

    private val light: LightDirectional = LightDirectional(
        Vector3f(0.6f, 0.6f, 0.6f),
        Vector3f(1.0f, 1.0f, 1.0f),
        Vector3f(0.85f, 0.8f, 0.75f).normalize()
    )

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        matrices.projection.identity().perspective(GlCommon.ALNGLE45, aspect, 1f, 100f)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        prog = ShaderProgramBuilder()
            .texture2D()
            .lightDirectional(true)
            .matrix()
            .build()

        sphere.model = Models
            .sphere(
                1f,
                36, 36,
                1f, 0.5f, 0.5f,
                "sphere-0"
            )
            .calcTangent()
            .toGlModel()

        sphere.transforms = Matrix4f()
        sphere.transforms!!.translate(0f, 0f, -6f)
        sphere.texture = GlTexture.newTexture2D(
            R.drawable.bricks053_1k,
            "bricks")
        sphere.normalTexture = GlTexture.newTexture2D( R.drawable.bricks053_1k_normal,
            "bricks-normal")

        glClearColor(0.5f, 0.5f, 0.5f, 0.5f)
        glEnable(GL_DEPTH_TEST)
    }

    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        sphere.transforms!!.rotateAffineXYZ(rotX, rotY, 0f)
        matrices.applyModel(sphere.transforms!!)
        prog.use()
        prog.uniform(sphere, matrices, light)

        // glEnable(GL_CULL_FACE);
        sphere.model!!.draw()
    }
}
