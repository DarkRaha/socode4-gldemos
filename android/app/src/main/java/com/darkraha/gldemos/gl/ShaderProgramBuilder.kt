package com.darkraha.gldemos.gl

import java.lang.StringBuilder

class ShaderProgramBuilder {
    private val inputDataDeclaration = arrayOf(
        "layout(location=0) in vec4 vPos;\n",
        "layout(location=1) in vec4 vColor;\n",
        "layout(location=2) in vec3 vNormal;\n",
        "layout(location=3) in vec2 vTexPos ;\n",
        "layout(location=4) in vec3 vTangent ;\n",
        "layout(location=5) in vec3 vBitangent ;\n"
    )
    private val useInputData = BooleanArray(inputDataDeclaration.size)
    private val matricesDeclaration = arrayOf(
        "uniform mat4 m;\n",
        "uniform mat4 mP;\n",
        "uniform mat4 mVM;\n",
        "uniform mat4 mV;\n",
        "uniform mat4 mM;\n",
        "uniform mat4 mNormal;\n"
    )
    private val useMatrices = BooleanArray(matricesDeclaration.size)
    private val solidColorDeclaration = "uniform vec4 solidColor;\n"
    private var useSolidColor = false
    private val samplerCount = 0
    private var useNormalSampler = false
    private val samplerDeclaration = arrayOf(
        "uniform sampler2D sampler;\n"
    )
    private var lightType = -1
    private val exchangeDataVS = arrayOf(
        "out vec4 exColor;\n",
        "out vec2 exTexPos;\n",
        "out vec3 exLighting;\n"
    )
    private val exchangeDataFS = arrayOf(
        "in vec4 exColor;\n",
        "in vec2 exTexPos;\n",
        "in vec3 exLighting;\n"
    )
    private val vertexSimpleLighting = arrayOf(
        "uniform vec3 lightAmbient;\n",
        "uniform vec3 lightDiffuse;\n",
        "uniform vec3 lightDirection;\n"
    )
    private var version = "300 es"
    private var precisionFloat = "mediump"
    private fun buildDeclareVertexLight(sb: StringBuilder) {
        when (lightType) {
            LIGHT_TYPE_DIRECTIONAL_SINGLE -> {
                for (s in vertexSimpleLighting) {
                    sb.append(s)
                }
                sb.append(exchangeDataVS[IND_EXCHANGE_LIGHT])
            }
            LIGHT_TYPE_PHONG, LIGHT_TYPE_GOURAD -> {
            }
            else -> {
            }
        }
    }

    private fun addCalcLight(sb: StringBuilder) {
        when (lightType) {
            LIGHT_TYPE_DIRECTIONAL_SINGLE -> {
                sb.append("vec3 calcLight(){\n")
                sb.append("  vec4 transformedNormal = mNormal *vec4 (vNormal,1.0);\n")
                sb.append("  float direction = max(dot(transformedNormal.xyz, lightDirection), 0.0);\n")
                sb.append("  return  lightAmbient + (lightDiffuse * direction);\n")
                sb.append("}\n")
            }
            LIGHT_TYPE_PHONG, LIGHT_TYPE_GOURAD -> {
            }
            else -> {
            }
        }
    }

    private fun buildAddVertexFunctions(sb: StringBuilder) {
        addCalcLight(sb)
    }

    private fun buildDeclareVertexIn(sb: StringBuilder) {
        for (i in useInputData.indices) {
            if (useInputData[i]) {
                sb.append(inputDataDeclaration[i])
            }
        }
    }

    private fun buildDeclareMatrices(sb: StringBuilder) {
        for (i in useMatrices.indices) {
            if (useMatrices[i]) {
                sb.append(matricesDeclaration[i])
            }
        }
    }

