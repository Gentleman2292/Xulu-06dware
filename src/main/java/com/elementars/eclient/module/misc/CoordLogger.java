package com.elementars.eclient.module.misc;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventSendPacket;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.Wrapper;

import java.util.List;

import dev.xulu.settings.Value;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEffect;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import java.util.HashMap;
import org.lwjgl.input.Keyboard;

public class CoordLogger extends Module {
    private final Value<Boolean> tp = register(new Value<>("TpExploit", this, false));
    private final Value<Boolean> lightning = register(new Value<>("Thunder", this, false));
    private final Value<Boolean> portal = register(new Value<>("EndPortal", this, false));
    private final Value<Boolean> wither = register(new Value<>("Wither", this, false));
    private final Value<Boolean> dragon = register(new Value<>("Dragon", this, false));
    private final Value<Boolean> savetofile = register(new Value<>("SaveToFile", this, false));
    private HashMap<Entity, Vec3d> knownPlayers;
    SPacketSoundEffect packet;
    SPacketEffect packet2;

    public CoordLogger() {
        super("CoordLogger", "Logs coords taken from several exploits", Keyboard.KEY_NONE, Category.MISC, true);
        this.knownPlayers = new HashMap<Entity, Vec3d>();
    }
    @EventTarget
    public void onPacket(EventSendPacket event) {
        if (this.lightning.getValue() && event.getPacket() instanceof SPacketSoundEffect) {
            packet = (SPacketSoundEffect)event.getPacket();
            if (packet.getCategory() == SoundCategory.WEATHER && packet.getSound() == SoundEvents.ENTITY_LIGHTNING_THUNDER) {
                this.sendNotification(ChatFormatting.RED.toString() + "Lightning spawned at X" + packet.getX() + " Z" + packet.getZ());
            }
        }
        if (event.getPacket() instanceof SPacketEffect) {
            packet2 = (SPacketEffect)event.getPacket();
            if (this.portal.getValue() && packet2.getSoundType() == 1038) {
                this.sendNotification(ChatFormatting.RED.toString() + "End Portal activated at X" + packet2.getSoundPos().getX() + " Y" + packet2.getSoundPos().getY() + " Z" + packet2.getSoundPos().getZ());
            }
            if (this.wither.getValue() && packet2.getSoundType() == 1023) {
                this.sendNotification(ChatFormatting.RED.toString() + "Wither spawned at X" + packet2.getSoundPos().getX() + " Y" + packet2.getSoundPos().getY() + " Z" + packet2.getSoundPos().getZ());
            }
            if (this.dragon.getValue() && packet2.getSoundType() == 1028) {
                this.sendNotification(ChatFormatting.RED.toString() + "Dragon killed at X" + packet2.getSoundPos().getX() + " Y" + packet2.getSoundPos().getY() + " Z" + packet2.getSoundPos().getZ());
            }
        }
        return;
    }

    @Override
    public void onUpdate() {
        if (!this.tp.getValue()) {
            return;
        }
        if (CoordLogger.mc.player == null) {
            return;
        }
        final List<Entity> tickEntityList = (List<Entity>)CoordLogger.mc.world.getLoadedEntityList();
        for (final Entity entity : tickEntityList) {
            if (entity instanceof EntityPlayer && !entity.getName().equals(CoordLogger.mc.player.getName())) {
                final Vec3d targetPos = new Vec3d(entity.posX, entity.posY, entity.posZ);
                if (this.knownPlayers.containsKey(entity)) {
                    if (Math.abs(CoordLogger.mc.player.getPositionVector().distanceTo(targetPos)) >= 128.0 && this.knownPlayers.get(entity).distanceTo(targetPos) >= 64.0) {
                        this.sendNotification(ChatFormatting.RED.toString() + "Player " + entity.getName() + " moved to Position " + targetPos.toString());
                    }
                    this.knownPlayers.put(entity, targetPos);
                }
                else {
                    this.knownPlayers.put(entity, targetPos);
                }
            }
        }
    }

    private void sendNotification(final String s) {
        Command.sendChatMessage(s);
        if (this.savetofile.getValue()) {
            Wrapper.getFileManager().appendTextFile(s, "CoordLogger.txt");
        }
    }
}
