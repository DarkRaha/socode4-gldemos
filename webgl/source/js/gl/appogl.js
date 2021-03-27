class AppOGL{
    canvas
    gl
    render
    idRAF
    
    constructor(idCanvas){
        this.canvas = document.getElementById(idCanvas);
        var gl = this.canvas.getContext("webgl2"); 
        this.gl = gl;
        gl.viewport(0, 0, gl.drawingBufferWidth, 
                    gl.drawingBufferHeight); 
        this.render = new Render();
        this.render.onSetup(this);
        
        window.main = () => {
            this.idRAF = window.requestAnimationFrame( main );
            this.render.onDrawFrame(this)
        };

        main(); 
    }
    
    start(){
        main();
    }
    
    stop(){
        window.cancelAnimationFrame( this.idRAF );
        this.idRAF=0;
    }
    
    setRender(r){
        stop();
        const old  = this.render;
        if (r == null) {
            r = new Render();
        }
        
        r.onSetup(this);
        this.render = r;
        old.onDispose(this);
    }
    
}