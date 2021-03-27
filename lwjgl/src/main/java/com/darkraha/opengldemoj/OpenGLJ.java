package com.darkraha.opengldemoj;

import com.darkraha.opengldemoj.gl.AppOGL;
import com.darkraha.opengldemoj.renders.ColoredCubeRender;
import com.darkraha.opengldemoj.renders.ColoredQuadRender;
import com.darkraha.opengldemoj.renders.QuadRender;
import com.darkraha.opengldemoj.renders.TexturedQuadRender;


public class OpenGLJ {

    public static void main(String[] args) {

       // AppOGL.isV120 =true; new AppOGL(new QuadRender(), 300,300);
        //  new AppOGL(new ColoredQuadRender(), 300,300);
      //  new AppOGL(new ColoredCubeRender(), 300,300);
        new AppOGL(new TexturedQuadRender(), 300,300);


    }

}