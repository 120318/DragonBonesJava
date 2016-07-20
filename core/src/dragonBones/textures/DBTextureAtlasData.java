package dragonBones.textures;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import dragonBones.DragonBones;
import dragonBones.parsers.XmlReader;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingzhao on 2016/2/27.
 */
public class DBTextureAtlasData {
    public FileHandle textureFile;
    public Texture texture;
    public String name;
    public boolean autoSearch;
    public DragonBones.PixelFormat format;
    public List<DBTextureData> textureDataList;

    public Texture.TextureFilter minFilter = Texture.TextureFilter.Linear;
    public Texture.TextureFilter magFilter = Texture.TextureFilter.Linear;
    public Texture.TextureWrap wrapU = Texture.TextureWrap.ClampToEdge;
    public Texture.TextureWrap wrapV = Texture.TextureWrap.ClampToEdge;

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
        textureFile = dbTextureAtlasData.textureFile;
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

    public Pixmap.Format getGdxFormat() {
        Pixmap.Format pixmapFormat = Pixmap.Format.RGBA8888;
        switch(format){
            case RGB565:
                pixmapFormat = Pixmap.Format.RGB565;
                break;
            case RGB888:
                pixmapFormat = Pixmap.Format.RGB888;
                break;
            case RGBA4444:
                pixmapFormat = Pixmap.Format.RGBA4444;
                break;
            case RGBA8888:
                pixmapFormat = Pixmap.Format.RGBA8888;
                break;
            case AUTO:

        }
        return pixmapFormat;
    }

}
