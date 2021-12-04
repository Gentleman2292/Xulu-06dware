package com.elementars.eclient.module.player;

import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.CloseInventoryEvent;
import com.elementars.eclient.event.events.EventSendPacket;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.network.play.client.CPacketCloseWindow;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @since 6/30/2020 - 3:52 PM
 */
public class XCarry extends Module {
    public XCarry() {
        super("XCarry", "Holds things in your crafting menu", Keyboard.KEY_NONE, Category.PLAYER, true);
    }

    @EventTarget
    public void onPacket(EventSendPacket event) {
        if (event.getPacket() instanceof CPacketCloseWindow) {
            CPacketCloseWindow packet = (CPacketCloseWindow)event.getPacket();
            event.setCancelled(packet.windowId == 0);
        }
    }

    @EventTarget
    public void onClose(CloseInventoryEvent event) {
        event.setCancelled(true);
    }
}
