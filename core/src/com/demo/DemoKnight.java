package com.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dragonBones.DragonBones;
import com.dragonBones.core.Bone;
import com.dragonBones.core.Slot;
import com.dragonBones.events.EventData;
import com.dragonBones.renderer.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujingzhao on 6/5/16.
 */
public class DemoKnight extends ApplicationAdapter{
    OrthographicCamera camera;
    SpriteBatch batch;
    DragonBonesRenderer renderer;

    DBGdxArmature armature;
    DBGdxArmature armArmature;


    boolean isLeft;
    boolean isRight;
    boolean isJump;
    int moveDir;
    int weaponIndex;
    float speedX;
    float speedY;

    boolean isAttacking;
    boolean isComboAttack;
    int hitCount;

    List<String> weaponList;
    List<Integer> weaponLevelList;
    List<DBGdxDisplay> arrowList;

    DBEventHandler eventHandler;

    @Override
    public void create(){
        super.create();
        camera  = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        batch = new SpriteBatch();
        renderer = new DragonBonesRenderer();

        DBGdxFactory factory = DBGdxFactory.getInstance();

        factory.loadDragonBonesData(Gdx.files.internal("Knight/skeleton.xml"), "Knight");
        factory.loadTextureAtlas(Gdx.files.internal("Knight/texture.xml"), "Knight");
        armature = factory.buildArmature("knight");
        armArmature = (DBGdxArmature)armature.getSlot("armOutside").getChildArmature();
        armature.getAnimation().gotoAndPlay("stand");
        armature.setPosition(480, 200);

        initEventHandler();

        armArmature.getDBEventManager().addHandle(eventHandler);

        isLeft = false;
        isRight = false;
        isJump = false;
        moveDir = 0;
        weaponIndex = 0;
        speedX = 0.f;
        speedY = 0.f;
        weaponList = new ArrayList<String>();
        weaponList.add("sword");
        weaponList.add("pike");
        weaponList.add("axe");
        weaponList.add("bow");
        weaponLevelList = new ArrayList<Integer>();
        weaponLevelList.add(0);
        weaponLevelList.add(0);
        weaponLevelList.add(0);
        weaponLevelList.add(0);
        arrowList = new ArrayList<DBGdxDisplay>();
        isAttacking = false;
        isComboAttack = false;
        hitCount = 1;

        Gdx.input.setInputProcessor(new InputAdapter(){
            @Override
            public boolean keyDown(int keycode) {
                switch(keycode){
                    case Input.Keys.W:
                        jump();
                        break;
                    case Input.Keys.S:
                        upgradeWeaponLevel();
                        break;
                    case Input.Keys.A:
                        move(-1, true);
                        break;
                    case Input.Keys.D:
                        move(1, true);
                        break;
                    case Input.Keys.SPACE:
                        changeWeapon();
                        break;
                    case Input.Keys.K:
                        attack();
                        break;
                }
                return super.keyDown(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                switch(keycode){
                    case Input.Keys.W:
                        break;
                    case Input.Keys.S:
                        break;
                    case Input.Keys.A:
                        move(-1, false);
                        break;
                    case Input.Keys.D:
                        move(1, false);
                        break;
                }
                return super.keyUp(keycode);
            }
        });

        updateAnimation();

    }


    @Override
    public void render() {
        super.render();
        Gdx.gl.glClearColor(0.75f, 0.75f, 0.75f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        updateSpeed();
        updateArrows();
        armature.advanceTime(Gdx.graphics.getDeltaTime());

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        renderer.render(batch, armature);
        renderArrow(batch);
        batch.end();
    }

    private void renderArrow(SpriteBatch batch){
        for(DBGdxDisplay display : arrowList){
            batch.draw(((DBGdxSpriteDisplay) display).getRegion().getTexture(), ((DBGdxSpriteDisplay) display).getVertices(), 0, 20);
        }
    }

    private void initEventHandler() {
        eventHandler = new DBEventHandler(){
            @Override
            public void handle(EventData eventData) {
                switch (eventData.getType()){
                    case FADE_IN:
                        isComboAttack = false;
                        Gdx.app.log("animation fade in", eventData.animationState.name);
                        break;
                    case COMPLETE:
                        Gdx.app.log("animation complete", eventData.animationState.name);
                        if(isComboAttack){
                            armReady();
                        }
                        else{
                            isAttacking = false;
                            hitCount = 1;
                            isComboAttack = false;
                        }
                        break;
                    case ANIMATION_FRAME_EVENT:
                        if(eventData.frameLabel.equals("fire")){
                            Bone bowBone = armArmature.getBone("bow");
                            Vector2 resultVec = new Vector2();
                            resultVec = armArmature.getDBDisplay().getGlobalTransform().getTranslation(resultVec);
                            float r = 0.f;
                            if(armature.getDBDisplay().getScaleX() > 0){
                                r = MathUtils.degRad * -armArmature.getDBDisplay().getRotation() + bowBone.global.getRotation();
                            }
                            else{
                                r= MathUtils.degRad * -armArmature.getDBDisplay().getRotation() - bowBone.global.getRotation() + MathUtils.PI;
                            }
                            switch(weaponLevelList.get(weaponIndex)){
                                case 0:
                                    createArrow(r, resultVec);
                                    break;
                                case 1:
                                    createArrow(3.f / 180f * MathUtils.PI + r, resultVec);
                                    createArrow(-3.f / 180.f * MathUtils.PI + r, resultVec);
                                    break;
                                case 2:
                                    createArrow(6.f / 180.f * MathUtils.PI + r, resultVec);
                                    createArrow(r, resultVec);
                                    createArrow(-6.f / 180.f * MathUtils.PI + r, resultVec);
                                    break;
                            }
                            Gdx.app.log("frameEvent", eventData.frameLabel);
                        }
                        else if(eventData.frameLabel.equals("ready")){
                            isAttacking = false;
                            isComboAttack = true;
                            ++ hitCount;
                            Gdx.app.log("attack ready", eventData.animationState.name);
                        }
                        break;
                }
            }
        };
    }

    private void createArrow(float r, Vector2 vector2){
        DBGdxDisplay arrowDisplay = (DBGdxDisplay)DBGdxFactory.getInstance().getTextureDisplay(getWeaponName("arrow", 1), null, null);
        arrowDisplay.setPosition(vector2.x ,vector2.y);
        arrowDisplay.setRotation(MathUtils.radDeg * r);
        Vector2 speedVec = new Vector2();
        speedVec.x = MathUtils.cos(r) * 36;
        speedVec.y = -MathUtils.sin(r) * 36;
        arrowDisplay.setUserData(speedVec);
        arrowList.add(arrowDisplay);
    }

    private void updateArrows(){
        for(int i = 0, l = arrowList.size(); i < l; ++i){
            DBGdxDisplay arrowDisplay = arrowList.get(i);
            Vector2 speedVec = (Vector2)arrowDisplay.getUserData();
            if(arrowDisplay.getY() < -400){
                if(i == l - 1){
                    arrowList.remove(arrowDisplay);
                }
            }
            else{
                speedVec.y += -1.f;
                arrowDisplay.setPosition(arrowDisplay.getPosition().x + speedVec.x, arrowDisplay.getPosition().y + speedVec.y);
                arrowDisplay.setRotation(-MathUtils.radDeg * MathUtils.atan2(speedVec.y, speedVec.x));
            }
        }
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
                if (armature.getAnimation().hasAnimation("fall")) {
                    armature.getAnimation().gotoAndPlay("fall", 0.2f);
                }
            }

            speedY += speedG;
            y += speedY * timeScale;

            if (y < 200) {
                y = 200.f;
                isJump = false;
                speedY = 0.f;
                speedX = 0.f;
                armature.getDBDisplay().setRotation(0.f);
                updateAnimation();
            } else {
                armature.getDBDisplay().setRotation(-speedY * armature.getDBDisplay().getScaleX());
            }
        }

        armature.setPosition(x, y);
    }

