package com.dragonBones.objects;

import com.dragonBones.geoms.Point;
import com.dragonBones.geoms.Transform;

public class TransformTimeline extends Timeline{
    public String name;
    public Transform originTransform;
    public Point originPivot;
    public float offset;
    public boolean transformed;

    public TransformTimeline(){
        offset = 0.f;
        transformed = false;
        originTransform = new Transform();
    }
    public TransformTimeline(TransformTimeline transformTimeline){
        super(transformTimeline);
        transformed = transformTimeline.transformed;
        offset = transformTimeline.offset;
        name = transformTimeline.name;
        originTransform = transformTimeline.originTransform;
        originPivot = transformTimeline.originPivot;
    }
}
