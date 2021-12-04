package com.elementars.eclient.util;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.core.Global;

import java.awt.*;

public class RainbowUtils {

    private static int rgb;
    public static int a;
    public static int r;
    public static int g;
    public static int b;
    static float hue = 0.01f;

    public static void updateRainbow() {
        rgb = Color.HSBtoRGB(hue, Global.rainbowSaturation.getValue() / 255f, Global.rainbowLightness.getValue() / 255f);
        a = (rgb >>> 24) & 0xFF;
        r = (rgb >>> 16) & 0xFF;
        g = (rgb >>> 8) & 0xFF;
        b = rgb & 0xFF;
        hue += Global.rainbowspeed.getValue() / 1000f;
        if (hue > 1) hue -= 1;
    }
}
