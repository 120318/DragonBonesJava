package dragonBones.core;

import dragonBones.DragonBones;
import dragonBones.animation.Animatable;
import dragonBones.animation.Animation;
import dragonBones.animation.AnimationState;
import dragonBones.events.EventData;
import dragonBones.events.EventManager;
import dragonBones.geoms.Rectangle;
import dragonBones.objects.ArmatureData;
import dragonBones.objects.Frame;

import java.util.*;

/**
 * Created by jingzhao on 2016/2/27.
 */
public abstract class Armature implements Animatable {

    private static Comparator<Map.Entry<Integer, Bone>> sortBone = new Comparator<Map.Entry<Integer, Bone>>() {
        @Override
        public int compare(Map.Entry<Integer, Bone> a, Map.Entry<Integer, Bone> b) {
            return a.getKey() > b.getKey() ? -1 : 1;
        }
    };

    private static Comparator<Slot> sortSlot = new Comparator<Slot>() {
        @Override
        public int compare(Slot a, Slot b) {
            return a.getZOrder() < b.getZOrder() ? -1 : 1;
        }
    };

    public static EventManager soundEventHandler = null;

    public String name;
    public Object userData = null;


    protected List<Bone> boneList = new ArrayList<Bone>();
    protected List<Slot> slotList = new ArrayList<Slot>();
    protected List<EventData> eventDataList = new ArrayList<EventData>();
    protected Animation animation;
    protected ArmatureData armatureData;
    protected boolean isInheritAnimation;
    protected boolean needUpdate;
    protected boolean slotsZOrderChanged;
    protected Object display;

    protected EventManager eventManager;

    public Armature(ArmatureData armatureData, Animation animation, EventManager eventManager, Object display){
        this.armatureData = armatureData;
        this.animation = animation;
        this.display = display;
        this.eventManager = eventManager;
        this.animation.setArmature(this);

        this.needUpdate = false;
        this.slotsZOrderChanged = false;
        this.isInheritAnimation = true;
    }



    public void clear(){
        animation = null;
        boneList.clear();
        slotList.clear();
        for (int i = 0, l = eventDataList.size(); i < l; ++i) {
            if (eventDataList.get(i) != null) {
                EventData.returnObject(eventDataList.get(i));
            }
        }
        eventDataList.clear();
        display = null;
        userData = null;
    }

    public Rectangle getBoundingBox(){
        return null;
    }
    public List<Bone> getBones(){
        return boneList;
    }
    public List<Slot> getSlots(){
        return slotList;
    }

    public ArmatureData getArmatureData(){
        return armatureData;
    }
    public Animation getAnimation() {
        return animation;
    }
    public Object getDisplay(){
        return display;
    }
    public boolean isInheritAnimation(){
        return isInheritAnimation;
    }
    public void addEvent(EventData eventData){
        eventDataList.add(eventData);
    }
    public EventManager getEventManager(){
        return eventManager;
    }
    public void setInheritAnimation(boolean isInheritAnimation){
        this.isInheritAnimation = isInheritAnimation;
    }

    public Bone getBone(String boneName){
        if(boneName == null){
            return null;
        }
        for (int i = 0, l = boneList.size(); i < l; ++i) {
            if (boneList.get(i).name.equals(boneName)) {
                return boneList.get(i);
            }
        }
        return null;
    }
    public Bone getBoneByDisplay(Object display){
        if(display == null){
            return null;
        }
        Slot slot = getSlotByDisplay(display);
        return slot != null ? slot.parent : null;
    }

    public void addBone(Bone bone, String parentBoneName) {
        Bone boneParent = getBone(parentBoneName);
        boneParent.addChild(bone);
    }

    public void addBone(Bone bone) {
        if(bone.parent != null){
            bone.parent.removeChild(bone);
        }
        bone.setArmature(this);
    }
    public void removeBone(Bone bone){
        if(bone.armature != this){
            throw new IllegalArgumentException("The armature doesn't have this bone");
        }
        if(bone.parent != null){
            bone.parent.removeChild(bone);
        }
        else{
            bone.setArmature(null);
        }
    }
    public Bone removeBone(String boneName){
        Bone bone = getBone(boneName);
        if(bone != null){
            removeBone(bone);
        }
        return bone;
    }

