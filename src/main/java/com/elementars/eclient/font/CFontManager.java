package com.elementars.eclient.font;

import com.elementars.eclient.font.custom.CustomFont;
import com.elementars.eclient.util.Wrapper;
import com.mojang.realmsclient.gui.ChatFormatting;

import java.awt.*;
import java.util.regex.Pattern;

public class CFontManager {

    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile(ChatFormatting.PREFIX_CODE +"[0123456789abcdefklmnor]");
    private final int[] colorCodes =
            { 0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA, 0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF };

    public static CustomFont customFont = new CustomFont(new Font("Verdana", Font.PLAIN, 18), true, false);
    public static XFontRenderer xFontRenderer = new XFontRenderer(new Font("Verdana", Font.PLAIN, 36), true, 8);
    public static RainbowTextRenderer rainbowTextRenderer = new RainbowTextRenderer();


    public float drawStringWithShadow(String text, double x, double y, int color, boolean isRainbow) {
        if (com.elementars.eclient.module.core.CustomFont.customFontMode.getValue().equalsIgnoreCase("Normal")) {
            return customFont.drawStringWithShadow(text, x, y - (float) com.elementars.eclient.module.core.CustomFont.fontOffset.getValue(), color);
        } else if (com.elementars.eclient.module.core.CustomFont.customFontMode.getValue().equalsIgnoreCase("Xdolf")) {
            xFontRenderer.drawStringWithShadow(text, (int)x, (int)y -com.elementars.eclient.module.core.CustomFont.fontOffset.getValue(), color);
            return 0f;
        } else if (com.elementars.eclient.module.core.CustomFont.customFontMode.getValue().equalsIgnoreCase("Rainbow")) {
            int rgb = isRainbow ? color : -1;
            int displayColor = 0;
            char[] characters = text.toCharArray();
            String[] parts = COLOR_CODE_PATTERN.split(text);
            int index = 0;
            for (String s : parts) {
                String[] parts2 = s.split("");
                for (String s1 : parts2) {
                    if (displayColor == 0) {
                        rgb = rainbowTextRenderer.drawStringWithShadow(s1, (float) x, (float) y, rgb);
                    } else {
                        rgb = rainbowTextRenderer.updateRainbow(rgb);
                        Wrapper.getMinecraft().fontRenderer.drawStringWithShadow(s1, (float) x, (float) y, displayColor);
                    }
                    try {
                        x += rainbowTextRenderer.getCharWidth(s1.charAt(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    index++;
                }
                if (index < characters.length)
                {
                    char colorCode = characters[index];
                    if (colorCode == ChatFormatting.PREFIX_CODE)
                    {
                        char colorChar = characters[index + 1];
                        int codeIndex = ("0123456789" + "abcdef").indexOf(colorChar);
                        if (codeIndex < 0)
                        {
                            if (colorChar == 'r')
                            {
                                displayColor = 0;
                            }
                        }
                        else
                        {
                            displayColor = colorCodes[codeIndex];
                        }
                        index += 2;
                    }
                }
            }
            return 0f;
        }
        return 0f;
    }

    public float drawStringWithShadow(String text, double x, double y, int color) {
        return drawStringWithShadow(text, x, y, color, false);
    }

    public float drawString(String text, float x, float y, int color) {
        if (com.elementars.eclient.module.core.CustomFont.customFontMode.getValue().equalsIgnoreCase("Normal")) {
            return customFont.drawString(text, x, y - (float) com.elementars.eclient.module.core.CustomFont.fontOffset.getValue(), color);
        } else {
            return xFontRenderer.drawStringWithShadow(text, x, y - (float) com.elementars.eclient.module.core.CustomFont.fontOffset.getValue(), color);
        }
    }

    public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
        if (com.elementars.eclient.module.core.CustomFont.customFontMode.getValue().equalsIgnoreCase("Normal")) {
            return customFont.drawCenteredStringWithShadow(text, x, y, color);
        } else {
            xFontRenderer.drawCenteredString(text, (int) x, (int) y, color, true);
            return 0f;
        }
    }

    public float drawCenteredString(String text, float x, float y, int color) {
        if (com.elementars.eclient.module.core.CustomFont.customFontMode.getValue().equalsIgnoreCase("Normal")) {
            return customFont.drawCenteredString(text, x, y, color);
        } else {
            xFontRenderer.drawCenteredString(text, (int) x, (int) y, color);
            return 0f;
        }
    }

    public float drawString(String text, double x, double y, int color, boolean shadow) {
        if (com.elementars.eclient.module.core.CustomFont.customFontMode.getValue().equalsIgnoreCase("Normal")) {
            return customFont.drawString(text, x, y, color, shadow);
        } else {
            if (shadow){
                return xFontRenderer.drawStringWithShadow(text, (float) x, (float) y, color);
            } else {
                return xFontRenderer.drawString(text, (float) x, (float) y, color);
            }
        }
    }

    public float getHeight() {
        if (com.elementars.eclient.module.core.CustomFont.customFontMode.getValue().equalsIgnoreCase("Normal")) {
            return customFont.getHeight();
        } else if (com.elementars.eclient.module.core.CustomFont.customFontMode.getValue().equalsIgnoreCase("Rainbow")) {
            return rainbowTextRenderer.getHeight();
        } else {
            return xFontRenderer.getHeight();
        }
    }


    public int getStringWidth(String text) {
        if (com.elementars.eclient.module.core.CustomFont.customFontMode.getValue().equalsIgnoreCase("Normal")) {
            return customFont.getStringWidth(text);
        } else if (com.elementars.eclient.module.core.CustomFont.customFontMode.getValue().equalsIgnoreCase("Rainbow")) {
            return rainbowTextRenderer.getStringWidth(text);
        } else if (com.elementars.eclient.module.core.CustomFont.customFontMode.getValue().equalsIgnoreCase("Xdolf")) {
            return xFontRenderer.getStringWidth(text);
        }else {
            return 0;
        }
    }
}
