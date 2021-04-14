class ColoredCubeRender extends Render {


    matrix
    prog
    idVao
    idVbo
    idIbo
    rotY = 1.5 * Math.PI / 180.0;
    rotX = Math.PI / 180.0;



    onSetup(appOGL) {
        super.onSetup(appOGL);
        const gl = appOGL.gl;
        const mat4 = glMatrix.mat4;

        const prog = new ShaderProgramBuilder(gl)
            .colors(true, false)
            .build();
        this.prog = prog;

        this.matrix = mat4.create();
        mat4.perspective(this.matrix, 45 * Math.PI / 180,
            gl.canvas.clientWidth / gl.canvas.clientHeight,
            0.1, 100.0);

        mat4.translate(this.matrix, this.matrix, [-0.0, 0.0, -6.0]);

        gl.useProgram(prog.idProgram);

        const vData = [
            // front
            -1.0, -1.0, 1, /* */ 1.0, 0.0, 0.0, //
            1.0, -1.0, 1.0, /* */ 0.0, 1.0, 0.0, //
            1.0, 1.0, 1.0, /* */ 0.0, 0.0, 1.0, //
            -1.0, 1.0, 1.0, /* */ 1.0, 1.0, 1.0, //
            // back
            -1.0, -1.0, -1.0, /* */ 1.0, 0.0, 0.0, //
            1.0, -1.0, -1.0, /* */ 0.0, 1.0, 0.0, //
            1.0, 1.0, -1.0, /* */ 0.0, 0.0, 1.0, //
            -1.0, 1.0, -1.0, /* */ 1.0, 1.0, 1.0 //
        ];


        const indices = [
            // front
            0, 1, 2,
            2, 3, 0,
            // right
            1, 5, 6,
            6, 2, 1,
            // back
            7, 6, 5,
            5, 4, 7,
            // left
            4, 0, 3,
            3, 7, 4,
            // bottom
            4, 5, 1,
            1, 0, 4,
            // top
            3, 2, 6,
            6, 7, 3
        ];

        this.idVao = gl.createVertexArray(); //GenVertexArrays();
        gl.bindVertexArray(this.idVao);

        //-----------------------------------------------
        // create vbo and upload data into it 
        this.idVbo = gl.createBuffer();
        gl.bindBuffer(gl.ARRAY_BUFFER, this.idVbo);
        gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vData), gl.STATIC_DRAW);

        //-----------------------------------------------
        // specify locations of attributes in data
        // specify locations of attributes in data
        const stride = 4 * (3 + 3); // 4 - size of float in bytes, 3 - x,y,z  3 - r,g,b

        const posLocation = 0; // 0 is location of the position variable in shader
        const numCoords = 3;
        gl.enableVertexAttribArray(posLocation);
        gl.vertexAttribPointer(posLocation, numCoords, gl.FLOAT, false, stride, 0);

        const colorLocation = 1; // 1 is location of the position variable in shader
        const numColorComponents = 3;
        gl.enableVertexAttribArray(colorLocation);
        gl.vertexAttribPointer(colorLocation, numColorComponents, gl.FLOAT, false, stride, 4 * numCoords);


        this.idIbo = gl.createBuffer();
        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.idIbo);
        //gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint32Array(indices), gl.STATIC_DRAW);

        gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint8Array(indices), gl.STATIC_DRAW);

        gl.clearColor(0.0, 0.0, 0.0, 1.0); // Clear to black, fully opaque
        gl.clearDepth(1.0); // Clear everything
        gl.enable(gl.DEPTH_TEST); // Enable depth testing
        gl.depthFunc(gl.LEQUAL); // Near things obscure far things
    }

    onDrawFrame(appOGL) {

        const mat4 = glMatrix.mat4;
        const matrix = this.matrix;
        const gl = appOGL.gl;
        const prog = this.prog;

        mat4.rotate(matrix, matrix, this.rotX, [1, 0, 0]);
        mat4.rotate(matrix, matrix, this.rotY, [0, 1, 0]);

        gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
        gl.useProgram(prog.idProgram);

        gl.uniformMatrix4fv(
            gl.getUniformLocation(prog.idProgram, 'm'),
            false, matrix);

        gl.bindVertexArray(this.idVao);
        //  gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.idIbo);
        // gl.drawElements(gl.TRIANGLES, 36, gl.UNSIGNED_BYTE, 0);
        gl.drawElements(gl.TRIANGLES, 36, gl.UNSIGNED_BYTE, 0);
    }

}