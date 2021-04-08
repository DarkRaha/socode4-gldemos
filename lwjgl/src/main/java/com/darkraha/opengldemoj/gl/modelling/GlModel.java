package com.darkraha.opengldemoj.gl.modelling;




import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GlModel {
    public static final int IND_VAO = 0;
    public static final int IND_COORDS = 1;
    public static final int IND_COLORS = 2;
    public static final int IND_NORMALS = 3;
    public static final int IND_TEXCOORDS = 4;
    public static final int IND_INDICES = 5;

    public final String name;
    private final int[] ids;
    public final int drawType;
    public final int count;

    public GlModel(int[] ids, int drawType, int count,String name) {
        this.name = name;
        this.ids = ids;
        this.drawType = drawType;
        this.count = count;
    }

    public int getVao() {
        return ids[0];
    }

    public void draw() {
        glBindVertexArray(ids[0]);

        if (ids[IND_INDICES] > 0) {
            glDrawElements(drawType, count, GL_UNSIGNED_INT, 0);
        } else {
            glDrawArrays(drawType, 0, count);
        }
    }

    public void dispose() {
        glDeleteVertexArrays(ids[0]);
        ids[0] = 0;
        glDeleteBuffers(ids);
    }

}
