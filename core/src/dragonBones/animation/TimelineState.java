package dragonBones.animation;

import dragonBones.DragonBones;
import dragonBones.core.Bone;
import dragonBones.geoms.ColorTransform;
import dragonBones.geoms.Point;
import dragonBones.geoms.Transform;
import dragonBones.objects.TransformFrame;
import dragonBones.objects.TransformTimeline;

import java.util.ArrayList;
import java.util.List;

public class TimelineState {

    private enum UpdateState {UPDATE, UPDATE_ONCE, UNUPDATE }

    private static List<TimelineState> pool = new ArrayList<TimelineState>();

    public static TimelineState borrowObject() {
        if(pool.isEmpty()){
            return new TimelineState();
        }
        return pool.remove(pool.size() - 1);
    }

    public static void returnObject(TimelineState timelineState) {
        if(!pool.contains(timelineState)){
            pool.add(timelineState);
        }
        timelineState.clear();
    }

    private static void clearObjects(){
        for(int i = 0, l = pool.size(); i < l; ++i){
            pool.get(i).clear();
        }
        pool.clear();
    }

    public String name;

    private boolean blendEnabled;
    private boolean isComplete;
    private boolean tweenTransform;
    private boolean tweenScale;
    private boolean tweenColor;
    private int currentTime;
    private int currentFrameIndex;
    private int currentFramePosition;
    private int currentFrameDuration;
    private int totalTime;
    private float weight;
    private float tweenEasing;

    private UpdateState updateState;
    private Transform transform = new Transform();
    private Transform durationTransform = new Transform();
    private Transform originTransform = new Transform();
    private Point pivot = new Point();
    private Point durationPivot = new Point();
    private Point originPivot = new Point();
    private ColorTransform durationColor = new ColorTransform();
    private Bone bone;
    private AnimationState animationState;
    private TransformTimeline timeline;

    public TimelineState(){

    }

    public boolean isBlendEnabled(){
        return blendEnabled;
    }

    public boolean isComplete(){
        return isComplete;
    }

    public void setWeight(float weight){
        this.weight = weight;
    }

    public float getWeight(){
        return weight;
    }

    public Transform getTransform(){
        return transform;
    }

    public Point getPivot(){
        return pivot;
    }

    public AnimationState getAnimationState(){
        return animationState;
    }

    public void fadeIn(Bone bone, AnimationState animationState, TransformTimeline timeline) {
        this.bone = bone;
        this.animationState = animationState;
        this.timeline = timeline;
        isComplete = false;
        blendEnabled = false;
        tweenTransform = false;
        tweenScale = false;
        tweenColor = false;
        currentTime = -1;
        currentFrameIndex = -1;
        weight = 1.f;
        tweenEasing = DragonBones.USE_FRAME_TWEEN_EASING;
        totalTime = timeline.duration;
        name = timeline.name;
        transform.x = 0.f;
        transform.y = 0.f;
        transform.scaleX = 0.f;
        transform.scaleY = 0.f;
        transform.skewX = 0.f;
        transform.skewY = 0.f;
        pivot.x = 0.f;
        pivot.y = 0.f;
        durationTransform.x = 0.f;
        durationTransform.y = 0.f;
        durationTransform.scaleX = 0.f;
        durationTransform.scaleY = 0.f;
        durationTransform.skewX = 0.f;
        durationTransform.skewY = 0.f;
        durationPivot.x = 0.f;
        durationPivot.y = 0.f;
        // copy
        originTransform = new Transform(timeline.originTransform);
        // copy
        originPivot = new Point(timeline.originPivot);

        switch (timeline.frameList.size()) {
            case 0:
                updateState = UpdateState.UNUPDATE;
                break;

            case 1:
                updateState = UpdateState.UPDATE_ONCE;
                break;

            default:
                updateState = UpdateState.UPDATE;
                break;
        }
        if (bone.name.equals("eyeR")) {
            int l = 12 -4;
        }
        bone.addState(this);
    }

    public void fadeOut() {
        transform.skewX = DragonBones.formatRadian(transform.skewX);
        transform.skewY = DragonBones.formatRadian(transform.skewY);
    }

    public void update(float progress) {
        if(updateState == UpdateState.UPDATE){
            updateMultipleFrame(progress);
        }
        else if(updateState == UpdateState.UPDATE_ONCE){
            updateSingleFrame();
            updateState = UpdateState.UNUPDATE;
        }
    }

