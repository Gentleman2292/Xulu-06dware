package com.elementars.eclient.module.movement;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.CPacketEntityAction;
import org.lwjgl.input.Keyboard;

public class ForgeFly extends Module {

    private final Value<Boolean> fenable = register(new Value<>("Fly on Enable", this, false));
    private final Value<Double> speed = register(new Value<>("Speed", this, 0.05D, 0D, 10D));

    public ForgeFly() {
        super("ForgeFly", "ForgeHax ElytraFlight", Keyboard.KEY_NONE, Category.MOVEMENT, true);
    }
    
    private void enableFly() {
        if (mc.player == null || mc.player.capabilities == null) {
            return;
        }

        mc.player.capabilities.allowFlying = true;
        mc.player.capabilities.isFlying = true;
    }

    private void disableFly() {
        if (mc.player == null || mc.player.capabilities == null) {
            return;
        }

        PlayerCapabilities gmCaps = new PlayerCapabilities();
        mc.playerController.getCurrentGameType().configurePlayerCapabilities(gmCaps);

        PlayerCapabilities capabilities = mc.player.capabilities;
        capabilities.allowFlying = gmCaps.allowFlying;
        capabilities.isFlying = gmCaps.allowFlying && capabilities.isFlying;
        capabilities.setFlySpeed(gmCaps.getFlySpeed());
    }

    @Override
    public void onEnable() {
        if (fenable.getValue()) {
            mc.addScheduledTask(
                    () -> {
                        if (mc.player != null && !mc.player.isElytraFlying()) {
                            mc.player.connection
                                    .sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                        }
                    });
        }
    }

    @Override
    public void onDisable() {
        disableFly();
        // Are we still here?
        if (mc.player != null) {
            // Ensure the player starts flying again.
            mc.player.connection
                    .sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        }
    }

    @Override
    public void onUpdate() {
        // Enable our flight as soon as the player starts flying his elytra.
        if (mc.player.isElytraFlying()) {
            enableFly();
        }
        mc.player.capabilities.setFlySpeed(speed.getValue().floatValue());
    }
}
