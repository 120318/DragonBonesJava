package com.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.dragonBones.core.Bone;
import com.dragonBones.renderer.DBGdxArmature;
import com.dragonBones.renderer.DBGdxFactory;
import com.dragonBones.renderer.DBGdxSpriteDisplay;
import com.dragonBones.renderer.DragonBonesRenderer;

/**
 * Created by liujingzhao on 6/5/16.
 */
public class DemoChaseStarling extends ApplicationAdapter{

    OrthographicCamera camera;
    SpriteBatch batch;
    DragonBonesRenderer renderer;

    DBGdxArmature armature;
    DBGdxSpriteDisplay starlingBird;

    Bone head;
    Bone armR;
    Bone armL;
    int moveDirection;
    float speedX;
    float touchX;
    float touchY;
    float r;
    float distance;
    boolean isChasing;

    final int footY = 100;

    @Override
    public void create() {
        super.create();
        camera  = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        batch = new SpriteBatch();
        renderer = new DragonBonesRenderer();

        DBGdxFactory factory = DBGdxFactory.getInstance();

        factory.loadDragonBonesData(Gdx.files.internal("dragon/skeleton.xml"), "Dragon");
        factory.loadTextureAtlas(Gdx.files.internal("dragon/texture.xml"), "Dragon");
        armature = factory.buildArmature("Dragon");
        armature.getDBDisplay().setSize(360, 400);
        armature.setPosition(0, footY);

        Texture birdTexture = new Texture(Gdx.files.internal("starling.png"));
        starlingBird = new DBGdxSpriteDisplay(birdTexture, new Rectangle(0, 0, birdTexture.getWidth(), birdTexture.getHeight()), false);

        head = armature.getBone("head");
        armR = armature.getBone("armUpperR");
        armL = armature.getBone("armUpperL");

        Gdx.input.setInputProcessor(new InputAdapter(){
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                updatePosition(screenX, Gdx.graphics.getHeight() - screenY);
                if(!isChasing){
                    isChasing = true;
                    update();
                }
                return super.touchDown(screenX, screenY, pointer, button);
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                updatePosition(screenX, Gdx.graphics.getHeight() - screenY);
                return super.touchDragged(screenX, screenY, pointer);
            }
        });
    }

    private void updatePosition(float x, float y){
        this.touchX = x;
        this.touchY = y;
        starlingBird.setPosition(x, y);
    }

    private void checkDistance(){
        this.distance = armature.getDBDisplay().getX() - this.touchX;
        if(distance < 150) {
            updateBehavior(1);
        }
        else if(distance > 190){
            updateBehavior(-1);
        }
        else{
            updateBehavior(0);
        }
    }

    private void updateBehavior(int direction){
        if(moveDirection == direction){
            return;
        }
        this.moveDirection = direction;
        if(moveDirection == 0){
            speedX = 0;
            armature.getAnimation().gotoAndPlay("stand");
        }
        else{
            speedX = 6 * moveDirection;
            armature.getAnimation().gotoAndPlay("walk");
        }
    }

    private void updateMove(){
        if(speedX != 0){
            armature.getDBDisplay().setX(armature.getDBDisplay().getX() + speedX);
            if(armature.getDBDisplay().getX() < 0){
                armature.getDBDisplay().setX(0);
            }
            else if(armature.getDBDisplay().getX() > Gdx.graphics.getWidth()){
                armature.getDBDisplay().setX(Gdx.graphics.getWidth());
            }
        }
    }

    private void updateBones(){
        float width = armature.getDBDisplay().getWidth();
        float height = armature.getDBDisplay().getHeight();
        this.r = MathUtils.PI + MathUtils.atan2(armature.getDBDisplay().getY() + height / 2 - touchY,
                touchX - armature.getDBDisplay().getX());
        if(r > MathUtils.PI){
            this.r -= MathUtils.PI2;
        }
        head.offset.setRotation(r * 0.3f);
        armR.offset.setRotation(r * 0.8f);
        armL.offset.setRotation(r * 1.5f);

        starlingBird.setRotation(r * 0.2f * (180 / MathUtils.PI));
    }
    private void update(){
        checkDistance();
        updateMove();
        updateBones();
    }
    @Override
    public void render() {
        super.render();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update();
        armature.advanceTime(Gdx.graphics.getDeltaTime());

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        renderer.render(batch, armature);
        drawBird(batch);
        batch.end();
    }

    private void drawBird(SpriteBatch batch){
        batch.draw(starlingBird.getRegion().getTexture(), starlingBird.getVertices(), 0, 20);
    }

}