    public void updateMultipleFrame(float progress){
        progress /= timeline.scale;
        progress += timeline.offset;
        int currentTime = (int)(totalTime * progress);
        int currentPlayTimes = 0;
        int playTimes = animationState.getPlayTimes();

        if (playTimes == 0) {
            isComplete = false;
            currentPlayTimes = (int)(Math.ceil(Math.abs(currentTime) / (float)(totalTime)));
            currentTime -= (int)(Math.floor(currentTime / (float)(this.totalTime))) * this.totalTime;

            if (currentTime < 0) {
                currentTime += this.totalTime;
            }
        }
        else {
            int totalTimes = playTimes * this.totalTime;

            if (currentTime >= totalTimes) {
                currentTime = totalTimes;
                this.isComplete = true;
            }
            else if (currentTime <= -totalTimes){
                currentTime = -totalTimes;
                this.isComplete = true;
            }
            else {
                this.isComplete = false;
            }

            if (currentTime < 0) {
                currentTime += totalTimes;
            }

            currentPlayTimes = (int)(Math.ceil(currentTime / (float)(this.totalTime)));

            if (this.isComplete) {
                currentTime = this.totalTime;
            }
            else {
                currentTime -= (int)(Math.floor(currentTime / (float)(this.totalTime))) * this.totalTime;
            }
        }

        if (currentPlayTimes == 0) {
            currentPlayTimes = 1;
        }

        if (this.currentTime != currentTime) {
            this.currentTime = currentTime;
            TransformFrame prevFrame = null;
            TransformFrame currentFrame = null;

            for (int i = 0, l = this.timeline.frameList.size(); i < l; ++i) {
                if (this.currentFrameIndex < 0) {
                    this.currentFrameIndex = 0;
                }
                else if (this.currentTime < this.currentFramePosition ||
                        this.currentTime >= this.currentFramePosition + this.currentFrameDuration) {
                    ++this.currentFrameIndex;
                    if (this.currentFrameIndex >= l) {
                        if (this.isComplete) {
                            --this.currentFrameIndex;
                            break;
                        }
                        else {
                            this.currentFrameIndex = 0;
                        }
                    }
                }
                else {
                    break;
                }

                currentFrame = (TransformFrame)(this.timeline.frameList.get(this.currentFrameIndex));

                if (prevFrame != null) {
                    this.bone.arriveAtFrame(prevFrame, this, this.animationState, true);
                }

                this.currentFrameDuration = currentFrame.duration;
                this.currentFramePosition = currentFrame.position;
                prevFrame = currentFrame;
            }

            if (currentFrame != null) {
                this.bone.arriveAtFrame(currentFrame, this, this.animationState, false);
                this.blendEnabled = currentFrame.displayIndex >= 0;

                if (this.blendEnabled) {
                    updateToNextFrame(currentPlayTimes);
                }
                else {
                    this.tweenEasing = DragonBones.NO_TWEEN_EASING;
                    this.tweenTransform = false;
                    this.tweenScale = false;
                    this.tweenColor = false;
                }
            }

            if (this.blendEnabled) {
                updateTween();
            }
        }
    }

