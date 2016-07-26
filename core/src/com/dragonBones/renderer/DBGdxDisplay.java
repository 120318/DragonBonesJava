package com.dragonBones.renderer;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.Vector2;

public class DBGdxDisplay {

    protected Affine2 transform = new Affine2();
    protected Affine2 parentTransform = new Affine2();
    protected Affine2 globalTransform = new Affine2();

    protected Vector2 position = new Vector2();
    protected Vector2 anchorPoint = new Vector2();

    protected float width, height;
    protected float scaleX = 1.0f, scaleY = 1.0f;
    protected float skewX = 0, skewY = 0;
    protected float rotationX = 0, rotationY = 0;
    protected boolean visible;
    protected boolean dirty = false;

    private Object userData;

    public DBGdxDisplay(){

    }

    public void setX(float x){
        position.x = x;
        dirty = true;
    }
    public float getX(){
        return position.x;
    }
    public void setY(float y){
        position.y = y;
        dirty = true;
    }
    public float getY(){
        return position.y;
    }
    public void setPosition(float x, float y){
        position.set(x, y);
        dirty = true;
    }
    public Vector2 getPosition(){
        return position;
    }
    public void setScale(float scaleX, float scaleY) {
        if (this.scaleX == scaleX && this.scaleY == scaleY) {
            return;
        }
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        dirty = true;
    }
    public void setScaleX(float scaleX){
        if(this.scaleX == scaleX){
            return;
        }
        this.scaleX = scaleX;
        dirty = true;
    }
    public void setScaleY(float scaleY){
        if(this.scaleY == scaleY){
            return;
        }
        this.scaleY = scaleY;
        dirty = true;
    }
    public void setScale(float scale){
        if(scaleX == scale && scaleY == scale){
            return;
        }
        scaleX = scaleY = scale;
        dirty = true;
    }
    public float getScaleX(){
        return scaleX;
    }
    public float getScaleY(){
        return scaleY;
    }

    public float getRotation(){
        if(rotationX != rotationY){
            throw new IllegalArgumentException("rotaionX != rotationY, don't know which one to return");
        }
        return rotationX;
    }

    public Object getUserData(){
        return userData;
    }

    public void setUserData(Object userData){
        this.userData = userData;
    }

    public void setSize(float width, float height){
        if(this.width == width && this.height == height){
            return;
        }
        this.width = width;
        this.height = height;
        dirty = true;
    }
    public void setWidth(float width){
        if(this.width == width){
            return;
        }
        this.width = width;
        dirty = true;
    }
    public float getWidth(){
        return width;
    }
    public void setHeight(float height){
        if(this.height == height){
            return;
        }
        this.height = height;
        dirty = true;
    }
    public float getHeight(){
        return height;
    }
    public void setSkewX(float skewX){
        if(this.skewX == skewX){
            return;
        }
        this.skewX = skewX;
        dirty = true;
    }
    public float getSkewX(){
        return skewX;
    }
    public void setSkewY(float skewY){
        if(this.skewY == skewY){
            return;
        }
        this.skewY = skewY;
    }
    public void setRotation(float rotation){
        if(rotationX == rotation){
            return;
        }
        rotationX = rotationY = rotation;
        dirty = true;
    }

    public void setRotationSkewX(float rotationSkewX){
        if(rotationX == rotationSkewX){
            return;
        }
        rotationX = rotationSkewX;
        dirty = true;
    }

    public void setRotationSkewY(float rotationSkewY){
        if(rotationY == rotationSkewY){
            return;
        }
        rotationY = rotationSkewY;
        dirty = true;
    }

    public Affine2 calTransformMatrix() {
        if(dirty){
            float x = position.x;
            float y = position.y;
            boolean isSkew = skewX != 0 || skewY != 0;
            float anchorX = anchorPoint.x;
            float anchorY = anchorPoint.y;
            if(!isSkew && !anchorPoint.equals(Vector2.Zero)){
                x += -anchorX;
                y += -anchorY;
            }
            transform.setToTrnRotScl(x + anchorX, y + anchorY,
                    rotationX == rotationY ? -rotationX : 0, scaleX, scaleY);
            if(rotationX != rotationY){
                float radianX = -MathUtils.degRad * rotationX;
                float radianY = -MathUtils.degRad * rotationY;
                float cosX = MathUtils.cos(radianX);
                float sinX = MathUtils.sin(radianX);
                float cosY = MathUtils.cos(radianY);
                float sinY = MathUtils.sin(radianY);
                float m00 = transform.m00;
                float m10 = transform.m10;
                float m01 = transform.m01;
                float m11 = transform.m11;
                transform.m00 = cosY * m00 - sinX * m10;
                transform.m01 = cosY * m01 - sinX * m11;
                transform.m10 = sinY * m00 + cosX * m10;
                transform.m11 = sinY * m01 + cosX * m11;
            }
            transform.translate(-anchorX, -anchorY);
            if(isSkew){
                transform.shear(skewX, skewY);
                if(!anchorPoint.equals(Vector2.Zero)){
                    transform.m02 += transform.m00 * -anchorPoint.x + transform.m01 * -anchorPoint.y;
                    transform.m12 += transform.m10 * -anchorPoint.x + transform.m11 * -anchorPoint.y;
                }
            }
            dirty = false;
        }
        return transform;
    }

    public void setTransforMatrix(Affine2 transform){
        this.transform = transform;
        dirty = true;
    }
    public void setParentTransform(Affine2 parentTransform){
        if(parentTransform != this.parentTransform){
            this.parentTransform = parentTransform;
        }
        dirty = true;
    }
    public Affine2 getGlobalTransform(){
        calTransformMatrix();
        globalTransform.set(transform);
        if(parentTransform != null){
            globalTransform.preMul(parentTransform);
        }
        return globalTransform;
    }
    public void setColor (float r, float g, float b, float a) {
    }

    public void setColor (float color) {

    }
    public void setVisible(boolean visible){
        if(visible != this.visible){
            this.visible = visible;
            if(this.visible){
                dirty = true;
            }
        }
    }
    public boolean isVisible(){
        return visible;
    }
    public void setAnchor(float x, float y) {
        anchorPoint.set(x * width, y * height);
        dirty = true;
    }

    public void translate(Affine2 transformAffineMatrix) {
        this.transform.mul(transformAffineMatrix);
        dirty = true;
    }
}
