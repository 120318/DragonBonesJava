package com.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dragonBones.renderer.DBGdxArmature;
import com.dragonBones.renderer.DBGdxFactory;
import com.dragonBones.renderer.DragonBonesRenderer;

/**
 * Created by liujingzhao on 6/7/16.
 */
public class TrimTextureTest extends ApplicationAdapter{
    OrthographicCamera camera;
    SpriteBatch batch;
    DragonBonesRenderer renderer;

    DBGdxArmature originArmature;
    DBGdxArmature trimArmature;

    @Override
    public void create() {
        super.create();
        super.create();
        camera  = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        batch = new SpriteBatch();
        renderer = new DragonBonesRenderer();

        DBGdxFactory factory = DBGdxFactory.getInstance();

        factory.loadDragonBonesData(Gdx.files.internal("trimTest/skeleton.xml"), "TrimBody");
        factory.loadTextureAtlas(Gdx.files.internal("trimTest/texture.xml"), "originTexture");
        factory.loadTextureAtlas(Gdx.files.internal("trimTest/texture1.xml"), "trimTexture");

        originArmature = factory.buildArmature("cdKeep", null, null, "TrimBody", "originTexture");
        originArmature.setPosition(Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() / 2);
        originArmature.getAnimation().gotoAndPlay("mv");

        trimArmature = factory.buildArmature("cdKeep", null, null, "TrimBody", "trimTexture");
        trimArmature.setPosition(Gdx.graphics.getWidth() / 2 + 150, Gdx.graphics.getHeight() / 2);
        trimArmature.getAnimation().gotoAndPlay("mv");
    }

    @Override
    public void render() {
        super.render();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        originArmature.advanceTime(Gdx.graphics.getDeltaTime());
        trimArmature.advanceTime(Gdx.graphics.getDeltaTime());

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        renderer.render(batch, originArmature);
        renderer.render(batch, trimArmature);
        batch.end();
    }
}
