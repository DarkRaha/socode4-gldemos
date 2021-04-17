package com.darkraha.opengldemoj.gl.shader;

import static com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder.SHADER_TYPE_FRAGMENT;
import static com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder.SHADER_TYPE_VERTEX;

abstract public class LightBuilder implements HelperBuilder {


    boolean withBumping;

    @Override
    public void addTypeDeclarations(StringBuilder sb, int shaderType) {
        sb.append("struct Light {\n");
        sb.append(" vec3 ambient; \n");
        sb.append(" vec3 diffuse; \n");
        sb.append(" vec3 direction; \n");
        sb.append("};\n");
    }

    @Override
    public void addInputDeclarations(StringBuilder sb, int shaderType) {
        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("layout(location=2) in vec3 vNormal;\n");
            if(withBumping) {
                sb.append("layout(location=4) in vec3 vTangent;\n");
            }
        }
    }

    @Override
    public void addUniformDeclarations(StringBuilder sb, int shaderType) {
        sb.append("uniform mat4 mNormal;\n");

        if (shaderType == SHADER_TYPE_VERTEX) {
            sb.append("uniform Light light;\n");
        }

        if(shaderType==SHADER_TYPE_FRAGMENT){
            if(withBumping){
                sb.append("uniform sampler2D normalSampler;\n");
            }
        }


    }
}
