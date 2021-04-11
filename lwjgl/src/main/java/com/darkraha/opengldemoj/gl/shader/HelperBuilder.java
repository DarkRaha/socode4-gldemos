package com.darkraha.opengldemoj.gl.shader;

public  interface HelperBuilder{
    void addTypeDeclarations(StringBuilder sb, int shaderType);
    void addInputDeclarations(StringBuilder sb, int shaderType);
    void addExchangeDeclarations(StringBuilder sb, int shaderType);
    void addUniformDeclarations(StringBuilder sb, int shaderType);
    void addFuncsDeclarations(StringBuilder sb, int shaderType);
    void addCalculations(StringBuilder sb, int shaderType);
}