    public void updateToNextFrame(int currentPlayTimes){
        boolean tweenEnabled = false;
        int nextFrameIndex = currentFrameIndex + 1;

        if (nextFrameIndex >= timeline.frameList.size()) {
            nextFrameIndex = 0;
        }
        TransformFrame currentFrame = (TransformFrame)(timeline.frameList.get(currentFrameIndex));
        TransformFrame nextFrame = (TransformFrame)(timeline.frameList.get(nextFrameIndex));

        if (nextFrameIndex == 0 &&
                (!animationState.lastFrameAutoTween ||
                        (animationState.getPlayTimes() != 0 &&
                                animationState.getCurrentPlayTimes() >= animationState.getPlayTimes() &&
                                ((currentFramePosition + currentFrameDuration) / totalTime + currentPlayTimes - timeline.offset) * timeline.scale > 0.999999f
                        )
                )
                ) {
            tweenEasing = DragonBones.NO_TWEEN_EASING;
            tweenEnabled = false;
        }
        else if (currentFrame.displayIndex < 0 || nextFrame.displayIndex < 0) {
            tweenEasing = DragonBones.NO_TWEEN_EASING;
            tweenEnabled = false;
        }
        else if (animationState.autoTween) {
            tweenEasing = animationState.getClip().tweenEasing;

            if (tweenEasing == DragonBones.USE_FRAME_TWEEN_EASING) {
                tweenEasing = currentFrame.tweenEasing;

                if (tweenEasing == DragonBones.NO_TWEEN_EASING){
                    tweenEnabled = false;
                }
                else {
                    if (tweenEasing == DragonBones.AUTO_TWEEN_EASING) {
                        tweenEasing = 0.f;
                    }

                    // _tweenEasing [-1, 0) 0 (0, 1] (1, 2]
                    tweenEnabled = true;
                }
            }
            // animationData overwrite tween
            else {
                // tweenEasing [-1, 0) 0 (0, 1] (1, 2]
                tweenEnabled = true;
            }
        }
        else {
            tweenEasing = currentFrame.tweenEasing;

            if (tweenEasing == DragonBones.NO_TWEEN_EASING || tweenEasing == DragonBones.AUTO_TWEEN_EASING){
                tweenEasing = DragonBones.NO_TWEEN_EASING;
                tweenEnabled = false;
            }
            else {
                // tweenEasing [-1, 0) 0 (0, 1] (1, 2]
                tweenEnabled = true;
            }
        }

        if (tweenEnabled) {
            // transform
            durationTransform.x = nextFrame.transform.x - currentFrame.transform.x;
            durationTransform.y = nextFrame.transform.y - currentFrame.transform.y;
            durationTransform.skewX = nextFrame.transform.skewX - currentFrame.transform.skewX;
            durationTransform.skewY = nextFrame.transform.skewY - currentFrame.transform.skewY;
            durationTransform.scaleX = nextFrame.transform.scaleX - currentFrame.transform.scaleX + nextFrame.scaleOffset.x;
            durationTransform.scaleY = nextFrame.transform.scaleY - currentFrame.transform.scaleY + nextFrame.scaleOffset.y;

            if (nextFrameIndex == 0) {
                durationTransform.skewX = DragonBones.formatRadian(durationTransform.skewX);
                durationTransform.skewY = DragonBones.formatRadian(durationTransform.skewY);
            }

            durationPivot.x = nextFrame.pivot.x - currentFrame.pivot.x;
            durationPivot.y = nextFrame.pivot.y - currentFrame.pivot.y;

            if (
                    durationTransform.x != 0 ||
                            durationTransform.y != 0 ||
                            durationTransform.skewX != 0 ||
                            durationTransform.skewY != 0 ||
                            durationTransform.scaleX != 0 ||
                            durationTransform.scaleY  != 0||
                            durationPivot.x != 0 ||
                            durationPivot.y != 0
                    ) {
                tweenTransform = true;
                tweenScale = currentFrame.tweenScale;
            }
            else {
                tweenTransform = false;
                tweenScale = false;
            }

            // color
            if (currentFrame.color != null && nextFrame.color != null) {
                durationColor.alphaOffset = nextFrame.color.alphaOffset - currentFrame.color.alphaOffset;
                durationColor.redOffset = nextFrame.color.redOffset - currentFrame.color.redOffset;
                durationColor.greenOffset = nextFrame.color.greenOffset - currentFrame.color.greenOffset;
                durationColor.blueOffset = nextFrame.color.blueOffset - currentFrame.color.blueOffset;
                durationColor.alphaMultiplier = nextFrame.color.alphaMultiplier - currentFrame.color.alphaMultiplier;
                durationColor.redMultiplier = nextFrame.color.redMultiplier - currentFrame.color.redMultiplier;
                durationColor.greenMultiplier = nextFrame.color.greenMultiplier - currentFrame.color.greenMultiplier;
                durationColor.blueMultiplier = nextFrame.color.blueMultiplier - currentFrame.color.blueMultiplier;

                if (
                        durationColor.alphaOffset != 0 ||
                                durationColor.redOffset != 0 ||
                                durationColor.greenOffset != 0 ||
                                durationColor.blueOffset != 0 ||
                                durationColor.alphaMultiplier != 0 ||
                                durationColor.redMultiplier != 0 ||
                                durationColor.greenMultiplier != 0 ||
                                durationColor.blueMultiplier != 0
                        ) {
                    tweenColor = true;
                }
                else {
                    tweenColor = false;
                }
            }
            else if (currentFrame.color != null) {
                tweenColor = true;
                durationColor.alphaOffset = -currentFrame.color.alphaOffset;
                durationColor.redOffset = -currentFrame.color.redOffset;
                durationColor.greenOffset = -currentFrame.color.greenOffset;
                durationColor.blueOffset = -currentFrame.color.blueOffset;
                durationColor.alphaMultiplier = 1 - currentFrame.color.alphaMultiplier;
                durationColor.redMultiplier = 1 - currentFrame.color.redMultiplier;
                durationColor.greenMultiplier = 1 - currentFrame.color.greenMultiplier;
                durationColor.blueMultiplier = 1 - currentFrame.color.blueMultiplier;
            }
            else if (nextFrame.color != null) {
                tweenColor = true;
                durationColor.alphaOffset = nextFrame.color.alphaOffset;
                durationColor.redOffset = nextFrame.color.redOffset;
                durationColor.greenOffset = nextFrame.color.greenOffset;
                durationColor.blueOffset = nextFrame.color.blueOffset;
                durationColor.alphaMultiplier = nextFrame.color.alphaMultiplier - 1;
                durationColor.redMultiplier = nextFrame.color.redMultiplier - 1;
                durationColor.greenMultiplier = nextFrame.color.greenMultiplier - 1;
                durationColor.blueMultiplier = nextFrame.color.blueMultiplier - 1;
            }
            else {
                tweenColor = false;
            }
        }
        else {
            tweenTransform = false;
            tweenScale = false;
            tweenColor = false;
        }

        if (!tweenTransform) {
            if (animationState.additiveBlending) {
                transform.x = currentFrame.transform.x;
                transform.y = currentFrame.transform.y;
                transform.skewX = currentFrame.transform.skewX;
                transform.skewY = currentFrame.transform.skewY;
                transform.scaleX = currentFrame.transform.scaleX;
                transform.scaleY = currentFrame.transform.scaleY;
                pivot.x = currentFrame.pivot.x;
                pivot.y = currentFrame.pivot.y;
            }
            else {
                transform.x = originTransform.x + currentFrame.transform.x;
                transform.y = originTransform.y + currentFrame.transform.y;
                transform.skewX = originTransform.skewX + currentFrame.transform.skewX;
                transform.skewY = originTransform.skewY + currentFrame.transform.skewY;
                transform.scaleX = originTransform.scaleX + currentFrame.transform.scaleX;
                transform.scaleY = originTransform.scaleY + currentFrame.transform.scaleY;
                pivot.x = originPivot.x + currentFrame.pivot.x;
                pivot.y = originPivot.y + currentFrame.pivot.y;
            }
            bone.invalidUpdate();
        }
        else if (!tweenScale) {
            if (animationState.additiveBlending) {
                transform.scaleX = currentFrame.transform.scaleX;
                transform.scaleY = currentFrame.transform.scaleY;
            }
            else {
                transform.scaleX = originTransform.scaleX + currentFrame.transform.scaleX;
                transform.scaleY = originTransform.scaleY + currentFrame.transform.scaleY;
            }
        }

        if (!tweenColor && animationState.displayControl) {
            if (currentFrame.color != null) {
                bone.updateColor(
                        currentFrame.color.alphaOffset,
                        currentFrame.color.redOffset,
                        currentFrame.color.greenOffset,
                        currentFrame.color.blueOffset,
                        currentFrame.color.alphaMultiplier,
                        currentFrame.color.redMultiplier,
                        currentFrame.color.greenMultiplier,
                        currentFrame.color.blueMultiplier,
                        true
                );
            }
            else if (bone.isColorChanged()) {
                bone.updateColor(0, 0, 0, 0, 1.f, 1.f, 1.f, 1.f, false);
            }
        }
    }

