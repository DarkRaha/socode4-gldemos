package com.darkraha.opengldemoj.gl.shader;

import static com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder.*;

class BaseBuilder implements HelperBuilder {

    final String[] matricesDeclaration = new String[]{
            "uniform mat4 m;\n",
            "uniform mat4 mP;\n",
            "uniform mat4 mVM;\n",
            "uniform mat4 mV;\n",
            "uniform mat4 mM;\n",
    };

    final boolean[] useMatrices = new boolean[matricesDeclaration.length];

    public BaseBuilder() {
        useMatrices[0] = true;
    }

    @Override
    public void addTypeDeclarations(StringBuilder sb, int shaderType) {

    }

    @Override
    public void addInputDeclarations(StringBuilder sb, int shaderType) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("layout(location=0) in vec4 vPos;\n");
        }
    }

    @Override
    public void addExchangeDeclarations(StringBuilder sb, int shaderType) {

    }

    @Override
    public void addUniformDeclarations(StringBuilder sb, int shaderType) {

        if (shaderType == SHADER_TYPE_VERTEX) {
            for (int i = 0; i < useMatrices.length; ++i) {
                if (useMatrices[i]) {
                    sb.append(matricesDeclaration[i]);
                }
            }
        }
    }

    @Override
    public void addFuncsDeclarations(StringBuilder sb, int shaderType) {

    }

    @Override
    public void addCalculations(StringBuilder sb, int shaderType) {

        if (SHADER_TYPE_VERTEX == shaderType) {
            if (useMatrices[IND_MATRIX]) {
                sb.append("    gl_Position = m  * vPos;\n");
            } else if (useMatrices[IND_MATRIX_VIEW_MODEL]) {
                sb.append("    gl_Position = mP  * mVM * vPos;\n");
            } else if (useMatrices[IND_MATRIX_MODEL]) {
                sb.append("    gl_Position = mP  * mV * mM * vPos;\n");
            }
        }
    }
}