    private void jump(){
        if(this.isJump){
            return;
        }
        this.isJump  =true;
        this.speedY = 24.f;
        armature.getAnimation().gotoAndPlay("jump");
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
        else if(moveDir != 0){
            speedX = moveDir * 6.f;
            armature.getAnimation().gotoAndPlay("run");
            armature.getDBDisplay().setScaleX(moveDir);
        }
        else{
            speedX = 0.f;
            armature.getAnimation().gotoAndPlay("stand");
        }
    }

    private void armReady(){
        String weaponName = weaponList.get(weaponIndex);
        String animationName = "ready_" + weaponName;
        armArmature.getAnimation().gotoAndPlay(animationName);
    }

    private void changeWeapon(){
        ++weaponIndex;
        if(weaponIndex >= weaponList.size()){
            weaponIndex = 0;
        }

        armReady();
    }

    private void upgradeWeaponLevel(){
        int weaponLevel = weaponLevelList.get(weaponIndex);
        ++weaponLevel;

        if (weaponLevel >= 3) {
            weaponLevel = 0;
        }

        weaponLevelList.set(weaponIndex, weaponLevel);
        String weaponName = weaponList.get(weaponIndex);

        int newWeaponLevel = weaponLevel + 1;
        switch (weaponIndex)
        {
            case 0:
            case 1:
            case 2:
            {
                Slot weaponSlot = armArmature.getSlot("weapon");
                weaponSlot.setDisplay(DBGdxFactory.getInstance().getTextureDisplay(
                        getWeaponName(weaponName, newWeaponLevel), null, null), DragonBones.DisplayType.DT_IMAGE);
                break;
            }

            case 3:
            {
                Slot bowSlot = armArmature.getSlot("bow");
                Slot bowBA = bowSlot.getChildArmature().getSlot("ba");
                Slot bowBB = bowSlot.getChildArmature().getSlot("bb");
                Slot bowArrow = bowSlot.getChildArmature().getSlot("arrow");
                Slot bowArrowB = bowSlot.getChildArmature().getSlot("arrowBackup");
                bowBA.setDisplay(DBGdxFactory.getInstance().getTextureDisplay(
                        getWeaponName(weaponName, newWeaponLevel), null, null), DragonBones.DisplayType.DT_IMAGE);
                bowBB.setDisplay(DBGdxFactory.getInstance().getTextureDisplay(
                        getWeaponName(weaponName, newWeaponLevel), null, null), DragonBones.DisplayType.DT_IMAGE);
                bowArrow.setDisplay(DBGdxFactory.getInstance().getTextureDisplay(
                        getWeaponName("arrow", newWeaponLevel), null, null), DragonBones.DisplayType.DT_IMAGE);
                bowArrowB.setDisplay(DBGdxFactory.getInstance().getTextureDisplay(
                        getWeaponName("arrow", newWeaponLevel), null, null), DragonBones.DisplayType.DT_IMAGE);
                break;
            }
        }
    }

    private String getWeaponName(String name, int level){
        return "knightFolder/" + name + "_" + level;
    }

    private void attack(){
        if(isAttacking){
            return;
        }
        this.isAttacking = true;
        String weaponName = weaponList.get(weaponIndex);
        armArmature.getAnimation().gotoAndPlay("attack_" + weaponName + "_" + hitCount);
    }
}
