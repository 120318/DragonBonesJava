package com.dragonBones;

public class DragonBones {

    public enum DisplayType {DT_IMAGE, DT_ARMATURE, DT_FRAME, DT_TEXT, DT_1, DT_2, DT_3, DT_4, DisplayType, DT_5};
    public enum BlendMode {BM_ADD, BM_ALPHA, BM_DARKEN, BM_DIFFERENCE, BM_ERASE, BM_HARDLIGHT, BM_INVERT, BM_LAYER, BM_LIGHTEN, BM_MULTIPLY, BM_NORMAL, BM_OVERLAY, BM_SCREEN, BM_SHADER, BM_SUBTRACT};

    public static final float AUTO_TWEEN_EASING = 10.f;
    public static final float NO_TWEEN_EASING = 20.f;
    public static final float USE_FRAME_TWEEN_EASING = 30.f;
    public static final boolean NEED_Z_ORDER_UPDATED_EVENT = false;

    public static float round(float value){
        return (value > 0.0f) ? (float)Math.floor(value + 0.5f) : (float)Math.ceil(value - 0.5f);
    }

    public static float formatRadian(float radian){
        // fmod
        radian = (float)(radian % (Math.PI * 2.f));
        if (radian > Math.PI){
            radian -= Math.PI * 2.f;
        }
        if (radian < -Math.PI) {
            radian += Math.PI * 2.f;
        }
        return radian;
    }

    public static float getEaseValue(float value, float easing){
        float valueEase = 1.f;
        // ease in out
        if (easing > 1) {
            valueEase =(float)(0.5f * (1.f - Math.cos(value * Math.PI)));
            easing -= 1.f;
        }
        // ease out
        else if (easing > 0) {
            valueEase = (float)(1.f - Math.pow(1.f - value, 2));
        }
        // ease in
        else if (easing < 0) {
            easing *= -1.f;
            valueEase =  (float)(Math.pow(value, 2));
        }
        return (valueEase - value) * easing + value;
    }

    public static DisplayType getDisplayTypeByString(String displayType) {
        if (displayType.equals("image")) {
            return DisplayType.DT_IMAGE;
        }
        else if (displayType.equals("armature")) {
            return DisplayType.DT_ARMATURE;
        }
        else if (displayType.equals("frame")) {
            return DisplayType.DT_FRAME;
        }
        else if (displayType.equals("text")) {
            return DisplayType.DT_TEXT;
        }
        return DisplayType.DT_IMAGE;
    }

    public static BlendMode getBlendModeByString(String blendMode)
    {
        if (blendMode.equals("normal")) {
            return BlendMode.BM_NORMAL;
        }
        else if (blendMode.equals("add")) {
            return BlendMode.BM_ADD;
        }
        else if (blendMode.equals("alpha")) {
            return BlendMode.BM_ALPHA;
        }
        else if (blendMode.equals("darken")) {
            return BlendMode.BM_DARKEN;
        }
        else if (blendMode.equals("difference")) {
            return BlendMode.BM_DIFFERENCE;
        }
        else if (blendMode.equals("erase")) {
            return BlendMode.BM_ERASE;
        }
        else if (blendMode.equals("hardLight")) {
            return BlendMode.BM_HARDLIGHT;
        }
        else if (blendMode.equals("invert")) {
            return BlendMode.BM_INVERT;
        }
        else if (blendMode.equals("layer")) {
            return BlendMode.BM_LAYER;
        }
        else if (blendMode.equals("lighten")) {
            return BlendMode.BM_LIGHTEN;
        }
        else if (blendMode.equals("multiply")) {
            return BlendMode.BM_MULTIPLY;
        }
        else if (blendMode.equals("overlay")) {
            return BlendMode.BM_OVERLAY;
        }
        else if (blendMode.equals("screen")) {
            return BlendMode.BM_SCREEN;
        }
        else if (blendMode.equals("shader")) {
            return BlendMode.BM_SHADER;
        }
        else if (blendMode.equals("subtract")) {
            return BlendMode.BM_SUBTRACT;
        }
        return BlendMode.BM_NORMAL;
    }

    public enum PixelFormat {
        AUTO, RGBA8888, BGRA8888, RGBA4444, RGB888, RGB565, RGBA5551
    };

    public static String formatNames[] = {"", "RGBA8888", "BGRA8888", "RGBA4444", "RGB888", "RGB565", "RGBA5551"};

    public static PixelFormat getPixelFormatByString(String format) {
        int l = formatNames.length;
        for (int i = 0; i < l; ++i) {
            if (format.equals(formatNames[i])) {
                return PixelFormat.values()[i];
            }
        }
        return PixelFormat.AUTO;
    }
}
