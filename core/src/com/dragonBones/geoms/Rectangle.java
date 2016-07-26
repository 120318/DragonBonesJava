package com.dragonBones.geoms;

public class Rectangle {
    public float x;
    public float y;
    public float height;
    public float width;

    public Rectangle(){
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }
    public Rectangle(float x, float y, float w, float h){
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }
    public Rectangle(Rectangle rectangle){
        x = rectangle.x;
        y = rectangle.y;
        width = rectangle.width;
        height = rectangle.height;
    }
}
