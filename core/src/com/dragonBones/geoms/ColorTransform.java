package com.dragonBones.geoms;

public class ColorTransform {
    public float alphaMultiplier;
    public float redMultiplier;
    public float greenMultiplier;
    public float blueMultiplier;

    public int alphaOffset;
    public int redOffset;
    public int greenOffset;
    public int blueOffset;

    public ColorTransform(){
        alphaMultiplier = 1.f;
        redMultiplier = 1.f;
        greenMultiplier = 1.f;
        blueMultiplier = 1.f;
        alphaOffset = 0;
        redOffset = 0;
        greenOffset = 0;
        blueOffset = 0;
    }
    public ColorTransform(ColorTransform colorTransform){
        alphaMultiplier = colorTransform.alphaMultiplier;
        redMultiplier = colorTransform.redMultiplier;
        greenMultiplier = colorTransform.greenMultiplier;
        blueMultiplier = colorTransform.blueMultiplier;
        alphaOffset = colorTransform.alphaOffset;
        redOffset = colorTransform.redOffset;
        greenOffset = colorTransform.greenOffset;
        blueOffset = colorTransform.blueOffset;
    }
}
