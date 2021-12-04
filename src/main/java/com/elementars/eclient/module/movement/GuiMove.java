package com.elementars.eclient.module.movement;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.Wrapper;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class GuiMove extends Module {

    public GuiMove() {
        super("GuiMove", "Move in gui menus", Keyboard.KEY_NONE, Category.MOVEMENT, true);
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiChat || mc.currentScreen == null) {
            return;
        }
        final int[] keys = new int[]{mc.gameSettings.keyBindForward.getKeyCode(), mc.gameSettings.keyBindLeft.getKeyCode(), mc.gameSettings.keyBindRight.getKeyCode(), mc.gameSettings.keyBindBack.getKeyCode(), mc.gameSettings.keyBindJump.getKeyCode()};

        for (int keyCode : keys) {
            if (Keyboard.isKeyDown(keyCode)) {
                KeyBinding.setKeyBindState(keyCode, true);
            } else {
                KeyBinding.setKeyBindState(keyCode, false);
            }
        }

        if (Wrapper.getMinecraft().currentScreen instanceof GuiContainer)
        {
            if (Keyboard.isKeyDown(Integer.valueOf(200).intValue())) {
                Wrapper.getMinecraft().player.rotationPitch -= 7.0F;
            }
            if (Keyboard.isKeyDown(Integer.valueOf(208).intValue())) {
                Wrapper.getMinecraft().player.rotationPitch += 7.0F;
            }
            if (Keyboard.isKeyDown(Integer.valueOf(205).intValue())) {
                Wrapper.getMinecraft().player.rotationYaw += 7.0F;
            }
            if (Keyboard.isKeyDown(Integer.valueOf(203).intValue())) {
                Wrapper.getMinecraft().player.rotationYaw -= 7.0F;
            }
            if(Keyboard.isKeyDown(Wrapper.getMinecraft().gameSettings.keyBindSprint.getKeyCode())) {
                Wrapper.getMinecraft().player.setSprinting(true);
            }
        }
    }
}