    public void updateTween(){
        float progress = (currentTime - currentFramePosition) / (float)(currentFrameDuration);

        if (tweenEasing != 0 && tweenEasing != DragonBones.NO_TWEEN_EASING) {
            progress = DragonBones.getEaseValue(progress, tweenEasing);
        }

        TransformFrame currentFrame = (TransformFrame)(timeline.frameList.get(currentFrameIndex));

        if (tweenTransform) {
            Transform currentTransform = currentFrame.transform;
            Point currentPivot = currentFrame.pivot;

            if (animationState.additiveBlending) {
                //additive blending
                transform.x = currentTransform.x + durationTransform.x * progress;
                transform.y = currentTransform.y + durationTransform.y * progress;

                transform.skewX = currentTransform.skewX + durationTransform.skewX * progress;
                transform.skewY = currentTransform.skewY + durationTransform.skewY * progress;
                if (tweenScale) {
                    transform.scaleX = currentTransform.scaleX + durationTransform.scaleX * progress;
                    transform.scaleY = currentTransform.scaleY + durationTransform.scaleY * progress;
                }

                pivot.x = currentPivot.x + durationPivot.x * progress;
                pivot.y = currentPivot.y + durationPivot.y * progress;
            }
            else {
                // normal blending
                transform.x = originTransform.x + currentTransform.x + durationTransform.x * progress;
                transform.y = originTransform.y + currentTransform.y + durationTransform.y * progress;
                transform.skewX = originTransform.skewX + currentTransform.skewX + durationTransform.skewX * progress;
                transform.skewY = originTransform.skewY + currentTransform.skewY + durationTransform.skewY * progress;

                if (tweenScale) {
                    transform.scaleX = originTransform.scaleX + currentTransform.scaleX + durationTransform.scaleX * progress;
                    transform.scaleY = originTransform.scaleY + currentTransform.scaleY + durationTransform.scaleY * progress;
                }

                pivot.x = originPivot.x + currentPivot.x + durationPivot.x * progress;
                pivot.y = originPivot.y + currentPivot.y + durationPivot.y * progress;
            }

            bone.invalidUpdate();
        }

        if (tweenColor && animationState.displayControl) {
            if (currentFrame.color != null) {
                bone.updateColor(
                        (int)(currentFrame.color.alphaOffset + durationColor.alphaOffset * progress),
                        (int)(currentFrame.color.redOffset + durationColor.redOffset * progress),
                        (int)(currentFrame.color.greenOffset + durationColor.greenOffset * progress),
                        (int)(currentFrame.color.blueOffset + durationColor.blueOffset * progress),
                        currentFrame.color.alphaMultiplier + durationColor.alphaMultiplier * progress,
                        currentFrame.color.redMultiplier + durationColor.redMultiplier * progress,
                        currentFrame.color.greenMultiplier + durationColor.greenMultiplier * progress,
                        currentFrame.color.blueMultiplier + durationColor.blueMultiplier * progress,
                        true
                );
            }
            else {
                bone.updateColor(
                        (int)(durationColor.alphaOffset * progress),
                        (int)(durationColor.redOffset * progress),
                        (int)(durationColor.greenOffset * progress),
                        (int)(durationColor.blueOffset * progress),
                        1.f + durationColor.alphaMultiplier * progress,
                        1.f + durationColor.redMultiplier * progress,
                        1.f + durationColor.greenMultiplier * progress,
                        1.f + durationColor.blueMultiplier * progress,
                        true
                );
            }
        }
    }

