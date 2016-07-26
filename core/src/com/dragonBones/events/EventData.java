package com.dragonBones.events;

import com.dragonBones.animation.AnimationState;
import com.dragonBones.core.Armature;
import com.dragonBones.core.Bone;
import com.dragonBones.objects.Frame;

import java.util.ArrayList;
import java.util.List;

public class EventData {

    public static final String Z_ORDER_UPDATED = "zorderUpdate";
    public static final String ANIMATION_FRAME_EVENT = "animationFrameEvent";
    public static final String BONE_FRAME_EVENT = "boneFrameEvent";
    public static final String SOUND = "sound";
    public static final String FADE_IN = "fadeIn";
    public static final String FADE_OUT = "fadeOut";
    public static final String START = "start";
    public static final String COMPLETE = "complete";
    public static final String LOOP_COMPLETE = "loopComplete";
    public static final String FADE_IN_COMPLETE = "fadeInComplete";
    public static final String FADE_OUT_COMPLETE = "fadeOutcomplete";
    public static final String _ERROR = "error";
    public enum EventType {
        Z_ORDER_UPDATED,
        ANIMATION_FRAME_EVENT,
        BONE_FRAME_EVENT,
        SOUND,
        FADE_IN, FADE_OUT, START, COMPLETE, LOOP_COMPLETE, FADE_IN_COMPLETE, FADE_OUT_COMPLETE,
        _ERROR
    };

    public static EventData borrowObject(EventType eventType) {
        if(pool.isEmpty()){
            return new EventData(eventType, null);
        }
        EventData eventData = pool.remove(pool.size() - 1);
        eventData.type = eventType;
        return eventData;
    }
    public static void returnObject(EventData eventData) {
        if(!pool.contains(eventData)){
            pool.add(eventData);
        }
        eventData.clear();
    }
    public static void clearObject(){
        for(int i = 0, l = pool.size(); i < l; ++i){
            pool.get(i).clear();
        }
        pool.clear();
    }

    private static List<EventData> pool = new ArrayList<EventData>();

    public String frameLabel;
    public String sound;
    public Armature armature;
    public Bone bone;
    public AnimationState animationState;
    public Frame frame;

    private EventType type;

    public EventData(){
        type = EventType._ERROR;
        armature = null;
        bone = null;;
        animationState = null;
    }
    public EventData(EventType type, Armature armatureTarget){
        this.type = type;
        armature = armatureTarget;
        bone = null;
        animationState = null;
        frame = null;
    }
    public void clear(){
        armature = null;
        bone = null;
        animationState = null;
        frame = null;
        frameLabel = null; // clear
        sound = null; // clear
    }
    public EventData(EventData eventData){
        this.type = eventData.type;
        frameLabel = eventData.frameLabel;
        sound = eventData.sound;
        armature = eventData.armature;
        bone = eventData.bone;
        animationState = eventData.animationState;
        frame = eventData.frame;
    }


    public EventType getType(){
        return type;
    }

}
