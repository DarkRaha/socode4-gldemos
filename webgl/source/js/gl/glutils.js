function GlUtils() {}


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