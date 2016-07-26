package com.dragonBones.parsers;

import com.dragonBones.geoms.*;
import com.dragonBones.textures.DBTextureAtlasData;
import com.dragonBones.textures.DBTextureData;
import com.dragonBones.DragonBones;
import com.dragonBones.objects.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLDataParser {
    private static float getNumber(XmlReader.Element data, String key, float defaultValue, float nanValue) {
        String value = data.getAttribute(key, null);
        if (value != null) {
            if (value.equals("NaN") || value.equals("") || value.equals("false") ||
                            value.equals("null") || value.equals("undefined")) {
                return nanValue;
            }
            else {
                return data.getFloatAttribute(key);
            }
        }
        return defaultValue;
    }

    private static boolean getBoolean(XmlReader.Element data, String key , boolean defaultValue) {
        String value = data.getAttribute(key, null);
        if (value != null) {
            if (value.equals("0")|| value.equals("NaN")|| value.equals("")||
                    value.equals("false") || value.equals("null")  || value.equals("undefined")) {
                return false;
            }
            else {
                return true;
            }
        }
        return defaultValue;
    }

    private XmlReader reader = new XmlReader();
    private XmlReader.Element element = null;
    private float armatureScale;
    private int frameRate;
    private float textureScale;

    public XMLDataParser(){
        textureScale = 1.f;
        armatureScale = 1.f;
        frameRate = 30;
    }

    public DragonBonesData parseDragonBonesData(File dragonBonesFile, float scale) {

        try {
            element = reader.parse(dragonBonesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        armatureScale = scale;
        String version = element.getAttribute(ConstValues.A_VERSION, null);
        frameRate = element.getIntAttribute(ConstValues.A_FRAME_RATE, 0);
        DragonBonesData dragonBonesData = new DragonBonesData();
        dragonBonesData.name = element.getAttribute(ConstValues.A_NAME, null);
        for(int i = 0, l = element.getChildCount(); i < l; ++i){
            ArmatureData armatureData = parseArmatureData(element.getChild(i));
            dragonBonesData.armatureDataList.add(armatureData);
        }
        return dragonBonesData;
    }

    public DBTextureAtlasData parseTextureAtlasData(File textureAtlasFile, float scale) {
        try {
            element = reader.parse(textureAtlasFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        textureScale = scale;
        DBTextureAtlasData textureAtlasData = new DBTextureAtlasData();
        String name = element.getAttribute(ConstValues.A_NAME, null);
        textureAtlasData.name = name != null ? name : "";
        String imagePath = element.getAttribute(ConstValues.A_IMAGE_PATH, null);
        textureAtlasData.imagePath = imagePath;
        String format = element.getAttribute(ConstValues.A_IMAGE_FORMAT, null);
        textureAtlasData.format = DragonBones.getPixelFormatByString(format != null ? format : "");
        for(int i = 0, l = element.getChildCount(); i < l; ++i){
            DBTextureData textureData = parseTextureData(element.getChild(i));
            textureAtlasData.textureDataList.add(textureData);
        }
        return textureAtlasData;
    }

    private DBTextureData parseTextureData(XmlReader.Element textureXML) {
        DBTextureData textureData = new DBTextureData();
        textureData.name = textureXML.getAttribute(ConstValues.A_NAME, null);
        textureData.rotated = textureXML.getBooleanAttribute(ConstValues.A_ROTATED, false);
        textureData.x = textureXML.getIntAttribute(ConstValues.A_X, 0) / (int)textureScale;
        textureData.y = textureXML.getIntAttribute(ConstValues.A_Y, 0) / (int)textureScale;
        textureData.width = textureXML.getIntAttribute(ConstValues.A_WIDTH, 0) / (int)textureScale;
        textureData.height = textureXML.getIntAttribute(ConstValues.A_HEIGHT, 0) / (int)textureScale;
        float frameWidth = textureXML.getFloatAttribute(ConstValues.A_FRAME_WIDTH, 0) / (int)textureScale;
        float frameHeight = textureXML.getFloatAttribute(ConstValues.A_FRAME_HEIGHT, .0f) / textureScale;

        if (frameWidth > 0 && frameHeight > 0) {
            textureData.frame = new Rectangle();
            textureData.frame.x = textureXML.getFloatAttribute(ConstValues.A_FRAME_X, .0f) / textureScale;
            textureData.frame.y = textureXML.getFloatAttribute(ConstValues.A_FRAME_Y, .0f) / textureScale;
            textureData.frame.width = frameWidth;
            textureData.frame.height = frameHeight;
        }
        return textureData;
    }

    public ArmatureData parseArmatureData(XmlReader.Element armatureXML){
        ArmatureData armatureData = new ArmatureData();
        armatureData.name = armatureXML.getAttribute(ConstValues.A_NAME);
        for(int i = 0, l = armatureXML.getChildCount(); i < l; ++i){
            if(armatureXML.getChild(i).getName().equals(ConstValues.BONE)) {
                BoneData boneData = parseBoneData(armatureXML.getChild(i));
                armatureData.boneDataList.add(boneData);
            }
        }

        for (XmlReader.Element skinXML = armatureXML.getChildByName(ConstValues.SKIN); skinXML != null; skinXML = skinXML.nextSiblingElement(ConstValues.SKIN)) {
            SkinData skinData = parseSkinData(skinXML);
            armatureData.skinDataList.add(skinData);
        }

        transformArmatureData(armatureData);
        armatureData.sortBoneDataList();

        for (XmlReader.Element animationXML = armatureXML.getChildByName(ConstValues.ANIMATION); animationXML != null; animationXML = animationXML.nextSiblingElement(ConstValues.ANIMATION)) {
            AnimationData animationData = parseAnimationData(animationXML, armatureData);
            armatureData.animationDataList.add(animationData);
        }

        for (XmlReader.Element rectangleXML = armatureXML.getChildByName(ConstValues.RECTANGLE); rectangleXML != null; rectangleXML = rectangleXML.nextSiblingElement(ConstValues.RECTANGLE)) {
            RectangleData rectangleData = parseRectangleData(rectangleXML);
            armatureData.areaDataList.add(rectangleData);
        }

        for (XmlReader.Element ellipseXML = armatureXML.getChildByName(ConstValues.ELLIPSE); ellipseXML != null; ellipseXML = ellipseXML.nextSiblingElement(ConstValues.ELLIPSE)) {
            EllipseData ellipseData = parseEllipseData(ellipseXML);
            armatureData.areaDataList.add(ellipseData);
        }

        return armatureData;
    }

    private EllipseData parseEllipseData(XmlReader.Element ellipseXML) {
        EllipseData ellipseData = new EllipseData();
        ellipseData.name = ellipseXML.getAttribute(ConstValues.A_NAME);
        ellipseData.width = ellipseXML.getFloatAttribute(ConstValues.A_WIDTH);
        ellipseData.height = ellipseXML.getFloatAttribute(ConstValues.A_HEIGHT);

        XmlReader.Element transformXML = ellipseXML.getChildByName(ConstValues.TRANSFORM);
        if (transformXML != null) {
            parseTransform(transformXML, ellipseData.transform);
            parsePivot(transformXML, ellipseData.pivot);
        }

        return ellipseData;
    }

    private void parsePivot(XmlReader.Element transformXML, Point pivot) {
        pivot.x = transformXML.getFloatAttribute(ConstValues.A_PIVOT_X, .0f) / armatureScale;
        pivot.y = transformXML.getFloatAttribute(ConstValues.A_PIVOT_Y, .0f) / armatureScale;
    }

    private void parseTransform(XmlReader.Element transformXML, Transform transform) {
        transform.x = transformXML.getFloatAttribute(ConstValues.A_X, .0f) / armatureScale;
        transform.y = transformXML.getFloatAttribute(ConstValues.A_Y, .0f) / armatureScale;
        transform.skewX = (float)Math.toRadians(transformXML.getFloatAttribute(ConstValues.A_SKEW_X, .0f));
        transform.skewY = (float)Math.toRadians(transformXML.getFloatAttribute(ConstValues.A_SKEW_Y, .0f));
        transform.scaleX = transformXML.getFloatAttribute(ConstValues.A_SCALE_X, .0f);
        transform.scaleY = transformXML.getFloatAttribute(ConstValues.A_SCALE_Y, .0f);
    }

    private RectangleData parseRectangleData(XmlReader.Element rectangleXML) {
        RectangleData rectangleData = new RectangleData();
        rectangleData.name = rectangleXML.getAttribute(ConstValues.A_NAME);
        rectangleData.width = rectangleXML.getFloatAttribute(ConstValues.A_WIDTH);
        rectangleData.height = rectangleXML.getFloatAttribute(ConstValues.A_HEIGHT);

        XmlReader.Element transformXML = rectangleXML.getChildByName(ConstValues.TRANSFORM);
        if (transformXML != null) {
            parseTransform(transformXML, rectangleData.transform);
            parsePivot(transformXML, rectangleData.pivot);
        }

        return rectangleData;
    }

    private AnimationData parseAnimationData(XmlReader.Element animationXML, ArmatureData armatureData) {
        AnimationData animationData = new AnimationData();
        animationData.name = animationXML.getAttribute(ConstValues.A_NAME);
        animationData.frameRate = frameRate;
        animationData.duration = (int)(Math.round(animationXML.getIntAttribute(ConstValues.A_DURATION) * 1000.f / frameRate));
        animationData.playTimes = animationXML.getIntAttribute(ConstValues.A_LOOP);
        animationData.fadeTime = animationXML.getFloatAttribute(ConstValues.A_FADE_IN_TIME);
        animationData.scale = getNumber(animationXML, ConstValues.A_SCALE, 1.f, 1.f);
        // use frame tweenEase, NaN
        // overwrite frame tweenEase, [-1, 0):ease in, 0:line easing, (0, 1]:ease out, (1, 2]:ease in out
        animationData.tweenEasing = getNumber(animationXML, ConstValues.A_TWEEN_EASING, DragonBones.USE_FRAME_TWEEN_EASING, DragonBones.USE_FRAME_TWEEN_EASING);
        animationData.autoTween = getBoolean(animationXML, ConstValues.A_AUTO_TWEEN, true);

        for (XmlReader.Element frameXML = animationXML.getChildByName(ConstValues.FRAME); frameXML != null; frameXML = frameXML.nextSiblingElement(ConstValues.FRAME)) {
            Frame frame = parseMainFrame(frameXML);
            animationData.frameList.add(frame);
        }

        parseTimeline(animationXML, animationData);

        for (XmlReader.Element timelineXML = animationXML.getChildByName(ConstValues.TIMELINE); timelineXML != null; timelineXML = timelineXML.nextSiblingElement(ConstValues.TIMELINE)) {
            TransformTimeline timeline = parseTransformTimeline(timelineXML, animationData.duration);
            animationData.timelineList.add(timeline);
        }

        addHideTimeline(animationData, armatureData);
        transformAnimationData(animationData, armatureData);
        return animationData;
    }

    private void transformAnimationData(AnimationData animationData, ArmatureData armatureData) {
        SkinData skinData = armatureData.getSkinData(null);
        for (int i = 0, l = armatureData.boneDataList.size(); i < l; ++i) {
            BoneData boneData = armatureData.boneDataList.get(i);
            TransformTimeline timeline = animationData.getTimeline(boneData.name);

            if (timeline == null) {
                continue;
            }

            SlotData slotData = null;

            if (skinData != null) {
                for (int i1 = 0, l1 = skinData.slotDataList.size(); i1 < l1; ++i1) {
                    slotData = skinData.slotDataList.get(i1);
                    if (slotData.parent.equals(boneData.name)) {
                        break;
                    }
                }
            }

            Transform originTransform = null;
            Point originPivot = null;
            TransformFrame prevFrame = null;

            for (int i2 = 0, l2 = timeline.frameList.size(); i2 < l2; ++i2) {
                TransformFrame frame = (TransformFrame)(timeline.frameList.get(i2));
                setFrameTransform(animationData, armatureData, boneData, frame);
                frame.transform.x -= boneData.transform.x;
                frame.transform.y -= boneData.transform.y;
                frame.transform.skewX -= boneData.transform.skewX;
                frame.transform.skewY -= boneData.transform.skewY;
                frame.transform.scaleX -= boneData.transform.scaleX;
                frame.transform.scaleY -= boneData.transform.scaleY;

                if (!timeline.transformed && slotData != null) {
                    frame.zOrder -= slotData.zOrder;
                }

                if (originTransform == null) {
                    timeline.originTransform = new Transform(frame.transform);
                    originTransform = timeline.originTransform;
                    originTransform.skewX = DragonBones.formatRadian(originTransform.skewX);
                    originTransform.skewY = DragonBones.formatRadian(originTransform.skewY);
                    timeline.originPivot = new Point(frame.pivot);
                    originPivot = timeline.originPivot;
                }

                frame.transform.x -= originTransform.x;
                frame.transform.y -= originTransform.y;
                frame.transform.skewX = DragonBones.formatRadian(frame.transform.skewX - originTransform.skewX);
                frame.transform.skewY = DragonBones.formatRadian(frame.transform.skewY - originTransform.skewY);
                frame.transform.scaleX -= originTransform.scaleX;
                frame.transform.scaleY -= originTransform.scaleY;

                if (!timeline.transformed) {
                    frame.pivot.x -= originPivot.x;
                    frame.pivot.y -= originPivot.y;
                }

                if (prevFrame != null) {
                    float dLX = frame.transform.skewX - prevFrame.transform.skewX;

                    if (prevFrame.tweenRotate != 0) {
                        if (prevFrame.tweenRotate > 0) {
                            if (dLX < 0) {
                                frame.transform.skewX += Math.PI * 2;
                                frame.transform.skewY += Math.PI * 2;
                            }

                            if (prevFrame.tweenRotate > 1) {
                                frame.transform.skewX += Math.PI * 2 * (prevFrame.tweenRotate - 1);
                                frame.transform.skewY += Math.PI * 2 * (prevFrame.tweenRotate - 1);
                            }
                        }
                        else {
                            if (dLX > 0) {
                                frame.transform.skewX -= Math.PI * 2;
                                frame.transform.skewY -= Math.PI * 2;
                            }

                            if (prevFrame.tweenRotate < 1) {
                                frame.transform.skewX += Math.PI * 2 * (prevFrame.tweenRotate + 1);
                                frame.transform.skewY += Math.PI * 2 * (prevFrame.tweenRotate + 1);
                            }
                        }
                    }
                    else {
                        frame.transform.skewX = prevFrame.transform.skewX + DragonBones.formatRadian(frame.transform.skewX - prevFrame.transform.skewX);
                        frame.transform.skewY = prevFrame.transform.skewY + DragonBones.formatRadian(frame.transform.skewY - prevFrame.transform.skewY);
                    }
                }
                prevFrame = frame;
            }

            timeline.transformed = true;
        }
    }

    private void setFrameTransform(AnimationData animationData, ArmatureData armatureData, BoneData boneData, TransformFrame frame) {
        frame.transform = new Transform(frame.global);
        BoneData parentData = armatureData.getBoneData(boneData.parent);

        if (parentData != null) {
            TransformTimeline parentTimeline = animationData.getTimeline(parentData.name);
            if (parentTimeline != null) {
                List<TransformTimeline> parentTimelineList = new ArrayList<TransformTimeline>();
                List<BoneData> parentDataList = new ArrayList<BoneData>();
                while (parentTimeline != null) {
                    parentTimelineList.add(parentTimeline);
                    parentDataList.add(parentData);
                    parentData = armatureData.getBoneData(parentData.parent);

                    if (parentData != null) {
                        parentTimeline = animationData.getTimeline(parentData.name);
                    }
                    else {
                        parentTimeline = null;
                    }
                }

                Matrix helpMatrix = new Matrix();
                Transform currentTransform = new Transform();
                Transform globalTransform = null;

                for (int i = parentTimelineList.size(); i-- != 0;) {
                    parentTimeline = parentTimelineList.get(i);
                    parentData = parentDataList.get(i);
                    currentTransform = getTimelineTransform(parentTimeline, frame.position, currentTransform, globalTransform == null);

                    if (globalTransform != null) {
                        //if(inheritRotation)
                        //{
                        globalTransform.skewX += currentTransform.skewX + parentTimeline.originTransform.skewX + parentData.transform.skewX;
                        globalTransform.skewY += currentTransform.skewY + parentTimeline.originTransform.skewY + parentData.transform.skewY;
                        //}
                        //if(inheritScale)
                        //{
                        //  globalTransform.scaleX *= currentTransform.scaleX + parentTimeline.originTransform.scaleX;
                        //  globalTransform.scaleY *= currentTransform.scaleY + parentTimeline.originTransform.scaleY;
                        //}
                        //else
                        //{
                        globalTransform.scaleX = currentTransform.scaleX + parentTimeline.originTransform.scaleX + parentData.transform.scaleX;
                        globalTransform.scaleY = currentTransform.scaleY + parentTimeline.originTransform.scaleY + parentData.transform.scaleY;
                        //}
                        float x = currentTransform.x + parentTimeline.originTransform.x + parentData.transform.x;
                        float y = currentTransform.y + parentTimeline.originTransform.y + parentData.transform.y;
                        globalTransform.x = helpMatrix.a * x + helpMatrix.c * y + helpMatrix.tx;
                        globalTransform.y = helpMatrix.d * y + helpMatrix.b * x + helpMatrix.ty;
                    }
                    else {
                        globalTransform = new Transform(currentTransform);
                    }

                    globalTransform.toMatrix(helpMatrix, true);
                }

                frame.transform.transformWith(globalTransform);

                if (globalTransform != null) {
                    // delete
                    globalTransform = null;
                }
            }
        }
    }

    private Transform getTimelineTransform(TransformTimeline timeline, int position, final Transform result, boolean isGlobal) {
        for (int i = 0, l = timeline.frameList.size(); i < l; ++i) {
            TransformFrame currentFrame = (TransformFrame)(timeline.frameList.get(i));

            if (currentFrame.position <= position && currentFrame.position + currentFrame.duration > position) {
                if (i == timeline.frameList.size() - 1 || position == currentFrame.position) {
                    //copy
                    Transform transform = isGlobal? currentFrame.global : currentFrame.transform;
                    result.x = transform.x;
                    result.y = transform.y;
                    result.skewX = transform.skewX;
                    result.skewY = transform.skewY;
                    result.scaleX = transform.scaleX;
                    result.scaleY = transform.scaleY;
                }
                else {
                    float progress = (position - currentFrame.position) / (float)(currentFrame.duration);
                    float tweenEasing = currentFrame.tweenEasing;

                    if (tweenEasing != 0 && tweenEasing != DragonBones.NO_TWEEN_EASING && tweenEasing != DragonBones.AUTO_TWEEN_EASING) {
                        progress = DragonBones.getEaseValue(progress, tweenEasing);
                    }

                    TransformFrame nextFrame = (TransformFrame)(timeline.frameList.get(i + 1));
                    Transform currentTransform = isGlobal ? currentFrame.global : currentFrame.transform;
                    Transform nextTransform = isGlobal ? nextFrame.global : nextFrame.transform;
                    result.x = currentTransform.x + (nextTransform.x - currentTransform.x) * progress;
                    result.y = currentTransform.y + (nextTransform.y - currentTransform.y) * progress;
                    result.skewX = DragonBones.formatRadian(currentTransform.skewX + (nextTransform.skewX - currentTransform.skewX) * progress);
                    result.skewY = DragonBones.formatRadian(currentTransform.skewY + (nextTransform.skewY - currentTransform.skewY) * progress);
                    result.scaleX = currentTransform.scaleX + (nextTransform.scaleX - currentTransform.scaleX) * progress;
                    result.scaleY = currentTransform.scaleY + (nextTransform.scaleY - currentTransform.scaleY) * progress;
                }
                break;
            }
        }
        return result;
    }


    private void addHideTimeline(AnimationData animationData, ArmatureData armatureData) {
        for (int i = 0, l = armatureData.boneDataList.size(); i < l; ++i) {
            BoneData boneData = armatureData.boneDataList.get(i);
            if (animationData.getTimeline(boneData.name) == null) {
                if (animationData.hideTimelineList.contains(boneData.name)) {
                    continue;
                }
                animationData.hideTimelineList.add(boneData.name);
            }
        }
    }

    private TransformTimeline parseTransformTimeline(XmlReader.Element timelineXML, int duration) {
        TransformTimeline timeline = new TransformTimeline();
        timeline.name = timelineXML.getAttribute(ConstValues.A_NAME);
        timeline.scale = timelineXML.getFloatAttribute(ConstValues.A_SCALE);
        timeline.offset = timelineXML.getFloatAttribute(ConstValues.A_OFFSET);
        timeline.duration = duration;

        for (XmlReader.Element frameXML = timelineXML.getChildByName(ConstValues.FRAME); frameXML != null; frameXML = frameXML.nextSiblingElement(ConstValues.FRAME)) {
            TransformFrame frame = parseTransformFrame(frameXML);
            timeline.frameList.add(frame);
        }

        parseTimeline(timelineXML, timeline);
        return timeline;
    }

    private TransformFrame parseTransformFrame(XmlReader.Element frameXML) {
        TransformFrame frame = new TransformFrame();
        parseFrame(frameXML, frame);

        frame.visible = !getBoolean(frameXML, ConstValues.A_HIDE, false);
        // NaN:no tween, 10:auto tween, [-1, 0):ease in, 0:line easing, (0, 1]:ease out, (1, 2]:ease in out
        frame.tweenEasing = getNumber(frameXML, ConstValues.A_TWEEN_EASING, DragonBones.AUTO_TWEEN_EASING, DragonBones.NO_TWEEN_EASING);
        frame.tweenRotate = frameXML.getIntAttribute(ConstValues.A_TWEEN_ROTATE, 0);
        frame.tweenScale = getBoolean(frameXML, ConstValues.A_TWEEN_SCALE, true);
        frame.displayIndex = frameXML.getIntAttribute(ConstValues.A_DISPLAY_INDEX, 0);
        frame.zOrder = getNumber(frameXML, ConstValues.A_Z_ORDER, 0.f, 0.f);

        XmlReader.Element transformXML = frameXML.getChildByName(ConstValues.TRANSFORM);
        if (transformXML != null) {
            parseTransform(transformXML, frame.global);
            parsePivot(transformXML, frame.pivot);
        }

        frame.transform = new Transform(frame.global);
        frame.scaleOffset.x = getNumber(frameXML, ConstValues.A_SCALE_X_OFFSET, 0.f, 0.f);
        frame.scaleOffset.y = getNumber(frameXML, ConstValues.A_SCALE_Y_OFFSET, 0.f, 0.f);

        XmlReader.Element colorTransformXML = frameXML.getChildByName(ConstValues.COLOR_TRANSFORM);
        if (colorTransformXML != null) {
            frame.color = new ColorTransform();
            parseColorTransform(colorTransformXML, frame.color);
        }

        return frame;
    }

    private void parseColorTransform(XmlReader.Element colorTransformXML, ColorTransform colorTransform) {
        colorTransform.alphaOffset = colorTransformXML.getIntAttribute(ConstValues.A_ALPHA_OFFSET);
        colorTransform.redOffset = colorTransformXML.getIntAttribute(ConstValues.A_RED_OFFSET);
        colorTransform.greenOffset = colorTransformXML.getIntAttribute(ConstValues.A_GREEN_OFFSET);
        colorTransform.blueOffset = colorTransformXML.getIntAttribute(ConstValues.A_BLUE_OFFSET);
        colorTransform.alphaMultiplier = colorTransformXML.getFloatAttribute(ConstValues.A_ALPHA_MULTIPLIER) * 0.01f;
        colorTransform.redMultiplier = colorTransformXML.getFloatAttribute(ConstValues.A_RED_MULTIPLIER) * 0.01f;
        colorTransform.greenMultiplier = colorTransformXML.getFloatAttribute(ConstValues.A_GREEN_MULTIPLIER) * 0.01f;
        colorTransform.blueMultiplier = colorTransformXML.getFloatAttribute(ConstValues.A_BLUE_MULTIPLIER) * 0.01f;
    }

    private void parseTimeline(XmlReader.Element timelineXml, Timeline timeline) {
        int position = 0;
        Frame frame = null;

        for (int i = 0, l = timeline.frameList.size(); i < l; ++i) {
            frame = timeline.frameList.get(i);
            frame.position = position;
            position += frame.duration;
        }

        if (frame != null) {
            frame.duration = timeline.duration - frame.position;
        }
    }

    private Frame parseMainFrame(XmlReader.Element frameXML) {
        Frame frame = new Frame();
        parseFrame(frameXML, frame);
        return frame;
    }

    private void parseFrame(XmlReader.Element frameXML, Frame frame) {
        frame.duration = (int)(Math.round(frameXML.getIntAttribute(ConstValues.A_DURATION) * 1000.f / frameRate));

        if (frameXML.getAttribute(ConstValues.A_ACTION, null) != null) {
            frame.action = frameXML.getAttribute(ConstValues.A_ACTION);
        }

        if (frameXML.getAttribute(ConstValues.A_EVENT, null) != null) {
            frame.event = frameXML.getAttribute(ConstValues.A_EVENT);
        }

        if (frameXML.getAttribute(ConstValues.A_SOUND, null) != null) {
            frame.sound = frameXML.getAttribute(ConstValues.A_SOUND);
        }
    }



    private void transformArmatureData(ArmatureData armatureData) {
        for (int i = armatureData.boneDataList.size(); i-- != 0;) {
            BoneData boneData = armatureData.boneDataList.get(i);

            if (boneData != null && boneData.parent != null) {
                BoneData parentBoneData = armatureData.getBoneData(boneData.parent);

                if (parentBoneData != null) {
                    boneData.transform = new Transform(boneData.global);
                    boneData.transform.transformWith(parentBoneData.global);
                }
            }
        }
    }

    private SkinData parseSkinData(XmlReader.Element skinXML) {
        SkinData skinData = new SkinData();
        skinData.name = skinXML.getAttribute(ConstValues.A_NAME);

        for (XmlReader.Element slotXML = skinXML.getChildByName(ConstValues.SLOT); slotXML != null; slotXML = slotXML.nextSiblingElement(ConstValues.SLOT)) {
            SlotData slotData = parseSlotData(slotXML);
            skinData.slotDataList.add(slotData);
        }

        return skinData;
    }

    private SlotData parseSlotData(XmlReader.Element slotXML) {
        SlotData slotData = new SlotData();
        slotData.name = slotXML.getAttribute(ConstValues.A_NAME);
        slotData.parent = slotXML.getAttribute(ConstValues.A_PARENT);
        slotData.zOrder = slotXML.getFloatAttribute(ConstValues.A_Z_ORDER);

        if (slotXML.getAttribute(ConstValues.A_BLENDMODE, null) != null) {
            slotData.blendMode = DragonBones.getBlendModeByString(slotXML.getAttribute(ConstValues.A_BLENDMODE));
        }

        for (XmlReader.Element displayXML = slotXML.getChildByName(ConstValues.DISPLAY); displayXML != null; displayXML = displayXML.nextSiblingElement(ConstValues.DISPLAY)) {
            DisplayData displayData = parseDisplayData(displayXML);
            slotData.displayDataList.add(displayData);
        }

        return slotData;
    }

    private DisplayData parseDisplayData(XmlReader.Element displayXML) {
        DisplayData displayData = new DisplayData();
        displayData.name = displayXML.getAttribute(ConstValues.A_NAME);
        displayData.type = DragonBones.getDisplayTypeByString(displayXML.getAttribute(ConstValues.A_TYPE));

        XmlReader.Element scalingGridXML = displayXML.getChildByName(ConstValues.SCALING_GRID);
        if (scalingGridXML != null) {
            displayData.scalingGrid = true;
            displayData.scalingGridLeft = scalingGridXML.getIntAttribute(ConstValues.A_LEFT);
            displayData.scalingGridRight = scalingGridXML.getIntAttribute(ConstValues.A_RIGHT);
            displayData.scalingGridTop = scalingGridXML.getIntAttribute(ConstValues.A_TOP);
            displayData.scalingGridBottom = scalingGridXML.getIntAttribute(ConstValues.A_BOTTOM);
        }
        else {
            displayData.scalingGrid = false;
        }

        XmlReader.Element transformXML = displayXML.getChildByName(ConstValues.TRANSFORM);
        if (transformXML != null) {
            parseTransform(transformXML, displayData.transform);
            parsePivot(transformXML, displayData.pivot);
        }

        XmlReader.Element textXML = displayXML.getChildByName(ConstValues.TEXT);
        if (textXML != null) {
            displayData.textData = new TextData();
            parseTextData(textXML, displayData.textData);
        }

        return displayData;
    }

    private void parseTextData(XmlReader.Element textXML, TextData textData) {
        textData.bold = getBoolean(textXML, ConstValues.A_BOLD, false);
        textData.italic = getBoolean(textXML, ConstValues.A_ITALIC, false);
        //u
        textData.size = textXML.getIntAttribute(ConstValues.A_SIZE);

        XmlReader.Element colorXML = textXML.getChildByName(ConstValues.COLOR);
        if (colorXML != null) {
            //u
            textData.alpha = colorXML.getIntAttribute(ConstValues.A_ALPHA);
            textData.red = colorXML.getIntAttribute(ConstValues.A_RED);
            textData.green = colorXML.getIntAttribute(ConstValues.A_GREEN);
            textData.blue = colorXML.getIntAttribute(ConstValues.A_BLUE);
        }
        //u
        textData.width = textXML.getIntAttribute(ConstValues.A_WIDTH);
        textData.height = textXML.getIntAttribute(ConstValues.A_HEIGHT);

        textData.face = textXML.getAttribute(ConstValues.A_FACE);
        textData.text = textXML.getAttribute(ConstValues.A_TEXT);

        textData.alignH = TextData.AlignHType.valueOf(textXML.getAttribute(ConstValues.A_ALIGN_H));
        textData.alignV = TextData.AlignVType.valueOf(textXML.getAttribute(ConstValues.A_ALIGN_V));
    }

    private BoneData parseBoneData(XmlReader.Element boneXML) {
        BoneData boneData = new BoneData();
        boneData.name = boneXML.getAttribute(ConstValues.A_NAME);
        String parent = boneXML.getAttribute(ConstValues.A_PARENT, null);

        if (parent != null) {
            boneData.parent = parent;
        }

        boneData.length = boneXML.getFloatAttribute(ConstValues.A_LENGTH, .0f);
        boneData.inheritRotation = getBoolean(boneXML, ConstValues.A_INHERIT_ROTATION, true);
        boneData.inheritScale = getBoolean(boneXML, ConstValues.A_INHERIT_SCALE, false);

        XmlReader.Element transformXML = boneXML.getChildByName(ConstValues.TRANSFORM);
        if (transformXML != null) {
            parseTransform(transformXML, boneData.global);
        }

        boneData.transform = new Transform(boneData.global);
        for (XmlReader.Element rectangleXML = boneXML.getChildByName(ConstValues.RECTANGLE); rectangleXML != null; rectangleXML = rectangleXML.nextSiblingElement(ConstValues.RECTANGLE)) {
            RectangleData rectangleData = parseRectangleData(rectangleXML);
            boneData.areaDataList.add(rectangleData);
        }

        for (XmlReader.Element ellipseXML = boneXML.getChildByName(ConstValues.ELLIPSE); ellipseXML !=null; ellipseXML = ellipseXML.nextSiblingElement(ConstValues.ELLIPSE)) {
            EllipseData ellipseData = parseEllipseData(ellipseXML);
            boneData.areaDataList.add(ellipseData);
        }
        return boneData;
    }
}
