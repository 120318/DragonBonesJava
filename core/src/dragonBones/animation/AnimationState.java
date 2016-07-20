package dragonBones.animation;

import dragonBones.DragonBones;
import dragonBones.core.Armature;
import dragonBones.core.Bone;
import dragonBones.events.EventData;
import dragonBones.objects.AnimationData;
import dragonBones.objects.Frame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingzhao on 2016/2/29.
 */
public class AnimationState {
    public enum FadeState {FADE_BEFORE, FADING, FADE_COMPLETE};
    private static List<AnimationState> pool = new ArrayList<AnimationState>();

    public static AnimationState borrowObject(){
        if(pool.isEmpty()){
            return new AnimationState();
        }
        return pool.remove(pool.size() - 1);
    }

    public static void returnObject(AnimationState animationState) {
        if(!pool.contains(animationState)){
            pool.add(animationState);
        }
        animationState.clear();
    }
    private static void clearObject(){
        for(AnimationState animationState : pool){
            animationState.clear();
        }
        pool.clear();
    }
    public boolean additiveBlending;
    public boolean isComplete;
    public int layer;
    public String group;
    public boolean autoTween;
    public boolean autoFadeOut;
    public boolean displayControl;
    public boolean lastFrameAutoTween;
    public float fadeOutTime;
    public float weight;
    public FadeState fadeState;
    public String name;


    private boolean isPlaying;
    private boolean isFadeOut;
    private boolean pausePlayheadInFade;
    private int currentPlayTimes;
    private int playTimes;
    private int currentTime;
    private int currentFrameIndex;
    private int currentFramePosition;
    private int currentFrameDuration;
    private int totalTime;
    private float time;
    private float timeScale;
    private float fadeWeight;
    private float fadeTotalWeight;
    private float fadeCurrentTime;
    private float fadeTotalTime;
    private float fadeBeginTime;

    private List<TimelineState> timelineStateList = new ArrayList<TimelineState>();
    private List<String> mixingTransforms = new ArrayList<String>();

    private AnimationData clip;
    private Armature armature;

    public AnimationState(){
        this.clip = null;
        this.armature = null;
    }

    public boolean getIsComplete(){
        return isComplete;
    }
    public boolean getIsplaying(){
        return (isPlaying && !isComplete);
    }
    public int getCurrentPlayTimes(){
        return currentPlayTimes < 0 ? 0 : currentPlayTimes;
    }
    public int getLayer(){
        return layer;
    }
    public float getTotalTime(){
        return totalTime * 0.001f;
    }
    public float getCurrentWeight(){
        return fadeWeight * weight;
    }
    public String getGroup(){
        return group;
    }
    public AnimationData getClip(){
        return clip;
    }
    public void setAdditiveBlending(boolean value){
        additiveBlending = value;
    }
    public void setAutoFadeOut(boolean value, float fadeOutTime){
        autoFadeOut = value;
        if(fadeOutTime >= 0){
            this.fadeOutTime = fadeOutTime;
        }
    }
    public void setWeight(float value){
        weight = value;
    }
    public void setFrameTween(boolean autoTween, boolean lastFrameAutoTween){
        this.autoTween = autoTween;
        this.lastFrameAutoTween = lastFrameAutoTween;
    }

    public int getPlayTimes(){
        return playTimes;
    }
    public void setPlayTimes(int playTimes){
        this.playTimes = playTimes;
        if(DragonBones.round(totalTime * 0.001f * clip.frameRate) < 2){
            this.playTimes = playTimes < 0 ? -1 : 1;
        }
        else{
            this.playTimes = playTimes < 0 ? -playTimes : playTimes;
        }
        autoFadeOut = playTimes < 0 ? true : false;
    }
    public float getCurrentTime(){
        return currentTime < 0 ? 0.f : currentTime * 0.001f;
    }
    public void setCurrentTime(float currentTime){
        if(currentTime < 0 || currentTime != currentTime){
            currentTime = 0.f;
        }
        this.time = currentTime;
        this.currentTime = (int)(this.time * 1000.f);
    }
    public float getTimeScale(){
        return timeScale;
    }
    public void setTimeScale(float timeScale) {
        if (timeScale != timeScale) {
            timeScale = 1.f;
        }
        this.timeScale = timeScale;
    }
    public void fadeIn(Armature armature, AnimationData clip, float fadeTotalTime, float timeScale, int playTimes, boolean pausePlayhead) {
        this.armature = armature;
        this.clip = clip;
        this.pausePlayheadInFade = pausePlayhead;
        this.totalTime = this.clip.duration;
        autoTween = this.clip.autoTween;
        name = this.clip.name;
        setTimeScale(timeScale);
        setPlayTimes(playTimes);
        // reset
        this.isComplete = false;
        this.currentFrameIndex = -1;
        this.currentPlayTimes = -1;

        if (DragonBones.round(this.totalTime * 0.001f * this.clip.frameRate) < 2)
        {
            this.currentTime = this.totalTime;
        }
        else
        {
            this.currentTime = -1;
        }

        this.time = 0.f;
        this.mixingTransforms.clear();
        // fade start
        this.isFadeOut = false;
        this.fadeWeight = 0.f;
        this.fadeTotalWeight = 1.f;
        this.fadeCurrentTime = 0.f;
        this.fadeBeginTime = this.fadeCurrentTime;
        this.fadeTotalTime = fadeTotalTime * this.timeScale;
        this.fadeState = FadeState.FADE_BEFORE;
        // default
        this.isPlaying = true;
        displayControl = true;
        lastFrameAutoTween = true;
        additiveBlending = false;
        weight = 1.f;
        fadeOutTime = fadeTotalTime;
        updateTimelineStates();
    }

