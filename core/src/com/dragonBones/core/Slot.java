package com.dragonBones.core;


import com.dragonBones.geoms.Matrix;
import com.dragonBones.geoms.ColorTransform;
import com.dragonBones.objects.SlotData;
import com.dragonBones.DragonBones;

import java.util.AbstractMap;
import java.util.List;

public abstract class Slot extends DBObject{

    protected boolean isShowDisplay;
    protected int displayIndex;
    protected float originZOrder;
    protected float tweenZOrder;
    protected float offsetZOrder;
    protected DragonBones.BlendMode blendMode;
    protected ColorTransform colorTransform = new ColorTransform();
    protected List<AbstractMap.SimpleEntry<Object, DragonBones.DisplayType>> displayList;
    protected SlotData slotData;
    protected Object display;
    protected Armature childArmature;

    public Slot(SlotData slotData){
        isShowDisplay = false;
        displayIndex = -1;
        originZOrder = 0.f;
        tweenZOrder = 0.f;
        offsetZOrder = 0.f;
        blendMode = DragonBones.BlendMode.BM_NORMAL;
        this.slotData = slotData;
        childArmature = null;
        display = null;
        inheritRotation = true;
        inheritScale = true;
    }
    public void clear(){
        super.clear();
        displayList.clear();
        childArmature = null;
        display = null;
    }
    public int getDisplayIndex(){
        return displayIndex;
    }
    public boolean isShowDisplay(){
        return isShowDisplay;
    }
    public void setOriginZOrder(float originZOrder){
        this.originZOrder = originZOrder;
    }
    public float getOriginZOrder(){
        return originZOrder;
    }
    public void setTweenZOrder(float tweenZOrder){
        this.tweenZOrder = tweenZOrder;
    }
    public float getTweenZOrder(){
        return tweenZOrder;
    }
    public void setOffsetZOrder(float offsetZOrder){
        this.offsetZOrder = offsetZOrder;
    }
    public float getOffsetZOrder(){
        return offsetZOrder;
    }
    public void setBlendMode(DragonBones.BlendMode blendMode){
        this.blendMode = blendMode;
    }
    public DragonBones.BlendMode getBlendMode(){
        return blendMode;
    }

    public void setSlotData(SlotData slotData) {
        this.slotData = slotData;
    }
    public SlotData getSlotData(){
        return slotData;
    }
    public float getZOrder() {
        return originZOrder + tweenZOrder + originZOrder;
    }
    public void setZOrder(float value){
        if(getZOrder() != value){
            offsetZOrder = value - originZOrder - tweenZOrder;
            if(armature != null){
                armature.slotsZOrderChanged = true;
            }
        }
    }
    public Object getDisplay(){
        return display;
    }
    public void setDisplay(Object display, DragonBones.DisplayType displayType){
        if(displayIndex < 0){
            isShowDisplay = true;
            displayIndex = 0;
        }
        AbstractMap.SimpleEntry<Object, DragonBones.DisplayType> displayItem =
                new AbstractMap.SimpleEntry<Object, DragonBones.DisplayType>(display, displayType);
        if(displayIndex >= displayList.size()){
            displayList.add(displayItem);
        }
        if(displayList.get(displayIndex).getKey() != display){
            displayList.set(displayIndex, displayItem);
            updateSlotDisplay();
        }
    }
    public Armature getChildArmature() {
        return childArmature;
    }
    public void setChildArmature(Armature childArmature){
        setDisplay(childArmature, DragonBones.DisplayType.DT_ARMATURE);
    }
    public List<AbstractMap.SimpleEntry<Object, DragonBones.DisplayType>> getDisplayList(){
        return displayList;
    }
    public void setDisplayList(List<AbstractMap.SimpleEntry<Object,DragonBones.DisplayType>> displayList) {
        if(displayIndex < 0){
            isShowDisplay = true;
            displayIndex = 0;
        }
        childArmature = null;
        display = null;
        this.displayList = displayList;
        int displayIndexBackup = this.displayIndex;
        this.displayIndex = -1;
        changeDisplay(displayIndexBackup);
    }

    @Override
    public void setVisible(boolean visible) {
        if(this.visible != visible){
            this.visible = visible;
            updateDisplayVisible(this.visible);
        }
    }