    public Slot getSlot(String slotName){
        if(slotName == null){
            return null;
        }
        for (int i = 0, l = slotList.size(); i < l; ++i) {
            if (slotList.get(i).name.equals(slotName)){
                return slotList.get(i);
            }
        }
        return null;
    }
    public Slot getSlotByDisplay(Object display){
        if(display == null){
            return null;
        }
        for (int i = 0, l = slotList.size(); i < l; ++i) {
            if (slotList.get(i).getDisplay() == display){
                return slotList.get(i);
            }
        }
        return null;
    }
    public void addSlot(Slot slot, String parentBoneName){
        Bone bone = getBone(parentBoneName);
        bone.addChild(slot);
    }
    public void removeSlot(Slot slot){
        if (slot.armature != this) {
            throw new IllegalArgumentException("The armature doesn't have this slot");
        }
        slot.parent.removeChild(slot);
    }
    public Slot removeSlot(String slotName){
        Slot slot = getSlot(slotName);
        if(slot != null){
            removeSlot(slot);
        }
        return slot;
    }
    public void replaceSlot(String boneName, String oldSlotName, Slot newSlot){
        Bone bone = getBone(boneName);
        if(bone == null){
            return;
        }
        List<Slot> slots = bone.getSlots();
        Slot oldSlot = null;
        for(Slot slot : slots){
            if(slot.name.equals(oldSlotName)){
                oldSlot = slot;
                newSlot.setTweenZOrder(oldSlot.getTweenZOrder());
                newSlot.setOriginZOrder(oldSlot.getOriginZOrder());
                newSlot.setOffsetZOrder(oldSlot.getOffsetZOrder());
                newSlot.setBlendMode(oldSlot.getBlendMode());
                break;
            }
        }
        if(oldSlot != null){
            removeSlot(oldSlot);
        }
        newSlot.name = oldSlotName;
        bone.addChild(newSlot);

    }
    public void sortSlotsByZorder(){
        Collections.sort(slotList, sortSlot);
        slotsZOrderChanged = false;
    }
    public void invalidUpdate(){
        for(int i = 0, l = boneList.size(); i < l; ++i){
            boneList.get(i).invalidUpdate();
        }
    }
    public void invalidUpdate(String boneName){
        Bone bone = getBone(boneName);
        if(bone != null){
            bone.invalidUpdate();
        }
    }
    @Override
    public void advanceTime(float passedTime) {
        animation.advanceTime(passedTime);
        passedTime *= animation.getTimeScale();
        boolean isFading = animation.isFading();
        for(int i = boneList.size(); i-- > 0; ){
            boneList.get(i).update(isFading);
        }
        for(int i = slotList.size(); i-- > 0; ){
            Slot slot = slotList.get(i);
            slot.update();
            if(slot.isShowDisplay() && slot.childArmature != null){
                slot.childArmature.advanceTime(passedTime);
            }
            if(!slot.isShowDisplay()){
                int l = i + 1;
            }
        }
        if(slotsZOrderChanged){
            sortSlotsByZorder();
        }
        if(DragonBones.NEED_Z_ORDER_UPDATED_EVENT) {
            EventData eventData = new EventData(EventData.EventType.Z_ORDER_UPDATED, this);
            eventDataList.add(eventData);
        }
        else{
            if (!eventDataList.isEmpty()) {
                for (int i = 0, l = eventDataList.size(); i < l; ++i) {
                    // handle event
                    eventManager.handle(eventDataList.get(i));
                    EventData.returnObject(eventDataList.get(i));
                }
                eventDataList.clear();
            }
        }
    }
    protected void addDBObject(DBObject object){
        if(object instanceof Bone){
            Bone bone = (Bone)object;
            if(!boneList.contains(bone)){
                boneList.add(bone);
                sortBones();
                animation.updateAnimationState();
            }
        }
        else if(object instanceof Slot){
            Slot slot = (Slot)object;
            if(!slotList.contains(slot)){
                slotList.add(slot);
            }
        }
    }
    protected void removeDBObject(DBObject object){
        if(object instanceof Bone){
            Bone bone = (Bone)object;
            if(boneList.contains(bone)){
                boneList.remove(bone);
                animation.updateAnimationState();
            }
        }
        else if(object instanceof Slot){
            Slot slot = (Slot)object;
            if(slotList.contains(slot)){
                slotList.remove(slot);
            }
        }
    }
    protected void sortBones(){
        if(boneList.isEmpty()){
            return;
        }
        List<AbstractMap.SimpleEntry<Integer, Bone>> sortedList = new ArrayList<AbstractMap.SimpleEntry<Integer, Bone>>();
        for(int i = 0, l = boneList.size(); i < l; ++i) {
            Bone bone = boneList.get(i);
            Bone parentBone = bone;
            int level = 0;
            while (parentBone != null) {
                parentBone = parentBone.parent;
                ++level;
            }
            sortedList.add(new AbstractMap.SimpleEntry<Integer, Bone>(level, bone));
        }
        Collections.sort(sortedList, sortBone);
        for(int i = 0, l = sortedList.size(); i < l; ++i){
            boneList.set(i, sortedList.get(i).getValue());
        }
    }

    /**
     * @param isCross is not used in CPP version */
    public void arriveAtFrame(Frame frame, AnimationState animationState, boolean isCross){

        if(frame.event != null){
            EventData eventData = EventData.borrowObject(EventData.EventType.ANIMATION_FRAME_EVENT);
            eventData.armature = this;
            eventData.animationState = animationState;
            eventData.frameLabel = frame.event;
            eventData.frame = frame;
            eventDataList.add(eventData);
        }
        if(frame.sound != null && soundEventHandler != null){
            EventData eventData = EventData.borrowObject(EventData.EventType.SOUND);
            eventData.armature = this;
            eventData.animationState = animationState;
            eventData.sound = frame.sound;
            soundEventHandler.handle(eventData);
            EventData.returnObject(eventData);
        }
        if (frame.action != null) {
            if (animationState.displayControl) {
                animation.gotoAndPlay(frame.action);
            }
        }
    }
}
