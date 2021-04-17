class GlModel {
    idVao
    ids
    idIbo
    idIboType
    drawType
    count
    name

    constructor(idVao, ids, idIbo, drawType, count, name) {
        this.idVao = idVao;
        this.ids = ids;
        this.idIbo = idIbo;
        this.drawType = drawType;
        this.count = count;
        this.name = name;
    }

    draw(gl) {
        gl.bindVertexArray(this.idVao);
        if (!!this.idIbo) {

            // gl.drawElements(this.drawType, this.count, gl.UNSIGNED_BYTE, 0);
            gl.drawElements(this.drawType, this.count, gl.UNSIGNED_INT, 0);

        } else {
            gl.drawArrays(this.drawType, 0, this.count);
        }
    }

    dispose() {
        // glDeleteVertexArrays(idVao)
        // glDeleteBuffers(idIbo)
        // glDeleteBuffers(ids)
    }
}


class Model {

    name
    vPos
    vColor
    vTexPos
    vNormal
    vTangent
    vBitangent
    indices
    color
    posComponents = 0
    colorComponents = 0
    texPosComponents = 0
    normalComponents = 0
    drawType = 0;

    toGlModel(gl) {
        const idVao = gl.createVertexArray();
        gl.bindVertexArray(idVao);

        var idIbo = 0;
        var ids = new Array(6);

        ids[0] = Model.createVBO(gl, this.vPos,
            ShaderProgramBuilder.A_LOCATION_VERTEX_POS,
            this.posComponents);

        var count = this.vPos.length / this.posComponents;


        if (!!this.vColor) {
            ids[1] = Model.createVBO(gl, this.vColor,
                ShaderProgramBuilder.A_LOCATION_VERTEX_COLOR,
                this.colorComponents);
        }

        if (!!this.vNormal) {
            ids[2] = Model.createVBO(gl, this.vNormal,
                ShaderProgramBuilder.A_LOCATION_VERTEX_NORMAL,
                this.normalComponents);
        }

        if (!!this.vTexPos) {
            ids[3] = Model.createVBO(gl, this.vTexPos,
                ShaderProgramBuilder.A_LOCATION_VERTEX_TEXPOS,
                this.texPosComponents);
        }

        if (!!this.vTangent) {
            ids[4] = Model.createVBO(gl, this.vTangent,
                ShaderProgramBuilder.A_LOCATION_VERTEX_TANGENT, 3);
        }

        if (!!this.vBitangent) {
            ids[5] = Model.createVBO(gl, this.vBitangent,
                ShaderProgramBuilder.A_LOCATION_VERTEX_BITANGENT, 3);
        }

        if (!!this.indices) {
            // if (count < 255) {
            //     idIbo = Model.createIBO8(gl, this.indices);
            // } else {
            idIbo = Model.createIBO(gl, this.indices);
            //}

            count = this.indices.length;
        }

        // gl.bindVertexArray(0);


        return new GlModel(idVao, ids, idIbo, this.drawType == 0 ?
            gl.TRIANGLES : this.drawType, count, this.name);
    }

    calcTangent() {
        this.vTangent = new Array(this.vPos.length);
        Model.calcTangent(this.vPos, this.vTexPos, this.indices, this.vTangent, null);
        return this;
    }

    calcTangentBitangent() {
        this.vTangent = new Array(vPos.length);
        this.vBitangent = new Array(vPos.length);
        Model.calcTangent(this.vPos, this.vTexPos, this.indices,
            this.vTangent, this.vBitangent);
        return this;
    }



}



Model.createIBO = function(gl, data) {
    const idIbo = gl.createBuffer();
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, idIbo);
    gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint32Array(data), gl.STATIC_DRAW);
    return idIbo;
}

Model.createIBO8 = function(gl, data) {
    const idIbo = gl.createBuffer();
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, idIbo);
    gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint8Array(data), gl.STATIC_DRAW);
    return idIbo;
}


Model.createVBO = function(gl, data, locations, size) {
    const idVbo = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, idVbo);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(data), gl.STATIC_DRAW);

    gl.enableVertexAttribArray(locations);
    gl.vertexAttribPointer(locations, size, gl.FLOAT, false, 0, 0);
    return idVbo;
}



/**
 * Calculates tangents/bitangents for bumping.
 *
 * @param vPos       array of the vertex positions (x,y,z)
 * @param vTexPos    array of the vertex texture positions (s,t)
 * @param srcIndices    indices of vertices to draw model with triangles
 * @param vTangent   array to store calculated tangent vectors (x,y,z)
 * @param vBitangent array to store calculated tangent vectors (x,y,z)
 */
