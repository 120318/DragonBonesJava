package com.dragonBones.textures;


import com.dragonBones.geoms.Rectangle;

public class DBTextureData {
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean rotated;
    public Rectangle frame;
    public String name;
    public DBTextureData(){
        rotated = false;
    }
    public DBTextureData(DBTextureData dbTextureData){
        rotated = dbTextureData.rotated;
        name = dbTextureData.name;
        if (dbTextureData.frame != null) {
            frame = new Rectangle(dbTextureData.frame);
        }
    }
}
