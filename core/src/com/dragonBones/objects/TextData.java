package com.dragonBones.objects;

public class TextData {
    public enum AlignHType {LEFT, CENTER, RIGHT, JUSTIFY};
    public enum AlignVType {TOP, CENTER, BOTTOM};
    public enum TextType {STATIC, DYNAMIC, INPUT};
    public enum LineType {SINGLE_LINE, MULTILINE, MULTILINE_NO_WRAP, PASSWORD};
    public boolean bold;
    public boolean italic;
    public boolean htmlText;
    public int size;

    public int alpha;
    public int red;
    public int green;
    public int blue;

    public int width;
    public int height;

    public int letterSpacing;
    public int lineSpacing;
    public int maxCharacters;

    public String face;
    public String text;

    public AlignHType alignH;
    public AlignVType alignV;
    public TextType textType;
    public LineType lineType;

    public TextData(){
        bold = false;
        italic = false;
        htmlText = false;
        size = 0;
        red = 0;
        green = 0;
        blue = 0;
        width = 0;
        height = 0;
        letterSpacing = 0;
        lineSpacing = 0;
        maxCharacters = 0;
        face = null;
        text = null;
        alignH = AlignHType.LEFT;
        alignV = AlignVType.TOP;
        textType = TextType.STATIC;
        lineType = LineType.SINGLE_LINE;
    }
    public TextData(TextData textData){
        bold = textData.bold;
        italic = textData.italic;
        htmlText = textData.htmlText;
        size = textData.size;
        red = textData.red;
        green = textData.green;
        blue = textData.blue;
        width = textData.width;
        height = textData.height;
        letterSpacing = textData.letterSpacing;
        lineSpacing = textData.lineSpacing;
        maxCharacters = textData.maxCharacters;
        face = textData.face;
        text = textData.text;
        alignH = textData.alignH;
        alignV = textData.alignV;
        textType = textData.textType;
        lineType = textData.lineType;
    }
}
