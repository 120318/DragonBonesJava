package com.dragonBones.objects;

import java.util.ArrayList;
import java.util.List;

public class Timeline {

    public float scale;
    public int duration;
    public List<Frame> frameList;

    public Timeline(){
        duration = 0;
        scale = 1.f;
        frameList = new ArrayList<Frame>();
    }
    public Timeline(Timeline timeline) {
        duration = timeline.duration;
        scale = timeline.scale;
        frameList = new ArrayList<Frame>();
        for (int i = 0, l = frameList.size(); i < l; ++i) {
            switch (timeline.frameList.get(i).frameType) {
                case FT_FRAME:
                    Frame frame = new Frame(timeline.frameList.get(i));
                    frameList.add(frame);
                    break;
                case FT_TRANSFORM_FRAME:
                    TransformFrame transformFrame = new TransformFrame((TransformFrame) timeline.frameList.get(i));
                    frameList.add(transformFrame);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type");
            }
        }
    }

}
