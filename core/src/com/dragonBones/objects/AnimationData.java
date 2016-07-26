package com.dragonBones.objects;

import com.dragonBones.DragonBones;

import java.util.ArrayList;
import java.util.List;

public class AnimationData extends Timeline {
    public boolean autoTween;
    public int frameRate;
    public int playTimes;
    public float fadeTime;
    public float tweenEasing;
    public String name;
    public List<TransformTimeline> timelineList;
    public List<String> hideTimelineList;
    public AnimationData(){
        autoTween = false;
        frameRate = 30;
        playTimes = 1;
        fadeTime = 0.f;
        tweenEasing = DragonBones.USE_FRAME_TWEEN_EASING;
        timelineList = new ArrayList<TransformTimeline>();
        hideTimelineList = new ArrayList<String>();
    }
    public AnimationData(AnimationData animationData){
        super(animationData);
        autoTween = animationData.autoTween;
        frameRate = animationData.frameRate;
        playTimes = animationData.playTimes;
        fadeTime = animationData.fadeTime;
        tweenEasing = animationData.tweenEasing;
        name = animationData.name;
        timelineList = new ArrayList<TransformTimeline>();
        for (int i = 0, l = animationData.timelineList.size(); i < l; ++i) {
            TransformTimeline tmp = new TransformTimeline(animationData.timelineList.get(i));
            timelineList.add(tmp);
        }
        hideTimelineList = new ArrayList<String>();
        for(int i = 0, l = animationData.hideTimelineList.size(); i < l; ++i){
            hideTimelineList.add(hideTimelineList.get(i));
        }
    }

    public TransformTimeline getTimeline(String timelineName){
        for(int i = 0, l = timelineList.size(); i < l; ++i){
            if(timelineList.get(i).name.equals(timelineName)){
                return timelineList.get(i);
            }
        }
        return null;
    }
}
