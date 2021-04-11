package com.darkraha.opengldemoj.renders;

import com.darkraha.opengldemoj.gl.*;
import com.darkraha.opengldemoj.gl.modelling.GlModel;
import com.darkraha.opengldemoj.gl.modelling.Models;
import com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder;
import org.joml.Matrix4f;


import static org.lwjgl.opengl.GL33.*;

public class TexturedCubeRender extends Render {
    private Matrix4f matrix;
    private ShaderProgram prog;
    private final float rotY = 1.5f * TO_RAD;
    private final float rotX = TO_RAD;
    private GlModel cube;
    private int idTexture;

    @Override
    public void onSetup(AppOGL appOGL) {
        setSurfaceSize(appOGL.getWidth(), appOGL.getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);

        prog = new ShaderProgramBuilder()
                .texture2D()
                .matrix()
                .build();

        glUseProgram(prog.idProgram);

        matrix = new Matrix4f();

        matrix.identity()
                .perspective(45 * TO_RAD, aspect, 1f, 100f)
                .translate(0, 0f, -6f);

        cube = Models.cube(1f, 1f, 1f, "cube-0").toGlModel();

        idTexture = GlUtils.loadTex2DResDefault(0, "/textures/235.jpg");
        glGenerateMipmap(idTexture);
    }


    @Override
    public void onDrawFrame(AppOGL appOGL) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glUseProgram(prog.idProgram);
        matrix.rotateAffineXYZ(rotX, rotY, 0);

        prog.uniformTexture(idTexture);
        prog.uniformMatrix(matrix);

        glEnable(GL_CULL_FACE);
        cube.draw();
    }

    @Override
    public void onDispose(AppOGL appOGL) {
        prog.dispose();
        glDeleteTextures(idTexture);
        cube.dispose();
    }
}
