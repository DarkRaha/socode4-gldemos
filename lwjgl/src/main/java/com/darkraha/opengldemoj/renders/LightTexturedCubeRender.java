package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.*;
import com.darkraha.opengldemoj.gl.modelling.Models;
import com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL33.*;

/**
 * Render textured cube with ambient light and single remote directional light (like sun).
 * Light direction and model normals must be normalized.
 */
public class LightTexturedCubeRender extends Render {
    private final Matrices matrices = new Matrices();
    private GlObject cube = new GlObject();
    private ShaderProgram prog;

    private float rotY = 1.5f * TO_RAD;
    private float rotX = TO_RAD;
    public final Vector3f lightAmbient = new Vector3f(0.6f, 0.6f, 0.6f);
    public final Vector3f lightDiffuse = new Vector3f(1.0f, 1.0f, 1.0f);
    public final Vector3f lightDirection = new Vector3f(0.85f, 0.8f, 0.75f).normalize();


    @Override
    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);

        prog = new ShaderProgramBuilder()
                .texture2D()
                .lightDirectional(false)
                .matrix()
                .build();

        glUseProgram(prog.idProgram);
        cube.model = Models.cube(1f, 1f, 1f, "cube-0").toGlModel();
        cube.texture = GlTexture.newTexture2D("/textures/235.jpg", "name");
        cube.transforms = new Matrix4f().translate(0f, 0f, -6f);
    }


    @Override
    public void onDrawFrame(AppOGL appOGL) {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glUseProgram(prog.idProgram);

        cube.transforms.rotateAffineXYZ(rotX, rotY, 0);
        matrices.applyModel(cube.transforms);

        prog.uniformMatrices(matrices);
        prog.uniformTexture(cube.texture);
        prog.uniformDirectionalLight(lightAmbient, lightDiffuse, lightDirection);

        cube.model.draw();
    }

    @Override
    public void onDispose(AppOGL appOGL) {
        prog.dispose();
        cube.model.dispose();
        cube.texture.dispose();
    }
}