    public void fadeOut(float fadeTotalTime, boolean pausePlayhead) {
        if (!(fadeTotalTime >= 0)) {
            fadeTotalTime = 0.f;
        }

        this.pausePlayheadInFade = pausePlayhead;

        if (this.isFadeOut) {
            if (fadeTotalTime > this.fadeTotalTime / this.timeScale - (this.fadeCurrentTime - this.fadeBeginTime)) {
                return;
            }
        }
        else
        {
            for (int i = 0, l = this.timelineStateList.size(); i < l; ++i)
            {
                this.timelineStateList.get(i).fadeOut();
            }
        }

        // fade start
        this.isFadeOut = true;
        this.fadeTotalWeight = this.fadeWeight;
        this.fadeState = FadeState.FADE_BEFORE;
        this.fadeBeginTime = this.fadeCurrentTime;
        this.fadeTotalTime = this.fadeTotalWeight >= 0 ? fadeTotalTime * this.timeScale : 0.f;
        // default
        displayControl = false;
    }
    public void play(){
        this.isPlaying = true;
    }
    public void stop() {
        this.isPlaying = false;
    }

    public boolean hasMixingTransform(String timelineName){
        return mixingTransforms.contains(timelineName);
    }
    public void addMixingTransform(String timelineName, boolean recursive){
        if (recursive) {
            Bone currentBone = null;
            // From root to leaf
            for (int i = this.armature.getBones().size(); i-- > 0;) {
                Bone bone = this.armature.getBones().get(i);
                String boneName = bone.name;
                if (boneName.equals(timelineName)) {
                    currentBone = bone;
                }
                if (currentBone != null && (currentBone == bone || currentBone.contains(bone)) &&
                    this.clip.getTimeline(boneName) != null && !this.mixingTransforms.contains(boneName)) {
                    this.mixingTransforms.add(boneName);
                }
            }
        }
        else if (
                this.clip.getTimeline(timelineName) != null &&
                        !this.mixingTransforms.contains(timelineName)) {
            this.mixingTransforms.add(timelineName);
        }
        updateTimelineStates();
    }

    public void removeMixingTransform(String timelineName, boolean recursive){
        if (recursive) {
            Bone currentBone = null;
            // From root to leaf
            for (int i = this.armature.getBones().size(); i-- > 0;) {
                Bone bone = this.armature.getBones().get(i);

                if (bone.name.equals(timelineName)) {
                    currentBone = bone;
                }

                if (currentBone != null && (currentBone == bone || currentBone.contains(bone))) {
                    if(this.mixingTransforms.contains(bone.name)){
                        this.mixingTransforms.remove(bone.name);
                    }
                }
            }
        }
        else {
            if (this.mixingTransforms.contains(timelineName)) {
                this.mixingTransforms.remove(timelineName);
            }
        }

        updateTimelineStates();
    }
    public void removeAllMixingTransform(){
        mixingTransforms.clear();
        updateTimelineStates();
    }

