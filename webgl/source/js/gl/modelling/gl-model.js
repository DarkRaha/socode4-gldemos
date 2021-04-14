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
        // val edge1 = Vector3f()
        // val edge2 = Vector3f()
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