package com.dragonBones.core;

import com.dragonBones.animation.AnimationState;
import com.dragonBones.geoms.Matrix;
import com.dragonBones.geoms.Transform;
import com.dragonBones.animation.TimelineState;
import com.dragonBones.events.EventData;
import com.dragonBones.geoms.Point;
import com.dragonBones.objects.TransformFrame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jingzhao on 2016/2/27.
 */
public class Bone extends DBObject{

    private static Comparator<TimelineState> sortState = new Comparator<TimelineState>() {
        @Override
        public int compare(TimelineState a, TimelineState b) {
            return a.getAnimationState().getLayer() < b.getAnimationState().getLayer() ? -1 : 1;
        }
    };

    public String displayController;


    protected boolean isColorChanged;
    protected int needUpdate;
    protected Point tweenPivot = new Point();
    protected Transform tween = new Transform();
    protected List<Bone> boneList = new ArrayList<Bone>();
    protected List<Slot> slotList = new ArrayList<Slot>();
    protected List<TimelineState> timelineStateList = new ArrayList<TimelineState>();



    public Bone(){
        isColorChanged = false;
        needUpdate = 2;
        tween = new Transform();
        tween.scaleX = tween.scaleY = 0.1f;
        inheritRotation = true;
        inheritScale = false;
        tweenPivot = new Point();
    }

    public void clear(){
        super.clear();
        boneList.clear();
        slotList.clear();
        timelineStateList.clear();
    }

    public boolean isColorChanged(){
        return isColorChanged;
    }

    public Slot getSlot(){
        return slotList.isEmpty() ? null : slotList.get(0);
    }
    public List<Slot> getSlots() {
        return slotList;
    }
    public List<Bone> getBones(){
        return boneList;
    }

    @Override
    public void setVisible(boolean visible){
        if(this.visible != visible){
            this.visible = visible;
            for(int i = 0, l = slotList.size(); i < l; ++i){
                slotList.get(i).updateDisplayVisible(this.visible);
            }
        }
    }

    @Override
    public void setArmature(Armature armature) {
        super.setArmature(armature);
        for(int i =0, l = boneList.size(); i < l; ++i){
            boneList.get(i).setArmature(armature);
        }
        for(int i = 0, l = this.slotList.size(); i < l; ++i){
            slotList.get(i).setArmature(armature);
        }
    }


    public void invalidUpdate() {
        needUpdate = 2;
    }

    public boolean contains(DBObject object) {
        if(object == this){
            return false;
        }
        DBObject ancestor = object;
        while(!(ancestor == this || ancestor == null)){
            ancestor = ancestor.getParent();
        }
        return ancestor == this;
    }

    public void addChild(DBObject object) {
        if(object instanceof Bone){
            Bone bone = (Bone)object;
            if(object == this || bone.contains(this)){
                throw new IllegalArgumentException("An Bone cannot be added as a child to itself or one of its children (or children's children, etc.)");
            }
        }
        if(object != null && object.getParent() != null){
            object.getParent().removeChild(object);
        }
        if(object instanceof Bone){
            Bone bone = (Bone)object;
            boneList.add(bone);
            bone.setParent(this);
            bone.setArmature(armature);
        }
        else if(object instanceof Slot){
            Slot slot = (Slot)object;
            slotList.add(slot);
            slot.setParent(this);
            slot.setArmature(armature);
        }
    }

    public void removeChild(Object object) {
        if(object instanceof Bone){
            Bone bone = (Bone)object;
            if (boneList.contains(bone)) {
                boneList.remove(bone);
                bone.setParent(null);
                bone.setArmature(null);
            }
        }
        else if(object instanceof Slot){
            Slot slot = (Slot)object;
            if(slotList.contains(slot)){
                slotList.remove(slot);
                slot.setParent(null);
                slot.setArmature(null);
            }
        }
    }

