package com.dragonBones.renderer;

import com.dragonBones.animation.Animation;
import com.dragonBones.core.Armature;
import com.dragonBones.events.EventManager;
import com.dragonBones.objects.ArmatureData;

public class DBGdxArmature extends Armature {

    public DBGdxArmature(ArmatureData armatureData, Animation animation, EventManager handler, DBGdxDisplay display){
        super(armatureData, animation, handler, display);
    }

    public void setPosition(float x, float y) {
        ((DBGdxDisplay)display).setPosition(x, y);
    }

    public DBGdxDisplay getDBDisplay(){
        return (DBGdxDisplay)display;
    }

    public DBEventManager getDBEventManager(){
        return (DBEventManager)eventManager;
    }
}
