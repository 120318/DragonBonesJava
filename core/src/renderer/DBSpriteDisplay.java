package renderer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.NumberUtils;

import static com.badlogic.gdx.graphics.g2d.SpriteBatch.*;
/**
 * Created by jingzhao on 2016/2/28.
 */
public class DBSpriteDisplay extends DBDisplay {

    private Vector2 tl = new Vector2();
    private Vector2 tr = new Vector2();
    private Vector2 bl = new Vector2();
    private Vector2 br = new Vector2();
    private TextureRegion region;
    private final float[] vertices = new float[20];

    public DBSpriteDisplay(Texture texture, int x, int y, int width, int height) {
        region = new TextureRegion(texture, x, y, width, height);
        setRegion(region);
        setColor(1, 1, 1, 1);
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

    private void setRegion(TextureRegion region) {
        vertices[U1] = region.getU();
        vertices[V1] = region.getV2();

        vertices[U2] = region.getU();
        vertices[V2] = region.getV();

        vertices[U3] = region.getU2();
        vertices[V3] = region.getV();

        vertices[U4] = region.getU2();
        vertices[V4] = region.getV2();
    }

    public float[] getVertices(){
        if(dirty){
            Affine2 global = getGlobalTransform();
            bl.set(0 ,0);
            tl.set(0, region.getRegionHeight());
            tr.set(region.getRegionWidth(), region.getRegionHeight());
            br.set(region.getRegionWidth(), 0);

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
