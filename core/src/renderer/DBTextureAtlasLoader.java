package renderer;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import dragonBones.parsers.XMLDataParser;
import dragonBones.parsers.XmlReader;
import dragonBones.textures.DBTextureAtlas;
import dragonBones.textures.DBTextureAtlasData;

import java.io.IOException;

/**
 * Created by jingzhao on 2016/3/13.
 */
public class DBTextureAtlasLoader extends SynchronousAssetLoader<DBTextureAtlas, DBTextureAtlasLoader.DBTextureAtlasParameter>{
    private DBTextureAtlasData data;

    private XMLDataParser parser = new XMLDataParser();

    public DBTextureAtlasLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public DBTextureAtlas load(AssetManager assetManager, String fileName, FileHandle file, DBTextureAtlasParameter parameter) {
        data.texture = assetManager.get(data.textureFile.path().replaceAll("\\\\", "/"), Texture.class);
        return new DBGdxTextureAtlas(data);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle atlasFile, DBTextureAtlasParameter parameter) {
        if(parameter != null) {
            data = parser.parseTextureAtlasData(atlasFile, parameter.scale);
            data.magFilter = parameter.magFilter;
            data.minFilter = parameter.minFilter;
            data.wrapU = parameter.wrapU;
            data.wrapV = parameter.wrapV;
        }
        else{
            data = parser.parseTextureAtlasData(atlasFile, 1.0f);
        }
        Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
        dependencies.add(new AssetDescriptor(data.textureFile, Texture.class));
        return dependencies;
    }

    static public class DBTextureAtlasParameter extends AssetLoaderParameters<DBTextureAtlas>{

        public float scale = 1.0f;
        public Texture.TextureFilter minFilter = Texture.TextureFilter.Linear;
        public Texture.TextureFilter magFilter = Texture.TextureFilter.Linear;
        public Texture.TextureWrap wrapU = Texture.TextureWrap.ClampToEdge;
        public Texture.TextureWrap wrapV = Texture.TextureWrap.ClampToEdge;

        public DBTextureAtlasParameter () {
        }

        public DBTextureAtlasParameter (float scale) {
            this.scale = scale;
        }
    }
}
