package com.dragonBones.renderer;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.dragonBones.textures.DBTextureAtlas;
import com.dragonBones.textures.DBTextureAtlasData;
import com.dragonBones.parsers.XMLDataParser;

public class DBTextureAtlasLoader extends SynchronousAssetLoader<DBTextureAtlas, DBTextureAtlasLoader.DBTextureAtlasParameter>{
    private DBTextureAtlasData data;

    private XMLDataParser parser = new XMLDataParser();
    private Texture texture;
    public DBTextureAtlasLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public DBTextureAtlas load(AssetManager assetManager, String fileName, FileHandle file, DBTextureAtlasParameter parameter) {
        texture = assetManager.get(data.imagePath.replaceAll("\\\\", "/"), Texture.class);
        if(parameter != null) {
            texture.setFilter(parameter.minFilter, parameter.magFilter);
            texture.setWrap(parameter.wrapU, parameter.wrapV);
        }
        return new DBGdxTextureAtlas(data, texture);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle atlasFile, DBTextureAtlasParameter parameter) {
        if(parameter != null) {
            data = parser.parseTextureAtlasData(atlasFile.file(), parameter.scale);
        }
        else{
            data = parser.parseTextureAtlasData(atlasFile.file(), 1.0f);
        }
        Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
        FileHandle textureFile = resolve(data.imagePath);
        TextureLoader.TextureParameter textureParameter = new TextureLoader.TextureParameter();
        textureParameter.format = DBGdxTextureAtlas.getGdxFormat(data.getFormat());
        dependencies.add(new AssetDescriptor(textureFile, Texture.class, textureParameter));
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
