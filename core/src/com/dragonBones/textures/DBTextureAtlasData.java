package com.dragonBones.textures;

import com.dragonBones.DragonBones;

import java.util.ArrayList;
import java.util.List;

public class DBTextureAtlasData {
    public String name;
    public boolean autoSearch;
    public String imagePath;
    public DragonBones.PixelFormat format;
    public List<DBTextureData> textureDataList;


    public DBTextureAtlasData(){
        autoSearch = false;
        name = null;
        format = DragonBones.PixelFormat.AUTO;
        textureDataList = new ArrayList<DBTextureData>();
    }
    public DBTextureAtlasData(DBTextureAtlasData dbTextureAtlasData){
        autoSearch = dbTextureAtlasData.autoSearch;
        name = dbTextureAtlasData.name;
        textureDataList = new ArrayList<DBTextureData>();
        for (int i = 0, l = dbTextureAtlasData.textureDataList.size(); i < l; ++i) {
            DBTextureData dbTextureData = new DBTextureData(dbTextureAtlasData.textureDataList.get(i));
            textureDataList.add(dbTextureData);
        }
    }

    public DBTextureData getTextureData(String textureName) {
        for (int i = 0, l = textureDataList.size(); i < l; ++i) {
            if (textureDataList.get(i).name.equals(textureName))
            {
                return textureDataList.get(i);
            }
        }
        return null;
    }
    public DragonBones.PixelFormat getFormat(){
        return format;
    }


}
