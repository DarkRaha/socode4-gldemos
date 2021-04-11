package com.darkraha.opengldemoj.gl.shader;

import static com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder.SHADER_TYPE_FRAGMENT;
import static com.darkraha.opengldemoj.gl.shader.ShaderProgramBuilder.SHADER_TYPE_VERTEX;

public class Texture2DBuilder implements HelperBuilder{


    @Override
    public void addTypeDeclarations(StringBuilder sb, int shaderType) {

    }

    @Override
    public void addInputDeclarations(StringBuilder sb, int shaderType) {
        if(shaderType==SHADER_TYPE_VERTEX){
            sb.append("layout(location=3) in vec2 vTexPos ;\n");
        }
    }

    @Override
    public void addExchangeDeclarations(StringBuilder sb, int shaderType) {

        if(shaderType==SHADER_TYPE_VERTEX){
            sb.append( "out vec2 exTexPos;\n");
        }

        if(shaderType==SHADER_TYPE_FRAGMENT){
            sb.append( "in vec2 exTexPos;\n");
        }
    }

    @Override
    public void addUniformDeclarations(StringBuilder sb, int shaderType) {
        if(shaderType==SHADER_TYPE_FRAGMENT){
            sb.append("uniform sampler2D sampler;\n");
        }

    }

    @Override
    public void addFuncsDeclarations(StringBuilder sb, int shaderType) {

    }

    @Override
    public void addCalculations(StringBuilder sb, int shaderType) {

        if(shaderType==SHADER_TYPE_VERTEX){
            sb.append("exTexPos = vTexPos;\n");
        }

        if(shaderType==SHADER_TYPE_FRAGMENT){
            sb.append("fragColor = fragColor*texture(sampler, exTexPos);\n");
        }

    }
}
