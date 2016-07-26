package com.dragonBones.objects;

public class Frame {
    public enum FrameType {FT_FRAME, FT_TRANSFORM_FRAME};
    public int duration;
    public int position;

    public FrameType frameType;
    public String eventParameters;
    public String event;
    public String sound;
    public String action;

    public Object eventParametersParsed;

    public Frame(){
        position = 0;
        duration = 0;
        frameType = FrameType.FT_FRAME;
        eventParametersParsed = null;
    }
    public Frame(Frame frame){
        position = frame.position;
        duration = frame.duration;
        action = frame.action;
        event = frame.event;
        sound = frame.sound;
        eventParameters = frame.eventParameters;
    }

}
