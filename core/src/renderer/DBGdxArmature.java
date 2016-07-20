package renderer;

import dragonBones.animation.Animation;
import dragonBones.core.Armature;
import dragonBones.events.EventManager;
import dragonBones.objects.ArmatureData;

import java.beans.EventHandler;

/**
 * Created by jingzhao on 2016/2/28.
 */
public class DBGdxArmature extends Armature{

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
