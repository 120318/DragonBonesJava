package dragonBones.animation;

import java.util.ArrayList;
import java.util.List;

public class WorldClock implements Animatable{

    public static WorldClock clock;

    private boolean isPlaying;
    private float time;
    private float timeScale;
    private List<Animatable> animatableList = new ArrayList<Animatable>();

    public static WorldClock getInstance(){
        if(clock == null){
            clock = new WorldClock(-1, 1);
        }
        return clock;
    }

    public float getTime(){
        return time;
    }

    public float getTimeScale(){
        return timeScale;
    }

    public void setTimeScale(float timeScale){
        if(timeScale < 0 || timeScale != timeScale){
            timeScale = 1.f;
        }
        this.timeScale = timeScale;
    }

    public WorldClock(float time, float timeScale){
        isPlaying = true;
        this.time = 0;
        setTimeScale(timeScale);
    }

    public boolean contains(Animatable animatable){
        return animatableList.contains(animatable);
    }

    public void add(Animatable animatable){
        if(animatable != null & !contains(animatable)){
            animatableList.add(animatable);
        }
    }

    public void remove(Animatable animatable){
        if(animatable == null){
            return;
        }
        if(animatableList.contains(animatable)){
            animatableList.remove(animatable);
        }
    }
    public void removeAll(){
        animatableList.clear();
    }

    public void play(){
        isPlaying = true;
    }

    public void stop(){
        isPlaying = false;
    }

    @Override
    public void advanceTime(float passedTime) {
        if(!isPlaying){
            return;
        }
        if(passedTime < 0 || passedTime != passedTime){
            passedTime = 0.f;
        }
        passedTime *= timeScale;
        time += passedTime;
        if(animatableList.isEmpty()){
            return;
        }
        for(int i = 0, l = animatableList.size(); i < l; ++i){
            if(animatableList.get(i) != null){
                animatableList.get(i).advanceTime(passedTime);
            }
        }

    }
}
