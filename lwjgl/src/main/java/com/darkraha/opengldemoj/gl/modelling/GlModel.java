package com.darkraha.opengldemoj.gl.modelling;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GlModel {
    public final String name;
    public final int idVao;
    private final int[] ids;
    public final int idIbo;
    public final int drawType;
    public final int count;

    public GlModel(int idVao, int[] ids, int idIbo, int drawType, int count, String name) {
        this.idVao = idVao;
        this.idIbo = idIbo;
        this.name = name;
        this.ids = ids;
        this.drawType = drawType;
        this.count = count;
    }

    public void draw() {
        glBindVertexArray(idVao);

        if (idIbo > 0) {
            glDrawElements(drawType, count, GL_UNSIGNED_INT, 0);
        } else {
            glDrawArrays(drawType, 0, count);
        }
    }

    public void dispose() {
        glDeleteVertexArrays(idVao);
        ids[0] = 0;
        glDeleteBuffers(ids);
        glDeleteBuffers(idIbo);
    }
}
