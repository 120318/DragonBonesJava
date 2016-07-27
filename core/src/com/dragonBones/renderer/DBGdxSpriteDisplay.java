package com.dragonBones.renderer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.NumberUtils;

import static com.badlogic.gdx.graphics.g2d.SpriteBatch.*;

public class DBGdxSpriteDisplay extends DBGdxDisplay {

    private Vector2 tl = new Vector2();
    private Vector2 tr = new Vector2();
    private Vector2 bl = new Vector2();
    private Vector2 br = new Vector2();
    private TextureRegion region;

    protected final float[] vertices = new float[20];

    private Vector2 offset = new Vector2();
    private Rectangle regionRect = new Rectangle();

    public DBGdxSpriteDisplay(Texture texture, Rectangle rectangle, boolean rotated) {
        regionRect = new Rectangle(rectangle);
        if(rotated) {
            region = new TextureRegion(texture, (int) rectangle.x, (int) rectangle.y, (int) rectangle.height, (int) rectangle.width);
        }
        else{
            region = new TextureRegion(texture, (int) rectangle.x, (int) rectangle.y, (int) rectangle.width, (int) rectangle.height);

        }
        setRegion(region, rotated);
        initColor();
    }

    public DBGdxSpriteDisplay(Texture texture, Rectangle rectangle, boolean rotated,
                              Vector2 offset, Vector2 originSize) {
        this(texture, rectangle, rotated);
        this.offset.x = offset.x + (originSize.x - rectangle.width) / 2;
        this.offset.y = offset.y + (originSize.y - rectangle.height) / 2;
    }

    private void initColor(){
        // init color is (1, 1, 1, 1)
        float color = NumberUtils.intToFloatColor(-1);

        float[] vertices = this.vertices;
        vertices[C1] = color;
        vertices[C2] = color;
        vertices[C3] = color;
        vertices[C4] = color;
    }

    public void setColor (float r, float g, float b, float a) {
        int intBits = ((int)(255 * a) << 24) | ((int)(255 * b) << 16) | ((int)(255 * g) << 8) | ((int)(255 * r));
        float color = NumberUtils.intToFloatColor(intBits);
        float[] vertices = this.vertices;
        vertices[C1] = color;
        vertices[C2] = color;
        vertices[C3] = color;
        vertices[C4] = color;
    }

    public void setColor (float color) {
        float[] vertices = this.vertices;
        vertices[C1] = color;
        vertices[C2] = color;
        vertices[C3] = color;
        vertices[C4] = color;
    }

    public TextureRegion getRegion(){
        return region;
    }

    private void setRegion(TextureRegion region, boolean rotated) {
        if(rotated){
            vertices[U1] = region.getU();
            vertices[V1] = region.getV();

            vertices[U2] = region.getU2();
            vertices[V2] = region.getV();

            vertices[U3] = region.getU2();
            vertices[V3] = region.getV2();

            vertices[U4] = region.getU();
            vertices[V4] = region.getV2();
        }
        else {
            vertices[U1] = region.getU();
            vertices[V1] = region.getV2();

            vertices[U2] = region.getU();
            vertices[V2] = region.getV();

            vertices[U3] = region.getU2();
            vertices[V3] = region.getV();

            vertices[U4] = region.getU2();
            vertices[V4] = region.getV2();
        }

    }

    public float[] getVertices(){
        if(dirty){
            Affine2 global = getGlobalTransform();
            bl.set(offset.x ,offset.y);
            tl.set(offset.x, offset.y + regionRect.height);
            tr.set(offset.x + regionRect.width, offset.y + regionRect.height);
            br.set(offset.x + regionRect.width, offset.y);

            global.applyTo(bl);
            global.applyTo(tl);
            global.applyTo(tr);
            global.applyTo(br);
            vertices[X1] = bl.x;
            vertices[Y1] = bl.y;

            vertices[X2] = tl.x;
            vertices[Y2] = tl.y;

            vertices[X3] = tr.x;
            vertices[Y3] = tr.y;

            vertices[X4] = br.x;
            vertices[Y4] = br.y;
        }
        return vertices;
    }
}
