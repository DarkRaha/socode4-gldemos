class ColoredQuadRender extends Render {

    matrix
    prog
    idVao
    idVbo

    onSetup(appOGL) {
        super.onSetup(appOGL);
        const gl = appOGL.gl;
        const mat4 = glMatrix.mat4;
        const prog = new ShaderProgramBuilder(gl)
            .colors(true, false).build();
        this.prog = prog;
        this.matrix = glMatrix.mat4.create();

        gl.useProgram(prog.idProgram);

        const vData = [ //
            -1.0, 1.0, 1.0, 0.0, 0.0, //
            1.0, 1.0, 0.0, 1.0, 0.0, //
            -1.0, -1.0, 0.0, 0.0, 1.0, //
            1.0, -1.0, 1.0, 1.0, 1.0 //
        ];

        this.idVao = gl.createVertexArray() //GenVertexArrays();
        gl.bindVertexArray(this.idVao);

        this.idVbo = gl.createBuffer();
        gl.bindBuffer(gl.ARRAY_BUFFER, this.idVbo);
        gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vData), gl.STATIC_DRAW);

        //-----------------------------------------------
        // specify locations of attributes in data
        const stride = 4 * (2 + 3); // 4 - size of float in bytes, 2 - x,y, 3 - r,g,b

        const posLocation = 0; // 0 is location of the position variable in shader
        const numCoords = 2;
        gl.enableVertexAttribArray(posLocation);
        gl.vertexAttribPointer(posLocation, numCoords, gl.FLOAT, false, stride, 0);

        const colorLocation = 1; // 1 is location of the position variable in shader
        const numColorComponents = 3;
        gl.enableVertexAttribArray(colorLocation);
        gl.vertexAttribPointer(colorLocation, numColorComponents, gl.FLOAT, false, stride, 4 * numCoords);
    }

    onDrawFrame(appOGL) {
        super.onDrawFrame(appOGL);
        const mat4 = glMatrix.mat4;
        const matrix = this.matrix;
        const gl = appOGL.gl;
        const prog = this.prog;
        const TO_RAD = Math.PI / 180.0;

        // you can move on setup stage in our case
        mat4.perspective(matrix, 45 * TO_RAD,
            gl.canvas.clientWidth / gl.canvas.clientHeight,
            0.1, 100.0
        );

        mat4.translate(matrix, matrix, [-0.0, 0.0, -6.0]);

        gl.useProgram(prog.idProgram);

        gl.uniformMatrix4fv(
            gl.getUniformLocation(prog.idProgram, 'm'),
            false, matrix);


        gl.bindVertexArray(this.idVao);
        gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);
    }

}