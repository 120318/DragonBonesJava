package dragonBones.objects;

import dragonBones.geoms.Point;
import dragonBones.geoms.Transform;

/**
 * Created by jingzhao on 2016/3/5.
 */
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
