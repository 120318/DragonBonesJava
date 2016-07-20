package dragonBones.objects;

import dragonBones.geoms.Point;
import dragonBones.geoms.Rectangle;
import dragonBones.geoms.Transform;

/**
 * Created by jingzhao on 2016/3/5.
 */
public class RectangleData extends AreaData {
    public float width;
    public float height;
    public Transform transform;
    public Point pivot;

    public RectangleData() {
        areaType = AreaType.AT_RECTANGLE;
        width = 0.f;
        height = 0.f;
    }
    public RectangleData(RectangleData rectangleData){
        super(rectangleData);
        width = rectangleData.width;
        height = rectangleData.height;
        transform = rectangleData.transform;
        pivot = rectangleData.pivot;
    }

}
