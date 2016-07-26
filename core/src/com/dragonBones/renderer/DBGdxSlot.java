package com.dragonBones.renderer;

import com.badlogic.gdx.math.MathUtils;
import com.dragonBones.objects.SlotData;
import com.dragonBones.core.Slot;

public class DBGdxSlot extends Slot {

    public DBGdxSlot(SlotData slotData) {
        super(slotData);
    }

    public DBGdxDisplay getGdxDisplay(){
        return (DBGdxDisplay)display;
    }

    @Override
    public void updateDisplayVisible(boolean visible) {
        if(display != null && parent != null){
            ((DBGdxDisplay)display).setVisible(visible);
        }
    }

    @Override
    public void updateDisplayColor(int aOffset, int rOffset, int gOffset, int bOffset, float aMultiplier, float rMultiplier, float gMultiplier, float bMultiplier) {
        if(display != null){
            DBGdxDisplay gdxDisplay = (DBGdxDisplay)display;
            super.updateDisplayColor(aOffset, rOffset, gOffset, bOffset, aMultiplier, rMultiplier, gMultiplier, bMultiplier);
            gdxDisplay.setColor(rMultiplier, gMultiplier, bMultiplier, aMultiplier);
        }
    }

    @Override
    public void updateDisplayTransform() {
        if(display != null){
            DBGdxDisplay gdxDisplay = (DBGdxDisplay)display;
            gdxDisplay.setScaleX(global.scaleX);
            gdxDisplay.setScaleY(global.scaleY);
            gdxDisplay.setRotationSkewX(MathUtils.radDeg * global.skewX);
            gdxDisplay.setRotationSkewY(MathUtils.radDeg * global.skewY);
            gdxDisplay.setPosition(global.x, -global.y);
        }
    }
}
