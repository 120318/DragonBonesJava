package com.dragonBones.objects;

import com.dragonBones.geoms.Transform;

import java.util.ArrayList;
import java.util.List;

public class BoneData implements Cloneable{
    public boolean inheritRotation;
    public boolean inheritScale;
    public float length;
    public String name;
    public String parent;
    public Transform global = new Transform();
    public Transform transform = null;
    public List<AreaData> areaDataList = new ArrayList<AreaData>();

    public BoneData(){
        inheritScale = false;
        inheritRotation = false;
        length = 0.f;
    }
    public BoneData(BoneData boneData){
        inheritScale = boneData.inheritScale;
        inheritRotation = boneData.inheritRotation;
        length = boneData.length;
        name = boneData.name;
        parent = boneData.parent;
        global = boneData.global;
        transform = boneData.transform;
        areaDataList = new ArrayList<AreaData>();
        for (int i = 0, l = boneData.areaDataList.size(); i < l; ++i) {
            switch (boneData.areaDataList.get(i).areaType) {
                case AT_ELLIPSE:
                    EllipseData ellipseData = new EllipseData((EllipseData)boneData.areaDataList.get(i));
                    areaDataList.add(ellipseData);
                    break;

                case AT_RECTANGLE:
                    RectangleData rectangleData = new RectangleData((RectangleData)boneData.areaDataList.get(i));
                    areaDataList.add(rectangleData);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported type");
            }
        }
    }

    public AreaData getAreaData(String areaName) {
        if (areaDataList.isEmpty()) {
            return null;
        }
        if (areaName.isEmpty()) {
            return areaDataList.get(0);
        }
        for(int i = 0, l = areaDataList.size(); i < l; ++i){
            if(areaDataList.get(i).name.equals(areaName)){
                return areaDataList.get(i);
            }
        }
        return null;
    }
}
