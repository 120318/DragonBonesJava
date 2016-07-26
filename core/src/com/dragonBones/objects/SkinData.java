package com.dragonBones.objects;

import java.util.ArrayList;
import java.util.List;

public class SkinData {
    public List<SlotData> slotDataList;
    public String name;

    public SkinData(){
        slotDataList = new ArrayList<SlotData>();
    }
    public SkinData(SkinData skinData){
        name = skinData.name;
        slotDataList = new ArrayList<SlotData>();
        for (int i = 0, l = slotDataList.size(); i < l; ++i) {
            SlotData slotData = new SlotData(skinData.slotDataList.get(i));
            slotDataList.add(slotData);
        }
    }
    public SlotData getSlotData(String slotName) {
        for (int i = 0, l = slotDataList.size(); i < l; ++i) {
            if (slotDataList.get(i).name.equals(slotName)) {
                return slotDataList.get(i);
            }
        }
        return null;
    }
}
