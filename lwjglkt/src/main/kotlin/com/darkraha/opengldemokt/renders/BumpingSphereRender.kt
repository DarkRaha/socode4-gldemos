package com.darkraha.opengldemokt.renders


import com.darkraha.opengldemokt.gl.*
import com.darkraha.opengldemokt.gl.modelling.Models
import com.darkraha.opengldemokt.gl.shader.ShaderProgramBuilder
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*


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

    override fun onSurfaceChanged(appOGL: AppOGL, w: Int, h: Int) {
        super.onSurfaceChanged(appOGL, w, h)
        matrices.projection.identity().perspective(45 * TO_RAD, aspect, 1f, 100f)
    }

    override fun onSetup(appOGL: AppOGL) {
        prog = ShaderProgramBuilder()
            .texture2D()
            .lightDirectional(true)
            .matrix()
            .build()

        setSurfaceSize(appOGL.width, appOGL.height)

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
        sphere.texture = GlTexture.newTexture2D("/textures/bricks/bricks053_1k.jpg",
            "bricks")
        sphere.normalTexture = GlTexture.newTexture2D("/textures/bricks/bricks053_1k_normal.jpg",
            "bricks-normal")

        glClearColor(0.5f, 0.5f, 0.5f, 0.5f)
        glEnable(GL_DEPTH_TEST)
    }

    override fun onDrawFrame(appOGL: AppOGL) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        sphere.transforms!!.rotateAffineXYZ(rotX, rotY, 0f)

        prog.use()
        prog.uniform(sphere, matrices, light)

        // glEnable(GL_CULL_FACE);
        sphere.model!!.draw()
    }
}
