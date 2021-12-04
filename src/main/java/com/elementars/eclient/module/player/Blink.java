package com.elementars.eclient.module.player;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventSendPacket;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import org.lwjgl.input.Keyboard;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by 086 on 24/01/2018.
 * Edited by Cuhnt on 30/7/2019
 */
public class Blink extends Module {

    Queue<CPacketPlayer> packets = new LinkedList<>();

    public Blink() {
        super("Blink", "Hides movement for a short distance", Keyboard.KEY_NONE, Category.PLAYER, true);
    }

    @EventTarget
    public void onPacket(EventSendPacket event) {
        if (isToggled() && event.getPacket() instanceof CPacketPlayer) {
            event.setCancelled(true);
            packets.add((CPacketPlayer) event.getPacket());
        }
    }
    private EntityOtherPlayerMP clonedPlayer;

    public void onEnable() {
        Xulu.EVENT_MANAGER.register(this);
        if (mc.player != null) {
            clonedPlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
            clonedPlayer.copyLocationAndAnglesFrom(mc.player);
            clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
            mc.world.addEntityToWorld(-100, clonedPlayer);
        }
    }

    public void onDisable() {
        Xulu.EVENT_MANAGER.unregister(this);
        while (!packets.isEmpty())
            mc.player.connection.sendPacket(packets.poll());
        EntityPlayer localPlayer = mc.player;
        if (localPlayer != null) {
            mc.world.removeEntityFromWorld(-100);
            clonedPlayer = null;
        }
    }

    @Override
    public String getHudInfo() {
        return String.valueOf(packets.size());
    }

}
