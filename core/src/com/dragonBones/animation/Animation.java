package com.dragonBones.animation;

import com.dragonBones.core.Armature;
import com.dragonBones.core.Slot;
import com.dragonBones.objects.AnimationData;

import java.util.ArrayList;
import java.util.List;

public class Animation {

    public enum AnimationFadeOutMode {NONE, SAME_LAYER, SAME_GROUP, SAME_LAYER_AND_GROUP, ALL}

    public boolean autoTween;

    protected boolean isFading;
    protected boolean isPlaying;
    protected float timeScale;
    protected List<String> animationList = new ArrayList<String>();
    protected List<AnimationData> animationDataList = new ArrayList<AnimationData>();
    protected List<AnimationState> animationStateList = new ArrayList<AnimationState>();
    protected Armature armature;
    protected AnimationState lastAnimationState;

    public Animation(){
        isPlaying = false;
        autoTween = true;
        timeScale = 1.0f;
        armature = null;
        lastAnimationState = null;
    }

    public void clear(){
        stop();
        for(int i = 0, l = animationStateList.size(); i < l; ++i){
            AnimationState.returnObject(animationStateList.get(i));
        }
        animationStateList.clear();
        lastAnimationState = null;
        List<Slot> slotList = armature.getSlots();
        for(int i = 0, l = slotList.size(); i < l; ++i){
            Armature childArmature = slotList.get(i).getChildArmature();
            if(childArmature != null){
                childArmature.getAnimation().clear();
            }
        }
    }

    public boolean getIsPlaying(){
        return isPlaying && !getIsComplete();
    }

