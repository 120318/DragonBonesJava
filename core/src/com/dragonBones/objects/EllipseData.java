package com.dragonBones.objects;

import com.dragonBones.geoms.Point;
import com.dragonBones.geoms.Transform;

public class EllipseData extends AreaData implements Cloneable{
    public float width;
    public float height;
    public Transform transform;
    public Point pivot;

    public EllipseData(){
        areaType = AreaType.AT_ELLIPSE;
        width = 0.f;
        height = 0.f;
    }
    public EllipseData(EllipseData ellipseData){
        super(ellipseData);
        width = ellipseData.width;
        height = ellipseData.height;
        transform = ellipseData.transform;
        pivot = ellipseData.pivot;
    }
}
