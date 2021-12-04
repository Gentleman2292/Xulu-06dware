package com.elementars.eclient.util;

import com.mojang.realmsclient.gui.ChatFormatting;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ColorTextUtils {

    public static ArrayList<String> colors;

    public static void initColors() {
        colors = new ArrayList<>();
        colors.add("White");
        colors.add("Black");
        colors.add("Blue");
        colors.add("Green");
        colors.add("Cyan");
        colors.add("Red");
        colors.add("Purple");
        colors.add("Gold");
        colors.add("LightGray");
        colors.add("Gray");
        colors.add("Lavender");
        colors.add("LightGreen");
        colors.add("LightBlue");
        colors.add("LightRed");
        colors.add("Pink");
        colors.add("Yellow");
    }

    public static String getColor(String value) {
        String prefix;
        if (value.equalsIgnoreCase("White")) {
            prefix = "&f";
        }
        else if (value.equalsIgnoreCase("Red")) {
            prefix = "&4";
        }
        else if (value.equalsIgnoreCase("Blue")) {
            prefix = "&1";
        }
        else if (value.equalsIgnoreCase("Cyan")) {
            prefix = "&3";
        }
        else if (value.equalsIgnoreCase("Pink")) {
            prefix = "&d";
        }
        else if (value.equalsIgnoreCase("Black")) {
            prefix = "&0";
        }
        else if (value.equalsIgnoreCase("Green")) {
            prefix = "&2";
        }
        else if (value.equalsIgnoreCase("Purple")) {
            prefix = "&5";
        }
        else if (value.equalsIgnoreCase("Yellow")) {
            prefix = "&e";
        }
        else if (value.equalsIgnoreCase("LightRed")) {
            prefix = "&c";
        }
        else if (value.equalsIgnoreCase("LightBlue")) {
            prefix = "&b";
        }
        else if (value.equalsIgnoreCase("LightGreen")) {
            prefix = "&a";
        }
        else if (value.equalsIgnoreCase("Gold")) {
            prefix = "&6";
        }
        else if (value.equalsIgnoreCase("Gray")) {
            prefix = "&8";
        }
        else if (value.equalsIgnoreCase("Lavender")) {
            prefix = "&9";
        }
        else if (value.equalsIgnoreCase("LightGray")) {
            prefix = "&7";
        }
        else {
            prefix = "&r";
        }
        return prefix;
    }
}
