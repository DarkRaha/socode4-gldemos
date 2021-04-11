package com.darkraha.opengldemokt.renders

import com.darkraha.opengldemokt.gl.*
import com.darkraha.opengldemokt.gl.modelling.Models
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11

class LightTexturedSphereRender : Render() {
    private val rotY = 1.5f * TO_RAD
    private val rotX = TO_RAD
    private val matrices = Matrices()
    private lateinit var prog: ShaderProgram
    private val sphere = GlObject()

    val lightAmbient = Vector3f(0.6f, 0.6f, 0.6f)
    val lightDiffuse = Vector3f(1.0f, 1.0f, 1.0f)
    val lightDirection = Vector3f(0.85f, 0.8f, 0.75f).normalize()

    override fun onSurfaceChanged(appOGL: AppOGL, w: Int, h: Int) {
        super.onSurfaceChanged(appOGL, w, h)
        matrices.projection.identity().perspective(45 * TO_RAD, aspect, 1f, 100f)
    }

    override fun onSetup(appOGL: AppOGL) {
        prog = ShaderProgramBuilder()
            .texture2D()
            .lightDirectional(false)
            .build()

        setSurfaceSize(appOGL.width, appOGL.height)

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GL11.glEnable(GL11.GL_DEPTH_TEST)

        sphere.model = Models.sphere(
            1f,
            36, 36,
            1f, 0.5f, 0.5f,
            "sphere-0"
        ).toGlModel()
        sphere.transforms = Matrix4f()
        sphere.transforms!!.translate(0f, 0f, -6f)
        sphere.texture = GlTexture.newTexture2D("/textures/texture.png", "terra-0")
    }

    override fun onDrawFrame(appOGL: AppOGL) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

        sphere.transforms!!.rotateAffineXYZ(rotX, rotY, 0f)
        matrices.applyModel(sphere.transforms!!)

        prog.use()
        prog.uniformMatrices(matrices)
        prog.uniformTexture(sphere.texture!!)
        prog.uniformDirectionalLight(lightAmbient, lightDiffuse, lightDirection)

        // glEnable(GL_CULL_FACE);
        sphere.model!!.draw()
    }
}
