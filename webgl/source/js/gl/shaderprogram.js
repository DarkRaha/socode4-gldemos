function ShaderProgram() {}


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

ShaderProgram.createProgram = function(gl,
    srcVertexShader = ShaderProgram.VERTEX_SHADER_DEFAULT,
    srcFragmentShader = ShaderProgram.FRAGMENT_SHADER_DEFAULT) {
    const idVS = ShaderProgram
        .compileShader(gl, gl.VERTEX_SHADER, srcVertexShader);

    const idFS = ShaderProgram
        .compileShader(gl, gl.FRAGMENT_SHADER, srcFragmentShader);

    const idProg = ShaderProgram.linkProgram(gl, idVS, idFS);

    return {
        idProgram: idProg,
        idVertexShader: idVS,
        idFragmentShader: idFS
    };
}