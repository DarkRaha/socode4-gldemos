package com.darkraha.opengldemoj.gl.meshes;


import com.darkraha.opengldemoj.gl.GlDrawable;

import static org.lwjgl.opengl.GL33.*;

public class MeshI implements GlDrawable {
    protected int idVao;
    protected int idVbo;
    protected int vCount;
    protected int drawMode;
    protected int idIbo;
    protected int indCount;

    public MeshI(int idVao, int idVbo, int idIbo, int vCount, int indCount, int drawMode) {
        this.idVao = idVao;
        this.idVbo = idVbo;
        this.vCount = vCount;
        this.drawMode = drawMode;
        this.idIbo = idIbo;
        this.indCount = indCount;
    }

    public final int getIdVao(){
        return idVao;
    }

    public final int getIdVbo(){
        return idVbo;
    }

    public final int getVertexCount(){
        return vCount;
    }

    public final int getIdIbo() {
        return idIbo;
    }

    public final int getIndexCount() {
        return indCount;
    }

    @Override
    public void draw() {
        glBindVertexArray(idVao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idIbo);
        glDrawElements(drawMode, indCount, GL_UNSIGNED_BYTE, 0);
    }

    @Override
    public void dispose() {
        glDeleteVertexArrays(idVao);
        glDeleteBuffers(idVbo);
        glDeleteBuffers(idIbo);
        idVao = 0;
        idVbo = 0;
        idIbo = 0;
    }
}