    public boolean advanceTime(float passedTime) {
        passedTime *= this.timeScale;
        advanceFadeTime(passedTime);
        if(fadeWeight != 0){
            advanceTimelineTime(passedTime);
        }
        return isFadeOut && fadeState == FadeState.FADE_COMPLETE;
    }
    public void addTimelineState(String timelineName){
        Bone bone = armature.getBone(timelineName);

        if (bone != null) {
            for (int i = 0, l = timelineStateList.size(); i < l; ++i) {
                if (timelineStateList.get(i).name.equals(timelineName)) {
                    return;
                }
            }

            TimelineState timelineState = TimelineState.borrowObject();
            timelineState.fadeIn(bone, this, clip.getTimeline(timelineName));
            timelineStateList.add(timelineState);
        }
    }
    public void removeTimelineState(TimelineState timelineState){
        if(timelineStateList.contains(timelineState)){
            TimelineState.returnObject(timelineState);
            timelineStateList.remove(timelineState);
        }
    }
    public void advanceFadeTime(float passedTime){
        boolean fadeStartFlg = false;
        boolean fadeCompleteFlg = false;

        if (fadeBeginTime >= 0) {
            FadeState fadeState = this.fadeState;
            this.fadeCurrentTime += passedTime < 0 ? -passedTime : passedTime;

            if (this.fadeCurrentTime >= this.fadeBeginTime + this.fadeTotalTime) {
                // fade complete
                if (this.fadeWeight == 1 || this.fadeWeight == 0) {
                    fadeState = FadeState.FADE_COMPLETE;
                    if (this.pausePlayheadInFade) {
                        this.pausePlayheadInFade = false;
                        this.currentTime = -1;
                    }
                }
                this.fadeWeight = this.isFadeOut ? 0.f : 1.f;
            }
            else if (this.fadeCurrentTime >= this.fadeBeginTime) {
                // fading
                fadeState = FadeState.FADING;
                this.fadeWeight = (this.fadeCurrentTime - this.fadeBeginTime) / this.fadeTotalTime * this.fadeTotalWeight;
                if (this.isFadeOut) {
                    this.fadeWeight = this.fadeTotalWeight - this.fadeWeight;
                }
            }
            else {
                // fade before
                fadeState = FadeState.FADE_BEFORE;
                this.fadeWeight = this.isFadeOut ? 1.f : 0.f;
            }

            if (this.fadeState != fadeState) {
                // this.fadeState == FadeState::FADE_BEFORE && (fadeState == FadeState::FADING || fadeState == FadeState::FADE_COMPLETE)
                if (this.fadeState == FadeState.FADE_BEFORE) {
                    fadeStartFlg = true;
                }

                // (_fadeState == FadeState::FADE_BEFORE || _fadeState == FadeState::FADING) && fadeState == FadeState::FADE_COMPLETE
                if (fadeState == FadeState.FADE_COMPLETE) {
                    fadeCompleteFlg = true;
                }

                this.fadeState = fadeState;
            }
        }

        if (fadeStartFlg) {
            EventData.EventType eventDataType;
            if(isFadeOut){
                eventDataType = EventData.EventType.FADE_OUT;
            }
            else {
                hideBones();
                eventDataType = EventData.EventType.FADE_IN;
            }
            if(armature.getEventManager().isHandle(eventDataType)) {
                EventData eventData = EventData.borrowObject(eventDataType);
                eventData.armature = this.armature;
                eventData.animationState = this;
                this.armature.addEvent(eventData);
            }
        }
        if (fadeCompleteFlg){
            EventData.EventType eventDataType;
            if(isFadeOut){
                eventDataType = EventData.EventType.FADE_OUT_COMPLETE;
            }
            else {
                hideBones();
                eventDataType = EventData.EventType.FADE_IN_COMPLETE;
            }
            if(armature.getEventManager().isHandle(eventDataType)) {
                EventData eventData = EventData.borrowObject(eventDataType);
                eventData.armature = this.armature;
                eventData.animationState = this;
                this.armature.addEvent(eventData);
            }
        }
    }
    public void advanceTimelineTime(float passedTime){
        if (isPlaying && !pausePlayheadInFade) {
            time += passedTime;
        }

        boolean startFlg = false;
        boolean completeFlg = false;
        boolean loopCompleteFlg = false;
        boolean isThisComplete = false;
        int currentPlayTimes = 0;
        int currentTime = (int)(time * 1000.f);

        if (playTimes == 0) {
            isThisComplete = false;
            currentPlayTimes = (int)(Math.ceil(Math.abs(currentTime) / (float)(this.totalTime)));
            currentTime -= (int)(Math.floor(currentTime / (float)(totalTime))) * totalTime;
            if (currentTime < 0) {
                currentTime += totalTime;
            }
        }
        else {
            int totalTimes = playTimes * totalTime;

            if (currentTime >= totalTimes) {
                currentTime = totalTimes;
                isThisComplete = true;
            }
            else if (currentTime <= -totalTimes) {
                currentTime = -totalTimes;
                isThisComplete = true;
            }
            else {
                isThisComplete = false;
            }

            if (currentTime < 0) {
                currentTime += totalTimes;
            }

            currentPlayTimes = (int)(Math.ceil(currentTime / (float)(totalTime)));
            currentTime -= (int)(Math.floor(currentTime / (float)(totalTime))) * totalTime;

            if (isThisComplete) {
                currentTime = totalTime;
            }
        }

        if (currentPlayTimes == 0) {
            currentPlayTimes = 1;
        }

        // update timeline
        isComplete = isThisComplete;
        float progress = time * 1000.f / (float)(totalTime);

        for (int i = 0, l = timelineStateList.size(); i < l; ++i) {
            timelineStateList.get(i).update(progress);
            isComplete = timelineStateList.get(i).isComplete() && isComplete;
        }

        // update main timeline
        if (this.currentTime != currentTime) {
            if (this.currentPlayTimes != currentPlayTimes) {    // check loop complete{
                if (this.currentPlayTimes > 0 && currentPlayTimes > 1) {
                    loopCompleteFlg = true;
                }

                this.currentPlayTimes = currentPlayTimes;
            }

            if (this.currentTime < 0 && !this.pausePlayheadInFade) {   // check start{
                startFlg = true;
            }

            if (isComplete) {    // check complete
                completeFlg = true;
            }
            this.currentTime = currentTime;
            updateMainTimeline(isThisComplete);

        }
        if (startFlg){
            if(armature.getEventManager().isHandle(EventData.EventType.SOUND)) {
                EventData eventData = EventData.borrowObject(EventData.EventType.START);
                eventData.armature = this.armature;
                eventData.animationState = this;
                this.armature.addEvent(eventData);
            }
        }
        if (completeFlg){
            if(armature.getEventManager().isHandle(EventData.EventType.COMPLETE)) {
                EventData eventData = EventData.borrowObject(EventData.EventType.COMPLETE);
                eventData.armature = this.armature;
                eventData.animationState = this;
                this.armature.addEvent(eventData);
            }
            if (autoFadeOut) {
                fadeOut(fadeOutTime, true);
            }
        }
        else if(loopCompleteFlg){
            if(armature.getEventManager().isHandle(EventData.EventType.LOOP_COMPLETE)) {
                EventData eventData = EventData.borrowObject(EventData.EventType.LOOP_COMPLETE);
                eventData.armature = this.armature;
                eventData.animationState = this;
                this.armature.addEvent(eventData);
            }
        }
    }

