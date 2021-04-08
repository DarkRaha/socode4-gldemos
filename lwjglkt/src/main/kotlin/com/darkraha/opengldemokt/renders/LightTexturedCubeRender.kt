package com.darkraha.opengldemokt.renders


import com.darkraha.opengldemokt.gl.*
import com.darkraha.opengldemokt.gl.modelling.Models

import org.joml.Matrix4f
import org.joml.Vector3f

import org.lwjgl.opengl.GL33.*

/**
 * Render textured cube with ambient light and single remote directional light (like sun).
 * Light direction and model normals must be normalized.
 */
class LightTexturedCubeRender : Render() {
    private val matrices = Matrices()
    private lateinit var prog: ShaderProgram
    private val cube = GlObject()

    private val rotY = 1.5f * TO_RAD
    private val rotX = TO_RAD

    val lightAmbient = Vector3f(0.6f, 0.6f, 0.6f)
    val lightDiffuse = Vector3f(1.0f, 1.0f, 1.0f)
    val lightDirection = Vector3f(0.85f, 0.8f, 0.75f).normalize()

    override fun onSetup(appOGL: AppOGL) {
        setSurfaceSize(appOGL.width, appOGL.height)
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        prog = ShaderProgramBuilder()
            .vertexAttributes(false, true, true)
            .lightDirectional()
            .normalMatrix()
            .matrix()
            .build()
        glUseProgram(prog.idProgram)
        cube.model = Models.cube(1f, 1f, 1f, "cube-0").toGlModel()
        cube.texture = GlTexture.newTexture2D("/textures/235.jpg", "name")
        cube.transforms = Matrix4f().translate(0f, 0f, -6f)
    }

    override fun onDrawFrame(appOGL: AppOGL) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glUseProgram(prog.idProgram)

        cube.transforms!!.rotateAffineXYZ(rotX, rotY, 0f)
        matrices.applyModel(cube.transforms!!)
        prog.uniformMatrices(matrices)
        prog.uniformTexture(cube.texture!!)
        prog.uniformDirectionalLight(lightAmbient, lightDiffuse, lightDirection)
        cube.model!!.draw()
    }

    override fun onDispose(appOGL: AppOGL) {
        prog.dispose()
        cube.model!!.dispose()
        cube.texture!!.dispose()
    }
}
