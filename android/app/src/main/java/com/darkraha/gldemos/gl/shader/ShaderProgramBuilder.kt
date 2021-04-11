package com.darkraha.opengldemokt.gl.shader



import com.darkraha.gldemos.gl.ShaderProgram
import java.lang.StringBuilder

class ShaderProgramBuilder : HelperBuilder {
    private var version = "300 es"
    private var precisionFloat = "mediump"

    private val baseBuilder = BaseBuilder()
    private val colorBuilder: ColorBuilder = ColorBuilder()
    private var texture2DBuilder: Texture2DBuilder? = null
    private var lightBuilder: LightBuilder? = null

    private fun buildShader(shaderType: Int): String {
        val sb = StringBuilder()
        sb.append("#version ").append(version).append("\n")
        sb.append("precision ").append(precisionFloat).append(" float;\n")
        addTypeDeclarations(sb, shaderType)
        addInputDeclarations(sb, shaderType)
        addUniformDeclarations(sb, shaderType)
        addExchangeDeclarations(sb, shaderType)
        addFuncsDeclarations(sb, shaderType)
        sb.append("void main() {\n")
        addCalculations(sb, shaderType)
        sb.append("}\n")
        val ret = sb.toString()
        println("Shader $shaderType:\n$ret")
        return ret
    }

    fun buildVertexShader(): String {
        return buildShader(SHADER_TYPE_VERTEX)
    }

    fun buildFragmentShader(): String {
        return buildShader(SHADER_TYPE_FRAGMENT)
    }

    //------------------------------------------------------------------
    fun colors(usePerVertex: Boolean, useSolidColor: Boolean): ShaderProgramBuilder {
        colorBuilder.useSolidColor = useSolidColor
        colorBuilder.usePerVertex = usePerVertex
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

    fun texture2D(): ShaderProgramBuilder {
        if (texture2DBuilder == null) {
            texture2DBuilder = Texture2DBuilder()
        }
        return this
    }

    fun lightDirectional(withBumping: Boolean): ShaderProgramBuilder {
        if (lightBuilder == null) {
            lightBuilder = DirectionLightBuilder()
        }

        lightBuilder = DirectionLightBuilder().also {
            it.withBumping = withBumping
        }
        return this
    }

    fun matrix(): ShaderProgramBuilder {
        baseBuilder.useMatrices[IND_MATRIX] = true
        return this
    }

    fun matrix(v: Boolean): ShaderProgramBuilder {
        baseBuilder.useMatrices[IND_MATRIX] = v
        return this
    }

    fun matrixP_VM(v: Boolean): ShaderProgramBuilder {
        baseBuilder.useMatrices[IND_MATRIX_VIEW_MODEL] = v
        baseBuilder.useMatrices[IND_MATRIX_PROJECTION] = v
        return this
    }

    fun matrixP_VM(): ShaderProgramBuilder {
        baseBuilder.useMatrices[IND_MATRIX_VIEW_MODEL] = true
        baseBuilder.useMatrices[IND_MATRIX_PROJECTION] = true
        return this
    }


    fun matrixP_V_M(): ShaderProgramBuilder {
        baseBuilder.useMatrices[IND_MATRIX_VIEW] = true
        baseBuilder.useMatrices[IND_MATRIX_MODEL] = true
        baseBuilder.useMatrices[IND_MATRIX_PROJECTION] = true
        return this
    }

    fun matrixP_V_M(v: Boolean): ShaderProgramBuilder {
        baseBuilder.useMatrices[IND_MATRIX_VIEW] = v
        baseBuilder.useMatrices[IND_MATRIX_MODEL] = v
        baseBuilder.useMatrices[IND_MATRIX_PROJECTION] = v
        return this
    }

    fun build(): ShaderProgram {
        return ShaderProgram(buildVertexShader(), buildFragmentShader())
    }

    //========================================================================
    override fun addTypeDeclarations(sb: StringBuilder, shaderType: Int) {
        baseBuilder.addTypeDeclarations(sb, shaderType)
        colorBuilder.addTypeDeclarations(sb, shaderType)

        texture2DBuilder?.apply {
            addTypeDeclarations(sb, shaderType)
        }

        lightBuilder?.apply {
            addTypeDeclarations(sb, shaderType)
        }
    }

    override fun addInputDeclarations(sb: StringBuilder, shaderType: Int) {
        baseBuilder.addInputDeclarations(sb, shaderType)
        colorBuilder.addInputDeclarations(sb, shaderType)

        texture2DBuilder?.apply {
            addInputDeclarations(sb, shaderType)
        }

        lightBuilder?.apply {
            addInputDeclarations(sb, shaderType)
        }
    }

    override fun addExchangeDeclarations(sb: StringBuilder, shaderType: Int) {
        baseBuilder.addExchangeDeclarations(sb, shaderType)
        colorBuilder.addExchangeDeclarations(sb, shaderType)

        texture2DBuilder?.apply {
            addExchangeDeclarations(sb, shaderType)
        }

        lightBuilder?.apply {
            addExchangeDeclarations(sb, shaderType)
        }
    }

    override fun addUniformDeclarations(sb: StringBuilder, shaderType: Int) {
        baseBuilder.addUniformDeclarations(sb, shaderType)
        colorBuilder.addUniformDeclarations(sb, shaderType)

        texture2DBuilder?.apply {
            addUniformDeclarations(sb, shaderType)
        }

        lightBuilder?.apply {
            addUniformDeclarations(sb, shaderType)
        }
    }

    override fun addFuncsDeclarations(sb: StringBuilder, shaderType: Int) {
        baseBuilder.addFuncsDeclarations(sb, shaderType)
        colorBuilder.addFuncsDeclarations(sb, shaderType)

        texture2DBuilder?.apply {
            addFuncsDeclarations(sb, shaderType)
        }

        lightBuilder?.apply {
            addFuncsDeclarations(sb, shaderType)
        }
    }

    override fun addCalculations(sb: StringBuilder, shaderType: Int) {
        baseBuilder.addCalculations(sb, shaderType)
        colorBuilder.addCalculations(sb, shaderType)

        texture2DBuilder?.apply {
            addCalculations(sb, shaderType)
        }

        lightBuilder?.apply {
            addCalculations(sb, shaderType)
        }
    }

    companion object {
        const val SHADER_TYPE_VERTEX = 1
        const val SHADER_TYPE_FRAGMENT = 2
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
        const val U_SOLID_COLOR_NAME = "solidColor"
        const val U_SAMPLER_NAME = "sampler"

        val U_MATRIX_NAMES = arrayOf(
            "m", "mP", "mVM", "mV", "mM", "mNormal"
        )
        val INPUT_DATA_NAMES = arrayOf(
            "vPos", "vColor", "vNormal", "vTexPos", "vTangent", "vBitangent"
        )
        val U_LIGHT_NAMES = arrayOf(
            "light.ambient",
            "light.diffuse",
            "light.direction"
        )

    }
}