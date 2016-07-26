package com.dragonBones.renderer;

import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dragonBones.core.Armature;
import com.dragonBones.DragonBones;
import com.dragonBones.core.Slot;

public class DragonBonesRenderer {
    private boolean preMultipliedAlpha = true;
    public void render(SpriteBatch batch, Armature armature){
        for(Slot slot : armature.getSlots()){
            DBGdxSlot gdxSlot = (DBGdxSlot)slot;
            DBGdxDisplay display = gdxSlot.getGdxDisplay();
            if(display == null || !gdxSlot.isShowDisplay()){
                continue;
            }
            display.setParentTransform(((DBGdxDisplay)armature.getDisplay()).getGlobalTransform());
            setBatchBlend(batch, slot.getBlendMode());
            if(display instanceof DBGdxSpriteDisplay){
                if(display.isVisible()) {
                    batch.draw(((DBGdxSpriteDisplay) display).getRegion().getTexture(), ((DBGdxSpriteDisplay) display).getVertices(), 0, 20);
                }
            }
            else if(display instanceof DBGdxDisplay){
                render(batch, gdxSlot.getChildArmature());
            }
        }
    }

    private void setBatchBlend(SpriteBatch batch, DragonBones.BlendMode blendMode){
        switch(blendMode){
            case BM_ADD:
                if(preMultipliedAlpha){
                    batch.setBlendFunction(GL11.GL_ONE, GL11.GL_ONE);
                }
                else{
                    batch.setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                }
        }
    }


}
