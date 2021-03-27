package com.darkraha.opengldemoj.gl;

import static org.lwjgl.opengl.GL33.*;

public class ShaderProgram {
    private int[] ids;
    public int idProgram;


    public ShaderProgram() {
        init(VERTEX_SHADER_DEFAULT, FRAGMENT_SHADER_DEFAULT);
    }

    public ShaderProgram(String vertexShaderCode, String fragmentShaderCode) {
        init(vertexShaderCode, fragmentShaderCode);
    }

    protected void init(String vertexShaderCode, String fragmentShaderCode) {
        ids = createProgram(vertexShaderCode, fragmentShaderCode);
        idProgram = ids[0];

    }

    public void dispose() {
        glDetachShader(ids[0], ids[1]);
        glDetachShader(ids[0], ids[2]);
        glDeleteProgram(ids[0]);
        glDeleteShader(ids[1]);
        glDeleteShader(ids[2]);
    }

    public void delete(){
        glDetachShader(idProgram, ids[1]);
        glDetachShader(idProgram, ids[2]);
        glDeleteProgram(ids[0]);
    }

    public static int[] createProgram(String vertexShaderSource, String fragmentShaderSource) {
        int idVertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
        int idFragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);

       return new int[] { linkProgram(idVertexShader, idFragmentShader), idVertexShader, idFragmentShader};
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


    public static final String VERTEX_SHADER_DEFAULT = "#version 330 core\n" +
            "precision mediump float;\n" +
            "layout(location=0) in vec4 vCoord;\n" +
            "layout(location=1) in vec4 vColor ;\n" +
            "layout(location=2) in vec3 vNormal;\n" +
            "layout(location=3) in vec2 vTexCoord ;\n" +
            "out vec4 exColor;\n" +
            "out vec2 exTexCoord;\n"+
            "uniform mat4 matrix;\n" +
            "uniform vec4 uColor = vec4(-1.0, -1.0, -1.0, -1.0);\n" +
            "void main() {\n" +
            "    gl_Position = matrix  * vCoord;\n" +
            "     if(uColor.w != -1.0) { exColor=uColor; } else {exColor=vColor;} \n"+

            "    exTexCoord = vTexCoord;\n"+
            "}";

    public static final String FRAGMENT_SHADER_DEFAULT = "#version 330 core\n" +
            "precision mediump float;\n"+
            "in vec4 exColor;\n" +
            "in vec2 exTexCoord;\n" +
            "out vec4 fragColor;\n" +
            "uniform int withTexture;\n"+
            "uniform sampler2D texSampler;\n"+
            "void main() {\n" +
            "   " +
            "if(withTexture==1) {fragColor = texture(texSampler, exTexCoord);}\n" +
            "else {fragColor = exColor;}\n"+
            "}\n";


    public static final String VERTEX_SHADER_120 = "#version 120 \n" +
            "attribute vec4 vCoord;\n" +
            "varying vec4 exColor;\n" +
            "uniform mat4 matrix;\n" +
            "void main() {\n" +
            "    gl_Position = matrix  * vCoord;\n" +
            "    exColor = vec4(1.0,1.0,1.0,1.0);\n"+
            "}";

    public static final String FRAGMENT_SHADER_120 = "#version 120 \n" +
            "varying vec4 exColor;\n" +
            "void main() {\n" +
            "    gl_FragColor = exColor;\n" +
            "}\n";

}
