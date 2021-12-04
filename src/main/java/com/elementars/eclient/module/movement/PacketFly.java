package com.elementars.eclient.module.movement;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class PacketFly extends Module {

    private final Value<Boolean> defaults = register(new Value<>("Defaults", this, false));
    private final Value<Short> cooldown = register(new Value<>("Cooldown", this, (short) 0, (short) 0, (short) 10));
    private final Value<Float> fallSpeed = register(new Value<>("Fall Speed", this, 0.005f, 0f, 10f));
    private final Value<Float> upSpeed = register(new Value<>("Up Speed", this, 0.05f, 0f, 10f));

    public PacketFly() {
        super("PacketFly", "Flies with packets", Keyboard.KEY_NONE, Category.MOVEMENT, true);
    }

    private float counter = 0f;
    int j;

    @Override
    public void onUpdate() {
        if (defaults.getValue()) {
            cooldown.setValue((short) 0);
            fallSpeed.setValue(0.005f);
            upSpeed.setValue(0.05f);
        }
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        j = 0;
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null) return;
        if (event.phase == TickEvent.Phase.END) {
            if (!mc.player.isElytraFlying()) {
                if (counter < 1) {
                    counter += cooldown.getValue();
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.03, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketConfirmTeleport(++j));
                } else {
                    counter -= 1;
                }
            }
        } else {
            if (mc.gameSettings.keyBindJump.isPressed()) {
                mc.player.motionY = upSpeed.getValue();
            } else {
                mc.player.motionY = -fallSpeed.getValue();
            }
        }
    }
}
