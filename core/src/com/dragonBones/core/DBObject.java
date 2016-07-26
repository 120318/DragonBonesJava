package com.dragonBones.core;

import com.dragonBones.geoms.Matrix;
import com.dragonBones.geoms.Transform;

public class DBObject {

    public String name;
    public boolean inheritRotation;
    public boolean inheritScale;
    public Transform global = new Transform();
    public Transform origin = new Transform();
    public Transform offset = new Transform();
    public Matrix globalTransformMatrix = new Matrix();
    public Object userData;


    protected Bone parent;
    protected Armature armature;
    protected boolean visible;


    public DBObject(){
        visible = true;
        armature = null;
        parent = null;
        offset.scaleX = offset.scaleY = 1.f;
        userData = null;
    }
    public void clear(){
        armature = null;
        parent = null;
        userData = null;
    }

    public boolean getVisible(){
        return visible;
    }
    public void setVisible(boolean visible){
        this.visible = visible;
    }
    public void setArmature(Armature armature){
        if(this.armature != null){
            this.armature.removeDBObject(this);
        }
        this.armature = armature;
        if(this.armature != null){
            this.armature.addDBObject(this);
        }
    }
    public Armature getArmature(){
        return armature;
    }
    public Bone getParent(){
        return parent;
    }
    public void setParent(Bone bone){
        this.parent = bone;
    }
}
