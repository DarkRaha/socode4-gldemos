package com.darkraha.opengldemokt.gl.shaders

import java.lang.IllegalArgumentException
import java.lang.StringBuilder

abstract class ShaderBuilderA {
    protected var mVersion = "#version 330 core\n"
    protected var mPrecision = "precision mediump float;\n"
    protected var mVertexCoords = false
    protected var mVertexColors = false
    protected var mVertexTextureCoords = false
    protected var mVertexNormals = false
    protected var mMatrix = false
    protected var mProjectionMatrix = false
    protected var mViewModelMatrix = false
    protected var mViewMatrix = false
    protected var mModelMatrix = false
    protected var mNormalMatrix = false
    protected var exOut = true
    protected var exColor = false
    protected var exTexCoord = false
    protected var exLighting = false

    protected open fun buildCalcTexCoord(): String {
        return if (mVertexTextureCoords) "exTexCoord = vTexCoord;\n" else ""
    }

    protected open fun buildCalcColor(): String {
        return if (mVertexColors) "exColor = vColor;\n" else ""
    }

    protected fun setVertexInputData(vCoords: Boolean, vColors: Boolean, vTexCoords: Boolean, vNormals: Boolean) {
        mVertexCoords = vCoords
        mVertexColors = vColors
        mVertexTextureCoords = vTexCoords
        mVertexNormals = vNormals
    }

    protected open fun buildDeclareVertexInputData(): String {
        val sb = StringBuilder()
        if (mVertexCoords) {
            sb.append("layout(location=0) in vec4 vCoord;\n")
        }
        if (mVertexColors) {
            sb.append("layout(location=1) in vec4 vColor;\n")
        }
        if (mVertexNormals) {
            sb.append("layout(location=2) in vec3 vNormal;\n")
        }
        if (mVertexTextureCoords) {
            sb.append("layout(location=3) in vec2 vTexCoord ;\n")
        }
        return sb.toString()
    }

    protected fun setMatrix(
        matrix: Boolean, projMatrix: Boolean,
        viewModelMatrix: Boolean,
        viewMatrix: Boolean,
        modelMatrix: Boolean,
        normalMatrix: Boolean
    ) {
        mMatrix = matrix
        mProjectionMatrix = projMatrix
        mViewMatrix = viewMatrix
        mViewModelMatrix = viewModelMatrix
        mModelMatrix = modelMatrix
        mNormalMatrix = normalMatrix
    }

    protected open fun buildCalcPosition(): String {
        return if (mMatrix) {
            "    gl_Position = matrix  * vCoord;\n"
        } else if (mViewModelMatrix) {
            "    gl_Position = projMatrix  * viewModelMatrix * vCoord;\n"
        } else if (mModelMatrix) {
            "    gl_Position = projMatrix  * viewMatrix * modelMatrix * vCoord;\n"
        } else {
            ""
        }
    }

    protected open fun buildMatrixDeclare(): String {
        val sb = StringBuilder()
        if (mMatrix) {
            sb.append("uniform mat4 matrix;\n")
        }
        if (mProjectionMatrix) {
            sb.append("uniform mat4 projectionMatrix;\n")
        }
        if (mViewModelMatrix) {
            sb.append("uniform mat4 viewModelMatrix;\n")
        }
        if (mViewMatrix) {
            sb.append("uniform mat4 viewMatrix;\n")
        }
        if (mModelMatrix) {
            sb.append("uniform mat4 modelMatrix;\n")
        }
        if (mNormalMatrix) {
            sb.append("uniform mat4 normalMatrix;\n")
        }
        /*  //binding not supported
        if (mMatrix) {
            sb.append("layout(binding=0) uniform mat4 matrix;\n");
        }

        if (mProjectionMatrix) {
            sb.append("layout(binding=1) uniform mat4 projectionMatrix;\n");
        }

        if (mViewModelMatrix) {
            sb.append("layout(binding=2) uniform mat4 viewModelMatrix;\n");
        }

        if (mViewMatrix) {
            sb.append("layout(binding=3) uniform mat4 viewMatrix;\n");
        }

        if (mModelMatrix) {
            sb.append("layout(binding=4) uniform mat4 modelMatrix;\n");
        }

        if (mNormalMatrix) {
            sb.append("layout(binding=5) uniform mat4 normalMatrix;\n");
        }*/return sb.toString()
    }

    protected fun setExchangeData(color: Boolean, texCoord: Boolean, lighting: Boolean) {
        exColor = color
        exTexCoord = texCoord
        exLighting = lighting
    }

    protected open fun buildDeclareExchangeOut(): String {
        val sb = StringBuilder()
        if (exColor) {
            sb.append("out vec4 exColor;\n")
        }
        if (exTexCoord) {
            sb.append("out vec2 exTexCoord;\n")
        }
        if (exLighting) {
            sb.append("out vec3 exLighting;\n")
        }
        return sb.toString()
    }

    protected open fun buildDeclareExchangeIn(): String {
        val sb = StringBuilder()
        if (exColor) {
            sb.append("in vec4 exColor;\n")
        }
        if (exTexCoord) {
            sb.append("in vec2 exTexCoord;\n")
        }
        if (exLighting) {
            sb.append("in vec3 exLighting;\n")
        }
        return sb.toString()
    }

    protected fun setPrecision(`val`: Int) {
        val sval: String
        sval = when (`val`) {
            0 -> "lowp"
            1 -> "mediump"
            2 -> "highp"
            else -> throw IllegalArgumentException(" only 0,1,2 values are allowed.")
        }
        mPrecision = "precision $sval float;\n"
    }

    protected fun setVersion(versionValue: String) {
        mVersion = "#version $versionValue \n"
    }

    protected open fun buildDeclareOutCustom(): String {
        return ""
    }

    protected open fun buildDeclareExchangeCustom(): String {
        return ""
    }

    protected open fun buildDeclareUniformCustom(): String {
        return ""
    }

    protected open fun buildCalcCustom(): String {
        return ""
    }

    fun build(): String {
        val sb = StringBuilder()
        sb.append(mVersion)
            .append(mPrecision)
            .append(buildDeclareVertexInputData())
            .append(buildMatrixDeclare())
            .append(buildDeclareUniformCustom())
            .append(if (exOut) buildDeclareExchangeOut() else buildDeclareExchangeIn())
            .append(buildDeclareExchangeCustom())
            .append(buildDeclareOutCustom())
            .append("void main() {\n")
            .append(buildCalcCustom())
            .append("}\n")
        return sb.toString()
    }
}
