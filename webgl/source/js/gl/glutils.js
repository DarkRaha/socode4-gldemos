function GlUtils() {}


GlUtils.createTextureStub = function(gl) {
    const texture = gl.createTexture();
    gl.bindTexture(gl.TEXTURE_2D, texture);

    const level = 0;
    const internalFormat = gl.RGBA;
    const width = 1;
    const height = 1;
    const border = 0;
    const srcFormat = gl.RGBA;
    const srcType = gl.UNSIGNED_BYTE;
    const pixel = new Uint8Array([0, 0, 255, 255]); // opaque blue
    gl.texImage2D(gl.TEXTURE_2D, level, internalFormat,
        width, height, border, srcFormat, srcType,
        pixel);
    return texture;
}


GlUtils.createTexture = function(gl, idTexture, image) {
    idTexture = (idTexture == 0) ? gl.createTexture() : idTexture;
    gl.bindTexture(gl.TEXTURE_2D, idTexture);
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, image);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
    return idTexture;
}

GlUtils.loadTextureDefault = function loadTexture(gl, url) {
    const texture = GlUtils.createTextureStub(gl);

    const image = new Image();
    image.onload = function() {
        GlUtils.createTexture(gl, texture, image);
    };
    image.src = url;
    return texture;
}

function isPowerOf2(value) {
    return (value & (value - 1)) == 0;
}

GlUtils.createVAO = function(gl) {
    const idVao = gl.createVertexArray() //GenVertexArrays();
    gl.bindVertexArray(idVao);
    return idVao;
}

GlUtils.createVBO = function(gl, array) {
    const idVbo = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, idVbo);
    gl.bufferData(gl.ARRAY_BUFFER,
        new Float32Array(array),
        gl.STATIC_DRAW);
    return idVbo;
}

GlUtils.bindAttributes = function(gl, idVbo, numCoords,
    numColorComponents,
    texCoord) {

    gl.bindBuffer(gl.ARRAY_BUFFER, idVbo);

    // size of float is 4
    const stride = 4 * (numCoords + numColorComponents + ((texCoord) ? 2 : 0));
    const A_LOCATION_COORDS = 0;
    const A_LOCATION_COLORS = 1;
    const A_LOCATION_TEXCOORDS = 3;

    if (numCoords > 0) {
        gl.enableVertexAttribArray(A_LOCATION_COORDS);
        gl.vertexAttribPointer(
            A_LOCATION_COORDS, numCoords,
            gl.FLOAT, false, stride, 0);
    }

    if (numColorComponents > 0) {
        gl.enableVertexAttribArray(A_LOCATION_COLORS);
        gl.vertexAttribPointer(
            A_LOCATION_COLORS, numCoords,
            gl.FLOAT, false, stride, 4 * numCoords);
    }

    if (texCoord) {
        gl.enableVertexAttribArray(A_LOCATION_TEXCOORDS);
        gl.vertexAttribPointer(
            A_LOCATION_TEXCOORDS, numCoords,
            gl.FLOAT, false, stride, 4 * (numCoords + numColorComponents));
    }
}