package com.darkraha.opengldemoj.gl.modelling;

import com.darkraha.opengldemoj.gl.GlUtils;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Contains the vertex data of model in different arrays.
 */
public class Model {
    public String name;
    public float[] vPos;
    public  float[] vColor;
    public float[] vTexPos;
    public float[] vNormal;
    public float[] vTangent;
    public float[] vBitangent;
    public int[] indices;
    public  float[] color;
    public int posComponets;
    public int colorComponets;
    public int texPosComponets;
    public int normalComponets;
    public int drawType = GL33.GL_TRIANGLES;

    private Model() {

    }



    public GlModel toGlModel() {
        int[] ids = new int[6];

        ids[0] = glGenVertexArrays();
        glBindVertexArray(ids[0]);
        ids[1] = GlUtils.createVBO(vPos, GlUtils.A_LOCATION_COORDS, posComponets);
        ids[2] = vColor != null ? GlUtils.createVBO(vColor, GlUtils.A_LOCATION_COLORS, colorComponets) : 0;
        ids[3] = vNormal != null ? GlUtils.createVBO(vNormal, GlUtils.A_LOCATION_NORMALS, normalComponets) : 0;
        ids[4] = vTexPos != null ? GlUtils.createVBO(vTexPos, GlUtils.A_LOCATION_TEXCOORDS, texPosComponets) : 0;
        ids[5] = indices != null ? GlUtils.createIBO(indices) : 0;

        int count = vPos.length / posComponets;

        if (indices != null) {
            count = indices.length;
        }

        glBindVertexArray(0);
        return new GlModel(ids, drawType, count, name);
    }

    public void calcTangent(){
        Vector3f edge1 = new Vector3f();
        Vector3f edge2 = new Vector3f();


    }




    public static class Builder {
        private final Model model = new Model();

        public Builder coordinates(float[] coords) {
            model.vPos = coords;
            model.posComponets = 3;
            return this;
        }

        public Builder coordinates2d(float[] coords) {
            model.vPos = coords;
            model.posComponets = 2;
            return this;
        }

        public Builder colorsRGB(float[] colorsRGB) {
            model.vColor = colorsRGB;
            model.colorComponets=3;
            return this;
        }

        public Builder textureCoordinates(float[] st) {
            model.vTexPos = st;
            model.texPosComponets =2;
            return this;
        }

        public Builder textureCoordinates3(float[] st) {
            model.vTexPos = st;
            model.texPosComponets =3;
            return this;
        }

        public Builder textureCoordinates4(float[] st) {
            model.vTexPos = st;
            model.texPosComponets =4;
            return this;
        }

        public Builder normals(float[] n) {
            model.vNormal = n;
            model.normalComponets =3;
            return this;
        }

        public Builder indices(int[] i) {
            model.indices = i;
            return this;
        }


        public Builder solidColor(float r, float g, float b, float a) {
            model.color = new float[]{r, g, b, a};
            return this;
        }

        public Builder name(String n) {
            model.name = n;
            return this;
        }


        /**
         * @param t default GL_TRIANGLES
         * @return
         */
        public Builder drawType(int t) {
            model.drawType = t;
            return this;
        }


        public Model build() {
            return model;
        }
    }

}
