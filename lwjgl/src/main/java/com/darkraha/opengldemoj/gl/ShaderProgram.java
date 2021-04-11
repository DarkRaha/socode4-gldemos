package com.darkraha.opengldemoj.gl;


import com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL33.*;

public class ShaderProgram {
    public static final FloatBuffer MATRIX_BUFFER = MemoryUtil.memAllocFloat(16);

    private int[] ids;
    private final int[] matricesLocations = new int[U_MATRIX_NAMES.length];
    private final int[] samplersLocations = new int[8];
    private int[] light = new int[3];
    private int normalLocation;

    public int solidColorLocation;
    public int idProgram;


    public ShaderProgram() {
        ShaderProgramBuilder builder = new ShaderProgramBuilder();
        builder.colors(false,true).matrix();
        init(builder.buildVertexShader(), builder.buildFragmentShader());
    }

    public ShaderProgram(String vertexShaderCode, String fragmentShaderCode) {
        init(vertexShaderCode, fragmentShaderCode);
    }

    protected void init(String vertexShaderCode, String fragmentShaderCode) {
        ids = createProgram(vertexShaderCode, fragmentShaderCode);
        idProgram = ids[0];
        setupLocations();
    }

    public void setupLocations() {
        glUseProgram(idProgram);

        for (int i = 0; i < matricesLocations.length; ++i) {
            matricesLocations[i] = glGetUniformLocation(idProgram, U_MATRIX_NAMES[i]);
        }

        samplersLocations[0] = glGetUniformLocation(idProgram, U_SAMPLER_NAME);

        for (int i = 1; i < samplersLocations.length; ++i) {
            samplersLocations[i] = glGetUniformLocation(idProgram, U_SAMPLER_NAME + i);
        }

        solidColorLocation = glGetUniformLocation(idProgram, U_SOLID_COLOR_NAME);
        light[0] = glGetUniformLocation(idProgram, U_LIGHT_NAMES[0]);
        light[1] = glGetUniformLocation(idProgram, U_LIGHT_NAMES[1]);
        light[2] = glGetUniformLocation(idProgram, U_LIGHT_NAMES[2]);
    }


    public void use() {
        glUseProgram(ids[0]);
    }

    public void uniformTexture(GlTexture texture) {
        glActiveTexture(texture.textureUnit);
        glBindTexture(texture.textureType, texture.idTexture);
        int ind = texture.textureUnit - GL_TEXTURE0;
        glUniform1i(samplersLocations[ind], ind);
    }

    public void uniformTexture(int idTexture) {
        int samplerLocation = glGetUniformLocation(idProgram, "sampler");
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, idTexture);
        glUniform1i(samplerLocation, 0);
    }

    public void uniformMatrix(Matrix4f m) {
        glUniformMatrix4fv(matricesLocations[IND_MATRIX], false, m.get(MATRIX_BUFFER));
    }

    public void uniformSolidColor(float[] color) {
        glUniform4fv(solidColorLocation, color);
    }

    public void uniformMatrices(Matrices m) {
        glUniformMatrix4fv(matricesLocations[IND_MATRIX], false, m.matrix.get(MATRIX_BUFFER));
        glUniformMatrix4fv(matricesLocations[IND_MATRIX_NORMAL], false, m.normals.get(MATRIX_BUFFER));
        glUniformMatrix4fv(matricesLocations[IND_MATRIX_PROJECTION], false, m.projection.get(MATRIX_BUFFER));
        glUniformMatrix4fv(matricesLocations[IND_MATRIX_VIEW], false, m.camera.get(MATRIX_BUFFER));
        glUniformMatrix4fv(matricesLocations[IND_MATRIX_VIEW_MODEL], false, m.viewModel.get(MATRIX_BUFFER));
        glUniformMatrix4fv(matricesLocations[IND_MATRIX_MODEL], false, m.model.get(MATRIX_BUFFER));
    }


    public void uniformDirectionalLight(float[] ambient, float[] lightDiffuse, float[] lightDirection) {
        glUniform3fv(light[0], ambient);
        glUniform3fv(light[1], lightDiffuse);
        glUniform3fv(light[2], lightDirection);
    }

    public void uniformDirectionalLight(Vector3f ambient, Vector3f lightDiffuse, Vector3f lightDirection) {
        glUniform3f(light[0], ambient.x, ambient.y, ambient.z);
        glUniform3f(light[1], lightDiffuse.x, lightDiffuse.y, lightDiffuse.z);
        glUniform3f(light[2], lightDirection.x, lightDirection.y, lightDirection.z);
    }


    public void dispose() {
        glDetachShader(ids[0], ids[1]);
        glDetachShader(ids[0], ids[2]);
        glDeleteProgram(ids[0]);
        glDeleteShader(ids[1]);
        glDeleteShader(ids[2]);
    }

    public void delete() {
        glDetachShader(idProgram, ids[1]);
        glDetachShader(idProgram, ids[2]);
        glDeleteProgram(ids[0]);
    }

    public static int[] createProgram(String vertexShaderSource, String fragmentShaderSource) {
        int idVertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
        int idFragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);

        return new int[]{linkProgram(idVertexShader, idFragmentShader), idVertexShader, idFragmentShader};
    }

    public static int linkProgram(int idVertexShader, int idFragmentShader) {
        int idProgram = glCreateProgram();
        glAttachShader(idProgram, idVertexShader);
        glAttachShader(idProgram, idFragmentShader);
        glLinkProgram(idProgram);

        if (glGetProgrami(idProgram, GL_LINK_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Could not link shader program.");
        }
        return idProgram;
    }

    public static int compileShader(int type, String source) {
        int idShader = glCreateShader(type);
        glShaderSource(idShader, source);
        glCompileShader(idShader);

        if (glGetShaderi(idShader, GL_COMPILE_STATUS) == GL_FALSE) {
            String reason = glGetShaderInfoLog(idShader, 500);
            glDeleteShader(idShader);
            throw new IllegalStateException("Could not compile shader: " + reason + "\nSource:\n " + source);
        }
        return idShader;
    }




    public static final String VERTEX_SHADER_120 = "#version 120 \n" +
            "attribute vec4 vCoord;\n" +
            "varying vec4 exColor;\n" +
            "uniform mat4 matrix;\n" +
            "void main() {\n" +
            "    gl_Position = matrix  * vCoord;\n" +
            "    exColor = vec4(1.0,1.0,1.0,1.0);\n" +
            "}";

    public static final String FRAGMENT_SHADER_120 = "#version 120 \n" +
            "varying vec4 exColor;\n" +
            "void main() {\n" +
            "    gl_FragColor = exColor;\n" +
            "}\n";

}
