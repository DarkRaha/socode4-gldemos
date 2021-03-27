class Render{
    
    onSetup(appOGL){
        const gl = appOGL.gl;
        gl.clearColor(0.0, 0.0, 0.0, 1.0);  // Clear to black, fully opaque
        gl.clearDepth(1.0);                 // Clear everything
        gl.enable(gl.DEPTH_TEST);           // Enable depth testing
        gl.depthFunc(gl.LEQUAL);            // Near things obscure far things
    }
    
    onDrawFrame(appOGL){
       const gl = appOGL.gl;
       gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT); 
    }
    
    onDispose(appOGL){
        
    }
}    
