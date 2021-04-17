package com.darkraha.opengldemoj.gl.shader;

import com.darkraha.opengldemoj.gl.ShaderProgram;

public class ShaderProgramBuilder implements HelperBuilder {

    public static final int SHADER_TYPE_VERTEX = 1;
    public static final int SHADER_TYPE_FRAGMENT = 2;

    public static final int A_LOCATION_VERTEX_POS = 0;
    public static final int A_LOCATION_VERTEX_COLOR = 1;
    public static final int A_LOCATION_VERTEX_NORMAL = 2;
    public static final int A_LOCATION_VERTEX_TEXPOS = 3;
    public static final int A_LOCATION_VERTEX_TANGENT = 4;
    public static final int A_LOCATION_VERTEX_BITANGENT = 5;

    public static final int IND_VERTEX_POS = 0;
    public static final int IND_VERTEX_COLOR = 1;
    public static final int IND_VERTEX_NORMAL = 2;
    public static final int IND_VERTEX_TEX_POS = 3;
    public static final int IND_VERTEX_TANGENT = 4;
    public static final int IND_VERTEX_BITANGENT = 5;

    public static final int IND_MATRIX = 0;
    public static final int IND_MATRIX_PROJECTION = 1;
    public static final int IND_MATRIX_VIEW_MODEL = 2;
    public static final int IND_MATRIX_VIEW = 3;
    public static final int IND_MATRIX_MODEL = 4;
    public static final int IND_MATRIX_NORMAL = 5;


    public static final String[] U_MATRIX_NAMES = new String[]{
            "m", "mP", "mVM", "mV", "mM", "mNormal"
    };

    public static final String[] INPUT_DATA_NAMES = new String[]{
            "vPos", "vColor", "vNormal", "vTexPos", "vTangent", "vBitangent"
    };

    public static final String[] U_LIGHT_NAMES = new String[]{
            "light.ambient",
            "light.diffuse",
            "light.direction"
    };

    public static final String U_SOLID_COLOR_NAME = "solidColor";
    public static final String U_SAMPLER_NAME = "sampler";
    public static final String U_NORMAL_SAMPLER_NAME = "normalSampler";


    private String version = "330 core";
    private String precisionFloat = "mediump";

    private final BaseBuilder baseBuilder = new BaseBuilder();
    private ColorBuilder colorBuilder = new ColorBuilder();
    private Texture2DBuilder texture2DBuilder;
    private LightBuilder lightBuilder;


    public ShaderProgramBuilder() {

    }

    private String buildShader(int shaderType) {
        StringBuilder sb = new StringBuilder();
        sb.append("#version ").append(version).append("\n");
        sb.append("precision ").append(precisionFloat).append(" float;\n");

        addTypeDeclarations(sb, shaderType);
        addInputDeclarations(sb, shaderType);
        addUniformDeclarations(sb, shaderType);
        addExchangeDeclarations(sb, shaderType);
        addFuncsDeclarations(sb, shaderType);

        sb.append("void main() {\n");
        addCalculations(sb, shaderType);
        sb.append("}\n");
        String ret = sb.toString();
        System.out.println("Shader " + shaderType + ":\n" + ret);
        return ret;
    }

    public String buildVertexShader() {
        return buildShader(SHADER_TYPE_VERTEX);
    }

    public String buildFragmentShader() {
        return buildShader(SHADER_TYPE_FRAGMENT);
    }

    //------------------------------------------------------------------
    public ShaderProgramBuilder colors(boolean usePerVertex, boolean useSolidColor) {
        if (colorBuilder == null) {
            colorBuilder = new ColorBuilder();
        }

        colorBuilder.useSolidColor = useSolidColor;
        colorBuilder.usePerVertex = usePerVertex;
        return this;
    }

    public ShaderProgramBuilder version(String v) {
        version = v;
        return this;
    }

    public ShaderProgramBuilder precision(String v) {
        precisionFloat = v;
        return this;
    }

    public ShaderProgramBuilder texture2D() {
        if (texture2DBuilder == null) {
            texture2DBuilder = new Texture2DBuilder();
        }
        return this;
    }

    public ShaderProgramBuilder lightDirectional(boolean withBumping) {
        lightBuilder = new DirectionLightBuilder();
        lightBuilder.withBumping = withBumping;
        return this;
    }


