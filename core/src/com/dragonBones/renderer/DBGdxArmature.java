package com.dragonBones.renderer;

import com.dragonBones.animation.Animation;
import com.dragonBones.core.Armature;
import com.dragonBones.events.EventManager;
import com.dragonBones.objects.ArmatureData;

public class DBGdxArmature extends Armature {

    public DBGdxArmature(ArmatureData armatureData, Animation animation, EventManager handler, DBDisplay display){
        super(armatureData, animation, handler, display);
    }

    public void setPosition(float x, float y) {
        ((DBDisplay)display).setPosition(x, y);
    }

    public DBDisplay getDBDisplay(){
        return (DBDisplay)display;
    }

    public DBEventManager getDBEventManager(){
        return (DBEventManager)eventManager;
    }
}
