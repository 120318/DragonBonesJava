package dragonBones.objects;

/**
 * Created by jingzhao on 2016/3/5.
 */
public abstract class AreaData implements Cloneable{
    public enum AreaType{AT_ELLIPSE, AT_RECTANGLE};
    public AreaType areaType;
    public String name;
    public AreaData(){

    }
    public AreaData(AreaData areaData){
        areaType = areaData.areaType;
        name = areaData.name;
    }
}
