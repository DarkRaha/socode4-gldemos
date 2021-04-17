package com.darkraha.opengldemoj.gl.shader;

import static com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder.SHADER_TYPE_FRAGMENT;
import static com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder.SHADER_TYPE_VERTEX;

public class ColorBuilder implements HelperBuilder {
    boolean usePerVertex;
    boolean useSolidColor;


    @Override
    public void addTypeDeclarations(StringBuilder sb, int shaderType) {


    }

    @Override
    public void addInputDeclarations(StringBuilder sb, int shaderType) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            if (usePerVertex) {
                sb.append("layout(location=1) in vec4 vColor;\n");
            }
        }
    }

    @Override
    public void addExchangeDeclarations(StringBuilder sb, int shaderType) {
        if (usePerVertex || useSolidColor) {

            if (shaderType == SHADER_TYPE_VERTEX) {
                sb.append("out vec4 exColor;\n");
            }

            if (shaderType == SHADER_TYPE_FRAGMENT) {
                sb.append("in vec4 exColor;\n");
            }
        }

        if (shaderType == SHADER_TYPE_FRAGMENT) {
            sb.append("out vec4 fragColor;\n");
        }
    }

    @Override
    public void addUniformDeclarations(StringBuilder sb, int shaderType) {
        if (useSolidColor) {
            sb.append("uniform vec4 solidColor;\n");
        }
    }

    @Override
    public void addFuncsDeclarations(StringBuilder sb, int shaderType) {

    }

    @Override
    public void addCalculations(StringBuilder sb, int shaderType) {

        if (shaderType == SHADER_TYPE_VERTEX) {
            if (usePerVertex && useSolidColor) {
                sb.append("exColor = vColor * solidColor;\n");
            } else if (usePerVertex) {
                sb.append("exColor = vColor;\n");
            } else if (useSolidColor) {
                sb.append("exColor = solidColor;\n");
            }
        }

        if (shaderType == SHADER_TYPE_FRAGMENT) {
            if (usePerVertex || useSolidColor) {
                sb.append("fragColor = exColor;\n");
            } else {
                sb.append("fragColor = vec4(1,1,1,1);\n");
            }
        }
    }
}
