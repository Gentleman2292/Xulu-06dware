package com.elementars.eclient.module.misc;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.client.gui.GuiGameOver;
import org.lwjgl.input.Keyboard;

public class AntiDeathScreen extends Module {

    public AntiDeathScreen() {
        super("AntiDeathScreen", "AntiDeathScreen apparently", Keyboard.KEY_NONE, Category.MISC, true);
    }

    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            if (mc.currentScreen instanceof GuiGameOver) {
                if (mc.player.getHealth() > 0.0f) {
                    mc.player.respawnPlayer();
                    mc.displayGuiScreen(null);
                }
            }
        }
    }
}
