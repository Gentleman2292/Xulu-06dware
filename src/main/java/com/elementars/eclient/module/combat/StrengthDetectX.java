package com.elementars.eclient.module.combat;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Elementars
 * @version Xulu v1.2.0
 * @since 6/28/2020 - 2:44 PM
 */
public class StrengthDetectX extends Module {

    public static final Set<EntityPlayer> strengthPlayers = new HashSet<>();
    public static final Map<EntityPlayer, Integer> strMap = new HashMap<>();

    public StrengthDetectX() {
        super("StrengthDetectX", "Hope this works", Keyboard.KEY_NONE, Category.COMBAT, true);
    }

    @Override
    public void onEnable() {
        EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onPotionColor(PotionColorCalculationEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            sendDebugMessage("Yo this event is being fired");
            boolean hasStrength = false;
            for (PotionEffect potionEffect : event.getEffects()) {
                if (potionEffect.getPotion() == MobEffects.STRENGTH) {
                    strMap.put((EntityPlayer)event.getEntityLiving(), potionEffect.getAmplifier());
                    sendRawDebugMessage(event.getEntityLiving().getName() + " has strength");
                    hasStrength = true;
                    break;
                }
            }
            if (strMap.containsKey((EntityPlayer)event.getEntityLiving()) && !hasStrength) {
                strMap.remove((EntityPlayer)event.getEntityLiving());
                sendRawDebugMessage(event.getEntityLiving().getName() + " no longer has strength");
            }
        }
    }
}
