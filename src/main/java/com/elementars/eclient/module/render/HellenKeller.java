package com.elementars.eclient.module.render;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.SoundCategory;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author Elementars
 */
public class HellenKeller extends Module {
    public HellenKeller() {
        super("HellenKeller", "Play like Hellen Keller", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    float masterLevel;

    @Override
    public void onEnable() {
        masterLevel = mc.gameSettings.getSoundLevel(SoundCategory.MASTER);
        mc.gameSettings.setSoundLevel(SoundCategory.MASTER, 0.0f);
    }

    @Override
    public void onRender() {
        GlStateManager.pushMatrix();
        Gui.drawRect(0, 0, mc.displayWidth, mc.displayHeight, new Color(0, 0, 0, 255).getRGB());
        GlStateManager.popMatrix();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.setSoundLevel(SoundCategory.MASTER, masterLevel);
    }
}
