package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.*;
import com.darkraha.opengldemoj.gl.modelling.Models;
import com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class BumpingSphereRender extends Render{

    private final float rotY = 1.5f * TO_RAD;
    private final float rotX = TO_RAD;
    private final Matrices matrices = new Matrices();
    private ShaderProgram prog;
    private final GlObject sphere = new GlObject();

    private final LightDirectional light= new LightDirectional(new Vector3f(0.6f, 0.6f, 0.6f),
            new Vector3f(1.0f, 1.0f, 1.0f),
            new Vector3f(0.85f, 0.8f, 0.75f).normalize());

    @Override
    public void onSurfaceChanged(AppOGL appOGL, int w, int h) {
        super.onSurfaceChanged(appOGL, w, h);
        matrices.projection.identity().perspective(45 * TO_RAD, aspect, 1f, 100);

    }

    @Override
    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());

        prog = new ShaderProgramBuilder()
                .texture2D()
                .lightDirectional(true)
                .matrix()
                .build();

        sphere.model = Models
                .sphere(1f,
                36, 36,
                1f, 0.5f, 0.5f,
                "sphere-0")
                .calcTangent()
                .toGlModel();

        sphere.transforms = new Matrix4f();
        sphere.transforms.translate(0,0,-6);
        sphere.texture = GlTexture.newTexture2D( "/textures/bricks/bricks053_1k.jpg" , "bricks");
        sphere.normalTexture = GlTexture.newTexture2D( "/textures/bricks/bricks053_1k_normal.jpg" , "bricks-normal");

        glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        glEnable(GL_DEPTH_TEST);
    }


    @Override
    public void onDrawFrame(AppOGL appOGL) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        sphere.transforms.rotateAffineXYZ(rotX, rotY, 0);

        prog.use();
        prog.uniform(sphere, matrices, light);

        // glEnable(GL_CULL_FACE);
        sphere.model.draw();

    }



}
