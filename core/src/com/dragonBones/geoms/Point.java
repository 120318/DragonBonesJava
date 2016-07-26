package com.dragonBones.geoms;

public class Point implements Cloneable{
    public float x;
    public float y;

    public Point(){
        x = 0.f;
        y = 0.f;
    }
    public Point(Point point){
        x = point.x;
        y = point.y;
    }
}
