package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.*;
import com.darkraha.opengldemoj.gl.modelling.Models;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;


public class LightTexturedSphereRender extends Render {

    private final float rotY = 1.5f * TO_RAD;
    private final float rotX = TO_RAD;
    private final Matrices matrices = new Matrices();
    private ShaderProgram prog;
    private final GlObject sphere = new GlObject();

    public final Vector3f lightAmbient = new Vector3f(0.6f, 0.6f, 0.6f);
    public final Vector3f lightDiffuse = new Vector3f(1.0f, 1.0f, 1.0f);
    public final Vector3f lightDirection = new Vector3f(0.85f, 0.8f, 0.75f).normalize();


    @Override
    public void onSurfaceChanged(AppOGL appOGL, int w, int h) {
        super.onSurfaceChanged(appOGL, w, h);
        matrices.projection.identity().perspective(45 * TO_RAD, aspect, 1f, 100);

    }

    @Override
    public void onSetup(AppOGL appOGL) {

        prog = new ShaderProgramBuilder()
                .lightDirectional()
                .vertexAttributes(false, true, true)
                .matrix(true)
                .build();

        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);

        sphere.model = Models.sphere(1f,
                36, 36,
                1f, 0.5f, 0.5f,
                "sphere-0").toGlModel();


        sphere.transforms = new Matrix4f();
        sphere.transforms.translate(0,0,-6);
        sphere.texture = GlTexture.newTexture2D( "/textures/texture.png" , "terra-0");
    }

    @Override
    public void onDrawFrame(AppOGL appOGL) {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        sphere.transforms.rotateAffineXYZ(rotX, rotY, 0);

        matrices.applyModel(sphere.transforms);

        prog.use();
        prog.uniformMatrices(matrices);
        prog.uniformTexture(sphere.texture);
        prog.uniformDirectionalLight(lightAmbient, lightDiffuse, lightDirection);

        // glEnable(GL_CULL_FACE);
        sphere.model.draw();
    }
}
