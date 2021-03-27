class QuadRender extends Render {
    
    matrix
    prog
    idVbo
    
    onSetup(appOGL){
        super.onSetup(appOGL);
        const gl = appOGL.gl;
        const mat4 = glMatrix.mat4;
        const prog = ShaderProgram.createProgram(gl);
        const matrix = glMatrix.mat4.create();

        gl.useProgram(prog.idProgram);
        gl.uniform1i(gl.getUniformLocation(prog.idProgram, "withTexture"),0);
        gl.uniform4f(gl.getUniformLocation(prog.idProgram, "uColor"), 1,1,1,1);
        
        mat4.perspective(matrix,
            45 * Math.PI / 180, 
            gl.canvas.clientWidth / gl.canvas.clientHeight, 
            0.1,  100.0 
        );
        
        mat4.translate(matrix, matrix, 
                 [-0.0, 0.0, -6.0]); 
        
        gl.uniformMatrix4fv(
             gl.getUniformLocation(prog.idProgram, 'matrix'), 
             false, 
             matrix);
        
        const positions = [
            -1.0,  1.0,
            1.0,  1.0,
            -1.0, -1.0,
            1.0, -1.0,];
/*
        const idVbo = gl.createBuffer();
        gl.bindBuffer(gl.ARRAY_BUFFER, idVbo);
        gl.bufferData(gl.ARRAY_BUFFER,
            new Float32Array(positions),
            gl.STATIC_DRAW);
  */
        
        const idVbo = GlUtils.createVBO(gl, positions);
        GlUtils.bindAttributes(gl, idVbo, 2, 0, false);
        
        this.idVbo = idVbo;  
        this.matrix = matrix;
        this.prog = prog;
    }
    
    onDrawFrame(appOGL){
        super.onDrawFrame(appOGL);
        const mat4 = glMatrix.mat4;
        const matrix = this.matrix;
        const gl = appOGL.gl;
        const idVbo = this.idVbo;
        const prog = this.prog;
        
      
       /* 
        const inVertexLocation = gl.getAttribLocation(prog.idProgram, 'vCoord');
        gl.vertexAttribPointer(
            inVertexLocation, 2, 
            gl.FLOAT, false, 0, 0); 
        
        gl.enableVertexAttribArray(inVertexLocation);
        */
        gl.useProgram(prog.idProgram);
        gl.bindBuffer(gl.ARRAY_BUFFER, idVbo);
    
        gl.drawArrays(gl.TRIANGLE_STRIP, 
                  0, // offset 
                  4);
    }   
}