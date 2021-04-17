class ShaderProgram {
    gl
    ids
    idProgram
    matricesLocations = new Array(ShaderProgramBuilder.U_MATRIX_NAMES.length);
    samplersLocations = new Array(8);
    light = new Array(3);
    normalSamplerLocation = 0;
    solidColorLocation = 0;


    constructor(gl, vertexShaderCode, fragmentShaderCode) {
        this.gl = gl;
        this.ids = ShaderProgram.createProgram(gl, vertexShaderCode,
            fragmentShaderCode);
        this.idProgram = this.ids[0];
        this.setupLocations();
    }


    setupLocations() {
        var gl = this.gl;
        gl.useProgram(this.idProgram);

        for (var i = 0; i < this.matricesLocations.length; ++i) {
            this.matricesLocations[i] = gl.getUniformLocation(
                this.idProgram,
                ShaderProgramBuilder.U_MATRIX_NAMES[i]
            );
        }

        this.samplersLocations[0] = gl.getUniformLocation(this.idProgram,
            ShaderProgramBuilder.U_SAMPLER_NAME);

        for (var i = 1; i < this.samplersLocations.length; ++i) {
            this.samplersLocations[i] = gl.getUniformLocation(this.idProgram,
                ShaderProgramBuilder.U_SAMPLER_NAME + i);
        }

        this.solidColorLocation = gl.getUniformLocation(this.idProgram,
            ShaderProgramBuilder.U_SOLID_COLOR_NAME);

        this.normalSamplerLocation = gl.getUniformLocation(this.idProgram,
            ShaderProgramBuilder.U_NORMAL_SAMPLER_NAME);

        this.light[0] = gl.getUniformLocation(this.idProgram,
            ShaderProgramBuilder.U_LIGHT_NAMES[0]);

        this.light[1] = gl.getUniformLocation(this.idProgram,
            ShaderProgramBuilder.U_LIGHT_NAMES[1]);
        this.light[2] = gl.getUniformLocation(this.idProgram,
            ShaderProgramBuilder.U_LIGHT_NAMES[2]);
    }


    use() {
        this.gl.useProgram(this.ids[0]);
    }

    uniformTexture(texture) {
        const gl = this.gl;
        gl.activeTexture(texture.textureUnit);
        gl.bindTexture(texture.textureType, texture.idTexture);
        const ind = texture.textureUnit - gl.TEXTURE0;
        gl.uniform1i(this.samplersLocations[ind], ind);
    }

    uniformIdTexture(idTexture) {
        const samplerLocation = this.gl.getUniformLocation(this.idProgram, "sampler");
        this.gl.activeTexture(gl.TEXTURE0);
        this.gl.bindTexture(gl.TEXTURE_2D, idTexture);
        this.gl.uniform1i(samplerLocation, 0);
    }


    uniformObjectTexture(glObject) {
        var usedUnit = 0;

        if (!!glObject.texture) {
            this.gl.activeTexture(this.gl.TEXTURE0);
            this.gl.bindTexture(glObject.texture.textureType, glObject.texture.idTexture);
            this.gl.uniform1i(this.samplersLocations[0], 0);
            ++usedUnit;
        }

        if (!!glObject.normalTexture) {
            this.gl.activeTexture(this.gl.TEXTURE0 + usedUnit);
            this.gl.bindTexture(glObject.normalTexture.textureType,
                glObject.normalTexture.idTexture);
            this.gl.uniform1i(this.normalSamplerLocation, usedUnit);
            ++usedUnit;
        }

        if (!!glObject.extraTextures) {
            for (var i = 0; i < glObject.extraTextures.length; ++i) {
                this.gl.activeTexture(this.gl.TEXTURE0 + usedUnit);
                glBindTexture(glObject.extraTextures[i].textureType,
                    glObject.extraTextures[i].idTexture);
                glUniform1i(this.samplersLocations[i + 1], usedUnit);
                ++usedUnit;
            }
        }
    }

    uniformMatrix(m) {
        this.gl.uniformMatrix4fv(
            this.matricesLocations[ShaderProgramBuilder.IND_MATRIX],
            false, m);
    }

    uniformSolidColor(color) {
        this.gl.uniform4fv(this.solidColorLocation, color);
    }

    uniformMatrices(m) {
        const gl = this.gl;

        gl.uniformMatrix4fv(
            this.matricesLocations[ShaderProgramBuilder.IND_MATRIX], false,
            m.matrix
        );

        gl.uniformMatrix4fv(
            this.matricesLocations[ShaderProgramBuilder.IND_MATRIX_NORMAL], false,
            m.normals
        );

        gl.uniformMatrix4fv(
            this.matricesLocations[ShaderProgramBuilder.IND_MATRIX_PROJECTION], false,
            m.projection
        );

        gl.uniformMatrix4fv(
            this.matricesLocations[ShaderProgramBuilder.IND_MATRIX_VIEW], false,
            m.view
        );

        gl.uniformMatrix4fv(
            this.matricesLocations[ShaderProgramBuilder.IND_MATRIX_VIEW_MODEL], false,
            m.viewModel
        );
        gl.uniformMatrix4fv(
            this.matricesLocations[ShaderProgramBuilder.IND_MATRIX_MODEL], false,
            m.model
        );
    }

    uniform(glObject, m, light) {

        this.uniformObjectTexture(glObject);

        if (!!glObject.transforms) {
            m.applyModel(glObject.transforms);
        }

        this.uniformMatrices(m);

        if (!!light) {
            this.uniformDirectionalLight(light.ambient, light.diffuseColor, light.direction);
        }

    }

    uniformDirectionalLight(ambient, lightDiffuse, lightDirection) {
        this.gl.uniform3fv(this.light[0], ambient);
        this.gl.uniform3fv(this.light[1], lightDiffuse);
        this.gl.uniform3fv(this.light[2], lightDirection);
    }

    dispose() {
        // glDetachShader(ids[0], ids[1])
        // glDetachShader(ids[0], ids[2])
        // glDeleteProgram(ids[0])
        // glDeleteShader(ids[1])
        // glDeleteShader(ids[2])
    }

    delete() {
        // glDetachShader(idProgram, ids[1])
        // glDetachShader(idProgram, ids[2])
        // glDeleteProgram(ids[0])
    }

    // companion object {
    //     val MATRIX_BUFFER = MemoryUtil.memAllocFloat(16)

};


