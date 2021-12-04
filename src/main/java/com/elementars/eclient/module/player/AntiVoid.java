package com.elementars.eclient.module.player;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventReceivePacket;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Elementars
 * @since 6/23/2020 - 3:24 PM
 */
public class AntiVoid extends Module {

    private final Value<String> mode = register(new Value<>("Mode", this, "Normal", new String[]{
            "AFK Elytra", "Normal"
    }));
    private final Value<Integer> y_level = register(new Value<>("Y Level", this, 100, 0, 256));

    public Set<String> ipList;

    public AntiVoid() {
        super("AntiVoid", "Prevents death from afk flying", Keyboard.KEY_NONE, Category.PLAYER, true);
        ipList = new HashSet<>();
        INSTANCE = this;
    }

    public static AntiVoid INSTANCE;

    @Override
    public void onEnable() {
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void onDisable() {
        FMLCommonHandler.instance().bus().unregister(this);
    }

    private boolean wasElytraFlying;

    private boolean isOverVoid() {
        return mc.world.getBlockState(new BlockPos((int) mc.player.posX, 0, (int) mc.player.posZ)).getBlock() == Blocks.AIR;
    }

    int delay;

    @Override
    public void onUpdate() {
        if (mc.player != null && mc.world != null) {
            if (mode.getValue().equalsIgnoreCase("Normal")) {
                boolean isVoid = true;
                for (int i = (int) mc.player.posY; i > -1; i--) {
                    if (mc.world.getBlockState(new BlockPos(mc.player.posX, i, mc.player.posZ)).getBlock() != Blocks.AIR) {
                        isVoid = false;
                        break;
                    }
                }
                if (mc.player.posY < y_level.getValue() && isVoid) {
                    mc.player.motionY = 0;
                }
            } else {
                if (mc.player.isElytraFlying()) {
                    wasElytraFlying = true;
                } else {
                    if (mc.player.posY < y_level.getValue() && wasElytraFlying && isOverVoid()) {
                        mc.world.sendQuittingDisconnectingPacket();
                        wasElytraFlying = false;
                    }
                }
            }
        }
    }

    @EventTarget
    public void onPacket(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketDisconnect) {
            if (mc.getCurrentServerData() != null && mc.player != null) {
                ipList.add(mc.getCurrentServerData().serverIP);
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (mc.getCurrentServerData() != null && mc.player != null) {
            ipList.add(mc.getCurrentServerData().serverIP);
        }
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (mc.getCurrentServerData() != null) {
            ipList.remove(mc.getCurrentServerData().serverIP);
        }
    }
}