    public void updateSingleFrame(){
        TransformFrame currentFrame = (TransformFrame)(timeline.frameList.get(0)/*front()*/);
        bone.arriveAtFrame(currentFrame, this, animationState, false);
        isComplete = true;
        tweenTransform = false;
        tweenScale = false;
        tweenColor = false;
        tweenEasing = DragonBones.NO_TWEEN_EASING;
        blendEnabled = currentFrame.displayIndex >= 0;

        if (blendEnabled) {
            if (animationState.additiveBlending) {
                // additive blending
                // singleFrame.transform (0)
                transform.x = transform.y = transform.skewX = transform.skewY = transform.scaleX = transform.scaleY = 0.f;
                pivot.x = pivot.y = 0.f;
            }
            else {
                // normal blending
                // timeline.originTransform + singleFrame.transform (0)
                // copy
                transform = originTransform;
                // copy
                pivot = originPivot;
            }

            bone.invalidUpdate();

            if (animationState.displayControl) {
                if (currentFrame.color != null) {
                    bone.updateColor(
                            currentFrame.color.alphaOffset,
                            currentFrame.color.redOffset,
                            currentFrame.color.greenOffset,
                            currentFrame.color.blueOffset,
                            currentFrame.color.alphaMultiplier,
                            currentFrame.color.redMultiplier,
                            currentFrame.color.greenMultiplier,
                            currentFrame.color.blueMultiplier,
                            true
                    );
                }
                else if (bone.isColorChanged()) {
                    bone.updateColor(0, 0, 0, 0, 1.f, 1.f, 1.f, 1.f, false);
                }
            }
        }
    }

    public void clear(){
        if(bone != null){
            bone.removeState(this);
            bone = null;
        }
        animationState = null;
        timeline = null;
    }
}
