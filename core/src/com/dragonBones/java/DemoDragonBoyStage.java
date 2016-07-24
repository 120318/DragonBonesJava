package com.dragonBones.java;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import renderer.*;

/**
 * Created by jingzhao on 2016/2/29.
 */
public class DemoDragonBoyStage extends Stage {
    DBGdxArmature armature;
    DragonBonesRenderer renderer = new DragonBonesRenderer();
    private int x = 480;
    private boolean isWalk = false;
    private AssetManager assetManager = new AssetManager();
    public DemoDragonBoyStage(){
        super(800, 800, false);
        DBGdxFactory factory = DBGdxFactory.getInstance();

        factory.loadDragonBonesData(Gdx.files.internal("DragonBoy/skeleton.xml"), "DragonBoy");
        factory.loadTextureAtlas(Gdx.files.internal("DragonBoy/texture.xml"), "DragonBoy");
        DBGdxTextureAtlas atlas = (DBGdxTextureAtlas)factory.getTextureAtlas("DragonBoy");
        armature = factory.buildArmature("dragonBoy");
        armature.getAnimation().gotoAndPlay("stand");
        armature.setPosition(480, 200);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        armature.advanceTime(delta);
    }

    @Override
    public void draw() {
        super.draw();
        SpriteBatch batch = getSpriteBatch();
        batch.begin();
        renderer.render(batch, armature);
        batch.end();
    }
}