    public boolean getIsComplete(){
        if(lastAnimationState != null){
            if(!lastAnimationState.isComplete){
                return false;
            }
            for(int i = 0, l = animationDataList.size(); i < l; ++i){
                if(!animationStateList.get(i).isComplete){
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public List<String> getAnimationList(){
        return animationList;
    }

    public AnimationState getLastAnimationState(){
        return lastAnimationState;
    }

    public void setLastAnimationState(AnimationState lastAnimationState) {
        this.lastAnimationState = lastAnimationState;
    }

    public float getTimeScale(){
        return timeScale;
    }

    public void setTimeScale(float timeScale){
        if(timeScale < 0){
            timeScale = 1;
        }
        this.timeScale = timeScale;
    }

    public List<AnimationData> getAnimationDataList(){
        return animationDataList;
    }

    public void setAnimationDataList(List<AnimationData> animationDataList) {
        this.animationDataList = animationDataList;
        animationList.clear();
        for(int i = 0, l = animationDataList.size(); i < l; ++i){
            animationList.add(animationDataList.get(i).name);
        }
    }

    public AnimationState gotoAndPlay(String animationName){
        return gotoAndPlay(animationName, -1.f);
    }

    public AnimationState gotoAndPlay(String animationName, float fadeInTime){
        return gotoAndPlay(animationName, fadeInTime, -1.0f, -1, 0, "", AnimationFadeOutMode.SAME_LAYER_AND_GROUP, true, true);
    }

    public AnimationState gotoAndPlay(
            String animationName,
            float fadeInTime,
            float duration,
            int playTimes,
            int layer,
            String group,
            AnimationFadeOutMode fadeOutMode,
            boolean pauseFadeOut,
            boolean pauseFadeIn
    ){
        AnimationData animationData = null;
        for(int i = 0, l = animationDataList.size(); i < l; ++i){
            if(animationDataList.get(i).name.equals(animationName)){
                animationData = animationDataList.get(i);
                break;
            }
        }
        if(animationData == null){
            throw new RuntimeException("No animation Data");
        }
        isPlaying = true;
        isFading = true;
        fadeInTime = fadeInTime < 0 ? (animationData.fadeTime < 0 ? 0.3f : animationData.fadeTime) : fadeInTime;
        if(fadeInTime <= 0){
            fadeInTime = 0.01f;
        }
        float durationScale;
        if(duration < 0){
            durationScale = animationData.scale < 0 ? 1.0f : animationData.scale;
        }
        else{
            durationScale = duration * 1000.0f / animationData.duration;
        }
        if(durationScale == 0){
            durationScale = 0.001f;
        }
        playTimes = playTimes < 0 ? animationData.playTimes : playTimes;
        switch(fadeOutMode) {
            case NONE:
                break;
            case SAME_LAYER:
                for (int i = 0, l = animationStateList.size(); i < l; ++i) {
                    AnimationState animationState = animationStateList.get(i);
                    if (animationState.layer == layer) {
                        animationState.fadeOut(fadeInTime, pauseFadeOut);
                    }
                }
                break;
            case SAME_GROUP:
                for (int i = 0, l = animationStateList.size(); i < l; ++i) {
                    AnimationState animationState = animationStateList.get(i);
                    if (animationState.group.equals(group)) {
                        animationState.fadeOut(fadeInTime, pauseFadeOut);
                    }
                }
                break;
            case ALL:
                for (int i = 0, l = animationStateList.size(); i < l; ++i) {
                    AnimationState animationState = animationStateList.get(i);
                    animationState.fadeOut(fadeInTime, pauseFadeOut);
                }
                break;
            case SAME_LAYER_AND_GROUP:
            default:
                for (int i = 0, l = animationStateList.size(); i < l; ++i) {
                    AnimationState animationState = animationStateList.get(i);
                    if (animationState.layer == layer && animationState.group.equals(group)) {
                        animationState.fadeOut(fadeInTime, pauseFadeOut);
                    }
                }
                break;
        }
        lastAnimationState = AnimationState.borrowObject();
        lastAnimationState.layer = layer;
        lastAnimationState.group = group;
        lastAnimationState.autoTween = autoTween;
        lastAnimationState.fadeIn(armature, animationData, fadeInTime, 1.0f / durationScale, playTimes, pauseFadeIn);
        addState(lastAnimationState);
        for(int i = 0, l = armature.getSlots().size(); i < l; ++i){
            Slot slot = armature.getSlots().get(i);
            if(slot.getChildArmature() != null && slot.getChildArmature().isInheritAnimation() &&
                    slot.getChildArmature().getAnimation().hasAnimation(animationName)){
                slot.getChildArmature().getAnimation().gotoAndPlay(animationName, fadeInTime);
            }
        }
        return lastAnimationState;
    }

    public AnimationState gotoAndStop(
            String animationName,
            float time,
            float normalizedTime,
            float fadeInTime,
            float duration,
            int layer,
            String group,
            AnimationFadeOutMode fadeOutMode
    ){
        AnimationState animationState = getState(animationName, layer);
        if(animationState == null){
            animationState = gotoAndPlay(animationName, fadeInTime, duration, -1, layer, group, fadeOutMode, true, true);
        }
        if(normalizedTime >= 0){
            animationState.setCurrentTime(animationState.getTotalTime() * normalizedTime);
        }
        else{
            animationState.setCurrentTime(time);
        }
        animationState.stop();
        return animationState;
    }

    public void play() {
        if(animationDataList.isEmpty()){
            return;
        }
        if(lastAnimationState == null){
            gotoAndPlay(animationDataList.get(0).name);
        }
        else if(!isPlaying){
            isPlaying = true;
        }
    }

    public void stop() {
        isPlaying = false;
    }

    public void advanceTime(float passedTime){
        if(!isPlaying){
            return;
        }
        boolean isFading = false;
        passedTime *= timeScale;
        for(int i = 0, l = animationStateList.size(); i < l; ++i){
            AnimationState animationState = animationStateList.get(i);
            if(animationState.advanceTime(passedTime)){
                removeState(animationState);
                --i;
                --l;
            }
            else if(animationState.fadeState != AnimationState.FadeState.FADE_COMPLETE){
                isFading = true;
            }
        }
        this.isFading = isFading;
    }

    public boolean hasAnimation(String animationName){
        for(int i = 0, l = animationDataList.size(); i < l; ++i){
            if(animationDataList.get(i).name.equals(animationName)){
                return true;
            }
        }
        return false;
    }

    public AnimationState getState(String name, int layer){
        for(int i = animationStateList.size(); i-- > 0; ){
            AnimationState animationState = animationStateList.get(i);
            if(animationState.name.equals(name) && animationState.layer == layer){
                return animationState;
            }
        }
        return null;
    }

    protected void addState(AnimationState animationState){
        if(!animationStateList.contains(animationState)){
            animationStateList.add(animationState);
        }
    }

    protected void removeState(AnimationState animationState){
        if(animationStateList.contains(animationState)){
            animationStateList.remove(animationState);
            AnimationState.returnObject(animationState);
            if(lastAnimationState == animationState){
                if(animationStateList.isEmpty()){
                    lastAnimationState = null;
                }
                else{
                    lastAnimationState = animationStateList.get(animationStateList.size() - 1);
                }
            }
        }
    }

    public void updateAnimationState(){
        for(int i = 0, l = animationStateList.size(); i < l; ++i){
            animationStateList.get(i).updateTimelineStates();
        }
    }

    public boolean isFading(){
        return isFading;
    }

    public void setArmature(Armature armature){
        this.armature = armature;
    }

}
