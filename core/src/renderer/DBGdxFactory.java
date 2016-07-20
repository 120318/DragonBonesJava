package renderer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import dragonBones.animation.Animation;
import dragonBones.core.Armature;
import dragonBones.core.Slot;
import dragonBones.factories.BaseFactory;
import dragonBones.objects.*;
import dragonBones.parsers.XMLDataParser;
import dragonBones.textures.DBTextureAtlas;
import dragonBones.textures.DBTextureAtlasData;
import dragonBones.textures.DBTextureData;

/**
 * Created by jingzhao on 2016/2/28.
 */
public class DBGdxFactory extends BaseFactory {

    private static DBGdxFactory instance;
    private float scale = 1.0f;

    private XMLDataParser parser;
    public static DBGdxFactory getInstance(){
        if(instance == null){
            instance = new DBGdxFactory();
        }
        return instance;
    }
    private DBGdxFactory(){
        parser = new XMLDataParser();
    }
    public void setScale(float scale){
        this.scale = scale;
    }
    @Override
    public DBGdxArmature buildArmature(String armatureName) {
        return (DBGdxArmature)super.buildArmature(armatureName);
    }

    @Override
    public DBGdxArmature buildArmature(String armatureName, String dragonBonesName) {
        return (DBGdxArmature)super.buildArmature(armatureName, dragonBonesName);
    }

    @Override
    public DBGdxArmature buildArmature(String armatureName, String skinName, String animationName, String dragonBonesName, String textureAtlasName) {
        return (DBGdxArmature)super.buildArmature(armatureName, skinName, animationName, dragonBonesName, textureAtlasName);
    }



    public DragonBonesData loadDragonBonesData(FileHandle dragonBonesFile, String name){
        DragonBonesData existDragonBonesData = getDragonBonesData(name);
        if (existDragonBonesData != null) {
            return existDragonBonesData;
        }
        DragonBonesData dragonBonesData = parser.parseDragonBonesData(dragonBonesFile, scale);
        addDragonBonesData(dragonBonesData, name);
        return dragonBonesData;
    }
    public DBTextureAtlas loadTextureAtlas(FileHandle textureAtlasFile, String name){
        DBTextureAtlas existTextureAtlas = getTextureAtlas(name);
        if (existTextureAtlas != null) {
            refreshTextureAtlasTexture(name == null ? existTextureAtlas.textureAtlasData.name : name);
            return existTextureAtlas;
        }
        DBGdxTextureAtlas textureAtlas = new DBGdxTextureAtlas(parser.parseTextureAtlasData(textureAtlasFile, scale));
        addTextureAtlas(textureAtlas, name);
        refreshTextureAtlasTexture(name == null ? textureAtlas.textureAtlasData.name : name);
        return textureAtlas;
    }
    public void refreshTextureAtlasTexture(String name){
        for (String key : textureAtlasMap.keySet()) {
            DBGdxTextureAtlas textureAtlas = (DBGdxTextureAtlas)textureAtlasMap.get(key);
            /*const */DBTextureAtlasData textureAtlasData = textureAtlas.textureAtlasData;
            if (key.equals(name)) {
                textureAtlas.reloadTexture();
            }
        }
    }
    public void refreshAllTextureAtlasTexture(){
        for (String key : textureAtlasMap.keySet()) {
            DBGdxTextureAtlas textureAtlas = (DBGdxTextureAtlas)textureAtlasMap.get(key);
            /*const */DBTextureAtlasData textureAtlasData = textureAtlas.textureAtlasData;
            textureAtlas.reloadTexture();
        }
    }
    public boolean hasDragonBones(/*const*/ String skeletonName, /*const*/ String armatureName, /*const*/ String animationName){
        DragonBonesData dragonbonesData = getDragonBonesData(skeletonName);

        if (dragonbonesData == null) {
            return false;
        }
        if (armatureName != null)
        {
            ArmatureData armatureData = dragonbonesData.getArmatureData(armatureName);

            if (armatureData == null) {
                return false;
            }
            if (animationName != null) {
                AnimationData animationData = armatureData.getAnimationData(animationName);
                return animationData != null;
            }
        }
        return true;
    }
    @Override
    protected Armature generateArmature(ArmatureData armatureData) {
        Animation animation = new Animation();
        // sprite
        DBDisplay display = new DBDisplay();
        // in libgdx it doesn't need?
        /*display->setCascadeColorEnabled(true);
        display->setCascadeOpacityEnabled(true);
        display->retain();*/
        // eventDispatcher
        /*DBGdxEventDispatcher eventDispatcher = new DBGdxEventDispatcher();
        eventDispatcher.eventDispatcher = new cocos2d::EventDispatcher();
        eventDispatcher->eventDispatcher->setEnabled(true);*/
        // armature
        return new DBGdxArmature(armatureData, animation, new DBEventManager(), display);
    }

    @Override
    protected Slot generateSlot(SlotData slotData) {
        return new DBGdxSlot(slotData);
    }

    @Override
    protected Object generateDisplay(DBTextureAtlas textureAtlas, DBTextureData textureData, DisplayData displayData) {
        DBGdxTextureAtlas dbGdxTextureAtlas = (DBGdxTextureAtlas)(textureAtlas);

        if (dbGdxTextureAtlas == null || textureData == null) {
            return null;
        }
        Texture texture = dbGdxTextureAtlas.getTexture();
        assert(texture != null);
        int x = textureData.x;
        int y = textureData.y;
        boolean rotated = textureData.rotated;
        int width = rotated ? textureData.height : textureData.width;
        int height = rotated ? textureData.width : textureData.height;
        Vector2 offset = new Vector2();
        Vector2 originSize = new Vector2(width, height);

        DBSpriteDisplay display = null;
        if (textureData.frame != null) {
//             spriteframe !!!!!!!!!!!!!!!!!
            float frameX = -textureData.frame.x;
            float frameY = -textureData.frame.y;
            originSize.x = textureData.frame.width;
            originSize.y = textureData.frame.height;
            // offset = trimed center - origin center
            // y use cocos2d coordinates
            offset.x = (width - originSize.x) * 0.5f + frameX;
            offset.y = (originSize.y - height) * 0.5f - frameY;



            DBSpriteDisplay sprite = new DBSpriteDisplay(texture, rect, textureData.rotated, offset, originSize);
        }
        else
        {
            display = new DBSpriteDisplay(texture, x, y, width, height);
        }
        // sprite

        /*display->setCascadeColorEnabled(true);
        display->setCascadeOpacityEnabled(true);
        display->retain();*/
        float pivotX = 0.f;
        float pivotY = 0.f;

        if (displayData != null) {
            pivotX = displayData.pivot.x;
            pivotY = displayData.pivot.y;
        }
        display.setSize(originSize.x, originSize.y);
        display.setAnchor(pivotX / originSize.x, 1 - pivotY / originSize.y);
        return display;
    }
}
