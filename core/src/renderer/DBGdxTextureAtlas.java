package renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import dragonBones.textures.DBTextureAtlas;
import dragonBones.textures.DBTextureAtlasData;

/**
 * Created by jingzhao on 2016/2/28.
 */
public class DBGdxTextureAtlas extends DBTextureAtlas implements Disposable{
    public Texture texture;
    public DBGdxTextureAtlas(DBTextureAtlasData data){
        if(data != null){
            load(data);
        }
    }

    private void load(DBTextureAtlasData data) {
        this.textureAtlasData = data;
        if(data.texture == null) {
            texture = new Texture(data.textureFile, data.getGdxFormat(), false);
        }
        else{
            texture = data.texture;
        }
        texture.setFilter(data.minFilter, data.magFilter);
        texture.setWrap(data.wrapU, data.wrapV);
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