ShaderProgram.VERTEX_SHADER_DEFAULT = `#version 300 es
precision mediump float;
layout(location=0) in vec4 vCoord;
layout(location=1) in vec4 vColor ;
layout(location=2) in vec3 vNormal;
layout(location=3) in vec2 vTexCoord ;
out vec4 exColor;
out vec2 exTexCoord;
uniform mat4 matrix;
uniform vec4 uColor;
void main() {
     gl_Position = matrix  * vCoord;
     if(uColor.w != -1.0) { exColor=uColor; } else {exColor=vColor;} 
     exTexCoord = vTexCoord;
}
`;

ShaderProgram.FRAGMENT_SHADER_DEFAULT = `#version 300 es
precision mediump float;
in vec4 exColor;
in vec2 exTexCoord;
out vec4 fragColor;
uniform int withTexture;
uniform sampler2D texSampler;

void main() {
    if(withTexture==1) {fragColor = texture(texSampler, exTexCoord);}
    else {fragColor = exColor;}
}
`;

ShaderProgram.compileShader = function(gl, type, source) {
    const idShader = gl.createShader(type);
    gl.shaderSource(idShader, source);
    gl.compileShader(idShader);

    if (!gl.getShaderParameter(idShader, gl.COMPILE_STATUS)) {
        console.log('An error occurred compiling the shaders: ',
            gl.getShaderInfoLog(idShader),
            "source: ", source);
        const msg = 'An error occurred compiling the shaders: ' +
            gl.getShaderInfoLog(idShader) +
            "\n source: " + source;
        alert(msg);
        gl.deleteShader(idShader);
        return null;
    }

    return idShader;
}

ShaderProgram.linkProgram = function(gl,
    idVertexShader,
    idFragmentShader) {

    const idProgram = gl.createProgram();
    gl.attachShader(idProgram, idVertexShader);
    gl.attachShader(idProgram, idFragmentShader);
    gl.linkProgram(idProgram);


    if (!gl.getProgramParameter(idProgram, gl.LINK_STATUS)) {
        alert('Unable to initialize the shader program: ' +
            gl.getProgramInfoLog(idProgram));
        return null;
    }

    return idProgram;
}

ShaderProgram.createProgram = function(gl, srcVertexShader, srcFragmentShader) {
    const idVS = ShaderProgram
        .compileShader(gl, gl.VERTEX_SHADER, srcVertexShader);

    const idFS = ShaderProgram
        .compileShader(gl, gl.FRAGMENT_SHADER, srcFragmentShader);

    const idProg = ShaderProgram.linkProgram(gl, idVS, idFS);

    return [idProg, idVS, idFS];
}