    public void update(boolean needUpdate) {
        this.needUpdate--;
        if (needUpdate || this.needUpdate > 0 || (parent != null && parent.needUpdate > 0)) {
            this.needUpdate = 1;
        }
        else {
            return;
        }

        blendingTimeline();
        global.scaleX = (origin.scaleX + tween.scaleX) * offset.scaleX;
        global.scaleY = (origin.scaleY + tween.scaleY) * offset.scaleY;

        if (parent != null) {
            float x = origin.x + offset.x + tween.x;
            float y = origin.y + offset.y + tween.y;
            Matrix parentMatrix = parent.globalTransformMatrix;

            globalTransformMatrix.tx = global.x = parentMatrix.a * x + parentMatrix.c * y + parentMatrix.tx;
            globalTransformMatrix.ty = global.y = parentMatrix.d * y + parentMatrix.b * x + parentMatrix.ty;

            if (inheritRotation) {
                global.skewX = origin.skewX + offset.skewX + tween.skewX + parent.global.skewX;
                global.skewY = origin.skewY + offset.skewY + tween.skewY + parent.global.skewY;
            }
            else {
                global.skewX = origin.skewX + offset.skewX + tween.skewX;
                global.skewY = origin.skewY + offset.skewY + tween.skewY;
            }

            if (inheritScale) {
                global.scaleX *= parent.global.scaleX;
                global.scaleY *= parent.global.scaleY;
            }
        }
        else {
            globalTransformMatrix.tx = global.x = origin.x + offset.x + tween.x;
            globalTransformMatrix.ty = global.y = origin.y + offset.y + tween.y;
            global.skewX = origin.skewX + offset.skewX + tween.skewX;
            global.skewY = origin.skewY + offset.skewY + tween.skewY;
        }

        globalTransformMatrix.a = global.scaleX * (float)Math.cos(global.skewY);
        globalTransformMatrix.b = global.scaleX * (float)Math.sin(global.skewY);
        globalTransformMatrix.c = -global.scaleY * (float)Math.sin(global.skewX);
        globalTransformMatrix.d = global.scaleY * (float)Math.cos(global.skewX);


    /*
    globalTransformMatrix.a = offset.scaleX * cos(global.skewY);
    globalTransformMatrix.b = offset.scaleX * sin(global.skewY);
    globalTransformMatrix.c = -offset.scaleY * sin(global.skewX);
    globalTransformMatrix.d = offset.scaleY * cos(global.skewX);
    */
    }

    public void updateColor(int aOffset,
                            int rOffset,
                            int gOffset,
                            int bOffset,
                            float aMultiplier,
                            float rMultiplier,
                            float gMultiplier,
                            float bMultiplier,
                            boolean colorChanged) {
        for(int i = 0, l = slotList.size(); i < l; ++i){
            slotList.get(i).updateDisplayColor(
                    aOffset, rOffset, gOffset, bOffset,
                    aMultiplier, rMultiplier, gMultiplier, bMultiplier
            );
        }
        this.isColorChanged = colorChanged;
    }

    public void hideSlot() {
        for(int i = 0, l = slotList.size(); i < l; ++i){
            slotList.get(i).changeDisplay(-1);
        }
    }

    public void arriveAtFrame(TransformFrame frame, TimelineState timelineState, AnimationState animationState, boolean isCross) {
        // TODO:

        boolean displayControl =
                animationState.displayControl &&
                        (displayController == null || displayController.equals(animationState.name));

        // && timelineState._weight > 0
        // TODO: 需要修正混合动画干扰关键帧数据的问题，如何正确高效的判断混合动画？
        if (displayControl) {
            int displayIndex = frame.displayIndex;
            for (int i = 0, l = slotList.size(); i < l; ++i) {
                Slot slot = slotList.get(i);
                slot.changeDisplay(displayIndex);
                slot.updateDisplayVisible(frame.visible);
                if (displayIndex >= 0) {
                    if (frame.zOrder != slot.tweenZOrder) {
                        slot.tweenZOrder = frame.zOrder;
                        armature.slotsZOrderChanged = true;
                    }
                }
            }

           if (frame.event != null) {
                if(armature.getEventManager().isHandle(EventData.EventType.BONE_FRAME_EVENT)) {
                    EventData eventData = EventData.borrowObject(EventData.EventType.BONE_FRAME_EVENT);
                    eventData.armature = this.armature;
                    eventData.bone = this;
                    eventData.animationState = animationState;
                    eventData.frameLabel = frame.event;
                    eventData.frame = frame;
                    this.armature.addEvent(eventData);
                }
            }

            if (frame.sound != null && Armature.soundEventHandler != null) {
                EventData eventData = EventData.borrowObject(EventData.EventType.SOUND);
                eventData.armature = this.armature;
                eventData.bone = this;
                eventData.animationState = animationState;
                eventData.sound = frame.sound;
                Armature.soundEventHandler.handle(eventData);
                EventData.returnObject(eventData);
            }

            if (frame.action != null) {
                for (int i = 0, l = slotList.size(); i < l; ++i) {
                    if (slotList.get(i).childArmature != null) {
                        slotList.get(i).childArmature.animation.gotoAndPlay(frame.action);
                    }
                }
            }
        }
    }