    public ShaderProgramBuilder matrix() {
        baseBuilder.useMatrices[IND_MATRIX] = true;
        return this;
    }

    public ShaderProgramBuilder matrix(boolean v) {
        baseBuilder.useMatrices[IND_MATRIX] = v;
        return this;
    }

    public ShaderProgramBuilder matrixP_VM() {
        baseBuilder.useMatrices[IND_MATRIX_VIEW_MODEL] = true;
        baseBuilder.useMatrices[IND_MATRIX_PROJECTION] = true;
        return this;
    }

    public ShaderProgramBuilder matrixP_VM(boolean v) {
        baseBuilder.useMatrices[IND_MATRIX_VIEW_MODEL] = v;
        baseBuilder.useMatrices[IND_MATRIX_PROJECTION] = v;
        return this;
    }

    public ShaderProgramBuilder matrixP_V_M() {
        baseBuilder.useMatrices[IND_MATRIX_VIEW] = true;
        baseBuilder.useMatrices[IND_MATRIX_MODEL] = true;
        baseBuilder.useMatrices[IND_MATRIX_PROJECTION] = true;
        return this;
    }

    public ShaderProgramBuilder matrixP_V_M(boolean v) {
        baseBuilder.useMatrices[IND_MATRIX_VIEW] = v;
        baseBuilder.useMatrices[IND_MATRIX_MODEL] = v;
        baseBuilder.useMatrices[IND_MATRIX_PROJECTION] = v;
        return this;
    }

    public ShaderProgram build() {
        return new ShaderProgram(buildVertexShader(), buildFragmentShader());
    }

    //========================================================================
    @Override
    public void addTypeDeclarations(StringBuilder sb, int shaderType) {
        baseBuilder.addTypeDeclarations(sb, shaderType);
        colorBuilder.addTypeDeclarations(sb, shaderType);

        if (texture2DBuilder != null) {
            texture2DBuilder.addTypeDeclarations(sb, shaderType);
        }

        if (lightBuilder != null) {
            lightBuilder.addTypeDeclarations(sb, shaderType);
        }

    }

    @Override
    public void addInputDeclarations(StringBuilder sb, int shaderType) {
        baseBuilder.addInputDeclarations(sb, shaderType);
        colorBuilder.addInputDeclarations(sb, shaderType);


        if (texture2DBuilder != null) {
            texture2DBuilder.addInputDeclarations(sb, shaderType);
        }

        if (lightBuilder != null) {
            lightBuilder.addInputDeclarations(sb, shaderType);
        }
    }

    @Override
    public void addExchangeDeclarations(StringBuilder sb, int shaderType) {
        baseBuilder.addExchangeDeclarations(sb, shaderType);
        colorBuilder.addExchangeDeclarations(sb, shaderType);


        if (texture2DBuilder != null) {
            texture2DBuilder.addExchangeDeclarations(sb, shaderType);
        }

        if (lightBuilder != null) {
            lightBuilder.addExchangeDeclarations(sb, shaderType);
        }
    }

    @Override
    public void addUniformDeclarations(StringBuilder sb, int shaderType) {
        baseBuilder.addUniformDeclarations(sb, shaderType);
        colorBuilder.addUniformDeclarations(sb, shaderType);


        if (texture2DBuilder != null) {
            texture2DBuilder.addUniformDeclarations(sb, shaderType);
        }

        if (lightBuilder != null) {
            lightBuilder.addUniformDeclarations(sb, shaderType);
        }
    }

    @Override
    public void addFuncsDeclarations(StringBuilder sb, int shaderType) {
        baseBuilder.addFuncsDeclarations(sb, shaderType);
        colorBuilder.addFuncsDeclarations(sb, shaderType);


        if (texture2DBuilder != null) {
            texture2DBuilder.addFuncsDeclarations(sb, shaderType);
        }

        if (lightBuilder != null) {
            lightBuilder.addFuncsDeclarations(sb, shaderType);
        }
    }

    @Override
    public void addCalculations(StringBuilder sb, int shaderType) {
        baseBuilder.addCalculations(sb, shaderType);
        colorBuilder.addCalculations(sb, shaderType);

        if (texture2DBuilder != null) {
            texture2DBuilder.addCalculations(sb, shaderType);
        }

        if (lightBuilder != null) {
            lightBuilder.addCalculations(sb, shaderType);
        }
    }
}
