package com.dragonBones.objects;

import com.dragonBones.geoms.Transform;
import com.dragonBones.DragonBones;
import com.dragonBones.geoms.Point;

/**
 * Created by jingzhao on 2016/2/27.
 */
public class DisplayData {
    public boolean scalingGrid;
    public int scalingGridLeft;
    public int scalingGridRight;
    public int scalingGridTop;
    public int scalingGridBottom;
    public String name;
    public DragonBones.DisplayType type;

    public Point pivot;
    public Transform transform;

    public TextData textData;

    public DisplayData(){
        scalingGrid = false;
        scalingGridLeft = 0;
        scalingGridRight = 0;
        scalingGridTop = 0;
        scalingGridBottom = 0;
        name = null;
        type = DragonBones.DisplayType.DT_IMAGE;
        pivot = new Point();
        transform = new Transform();
        textData = null;
    }
    public DisplayData(DisplayData displayData){
        scalingGrid = displayData.scalingGrid;
        scalingGridLeft = displayData.scalingGridLeft;
        scalingGridRight = displayData.scalingGridRight;
        scalingGridTop = displayData.scalingGridTop;
        scalingGridBottom = displayData.scalingGridBottom;
        name = displayData.name;
        type = displayData.type;
        transform = displayData.transform;
        pivot = displayData.pivot;
        if (displayData.textData != null) {
            textData = new TextData(displayData.textData);
        }
    }
}
