package com.elementars.eclient.util;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.font.CFontManager;
import com.elementars.eclient.font.XFontRenderer;
import com.elementars.eclient.font.custom.CustomFont;

import java.awt.*;

/**
 * @author Elementars
 * @since 8/16/2020 - 10:34 PM
 */
public class FontHelper {
    public static void setCFontRenderer(String stringIn, int style, int size, boolean antialias, boolean metrics) {
        try{
            if (Xulu.getCorrectFont(stringIn) == null) {
                Command.sendChatMessage("Invalid font!");
                return;
            }
            if (stringIn.equalsIgnoreCase("Comfortaa Regular")) {
                CFontManager.customFont = new CustomFont(new Font("Comfortaa Regular", style, size), antialias, metrics);
                return;
            }
            CFontManager.customFont = new CustomFont(new Font(Xulu.getCorrectFont(stringIn), style, size), antialias, metrics);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setXdolfFontRenderer(String stringIn, int style, int size, boolean antialias) {
        try{
            if (Xulu.getCorrectFont(stringIn) == null && !stringIn.equalsIgnoreCase("Xulu")) {
                Command.sendChatMessage("Invalid font!");
                return;
            }
            CFontManager.xFontRenderer = new XFontRenderer(new Font(Xulu.getCorrectFont(stringIn), style, size), antialias, 8);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
