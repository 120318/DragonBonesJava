package com.dragonBones.objects;

import com.dragonBones.geoms.Transform;
import com.dragonBones.DragonBones;
import com.dragonBones.geoms.ColorTransform;
import com.dragonBones.geoms.Point;

public class TransformFrame extends Frame{
    public boolean visible;
    public boolean tweenScale;
    public int tweenRotate;
    public int displayIndex;
    public float zOrder;
    public float tweenEasing;
    public Transform global;
    public Transform transform;
    public Point scaleOffset;
    public Point pivot;
    public ColorTransform color;

    public TransformFrame(){
        visible = true;
        tweenScale = true;
        tweenRotate = 0;
        displayIndex = 0;
        zOrder = 0.f;
        tweenEasing = DragonBones.NO_TWEEN_EASING;
        frameType = FrameType.FT_TRANSFORM_FRAME;
        color = null;
        global = new Transform();
        transform = new Transform();
        pivot = new Point();
        scaleOffset = new Point();
    }
    public TransformFrame(TransformFrame transformFrame){
        super(transformFrame);
        visible = transformFrame.visible;
        tweenScale = transformFrame.tweenScale;
        tweenRotate = transformFrame.tweenRotate;
        displayIndex = transformFrame.displayIndex;
        zOrder = transformFrame.zOrder;
        tweenEasing = transformFrame.tweenEasing;
        global = transformFrame.global;
        transform = transformFrame.transform;
        pivot = transformFrame.pivot;
        scaleOffset = transformFrame.scaleOffset;

        if (transformFrame.color != null) {
            color = new ColorTransform(transformFrame.color);
        }
        else{
            color = null;
        }
    }
}
