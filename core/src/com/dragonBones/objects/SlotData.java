package com.dragonBones.objects;

import com.dragonBones.DragonBones;

import java.util.ArrayList;
import java.util.List;

public class SlotData {
    public List<DisplayData> displayDataList;
    public String parent;
    public String name;
    public float zOrder;
    public DragonBones.BlendMode blendMode;

    public SlotData(){
        zOrder = 0.f;
        name = null;
        parent = null;
        blendMode = DragonBones.BlendMode.BM_NORMAL;
        displayDataList = new ArrayList<DisplayData>();
    }
    public SlotData(SlotData slotData){
        zOrder = slotData.zOrder;
        name = slotData.name;
        parent = slotData.parent;
        blendMode = slotData.blendMode;
        displayDataList = new ArrayList<DisplayData>();
        for (int i = 0, l = displayDataList.size(); i < l; ++i) {
            DisplayData displayData = new DisplayData(slotData.displayDataList.get(i));
            displayDataList.add(displayData);
        }
    }

    public DisplayData getDisplayData(String displayName){
        for (int i = 0, l = displayDataList.size(); i < l; ++i) {
            if (displayDataList.get(i).name.equals(displayName)) {
                return displayDataList.get(i);
            }
        }
        return null;
    }
}
