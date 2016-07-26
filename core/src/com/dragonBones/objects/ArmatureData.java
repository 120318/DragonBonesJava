package com.dragonBones.objects;

import java.util.*;

public class ArmatureData {
    private static Comparator sortBone = new Comparator<AbstractMap.SimpleEntry<Integer, BoneData>>() {
        @Override
        public int compare(AbstractMap.SimpleEntry<Integer, BoneData> a, AbstractMap.SimpleEntry<Integer, BoneData> b) {
            return a.getKey() < b.getKey() ? -1 : 1;
        }
    };

    public String name;
    public List<AreaData> areaDataList;
    public List<BoneData> boneDataList;
    public List<SkinData> skinDataList;
    public List<AnimationData> animationDataList;

    public ArmatureData(){
        areaDataList = new ArrayList<AreaData>();
        boneDataList = new ArrayList<BoneData>();
        skinDataList = new ArrayList<SkinData>();
        animationDataList = new ArrayList<AnimationData>();
    }
    public ArmatureData(ArmatureData armatureData){
        name = armatureData.name;
        areaDataList = new ArrayList<AreaData>();
        for (int i = 0, l = areaDataList.size(); i < l; ++i) {
            switch (armatureData.areaDataList.get(i).areaType) {
                case AT_ELLIPSE:
                    EllipseData ellipseData = new EllipseData((EllipseData)armatureData.areaDataList.get(i));
                    areaDataList.add(ellipseData);
                    break;
                case AT_RECTANGLE:
                    RectangleData rectangleData = new RectangleData((RectangleData)armatureData.areaDataList.get(i));
                    areaDataList.add(rectangleData);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type");
            }
        }
        boneDataList = new ArrayList<BoneData>();
        for (int i = 0, l = boneDataList.size(); i < l; ++i) {
            BoneData boneData = new BoneData(armatureData.boneDataList.get(i));
            boneDataList.add(boneData);
        }

        skinDataList = new ArrayList<SkinData>();
        for (int i = 0, l = skinDataList.size(); i < l; ++i) {
            SkinData skinData = new SkinData(armatureData.skinDataList.get(i));
            skinDataList.add(skinData);
        }
        animationDataList = new ArrayList<AnimationData>();
        for (int i = 0, l = animationDataList.size(); i < l; ++i) {
            AnimationData animationData = new AnimationData(armatureData.animationDataList.get(i));
            animationDataList.add(animationData);
        }
    }

    public AreaData getAreaData(String areaName){
        if(areaDataList.isEmpty()){
            return null;
        }
        if(areaName.isEmpty()){
            return areaDataList.get(0);
        }
        for(int i = 0, l = areaDataList.size(); i < l; ++i){
            if (areaDataList.get(i).name.equals(areaName)) {
                return areaDataList.get(i);
            }
        }
        return null;
    }

    public BoneData getBoneData(String boneName) {
        for(int i = 0, l = boneDataList.size(); i < l; ++i){
            if (boneDataList.get(i).name.equals(boneName)) {
                return boneDataList.get(i);
            }
        }
        return null;
    }

    public SkinData getDefaultSkinData(){
        if (skinDataList.isEmpty()) {
            return null;
        }
        for (int i = 0, l = skinDataList.size(); i < l; ++i) {
            String skinDataName = skinDataList.get(i).name;
            if (skinDataName == null || skinDataName.equals("default")) {
                return skinDataList.get(i);
            }
        }
        return skinDataList.get(0);
    }

    public SkinData getSkinData(String skinName) {
        if (skinDataList.isEmpty()) {
            return null;
        }

        if (skinName == null || skinName.equals("")) {
            return getDefaultSkinData();
        }

        for (int i = 0, l = skinDataList.size(); i < l; ++i) {
            if (skinDataList.get(i).name.equals(skinName)) {
                return skinDataList.get(i);
            }
        }
        return null;
    }
    public AnimationData getAnimationData(String animationName) {
        for(int i = 0, l = animationDataList.size(); i < l; ++i){
            if (animationDataList.get(i).name.equals(animationName)) {
                return animationDataList.get(i);
            }
        }
        return null;
    }
    public void sortBoneDataList(){
        if (boneDataList.isEmpty()) {
            return;
        }
        List<AbstractMap.SimpleEntry<Integer , BoneData>> sortedList
               = new ArrayList<AbstractMap.SimpleEntry<Integer , BoneData>>();
        for (int i = 0, l = boneDataList.size(); i < l; ++i) {
            BoneData boneData = boneDataList.get(i);
            BoneData parentData = boneData;
            int level = 0;

            while (parentData != null) {
                parentData = getBoneData(parentData.parent);
                level++;
            }
            sortedList.add(new AbstractMap.SimpleEntry<Integer , BoneData>(level , boneData));
        }
        Collections.sort(sortedList, sortBone);

        for (int i = 0, l = sortedList.size(); i < l; ++i) {
            boneDataList.set(i, sortedList.get(i).getValue());
        }
    }
}
