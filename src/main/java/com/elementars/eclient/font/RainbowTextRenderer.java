package com.elementars.eclient.font;

import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.Helper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * @author Elementars
 * @since 7/2/2020 - 6:28 PM
 */
public class RainbowTextRenderer implements Helper {

    private static int rgb;
    public static int a;
    public static int r;
    public static int g;
    public static int b;
    static float hue = 0.01f;

    public void updateRainbow() {
        rgb = Color.HSBtoRGB(hue, Global.rainbowSaturation.getValue() / 255f, Global.rainbowLightness.getValue() / 255f);
        a = (rgb >>> 24) & 0xFF;
        r = (rgb >>> 16) & 0xFF;
        g = (rgb >>> 8) & 0xFF;
        b = rgb & 0xFF;
        hue += 1 / 100000f;
        if (hue > 1) hue -= 1;
    }

    public int updateRainbow(int IN) {
        float hue2 = Color.RGBtoHSB(new Color(IN).getRed(), new Color(IN).getGreen(), new Color(IN).getBlue(), null)[0];
        hue2 += Global.rainbowAmount.getValue() / 1000f;
        if (hue2 > 1) hue2 -= 1;
        return Color.HSBtoRGB(hue2, Global.rainbowSaturation.getValue() / 255f, Global.rainbowLightness.getValue() / 255f);
    }

    public int FONT_HEIGHT = 9;

    public int drawStringWithShadow(String text, float x, float y, int color) {
        if (color == -1) {
            color = rgb;
            updateRainbow();
        } else {
            color = updateRainbow(color);
        }
        fontRenderer.drawStringWithShadow(text, x, y, color);
        return color;
    }

    public int drawString(String text, int x, int y, int color) {
        if (color == -1) color = rgb;
        updateRainbow();
        fontRenderer.drawString(text, x, y, color);
        return color;
    }

    public int getStringWidth(String text) {
        return fontRenderer.getStringWidth(text);
    }

    public int getCharWidth(char character) {
        return fontRenderer.getCharWidth(character);
    }

    public int getHeight() {
        return FONT_HEIGHT;
    }
}
