package renderer;

import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dragonBones.DragonBones;
import dragonBones.core.Armature;
import dragonBones.core.Slot;

/**
 * Created by jingzhao on 2016/3/20.
 */
public class DragonBonesRenderer {
    private boolean preMultipliedAlpha = true;
    public void render(SpriteBatch batch, Armature armature){
        for(Slot slot : armature.getSlots()){
            DBGdxSlot gdxSlot = (DBGdxSlot)slot;
            DBDisplay display = gdxSlot.getGdxDisplay();
            if(display == null || !gdxSlot.isShowDisplay()){
                continue;
            }
            display.setParentTransform(((DBDisplay)armature.getDisplay()).getGlobalTransform());
            setBatchBlend(batch, slot.getBlendMode());
            if(display instanceof DBSpriteDisplay){
                if(display.isVisible()) {
                    batch.draw(((DBSpriteDisplay) display).getRegion().getTexture(), ((DBSpriteDisplay) display).getVertices(), 0, 20);
                }
            }
            else if(display instanceof DBDisplay){
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
