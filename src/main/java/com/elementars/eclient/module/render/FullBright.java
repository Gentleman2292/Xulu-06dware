package com.elementars.eclient.module.render;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 */
public class FullBright extends Module {
    public FullBright() {
        super("Fullbright", "Permanent brightness", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    float oldBrightness;

    @Override
    public void onEnable() {
        oldBrightness = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = 1000f;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = oldBrightness;
    }

    @Override
    public void onUpdate() {
        if (mc.gameSettings.gammaSetting != 1000f)
            mc.gameSettings.gammaSetting = 1000f;
    }
}
