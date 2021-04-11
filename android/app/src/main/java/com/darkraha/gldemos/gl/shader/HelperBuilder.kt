package com.darkraha.opengldemokt.gl.shader

interface HelperBuilder {
    fun addTypeDeclarations(sb: StringBuilder, shaderType: Int)
    fun addInputDeclarations(sb: StringBuilder, shaderType: Int)
    fun addExchangeDeclarations(sb: StringBuilder, shaderType: Int)
    fun addUniformDeclarations(sb: StringBuilder, shaderType: Int)
    fun addFuncsDeclarations(sb: StringBuilder, shaderType: Int)
    fun addCalculations(sb: StringBuilder, shaderType: Int)
}