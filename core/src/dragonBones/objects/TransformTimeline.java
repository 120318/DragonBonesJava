package dragonBones.objects;

import dragonBones.geoms.Point;
import dragonBones.geoms.Transform;

import java.util.List;

/**
 * Created by jingzhao on 2016/3/2.
 */
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
