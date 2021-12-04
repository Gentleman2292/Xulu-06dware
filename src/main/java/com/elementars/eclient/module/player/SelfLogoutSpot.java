package com.elementars.eclient.module.player;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventReceivePacket;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.ConcurrentHashMap;

public class SelfLogoutSpot extends Module {

    public ConcurrentHashMap<String, String> logoutMap;

    public SelfLogoutSpot() {
        super("SelfLogoutSpot", "Saves your logout spot in case you forget", Keyboard.KEY_NONE, Category.PLAYER, true);
        logoutMap = new ConcurrentHashMap<>();
        INSTANCE = this;
    }

    public static SelfLogoutSpot INSTANCE;

    @Override
    public void onEnable() {
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void onDisable() {
        FMLCommonHandler.instance().bus().unregister(this);
    }

    @EventTarget
    public void onPacket(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketDisconnect) {
            if (mc.getCurrentServerData() != null && mc.player != null) {
                logoutMap.put(mc.getCurrentServerData().serverIP, "X: " + Xulu.df.format(mc.player.posX) + ", Y: " + Xulu.df.format(mc.player.posY) + ", Z: " + Xulu.df.format(mc.player.posZ));
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (mc.getCurrentServerData() != null && mc.player != null) {
            logoutMap.put(mc.getCurrentServerData().serverIP, "X: " + Xulu.df.format(mc.player.posX) + ", Y: " + Xulu.df.format(mc.player.posY) + ", Z: " + Xulu.df.format(mc.player.posZ));
        }
    }
}