    public void addState(TimelineState timelineState) {
        if(!timelineStateList.contains(timelineState)){
            timelineStateList.add(timelineState);
            Collections.sort(timelineStateList, sortState);
        }
    }

    public void removeState(TimelineState timelineState) {
        if(timelineStateList.contains(timelineState)){
            timelineStateList.remove(timelineState);
        }
    }
    public void blendingTimeline(){
        int i = timelineStateList.size();

        if (i == 1) {
            TimelineState timelineState = timelineStateList.get(0);
            Transform transform = timelineState.getTransform();
            Point pivot = timelineState.getPivot();
            timelineState.setWeight(timelineState.getAnimationState().getCurrentWeight());
            float weight = timelineState.getWeight();
            tween.x = transform.x * weight;
            tween.y = transform.y * weight;
            tween.skewX = transform.skewX * weight;
            tween.skewY = transform.skewY * weight;
            tween.scaleX = transform.scaleX * weight;
            tween.scaleY = transform.scaleY * weight;
            tweenPivot.x = pivot.x * weight;
            tweenPivot.y = pivot.y * weight;

        }
        else if (i > 1) {
            int prevLayer = timelineStateList.get(i - 1).getAnimationState().getLayer();
            int currentLayer = 0;
            float weigthLeft = 1.f;
            float layerTotalWeight = 0.f;
            float x = 0.f;
            float y = 0.f;
            float skewX = 0.f;
            float skewY = 0.f;
            float scaleX = 0.f;
            float scaleY = 0.f;
            float pivotX = 0.f;
            float pivotY = 0.f;

            while (i-- > 0) {
                TimelineState timelineState = timelineStateList.get(i);
                currentLayer = timelineState.getAnimationState().getLayer();

                if (prevLayer != currentLayer) {
                    if (layerTotalWeight >= weigthLeft) {
                        timelineState.setWeight(0);
                        break;
                    } else
                    {
                        weigthLeft -= layerTotalWeight;
                    }
                }

                prevLayer = currentLayer;
                timelineState.setWeight(timelineState.getAnimationState().getCurrentWeight() * weigthLeft);
                float weight = timelineState.getWeight();

                //timelineState
                if (weight != 0 && timelineState.isBlendEnabled())
                {
                    Transform transform = timelineState.getTransform();
                    Point pivot = timelineState.getPivot();
                    x += transform.x * weight;
                    y += transform.y * weight;
                    skewX += transform.skewX * weight;
                    skewY += transform.skewY * weight;
                    scaleX += transform.scaleX * weight;
                    scaleY += transform.scaleY * weight;
                    pivotX += pivot.x * weight;
                    pivotY += pivot.y * weight;
                    layerTotalWeight += weight;
                }
            }

            tween.x = x;
            tween.y = y;
            tween.skewX = skewX;
            tween.skewY = skewY;
            tween.scaleX = scaleX;
            tween.scaleY = scaleY;
            tweenPivot.x = pivotX;
            tweenPivot.y = pivotY;
        }
    }
}
