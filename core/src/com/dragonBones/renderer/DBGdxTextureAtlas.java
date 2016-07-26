package com.dragonBones.renderer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.dragonBones.textures.DBTextureAtlas;
import com.dragonBones.textures.DBTextureAtlasData;
import com.dragonBones.DragonBones;

public class DBGdxTextureAtlas extends DBTextureAtlas implements Disposable{

    public static Pixmap.Format getGdxFormat(DragonBones.PixelFormat format) {
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

    private Texture texture;

    public DBGdxTextureAtlas(DBTextureAtlasData data, Texture texture){
        if(data != null && texture != null){
            load(data, texture);
        }
    }
    public DBGdxTextureAtlas(DBTextureAtlasData data, FileHandle textureFile){
        if(data != null && textureFile != null){
            texture = new Texture(textureFile, getGdxFormat(data.getFormat()), false);
            load(data, texture);
        }
    }
    private void load(DBTextureAtlasData data, Texture texture) {
        this.textureAtlasData = data;
        this.texture = texture;
    }

    public void reloadTexture() {

    }

    public Texture getTexture() {
        return texture;
    }

    @Override
    public void dispose() {
        texture.dispose();
        texture = null;
    }
}