    private val calcPositionV: String
        get() = if (useMatrices[IND_MATRIX]) {
            "    gl_Position = m  * vPos;\n"
        } else if (useMatrices[IND_MATRIX_VIEW_MODEL]) {
            "    gl_Position = mP  * mVM * vPos;\n"
        } else if (useMatrices.get(IND_MATRIX_MODEL)) {
            "    gl_Position = mP  * mV * mM * vPos;\n"
        } else {
            ""
        }
    private val calcColorV: String
        get() {
            if (useInputData[IND_VERTEX_COLOR]) {
                return "exColor = vColor;\n"
            } else if (useSolidColor) {
                return "exColor = solidColor;\n"
            }
            return ""
        }

    private fun buildCalcObjectColor(sb: StringBuilder) {
        val isColor = useSolidColor || useInputData[IND_VERTEX_COLOR]
        val isTexture = useInputData[IND_VERTEX_TEX_POS]
        sb.append("vec4 objectColor = ")
        if (isTexture) {
            sb.append("texture(sampler, exTexPos)")
        }
        if (isColor && isTexture) {
            sb.append(" * ")
        }
        if (isColor) {
            sb.append("exColor ")
        }
        sb.append(";\n")
    }

    private fun buildCalcColorF(sb: StringBuilder) {
        when (lightType) {
            LIGHT_TYPE_DIRECTIONAL_SINGLE -> sb.append("fragColor = vec4(objectColor.rgb * exLighting, objectColor.a);\n")
            LIGHT_TYPE_PHONG, LIGHT_TYPE_GOURAD -> sb.append(
                "fragColor = objectColor;\n"
            )
            else -> sb.append("fragColor = objectColor;\n")
        }
    }

    private val calcTexPositionV: String
        private get() {
            return if (useInputData.get(IND_VERTEX_TEX_POS)) {
                "exTexPos = vTexPos;\n"
            } else ""
        }
    private val calcLightV: String
        get() {
            return when (lightType) {
                LIGHT_TYPE_DIRECTIONAL_SINGLE, LIGHT_TYPE_GOURAD -> " exLighting = calcLight();\n"
                else -> ""
            }
        }

    fun buildVertexShader(): String {
        val sb = StringBuilder()
        sb.append("#version ").append(version).append("\n")
        sb.append("precision ").append(precisionFloat).append(" float;\n")
        buildDeclareVertexIn(sb)
        buildDeclareMatrices(sb)
        buildDeclareVertexLight(sb)
        if (useSolidColor) {
            sb.append(solidColorDeclaration)
        }
        if (useSolidColor || useInputData[IND_VERTEX_COLOR]) {
            sb.append(exchangeDataVS[IND_EXCHANGE_COLOR])
        }
        if (useInputData[IND_VERTEX_TEX_POS]) {
            sb.append(exchangeDataVS[IND_EXCHANGE_TEX_POS])
        }
        buildAddVertexFunctions(sb)
        sb.append("void main() {\n")
        sb.append(calcPositionV)
        sb.append(calcTexPositionV)
        sb.append(calcColorV)
        sb.append(calcLightV)
        sb.append("}\n")
        println("VertexShader:\n $sb")
        return sb.toString()
    }

    fun buildFragmentShader(): String {
        val sb = StringBuilder()
        sb.append("#version ").append(version).append("\n")
        sb.append("precision ").append(precisionFloat).append(" float;\n")
        sb.append("out vec4 fragColor;\n")
        if (useSolidColor || useInputData[IND_VERTEX_COLOR]) {
            sb.append(exchangeDataFS[IND_EXCHANGE_COLOR])
        }
        if (useInputData[IND_VERTEX_TEX_POS]) {
            sb.append(exchangeDataFS[IND_EXCHANGE_TEX_POS])
            sb.append(samplerDeclaration[0])
        }
        if (lightType == 0) {
            sb.append(exchangeDataFS[IND_EXCHANGE_LIGHT])
        }
        sb.append("void main() {\n")
        buildCalcObjectColor(sb)
        buildCalcColorF(sb)
        sb.append("}\n")
        println("FragmentShader:\n $sb")
        return sb.toString()
    }

