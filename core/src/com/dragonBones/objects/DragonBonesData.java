package com.dragonBones.objects;

import java.util.ArrayList;
import java.util.List;


public class DragonBonesData {
    public boolean autoSearch;
    public String name;
    public List<ArmatureData> armatureDataList;

    public DragonBonesData(){
        autoSearch = false;
        armatureDataList = new ArrayList<ArmatureData>();
    }

    public DragonBonesData(DragonBonesData dragonBonesData){
        autoSearch = dragonBonesData.autoSearch;
        name = dragonBonesData.name;
        armatureDataList = new ArrayList<ArmatureData>();
        for (int i = 0, l = armatureDataList.size(); i < l; ++i) {
            ArmatureData armatureData = new ArmatureData(dragonBonesData.armatureDataList.get(i));
            armatureDataList.add(armatureData);
        }
    }

    public ArmatureData getArmatureData(String armatureName) {
        for(int i = 0, l = armatureDataList.size(); i < l; ++i){
            if(armatureDataList.get(i).name.equals(armatureName)){
                return armatureDataList.get(i);
            }
        }
        return null;
    }
}
