package com.dragonBones.geoms;

import com.dragonBones.DragonBones;

public class Transform implements Cloneable{
    public float x;
    public float y;
    public float skewX;
    public float skewY;
    public float scaleX;
    public float scaleY;

    public float getRotation(){
        return skewX;
    }

    public void setRotation(float value){
        skewX = skewY = value;
    }

    public Transform(){
        x = 0.f;
        y = 0.f;
        skewX = 0.f;
        skewY = 0.f;
        scaleX = 1.f;
        scaleY = 1.f;
    }
    public Transform(Transform transform){
        x = transform.x;
        y = transform.y;
        skewX = transform.skewX;
        skewY = transform.skewY;
        scaleX = transform.scaleX;
        scaleY = transform.scaleY;
    }

    public void toMatrix(Matrix matrix, boolean keepScale){
        if (keepScale)
        {
            matrix.a = scaleX * (float)Math.cos(skewY);
            matrix.b = scaleX * (float)Math.sin(skewY);
            matrix.c = -scaleY * (float)Math.sin(skewX);
            matrix.d = scaleY * (float)Math.cos(skewX);
        }
        else
        {
            matrix.a = (float)Math.cos(skewY);
            matrix.b = (float)Math.sin(skewY);
            matrix.c = -(float)Math.sin(skewX);
            matrix.d = (float)Math.cos(skewX);
        }

        matrix.tx = x;
        matrix.ty = y;
    }

    public void transformWith(Transform parent){
        Matrix matrix = new Matrix();
        parent.toMatrix(matrix, true);
        matrix.invert();
        float x0 = x;
        float y0 = y;
        x = matrix.a * x0 + matrix.c * y0 + matrix.tx;
        y = matrix.d * y0 + matrix.b * x0 + matrix.ty;
        skewX = DragonBones.formatRadian(skewX - parent.skewX);
        skewY = DragonBones.formatRadian(skewY - parent.skewY);
    }
}