Model.calcTangent = function(vPos, vTexPos, srcIndices, vTangent, vBitangent) {
    if (!vPos || !vTexPos) {
        throw "Wrong arguments to calculate calcTangent()";
    }
    const posComponents = 3;
    const texPosComponets = 2;
    var offset;

    const indices = !srcIndices ? new Array(vPos.length / posComponents) : srcIndices;

    vTangent.fill(0.0);

    if (!!vBitangent) {
        vBitangent.fill(0.0);
    }

    if (!srcIndices) {
        for (var i = 0; i < indices.length; ++i) {
            indices[i] = i;
        }
    }

    // const { vec3, vec2 } = glMatrix;

    const vec3 = glMatrix.vec3;
    const vec2 = glMatrix.vec2;

    // coordinates of points of triangle
    const p0 = vec3.create();
    const p1 = vec3.create();
    const p2 = vec3.create();

    // texture coordinates of points of triangle
    const pTex0 = vec2.create();
    const pTex1 = vec2.create();
    const pTex2 = vec2.create();

    // for storing calculated edges (p1,p0) and (p2,p0)
    const deltaP10 = vec3.create();
    const deltaP20 = vec3.create();

    // for storing calculated edges
    // in texture coordinates (pTex1, pTex0) and (pTex2,pTex0)
    const deltaTex10 = vec2.create();
    const deltaTex20 = vec2.create();

    //
    const tmp1 = vec3.create();
    const tmp2 = vec3.create();
    const tangent = vec3.create();
    const bitangent = vec3.create();

    var i = 0;

    while (i < indices.length) {

        offset = indices[i] * posComponents;
        vec3.set(p0, vPos[offset], vPos[offset + 1], vPos[offset + 2]);

        offset = indices[i + 1] * posComponents;
        vec3.set(p1, vPos[offset], vPos[offset + 1], vPos[offset + 2]);

        offset = indices[i + 2] * posComponents;
        vec3.set(p2, vPos[offset], vPos[offset + 1], vPos[offset + 2]);

        offset = indices[i] * texPosComponets;
        vec2.set(pTex0, vTexPos[offset], vTexPos[offset + 1]);

        offset = indices[i + 1] * texPosComponets;
        vec2.set(pTex1, vTexPos[offset], vTexPos[offset + 1]);

        offset = indices[i + 2] * texPosComponets;
        vec2.set(pTex2, vTexPos[offset], vTexPos[offset + 1]);


        // calc edges
        vec3.sub(deltaP10, p1, p0);
        vec3.sub(deltaP20, p2, p0);
        vec2.sub(deltaTex10, pTex1, pTex0);
        vec2.sub(deltaTex20, pTex2, pTex0);

        const r = 1.0 / (deltaTex10[0] * deltaTex20[1] - deltaTex10[1] * deltaTex20[0]);

        // tangent = (deltaP10 * deltaTex20.y   - deltaP20 * deltaTex10.y)*r;
        vec3.scale(tmp1, deltaP10, deltaTex20[1]);
        vec3.scale(tmp2, deltaP20, deltaTex10[1]);
        vec3.sub(tangent, tmp1, tmp2);
        vec3.scale(tangent, tangent, r);

        // add same tangent for each points of triangle
        // if point already had tangent we will
        // average tangent from old value and new value
        offset = indices[i] * posComponents;
        vTangent[offset] += tangent[0]; // x
        vTangent[offset + 1] += tangent[1]; // y
        vTangent[offset + 2] += tangent[2]; // z

        offset = indices[i + 1] * posComponents;
        vTangent[offset] += tangent[0];
        vTangent[offset + 1] += tangent[1];
        vTangent[offset + 2] += tangent[2];

        offset = indices[i + 2] * posComponents;
        vTangent[offset] += tangent[0];
        vTangent[offset + 1] += tangent[1];
        vTangent[offset + 2] += tangent[2];

        if (!!vBitangent) {
            // bitangent = (deltaP20 * deltaTex10.x   - deltaP10 * deltaTex20.x)*r;
            vec3.scale(tmp1, deltaP20, deltaTex10[0]);
            vec3.scale(tmp2, deltaP10, deltaTex20[0]);
            vec3.sub(bitangent, tmp1, tmp2);
            vec3.scale(bitangent, bitangent, r);

            // add same bitangent for each points of triangle
            // if point already had bitangent we will
            // average bitangent from old value and new value
            offset = indices[i] * posComponents;
            vBitangent[offset] += bitangent[0];
            vBitangent[offset + 1] += bitangent[1];
            vBitangent[offset + 2] += bitangent[2];

            offset = indices[i + 1] * posComponents;
            vBitangent[offset] += bitangent[0];
            vBitangent[offset + 1] += bitangent[1];
            vBitangent[offset + 2] += bitangent[2];

            offset = indices[i + 2] * posComponents;
            vBitangent[offset] += bitangent[0];
            vBitangent[offset + 1] += bitangent[1];
            vBitangent[offset + 2] += bitangent[2];
        }
        i += 3;
    }

    Model.normalize3(vTangent);

    if (!!vBitangent) {
        Model.normalize3(vBitangent);
    }

}


Model.normalize3 = function(src) {
    const v = glMatrix.vec3.create();
    var i = 0;

    while (i < src.length) {
        glMatrix.vec3.set(v, src[i], src[i + 1], src[i + 2]);
        glMatrix.vec3.normalize(v, v);

        src[i] = v[0];
        src[i + 1] = v[1];
        src[i + 2] = v[2];

        i += 3;
    }
}




class ModelBuilder {
    model = new Model();

    coordinates(pos) {
        this.model.vPos = pos;
        this.model.posComponents = 3;
        return this;
    }

    coordinates2d(pos) {
        this.model.vPos = pos;
        this.model.posComponents = 2;
        console.log(",,,,,", this.model.posComponents);
        return this;
    }

    colorsRGB(colorsRGB) {
        this.model.vColor = colorsRGB;
        this.model.colorComponents = 3;
        return this;
    }

    textureCoordinates(st) {
        this.model.vTexPos = st;
        this.model.texPosComponents = 2;
        return this;
    }

    textureCoordinates3(st) {
        this.model.vTexPos = st;
        this.model.texPosComponents = 3;
        return this;
    }

    textureCoordinates4(st) {
        this.model.vTexPos = st;
        this.model.texPosComponents = 4;
        return this;
    }

    normals(n) {
        this.model.vNormal = n;
        this.model.normalComponents = 3;
        return this;
    }

    indices(i) {
        this.model.indices = i;
        return this;
    }

    solidColor(r, g, b, a) {
        this.model.color = [r, g, b, a];
        return this;
    }

    name(n) {
        this.model.name = n;
        return this;
    }

    /**
     * @param t  GL_TRIANGLES,...
     * @return
     */
    drawType(t) {
        this.model.drawType = t;
        return this;
    }

    build() {
        return this.model;
    }
}