    @Override
    public void setArmature(Armature armature) {
        super.setArmature(armature);
        if(this.armature != null){
            this.armature.slotsZOrderChanged = true;
        }
    }
    public void update() {
        if (parent.needUpdate <= 0) {
            return;
        }
        float x = origin.x + offset.x + parent.tweenPivot.x;
        float y = origin.y + offset.y + parent.tweenPivot.y;
        Matrix parentMatrix = parent.globalTransformMatrix;
        globalTransformMatrix.tx = global.x = parentMatrix.a * x + parentMatrix.c * y + parentMatrix.tx;
        globalTransformMatrix.ty = global.y = parentMatrix.d * y + parentMatrix.b * x + parentMatrix.ty;
        //globalTransformMatrix.tx = global.x = parentMatrix.a * x * parent.global.scaleX + parentMatrix.c * y * parent.global.scaleY + parentMatrix.tx;
        //globalTransformMatrix.ty = global.y = parentMatrix.d * y * parent.global.scaleY + parentMatrix.b * x * parent.global.scaleX + parentMatrix.ty;

        if (inheritRotation) {
            global.skewX = origin.skewX + offset.skewX + parent.global.skewX;
            global.skewY = origin.skewY + offset.skewY + parent.global.skewY;
        }
        else {
            global.skewX = origin.skewX + offset.skewX;
            global.skewY = origin.skewY + offset.skewY;
        }
        if (inheritScale) {
            global.scaleX = origin.scaleX * offset.scaleX * parent.global.scaleX;
            global.scaleY = origin.scaleY * offset.scaleY * parent.global.scaleY;
        }
        else {
            global.scaleX = origin.scaleX * offset.scaleX;
            global.scaleY = origin.scaleY * offset.scaleY;
        }

        globalTransformMatrix.a = global.scaleX * (float)Math.cos(global.skewY);
        globalTransformMatrix.b = global.scaleX * (float)Math.sin(global.skewY);
        globalTransformMatrix.c = -global.scaleY * (float)Math.sin(global.skewX);
        globalTransformMatrix.d = global.scaleY * (float)Math.cos(global.skewX);
        updateDisplayTransform();
    }

    public void changeDisplay(int displayIndex) {
        if (displayIndex < 0) {
            if (this.isShowDisplay) {
                this.isShowDisplay = false;
                updateChildArmatureAnimation();
            }
        }
        else if (!this.displayList.isEmpty()) {
            if (displayIndex >= this.displayList.size()) {
                displayIndex = this.displayList.size() - 1;
            }

            if (this.displayIndex != displayIndex) {
                this.isShowDisplay = true;
                this.displayIndex = displayIndex;
                updateSlotDisplay();
                if (this.slotData  != null && !this.slotData.displayDataList.isEmpty() &&
                                        this.displayIndex < this.slotData.displayDataList.size()) {
                    origin = this.slotData.displayDataList.get(this.displayIndex).transform;
                }
            }
            else if (!this.isShowDisplay) {
                this.isShowDisplay = true;
                if (this.armature != null) {
                    this.armature.slotsZOrderChanged = true;
                }
                updateChildArmatureAnimation();
            }
        }
    }
    public void updateSlotDisplay(){
        stopChildArmatureAnimation();
        Object display = this.displayList.get(this.displayIndex).getKey();
        DragonBones.DisplayType displayType = this.displayList.get(this.displayIndex).getValue();
        if (display != null) {
            if (displayType == DragonBones.DisplayType.DT_ARMATURE) {
                this.childArmature = (Armature)display;
                this.display = this.childArmature.display;
            }
            else {
                this.childArmature = null;
                this.display = display;
            }
        }
        else
        {
            this.display = null;
            this.childArmature = null;
        }

        playChildArmatureAnimation();
        if (this.display != null) {
            if (this.blendMode != DragonBones.BlendMode.BM_NORMAL) {
                updateDisplayBlendMode(this.blendMode);
            }
            else if (this.slotData != null) {
                updateDisplayBlendMode(this.slotData.blendMode);
            }

            updateDisplayColor(
                    this.colorTransform.alphaOffset, this.colorTransform.redOffset, this.colorTransform.greenOffset, this.colorTransform.blueOffset,
                    this.colorTransform.alphaMultiplier, this.colorTransform.redMultiplier, this.colorTransform.greenMultiplier, this.colorTransform.blueMultiplier
            );
            updateDisplayVisible(this.visible);
            updateDisplayTransform();
        }
    }
    public void updateDisplayColor(int aOffset, int rOffset, int gOffset, int bOffset, float aMultiplier, float rMultiplier, float gMultiplier, float bMultiplier) {
        colorTransform.alphaOffset = aOffset;
        colorTransform.redOffset = rOffset;
        colorTransform.greenOffset = gOffset;
        colorTransform.blueOffset = bOffset;
        colorTransform.alphaMultiplier = aMultiplier;
        colorTransform.redMultiplier = rMultiplier;
        colorTransform.greenMultiplier = gMultiplier;
        colorTransform.blueMultiplier = bMultiplier;
    }
    public void updateChildArmatureAnimation(){
        if(isShowDisplay){
            playChildArmatureAnimation();
        }
        else{
            stopChildArmatureAnimation();
        }
    }
    public void playChildArmatureAnimation(){
        if(childArmature != null && childArmature.isInheritAnimation){
            if(armature != null && armature.animation.getLastAnimationState() != null &&
                    childArmature.animation.hasAnimation(armature.animation.getLastAnimationState().name)){
                childArmature.animation.gotoAndPlay(armature.animation.getLastAnimationState().name);
            }
            else{
                childArmature.animation.play();
            }
        }
    }
    public void stopChildArmatureAnimation(){
        if(childArmature != null){
            childArmature.animation.stop();
            childArmature.animation.setLastAnimationState(null);
        }
    }

    public void updateDisplayBlendMode(DragonBones.BlendMode blendMode){
        if(childArmature != null){
            for(Slot slot : childArmature.getSlots()){
                slot.blendMode = blendMode;
            }
        }
    }
    public abstract void updateDisplayVisible(boolean visible);
    public abstract void updateDisplayTransform();

}
