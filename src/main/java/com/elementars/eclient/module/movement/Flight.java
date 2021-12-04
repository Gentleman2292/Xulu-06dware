package com.elementars.eclient.module.movement;

import com.elementars.eclient.event.Event;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.MotionEvent;
import com.elementars.eclient.event.events.PlayerMoveEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.MathUtil;
import dev.xulu.settings.Value;
import net.minecraft.init.MobEffects;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

/**
 * @author Elementars
 * @since 8/19/2020 - 10:54 PM
 */
public class Flight extends Module {

    private Double flyHeight;

    private Value<Float> speed = register(new Value<>("Speed", this, 10f, 0f, 20f));
    private Value<Float> vSpeed = register(new Value<>("V Speed", this, 3f, 0f, 20f));
    private Value<Boolean> glide = register(new Value<>("Glide", this ,true));
    private Value<Float> glideSpeed = register(new Value<>("GlideSpeed", this, 0.25f, 0f, 5f));

    public Flight() {
        super("Flight", "Get off the ground!", Keyboard.KEY_NONE, Category.MOVEMENT, true);
    }

    @EventTarget
    public void onWalkingUpdate(MotionEvent event) {
        final double[] dir = MathUtil.directionSpeed(speed.getValue());
        if (ElytraFly .mc.player.isElytraFlying()) {
            if (this.flyHeight == null) {
                this.flyHeight = ElytraFly .mc.player.posY;
            }
            if (this.glide.getValue()) {
                this.flyHeight -= this.glideSpeed.getValue();
            }
            double posX = 0.0;
            double posZ = 0.0;
            if (ElytraFly .mc.player.movementInput.moveStrafe != 0.0f || ElytraFly .mc.player.movementInput.moveForward != 0.0f) {
                posX = dir[0];
                posZ = dir[1];
            }
            if (ElytraFly .mc.gameSettings.keyBindJump.isKeyDown()) {
                this.flyHeight = ElytraFly .mc.player.posY + this.vSpeed.getValue();
            }
            if (ElytraFly .mc.gameSettings.keyBindSneak.isKeyDown()) {
                this.flyHeight = ElytraFly .mc.player.posY - this.vSpeed.getValue();
            }
            ElytraFly .mc.player.setPosition(ElytraFly .mc.player.posX + posX, this.flyHeight, ElytraFly .mc.player.posZ + posZ);
            ElytraFly .mc.player.setVelocity(0.0, 0.0, 0.0);
        }
        this.flyHeight = null;
    }
}
