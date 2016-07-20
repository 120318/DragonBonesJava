package dragonBones.factories;

import dragonBones.DragonBones;
import dragonBones.core.Armature;
import dragonBones.core.Bone;
import dragonBones.core.Slot;
import dragonBones.objects.*;
import dragonBones.textures.DBTextureAtlas;
import dragonBones.textures.DBTextureData;

import java.util.*;

/**
 * Created by jingzhao on 2016/2/27.
 */
public abstract class BaseFactory {
    protected enum AutoSearchType {AST_ALL, AST_AUTO, AST_NONE};

    public boolean autoSearchDragonBonesData;
    public boolean autoSearchTexture;

    protected String currentDragonBonesDataName;
    protected String currentTextureAtlasName;
    protected TreeMap<String, DragonBonesData> dragonBonesDataMap = new TreeMap<String, DragonBonesData>();
    protected TreeMap<String, DBTextureAtlas> textureAtlasMap = new TreeMap<String, DBTextureAtlas>();

    public BaseFactory(){
        autoSearchDragonBonesData = false;
        autoSearchTexture = false;
    }
    public TreeMap<String, DragonBonesData> getDragonBonesDataMap(){
        return dragonBonesDataMap;
    }
    public TreeMap<String, DBTextureAtlas> getTextureAtlasMap(){
        return textureAtlasMap;
    }
    public void clear(){
        dragonBonesDataMap.clear();
        textureAtlasMap.clear();
    }
    
    public DragonBonesData getDragonBonesData(String name){
        if(name == null){
            return null;
        }
        if (dragonBonesDataMap.containsKey(name)) {
            return dragonBonesDataMap.get(name);
        }
        else {
            return null;
        }
    }
    public void addDragonBonesData(DragonBonesData data, String name){
        if(data == null){
            throw new NullPointerException("Invalid data");
        }
        String key = name == null ? data.name : name;
        if(key == null){
            throw new NullPointerException("Name is empty");
        }
        if(!dragonBonesDataMap.containsKey(key)){
            dragonBonesDataMap.put(key, data);
        }
    }
    public void removeDragonBonesData(String name){
        if (dragonBonesDataMap.containsKey(name)) {
            dragonBonesDataMap.remove(name);
        }
    }

    public DBTextureAtlas getTextureAtlas(String name){
        if(name == null){
            return null;
        }
        if (textureAtlasMap.containsKey(name)) {
            return textureAtlasMap.get(name);
        }
        return null;
    }
    public void addTextureAtlas(DBTextureAtlas textureAtlas, String name){
        if(textureAtlas == null){
            throw new NullPointerException("Invalid textureAtlas");
        }
        String key = name == null ? textureAtlas.textureAtlasData.name : name;
        if(key == null){
            throw new NullPointerException("Name is empty");
        }
        if(!textureAtlasMap.containsKey(key)) {
            textureAtlasMap.put(key, textureAtlas);
        }
    }
    public void removeTextureAtlas(String name){
        if (textureAtlasMap.containsKey(name)) {
            textureAtlasMap.remove(name);
        }
    }

