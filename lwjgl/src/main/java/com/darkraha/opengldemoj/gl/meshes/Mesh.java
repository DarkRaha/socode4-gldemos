package com.darkraha.opengldemoj.gl.meshes;

import com.darkraha.opengldemoj.gl.GlDrawable;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

class Mesh implements GlDrawable {
    protected int idVao;
    protected int idVbo;
    protected int vCount;
    protected int drawMode;


    public final int getIdVao(){
        return idVao;
    }

    public final int getIdVbo(){
        return idVbo;
    }

    public final int getVertexCount(){
        return vCount;
    }

    public Mesh(int idVao, int idVbo, int vCount, int drawMode){
        this.idVao = idVao;
        this.idVbo = idVbo;
        this.vCount = vCount;
        this.drawMode = drawMode;
    }

    @Override
    public void draw() {
        glBindVertexArray(idVao);
        glDrawArrays(drawMode, 0, vCount); // vertex count
    }


    @Override
    public void dispose() {
        glDeleteVertexArrays(idVao);
        glDeleteBuffers(idVbo);
        idVao=0;
        idVbo=0;

    }
}
