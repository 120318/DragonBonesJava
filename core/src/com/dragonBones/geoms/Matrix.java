package com.dragonBones.geoms;

public class Matrix {
    public float a;
    public float b;
    public float c;
    public float d;
    public float tx;
    public float ty;

    public Matrix(){
        a = 0.f;
        b = 0.f;
        c = 0.f;
        d = 0.f;
        tx = 0.f;
        ty = 0.f;
    }
    public Matrix(Matrix matrix){
        a = matrix.a;
        b = matrix.b;
        c = matrix.c;
        d = matrix.d;
        tx = matrix.tx;
        ty = matrix.ty;
    }

    public void invert(){
        float a0 = a;
        float b0 = b;
        float c0 = c;
        float d0 = d;
        float tx0 = tx;
        float ty0 = ty;
        float determinant = 1 / (a0 * d0 - b0 * c0);
        a = determinant * d0;
        b = -determinant * b0;
        c = -determinant * c0;
        d = determinant * a0;
        tx = determinant * (c0 * ty0 - d0 * tx0);
        ty = determinant * (b0 * tx0 - a0 * ty0);
    }
    public void transformPoint(Point point){
        float x = point.x;
        float y = point.y;
        point.x = a * x + c * y + tx;
        point.y = d * y + b * x + ty;
    }
}
