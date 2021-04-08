package com.darkraha.opengldemoj;

import com.darkraha.opengldemoj.gl.AppOGL;
import com.darkraha.opengldemoj.renders.*;


public class OpenGLJ {

    /**
     * MacOS users should start their application passing "-XstartOnFirstThread" as a VM option.
     */
    public static void main(String[] args) {

        // AppOGL.isV120 =true; new AppOGL(new QuadRender(), 300,300);
         // new AppOGL(new QuadRender(), 300,300);
         // new AppOGL(new ColoredQuadRender(), 300,300);
         // new AppOGL(new ColoredCubeRender(), 300,300);
         // new AppOGL(new TexturedQuadRender(), 300,300);
          new AppOGL(new TexturedCubeRender(), 300,300);
         // new AppOGL(new LightTexturedCubeRender(), 300,300);
          // new AppOGL(new LightTexturedSphereRender(), 300,300);

    }

}