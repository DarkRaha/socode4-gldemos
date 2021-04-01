class TexturedCubeRender extends Render {


    matrix
    prog
    idVao
    ids
    idTexture
    rotY = 0
    rotX = 0

    onSetup(appOGL) {
        super.onSetup(appOGL);
        const gl = appOGL.gl;
        const prog = ShaderProgram.createProgram(gl);
        this.prog = prog;
        this.matrix = glMatrix.mat4.create();

        const coordinates = [
            // Front face
            -1.0, -1.0, 1.0, //
            1.0, -1.0, 1.0, //
            1.0, 1.0, 1.0, //
            -1.0, 1.0, 1.0, // Back face
            -1.0, -1.0, -1.0, //
            -1.0, 1.0, -1.0, //
            1.0, 1.0, -1.0, //
            1.0, -1.0, -1.0, // Top face
            -1.0, 1.0, -1.0, //
            -1.0, 1.0, 1.0, //
            1.0, 1.0, 1.0, //
            1.0, 1.0, -1.0, // Bottom face
            -1.0, -1.0, -1.0, //
            1.0, -1.0, -1.0, //
            1.0, -1.0, 1.0, //
            -1.0, -1.0, 1.0, // Right face
            1.0, -1.0, -1.0, //
            1.0, 1.0, -1.0, //
            1.0, 1.0, 1.0, //
            1.0, -1.0, 1.0, // Left face
            -1.0, -1.0, -1.0, //
            -1.0, -1.0, 1.0, //
            -1.0, 1.0, 1.0, //
            -1.0, 1.0, -1.0 //
        ];


        const indices = [
            0, 1, 2, /*        */ 0, 2, 3, // front
            4, 5, 6, /*        */ 4, 6, 7, // back
            8, 9, 10, /*       */ 8, 10, 11, // top
            12, 13, 14, /*     */ 12, 14, 15, // bottom
            16, 17, 18, /*     */ 16, 18, 19, // right
            20, 21, 22, /*     */ 20, 22, 23
        ];

        const textCoords = [
            // Front
            0.0, 0.0, //
            1.0, 0.0, //
            1.0, 1.0, //
            0.0, 1.0, //
            // Back
            0.0, 0.0, //
            1.0, 0.0, //
            1.0, 1.0, //
            0.0, 1.0, //
            // Right
            0.0, 0.0, //
            1.0, 0.0, //
            1.0, 1.0, //
            0.0, 1.0, //
            // Left
            0.0, 0.0, //
            1.0, 0.0, //
            1.0, 1.0, //
            0.0, 1.0, //
            // Top
            0.0, 0.0, //
            1.0, 0.0, //
            1.0, 1.0, //
            0.0, 1.0, //
            // Bottom
            0.0, 0.0, //
            1.0, 0.0, //
            1.0, 1.0, //
            0.0, 1.0
        ];

        gl.useProgram(prog.idProgram);
        this.idVao = GlUtils.createVAO(gl);
        GlUtils.prepareVertexData(gl, coordinates, null, textCoords, indices);

        this.idTexture = GlUtils.loadTextureDefault(gl, "img/textures/235.jpg",
            (gl, idTexture, image) => {
                gl.generateMipmap(gl.TEXTURE_2D);
            });
        //      this.idTexture = GlUtils.loadTextureDefault(gl, "/images/textures/235.jpg");

    }

    onDrawFrame(appOGL) {
        super.onDrawFrame(appOGL);
        const mat4 = glMatrix.mat4;
        const matrix = this.matrix;
        const gl = appOGL.gl;
        const prog = this.prog;
        const TO_RAD = Math.PI / 180.0;


        var rotY = this.rotY;
        var rotX = this.rotX;

        rotY += 1.5 * TO_RAD
        if (rotY > 2 * Math.PI) {
            rotY = rotY - 2 * Math.PI
        }
        rotX += 1 * TO_RAD
        if (rotX > 2 * Math.PI) {
            rotX = rotX - 2 * Math.PI
        }

        this.rotX = rotX;
        this.rotY = rotY;


        mat4.perspective(matrix, 45 * TO_RAD,
            gl.canvas.clientWidth / gl.canvas.clientHeight,
            0.1, 100.0
        );

        mat4.translate(matrix, matrix, [-0.0, 0.0, -6.0]);
        mat4.rotate(matrix, matrix, rotX, [1, 0, 0]);
        mat4.rotate(matrix, matrix, rotY, [0, 1, 0]);

        gl.useProgram(prog.idProgram);
        gl.uniform1i(gl.getUniformLocation(prog.idProgram, "withTexture"), true);
        gl.uniform4f(gl.getUniformLocation(prog.idProgram, "uColor"), -1, -1, -1, -1);

        gl.uniformMatrix4fv(
            gl.getUniformLocation(prog.idProgram, 'matrix'),
            false, matrix);


        gl.activeTexture(gl.TEXTURE0);
        gl.bindTexture(gl.TEXTURE_2D, this.idTexture);
        const sampleLocation = gl.getUniformLocation(prog.idProgram, 'texSampler');
        gl.uniform1i(sampleLocation, 0);


        gl.bindVertexArray(this.idVao);
        gl.enable(gl.CULL_FACE);
        gl.drawElements(gl.TRIANGLES, 36, gl.UNSIGNED_BYTE, 0);
    }

}