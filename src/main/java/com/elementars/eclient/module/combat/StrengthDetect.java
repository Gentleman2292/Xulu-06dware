package com.elementars.eclient.module.combat;

import java.util.*;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventReceivePacket;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.ColorTextUtils;
import dev.xulu.settings.Value;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.input.Keyboard;

public class StrengthDetect extends Module {
    private final Value<Boolean> watermark = register(new Value<>("Watermark", this, true));
    private final Value<String> color = register(new Value<>("Color", this, "White", ColorTextUtils.colors));
    
    public StrengthDetect() {
        super("StrengthDetect", "Detects when someone has strength (BUGGY)", Keyboard.KEY_NONE, Category.COMBAT, true);
    }

    public static List<EntityPlayer> drinkSet = new ArrayList<>();
    public static List<EntityPlayer> strPlayers = new ArrayList<>();

    @Override
    public void onUpdate() {
        if (mc.player == null) return;
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            for (PotionEffect potionEffect : entityPlayer.getActivePotionEffects()) {
                boolean flag = true;
                if (potionEffect.getPotion() == MobEffects.STRENGTH) {
                    strPlayers.add(entityPlayer);
                    flag = false;
                }
                if (flag) {
                    strPlayers.remove(entityPlayer);
                }
            }
            if (entityPlayer.getHealth() == 0.0f  && strPlayers.contains(entityPlayer)) {
                strPlayers.remove(entityPlayer);
            }
        }
    }

    @EventTarget
    public void onPacket(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                EntityPlayer entity = (EntityPlayer) packet.getEntity(mc.world);
                strPlayers.remove(entity);
                Command.sendRawChatMessage(ColorTextUtils.getColor(color.getValue()) + entity.getName() + " no longer has strength!");
            }
        }
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.sound.getSoundName().toString().equalsIgnoreCase("minecraft:entity.generic.drink")) {
                List<EntityPlayer> players = mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(packet.getX() - 1, packet.getY() - 1, packet.getZ() - 1, packet.getX() + 1, packet.getY() + 1, packet.getZ() + 1));
                EntityPlayer drinker = null;
                if (players.size() > 1) {
                    for (EntityPlayer player : players) {
                        if (drinker == null || player.getDistance(packet.getX(), packet.getY(), packet.getZ()) < drinker.getDistance(packet.getX(), packet.getY(), packet.getZ())) {
                            drinker = player;
                        }
                    }
                } else {
                    drinker = players.get(0);
                }
                if (drinker.getHeldItemMainhand().getItem() instanceof ItemPotion) {
                    List<PotionEffect> effects = PotionUtils.getEffectsFromStack(drinker.getHeldItemMainhand());
                    for (PotionEffect p : effects) {
                        if (p.getPotion() == MobEffects.STRENGTH) {
                            drinkSet.add(drinker);
                        }
                    }
                }
            }
            else if (packet.sound.getSoundName().toString().equalsIgnoreCase("minecraft:item.armor.equip_generic")) {
                List<EntityPlayer> players = mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(packet.getX() - 1, packet.getY() - 1, packet.getZ() - 1, packet.getX() + 1, packet.getY() + 1, packet.getZ() + 1));
                EntityPlayer drinker = null;
                if (players.size() > 1) {
                    for (EntityPlayer player : players) {
                        if (drinker == null || player.getDistance(packet.getX(), packet.getY(), packet.getZ()) < drinker.getDistance(packet.getX(), packet.getY(), packet.getZ())) {
                            drinker = player;
                        }
                    }
                } else {
                    drinker = players.get(0);
                }
                if (drinkSet.contains(drinker) && drinker.getHeldItemMainhand().getItem() == Items.GLASS_BOTTLE) {
                    strPlayers.add(drinker);
                    drinkSet.remove(drinker);
                    Command.sendRawChatMessage(ColorTextUtils.getColor(color.getValue()) + drinker.getName() + " has drank a strength pot!");
                }
            }
        }
    }
}