    public Armature buildArmature(String armatureName){
        return buildArmature(armatureName, "", armatureName, "", "");
    }
    public Armature buildArmature(String armatureName, String dragonBonesName){
        return buildArmature(armatureName, "", armatureName, dragonBonesName, dragonBonesName);
    }
    public Armature buildArmature(String armatureName, String skinName, String animationName,
                                                    String dragonBonesName, String textureAtlasName){
        DragonBonesData dragonBonesData = null;
        ArmatureData armatureData = null;
        ArmatureData animationArmatureData = null;
        SkinData skinData = null;
        SkinData skinDataCopy = null;

        if (!dragonBonesName.isEmpty()){
            if (dragonBonesDataMap.containsKey(dragonBonesName)) {
                dragonBonesData = dragonBonesDataMap.get(dragonBonesName);
                armatureData = dragonBonesData.getArmatureData(armatureName);
                currentDragonBonesDataName = dragonBonesName;
                currentTextureAtlasName = textureAtlasName == null ? currentDragonBonesDataName : textureAtlasName;
            }
        }

        if (armatureData == null) {
            AutoSearchType searchType = (dragonBonesName.isEmpty() ? AutoSearchType.AST_ALL :
                    (autoSearchDragonBonesData ? AutoSearchType.AST_AUTO : AutoSearchType.AST_NONE));
            if (searchType != AutoSearchType.AST_NONE) {
                Iterator<String> iterator = dragonBonesDataMap.keySet().iterator();
                while(iterator.hasNext()){
                    String name = iterator.next();
                    dragonBonesData = dragonBonesDataMap.get(name);
                    if (searchType == AutoSearchType.AST_ALL || dragonBonesData.autoSearch) {
                        armatureData = dragonBonesData.getArmatureData(armatureName);
                        if (armatureData != null) {
                            currentDragonBonesDataName = name;
                            currentTextureAtlasName = currentDragonBonesDataName;
                            break;
                        }
                    }
                }
            }
        }

        if (armatureData == null) {
            return null;
        }

        if (animationName != null && animationName != armatureName) {
            animationArmatureData = dragonBonesData.getArmatureData(animationName);
            if (animationArmatureData == null) {
                for(String name : dragonBonesDataMap.keySet()){
                    dragonBonesData = dragonBonesDataMap.get(name);
                    animationArmatureData = dragonBonesData.getArmatureData(animationName);
                    if(animationArmatureData != null){
                        break;
                    }
                }
            }
            if (animationArmatureData != null) {
                skinDataCopy = animationArmatureData.getSkinData("");
            }
        }

        skinData = armatureData.getSkinData(skinName);
        Armature armature = generateArmature(armatureData);
        armature.name = armatureName;

        if (animationArmatureData != null) {
            armature.getAnimation().setAnimationDataList(animationArmatureData.animationDataList);
        }
        else {
            armature.getAnimation().setAnimationDataList(armatureData.animationDataList);
        }

        //
        buildBones(armature, armatureData);

        //
        if (skinData != null) {
            buildSlots(armature, armatureData, skinData, skinDataCopy);
        }

        // update armature pose
        armature.getAnimation().play();
        armature.advanceTime(0);
        armature.getAnimation().stop();
        return armature;
    }
    public Object getTextureDisplay(String textureName, String textureAtlasName, DisplayData displayData){
        DBTextureAtlas textureAtlas = null;
        DBTextureData textureData = null;
        if (textureAtlasName != null) {
            if (textureAtlasMap.containsKey(textureAtlasName))
            {
                textureAtlas = textureAtlasMap.get(textureAtlasName);
                textureData = textureAtlas.textureAtlasData.getTextureData(textureName);
            }
        }

        if (textureData == null) {
            AutoSearchType searchType = (textureAtlasName == null ? AutoSearchType.AST_ALL :
                    (autoSearchTexture ? AutoSearchType.AST_AUTO : AutoSearchType.AST_NONE));

            if (searchType != AutoSearchType.AST_NONE) {
                for (String name : textureAtlasMap.keySet()) {
                    textureAtlas = textureAtlasMap.get(name);
                    if (searchType == AutoSearchType.AST_ALL || textureAtlas.textureAtlasData.autoSearch) {
                        textureData = textureAtlas.textureAtlasData.getTextureData(textureName);
                        if (textureData != null) {
                            break;
                        }
                    }
                }
            }
        }
        if (textureData == null){
            return null;
        }

        if (displayData == null) {
            if (dragonBonesDataMap.containsKey(textureAtlas.textureAtlasData.name)){
                DragonBonesData dragonBonesData = dragonBonesDataMap.get(textureAtlas.textureAtlasData.name);
                for (int i = 0, size1 = dragonBonesData.armatureDataList.size(); i < size1; ++i) {
                    for (int j = 0, size2 = dragonBonesData.armatureDataList.get(i).skinDataList.size(); j < size2; ++j) {
                        for (int k = 0, size3 = dragonBonesData.armatureDataList.get(i).skinDataList.get(j).slotDataList.size(); k < size3; ++k) {
                            for (int m = 0, size4 = dragonBonesData.armatureDataList.get(i).skinDataList.get(j).slotDataList.get(k).displayDataList.size(); m < size4; ++m) {
                                displayData = dragonBonesData.armatureDataList.get(i).skinDataList.get(j).slotDataList.get(k).displayDataList.get(m);
                                if (!displayData.name.equals(textureName)) {
                                    displayData = null;
                                }
                                else {
                                    break;
                                }
                            }
                            if (displayData != null) {
                                break;
                            }
                        }
                        if (displayData != null) {
                            break;
                        }
                    }
                    if (displayData != null) {
                        break;
                    }
                }
            }
        }

        return generateDisplay(textureAtlas, textureData, displayData);
    }