    //------------------------------------------------------------------
    fun vertexAttributes(color: Boolean, texPos: Boolean, normals: Boolean): ShaderProgramBuilder {
        useInputData[IND_VERTEX_COLOR] = color
        useInputData[IND_VERTEX_TEX_POS] = texPos
        useInputData[IND_VERTEX_NORMAL] = normals
        return this
    }

    fun solidColor(): ShaderProgramBuilder {
        useSolidColor = true
        return this
    }

    fun version(v: String): ShaderProgramBuilder {
        version = v
        return this
    }

    fun precision(v: String): ShaderProgramBuilder {
        precisionFloat = v
        return this
    }

    fun lightDirectional(): ShaderProgramBuilder {
        lightType = LIGHT_TYPE_DIRECTIONAL_SINGLE
        return this
    }

    fun bumping(): ShaderProgramBuilder {
        useNormalSampler = true
        return this
    }

    fun matrix(withNormal: Boolean): ShaderProgramBuilder {
        useMatrices[IND_MATRIX] = true
        useMatrices[IND_MATRIX_NORMAL] = withNormal
        return this
    }

    fun matrix(): ShaderProgramBuilder {
        useMatrices[IND_MATRIX] = true
        return this
    }

    fun matrixP_VM(): ShaderProgramBuilder {
        useMatrices[IND_MATRIX_VIEW_MODEL] = true
        useMatrices[IND_MATRIX_PROJECTION] = true
        return this
    }

    fun matrixP_V_M(): ShaderProgramBuilder {
        useMatrices[IND_MATRIX_VIEW] = true
        useMatrices[IND_MATRIX_MODEL] = true
        useMatrices[IND_MATRIX_PROJECTION] = true
        return this
    }

    fun normalMatrix(): ShaderProgramBuilder {
        useMatrices[IND_MATRIX_NORMAL] = true
        return this
    }

    fun build(): ShaderProgram {
        return ShaderProgram(buildVertexShader(), buildFragmentShader())
    }

    companion object {
        const val A_LOCATION_VERTEX_POS = 0
        const val A_LOCATION_VERTEX_COLOR = 1
        const val A_LOCATION_VERTEX_NORMAL = 2
        const val A_LOCATION_VERTEX_TEXPOS = 3
        const val IND_VERTEX_POS = 0
        const val IND_VERTEX_COLOR = 1
        const val IND_VERTEX_NORMAL = 2
        const val IND_VERTEX_TEX_POS = 3
        const val IND_VERTEX_TANGENT = 4
        const val IND_VERTEX_BITANGENT = 5
        const val IND_MATRIX = 0
        const val IND_MATRIX_PROJECTION = 1
        const val IND_MATRIX_VIEW_MODEL = 2
        const val IND_MATRIX_VIEW = 3
        const val IND_MATRIX_MODEL = 4
        const val IND_MATRIX_NORMAL = 5
        const val LIGHT_TYPE_DIRECTIONAL_SINGLE = 0
        const val LIGHT_TYPE_GOURAD = 1
        const val LIGHT_TYPE_PHONG = 2
        const val IND_EXCHANGE_COLOR = 0
        const val IND_EXCHANGE_TEX_POS = 1
        const val IND_EXCHANGE_LIGHT = 2
        val U_MATRIX_NAMES = arrayOf(
            "m", "mP", "mVM", "mV", "mM", "mNormal"
        )
        val INPUT_DATA_NAMES = arrayOf(
            "vPos", "vColor", "vNormal", "vTexPos", "vTangent", "vBitangent"
        )
        val U_LIGHT_NAMES = arrayOf(
            "lightAmbient",
            "lightDiffuse",
            "lightDirection"
        )
        const val U_SOLID_COLOR_NAME = "solidColor"
        const val U_SAMPLER_NAME = "sampler"
    }

    init {
        useInputData[0] = true
        useMatrices[0] = true
    }
}