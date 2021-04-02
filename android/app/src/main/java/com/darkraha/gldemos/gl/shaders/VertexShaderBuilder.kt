package com.darkraha.gldemos.gl.shaders

import java.lang.StringBuilder

class VertexShaderBuilder : ShaderBuilderA() {
    private var mLightAmbient: FloatArray? = null
    private var mLightDirectionalColor: FloatArray? =null
    private var mLightDirectionalVector: FloatArray? = null

    fun directionalLightEmbeded(
        ambient: FloatArray?,
        lightDirectionalColor: FloatArray?,
        lightDirectionalVector: FloatArray?
    ): VertexShaderBuilder {
        mLightAmbient = ambient
        mLightDirectionalColor = lightDirectionalColor
        mLightDirectionalVector = lightDirectionalVector
        return this
    }

    protected fun buildCalcLightEmbeded(): String {
        if (mLightAmbient == null || mLightDirectionalColor == null || mLightDirectionalVector == null) {
            return ""
        }
        val sb = StringBuilder()
        sb.append("vec3 lightAmbient = vec3(")
            .append(mLightAmbient!![0]).append(",")
            .append(mLightAmbient!![1]).append(",")
            .append(mLightAmbient!![2]).append(");\n")

        sb.append("vec3 lightDirectionalColor = vec3(")
            .append(mLightDirectionalColor!![0]).append(",")
            .append(mLightDirectionalColor!![1]).append(",")
            .append(mLightDirectionalColor!![2]).append(");\n")

        sb.append("vec3 lightDirectionalVector = vec3(")
            .append(mLightDirectionalVector!![0]).append(",")
            .append(mLightDirectionalVector!![1]).append(",")
            .append(mLightDirectionalVector!![2]).append(");\n")

        sb.append("vec4 transformedNormal = normalMatrix*vec4(vNormal,1.0);\n")
        sb.append("float directional = max(dot(transformedNormal.xyz, lightDirectionalVector), 0.0);\n")
        sb.append("exLighting =  lightAmbient + (lightDirectionalColor * directional);")

        return sb.toString()
    }

    fun exchangeData(color: Boolean, texCoord: Boolean, lighting: Boolean): VertexShaderBuilder {
        setExchangeData(color, texCoord, lighting)
        return this
    }

    fun matrixData(normal: Boolean): VertexShaderBuilder {
        mMatrix = true
        mNormalMatrix = normal
        mModelMatrix = false
        mViewMatrix = false
        mViewModelMatrix = false
        mProjectionMatrix = false
        return this
    }

    fun matrixViewModelData(normal: Boolean): VertexShaderBuilder {
        mMatrix = false
        mNormalMatrix = normal
        mModelMatrix = false
        mViewMatrix = false
        mViewModelMatrix = true
        mProjectionMatrix = true
        return this
    }

    fun matrix3Data(normal: Boolean): VertexShaderBuilder {
        mMatrix = false
        mNormalMatrix = normal
        mModelMatrix = true
        mViewMatrix = true
        mViewModelMatrix = false
        mProjectionMatrix = true
        return this
    }

    fun vertexInputData(coord: Boolean, color: Boolean, texCoord: Boolean, normal: Boolean): VertexShaderBuilder {
        setVertexInputData(coord, color, texCoord, normal)
        return this
    }

    fun version(versionValue: String): VertexShaderBuilder {
        setVersion(versionValue)
        return this
    }

    fun precision(`val`: Int): VertexShaderBuilder {
        setPrecision(`val`)
        return this
    }

    override fun buildCalcCustom(): String {
        val sb = StringBuilder()
        sb.append(buildCalcPosition())
        sb.append(buildCalcTexCoord())
        sb.append(buildCalcColor())
        sb.append(buildCalcLightEmbeded())
        return sb.toString()
    }
}