    protected void buildBones(Armature armature, ArmatureData armatureData){
        for (int i = 0, l = armatureData.boneDataList.size(); i < l; ++i) {
            BoneData boneData = armatureData.boneDataList.get(i);
            Bone bone = new Bone();
            bone.name = boneData.name;
            bone.inheritRotation = boneData.inheritRotation;
            bone.inheritScale = boneData.inheritScale;
            // copy
            bone.origin = boneData.transform;

            if (armatureData.getBoneData(boneData.parent) != null) {
                armature.addBone(bone, boneData.parent);
            }
            else {
                armature.addBone(bone);
            }
        }
    }
    protected void buildSlots(Armature armature, ArmatureData armatureData, SkinData skinData, SkinData skinDataCopy){
        for (int i = 0, size1 = skinData.slotDataList.size(); i < size1; ++i) {
            SlotData slotData = skinData.slotDataList.get(i);
            Bone bone = armature.getBone(slotData.parent);
            if (bone == null) {
                continue;
            }
            Slot slot = generateSlot(slotData);
            slot.name = slotData.name;
            slot.setOriginZOrder(slotData.zOrder);
            slot.setSlotData(slotData);
            List<AbstractMap.SimpleEntry<Object, DragonBones.DisplayType>> displayList = new ArrayList<AbstractMap.SimpleEntry<Object, DragonBones.DisplayType>>();
            for (int j = 0, size2 = slotData.displayDataList.size(); j < size2; ++j) {
                DisplayData displayData = slotData.displayDataList.get(j);
                switch (displayData.type) {
                    case DT_ARMATURE: {
                        DisplayData displayDataCopy = null;
                        if (skinDataCopy != null) {
                            SlotData slotDataCopy = skinDataCopy.getSlotData(slotData.name);
                            if (slotDataCopy != null) {
                                displayDataCopy = slotDataCopy.displayDataList.get(i);
                            }
                        }
                        String tmpCurrentDragonBonesDataName = currentDragonBonesDataName;
                        String tmpCcurrentTextureAtlasName = currentTextureAtlasName;
                        Armature childArmature = buildArmature(displayData.name, "", displayDataCopy != null ? displayDataCopy.name : "", tmpCurrentDragonBonesDataName, tmpCcurrentTextureAtlasName);
                        displayList.add(new AbstractMap.SimpleEntry<Object, DragonBones.DisplayType>(childArmature, DragonBones.DisplayType.DT_ARMATURE));
                        currentDragonBonesDataName = tmpCurrentDragonBonesDataName;
                        currentTextureAtlasName = tmpCcurrentTextureAtlasName;
                        break;
                    }
                    case DT_IMAGE: {
                        Object display = getTextureDisplay(displayData.name, currentTextureAtlasName, displayData);
                        displayList.add(new AbstractMap.SimpleEntry<Object, DragonBones.DisplayType>(display, DragonBones.DisplayType.DT_IMAGE));
                        break;
                    }

                    /*
                    case DisplayType::DT_FRAME:
                    {
                        break;
                    }

                    case DisplayType::DT_TEXT:
                    {
                        break;
                    }
                    */

                    default:
                        displayList.add(new AbstractMap.SimpleEntry<Object, DragonBones.DisplayType>(null, DragonBones.DisplayType.DT_IMAGE));
                        break;
                }
            }

            bone.addChild(slot);

            if (displayList != null) {
                slot.setDisplayList(displayList);
            }
        }
    }
    protected abstract Armature generateArmature(ArmatureData armatureData);
    protected abstract Slot generateSlot(SlotData slotData);
    protected abstract Object generateDisplay(DBTextureAtlas textureAtlas, DBTextureData textureData, DisplayData displayData);

}