    public void updateTimelineStates() {
        for (int i = this.timelineStateList.size(); i-- > 0;) {
            TimelineState timelineState = this.timelineStateList.get(i);
            if (this.armature.getBone(timelineState.name) == null) {
                removeTimelineState(timelineState);
            }
        }

        if (this.mixingTransforms.isEmpty()) {
            for (int i = 0, l = this.clip.timelineList.size(); i < l; ++i) {
                addTimelineState(this.clip.timelineList.get(i).name);
            }
        }
        else {
            for (int i = this.timelineStateList.size(); i-- > 0;) {
                TimelineState timelineState = this.timelineStateList.get(i);
                if (!this.mixingTransforms.contains(timelineState.name)) {
                    removeTimelineState(timelineState);
                }
            }

            for (int i = 0, l = this.mixingTransforms.size(); i < l; ++i) {
                addTimelineState(this.mixingTransforms.get(i));
            }
        }
    }
    public void updateMainTimeline(boolean isThisComplete){
        if (!clip.frameList.isEmpty()) {
            Frame prevFrame = null;
            Frame currentFrame = null;

            for (int i = 0, l = clip.frameList.size(); i < l; ++i) {
                if (currentFrameIndex < 0) {
                    currentFrameIndex = 0;
                }
                else if (currentTime < currentFramePosition || currentTime >= currentFramePosition + currentFrameDuration) {
                    ++currentFrameIndex;
                    if (currentFrameIndex >= l) {
                        if (isThisComplete) {
                            --currentFrameIndex;
                            break;
                        }
                        else {
                            currentFrameIndex = 0;
                        }
                    }
                }
                else {
                    break;
                }

                currentFrame = clip.frameList.get(currentFrameIndex);

                if (prevFrame != null) {
                    armature.arriveAtFrame(prevFrame, this, true);
                }

                currentFrameDuration = currentFrame.duration;
                currentFramePosition = currentFrame.position;
                prevFrame = currentFrame;
            }

            if (currentFrame != null) {
                armature.arriveAtFrame(currentFrame, this, false);
            }
        }
    }

    public void hideBones(){
        for(int i = 0, l = clip.hideTimelineList.size(); i < l; ++i){
            Bone bone = armature.getBone(clip.hideTimelineList.get(i));
            if(bone != null){
                bone.hideSlot();
            }
        }
    }
    public void clear(){
        for(int i = timelineStateList.size(); i-- > 0; ){
            TimelineState.returnObject(timelineStateList.get(i));
        }
        timelineStateList.clear();
        mixingTransforms.clear();
        armature = null;
        clip = null;
    }
}
