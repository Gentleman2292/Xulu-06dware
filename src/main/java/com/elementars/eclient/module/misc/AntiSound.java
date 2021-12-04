package com.elementars.eclient.module.misc;

import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventReceivePacket;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @since 7/28/2020 - 2:18 PM
 */
public class AntiSound extends Module {

    public final Value<Boolean> wither = register(new Value<>("Wither Ambient", this, true));
    private final Value<Boolean> witherHurt = register(new Value<>("Wither Hurt", this, true));
    public final Value<Boolean> witherSpawn = register(new Value<>("Wither Spawn", this, false));
    private final Value<Boolean> witherDeath = register(new Value<>("Wither Death", this, false));
    private final Value<Boolean> punches = register(new Value<>("Punches", this, true));
    private final Value<Boolean> punchW = register(new Value<>("Weak Punch", this, true));
    private final Value<Boolean> punchKB = register(new Value<>("Knockback Punch", this, true));
    private final Value<Boolean> explosion = register(new Value<>("Explosion", this, false));
    public final Value<Boolean> totem = register(new Value<>("Totem Pop", this, false));
    public final Value<Boolean> elytra = register(new Value<>("Elytra Wind", this, true));
    public final Value<Boolean> portal = register(new Value<>("Nether Portal", this, true));

    public AntiSound() {
        super("AntiSound", "Blacklists certain annoying sounds", Keyboard.KEY_NONE, Category.MISC, true);
    }

    @Override
    public void onEnable() {
        EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        EVENT_BUS.unregister(this);
    }

    @EventTarget
    public void onRecieve(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (shouldCancelSound(packet.getSound())) {
                event.setCancelled(true);
            }
        }
    }

    private boolean shouldCancelSound(SoundEvent soundEvent) {
        if (soundEvent == SoundEvents.ENTITY_WITHER_AMBIENT && wither.getValue()) {
            return true;
        } else if (soundEvent == SoundEvents.ENTITY_WITHER_SPAWN && witherSpawn.getValue()) {
            return true;
        } else if (soundEvent == SoundEvents.ENTITY_WITHER_HURT && witherHurt.getValue()) {
            return true;
        } else if (soundEvent == SoundEvents.ENTITY_WITHER_DEATH && witherDeath.getValue()) {
            return true;
        } else if (soundEvent == SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE && punches.getValue()) {
            return true;
        } else if (soundEvent == SoundEvents.ENTITY_PLAYER_ATTACK_WEAK && punchW.getValue()) {
            return true;
        } else if (soundEvent == SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK && punchKB.getValue()) {
            return true;
        } else if (soundEvent == SoundEvents.ENTITY_GENERIC_EXPLODE && explosion.getValue()) {
            return true;
        }
        return false;
    }
}
