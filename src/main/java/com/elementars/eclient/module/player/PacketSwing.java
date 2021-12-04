package com.elementars.eclient.module.player;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.item.ItemSword;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @since 7/19/2020 - 10:09 PM
 */
public class PacketSwing extends Module {
    public PacketSwing() {
        super("PacketSwing", "Swings with packets lol", Keyboard.KEY_NONE, Category.PLAYER, true);
    }

    @Override
    public void onUpdate() {
        if (PacketSwing.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && PacketSwing.mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            PacketSwing.mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            PacketSwing.mc.entityRenderer.itemRenderer.itemStackMainHand = PacketSwing.mc.player.getHeldItemMainhand();
        }
    }
}
