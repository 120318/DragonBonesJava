package com.demo;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dragonBones.DragonBones;
import com.dragonBones.events.EventData;
import com.dragonBones.renderer.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujingzhao on 6/5/16.
 */
public class DemoDragonBoy extends ApplicationAdapter{
    OrthographicCamera camera;
    SpriteBatch batch;
    DragonBonesRenderer renderer;

    DBGdxArmature armature;

    boolean isLeft;
    boolean isRight;
    boolean isJump;
    boolean isSquat;
    int moveDir;
    float speedX;
    float speedY;
    int currentClothIndex;
    List<String> clothesList;

    @Override
    public void create() {

        super.create();
        camera  = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        batch = new SpriteBatch();
        renderer = new DragonBonesRenderer();

        DBGdxFactory factory = DBGdxFactory.getInstance();

        factory.loadDragonBonesData(Gdx.files.internal("DragonBoy/skeleton.xml"), "DragonBoy");
        factory.loadTextureAtlas(Gdx.files.internal("DragonBoy/texture.xml"), "DragonBoy");
        armature = factory.buildArmature("dragonBoy");
        armature.setPosition(480, 200);
        ((DBDisplay)armature.getDisplay()).setScale(0.5f);
        armature.getDBEventManager().addHandle(new DBEventHandler(){
            @Override
            public void handle(EventData eventData) {
                super.handle(eventData);
                if(eventData.animationState != null){
                    Gdx.app.log("Animation name", eventData.animationState.name);
                    Gdx.app.log("Event Type", eventData.getType().toString());
                }
            }
        });

        currentClothIndex = 0;
        clothesList = new ArrayList<String>();
        clothesList.add("parts/clothes1");
        clothesList.add("parts/clothes2");
        clothesList.add("parts/clothes3");
        clothesList.add("parts/clothes4");

        Gdx.input.setInputProcessor(new InputAdapter(){
            @Override
            public boolean keyUp(int keycode) {
                switch(keycode){

                    case Input.Keys.A:
                        move(-1, false);
                        break;
                    case Input.Keys.D:
                        move(1, false);
                        break;

                    case Input.Keys.S:
                        squat(false);
                        break;
                }


                return super.keyUp(keycode);
            }

            @Override
            public boolean keyDown(int keycode) {
                switch(keycode){
                    case Input.Keys.SPACE:
                        changeClothes();
                        break;
                    case Input.Keys.A:
                        move(-1, true);
                        break;
                    case Input.Keys.D:
                        move(1, true);
                        break;
                    case Input.Keys.S:
                        squat(true);
                        break;
                    case Input.Keys.W:
                        jump();
                        break;
                }
                return super.keyDown(keycode);
            }
        });
        updateAnimation();
    }

    @Override
    public void render() {
        super.render();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        updateSpeed();
        armature.advanceTime(Gdx.graphics.getDeltaTime());

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        renderer.render(batch, armature);
        batch.end();
    }
    private void squat(boolean isSquat){
        if(this.isSquat == isSquat){
            return;
        }
        this.isSquat = isSquat;
        updateAnimation();
    }
    private void jump(){
        if(this.isJump){
            return;
        }
        this.isJump = true;
        this.speedY = 24.f;
        armature.getAnimation().gotoAndPlay("jump");
    }
    private void updateSpeed(){
        float timeScale = armature.getAnimation().getTimeScale();
        float x = armature.getDBDisplay().getX();
        float y = armature.getDBDisplay().getY();

        if (speedX != 0) {
            x += speedX * timeScale;

            if (x < 0) {
                x = 0.f;
            }
            else if (x > 960) {
                x = 960.f;
            }
        }

        if (isJump) {
            float speedG = -1.f * timeScale;

            if (speedY >= 0 && speedY + speedG < 0) {
                armature.getAnimation().gotoAndPlay("fall", 0.2f);
            }

            speedY += speedG;
            y += speedY * timeScale;

            if (y < 200) {
                y = 200.f;
                isJump = false;
                speedY = 0.f;
                speedX = 0.f;
                armature.getAnimation().gotoAndPlay("fallEnd");
            }
        }

        armature.getDBDisplay().setPosition(x, y);
    }


    private void changeClothes(){
        ++currentClothIndex;
        if(currentClothIndex > clothesList.size()){
            currentClothIndex = 0;
        }
        if (currentClothIndex < clothesList.size()) {
            armature.getSlot("clothes").setDisplay(DBGdxFactory.getInstance().getTextureDisplay(
                    clothesList.get(currentClothIndex), null, null), DragonBones.DisplayType.DT_IMAGE);
        }
        else{
            armature.getSlot("clothes").setDisplay(null, DragonBones.DisplayType.DT_IMAGE);
        }
    }

    private void move(int dir, boolean isDown){
        if(dir < 0){
            isLeft = isDown;
        }
        else if(dir > 0){
            isRight = isDown;
        }
        int moveDir = 0;
        if(isLeft && isRight){
            moveDir = this.moveDir;
        }
        else if(isLeft){
            moveDir = -1;
        }
        else if(isRight){
            moveDir = 1;
        }
        else{
            moveDir = 0;
        }
        if(this.moveDir == moveDir){
            return;
        }
        this.moveDir = moveDir;
        updateAnimation();
    }

    private void updateAnimation(){
        if(isJump){

        }
        else if(isSquat){
            this.speedX = 0.f;
            armature.getAnimation().gotoAndPlay("squat");
        }
        else if(this.moveDir != 0){
            this.speedX = this.moveDir * 6.f;
            armature.getAnimation().gotoAndPlay("walk");
            armature.getDBDisplay().setScaleX(-this.moveDir * 0.5f);
        }
        else{
            this.speedX = 0.f;
            armature.getAnimation().gotoAndPlay("stand");
        }
    }

}
