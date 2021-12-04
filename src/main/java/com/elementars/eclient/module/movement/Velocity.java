package com.elementars.eclient.module.movement;

import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EntityEvent;
import com.elementars.eclient.event.events.EventReceivePacket;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import org.lwjgl.input.Keyboard;

/**
 * Created by 086 on 16/11/2017.
 */
public class Velocity extends Module {

    private final Value<Float> horizontal = register(new Value<>("Horizontal", this, 0f, 0f, 100f));
    private final Value<Float> vertical = register(new Value<>("Vertical", this, 0f, 0f, 100f));

    public Velocity() {
        super("Velocity", "Modifies knockback", Keyboard.KEY_NONE, Category.MOVEMENT, true);
    }
    @EventTarget
    public void onPacket(EventReceivePacket event) {
        if (mc.player == null) return;
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity velocity = (SPacketEntityVelocity) event.getPacket();
            if (velocity.getEntityID() == mc.player.entityId) {
                if (horizontal.getValue() == 0 && vertical.getValue() == 0) event.setCancelled(true);
                velocity.motionX *= horizontal.getValue();
                velocity.motionY *= vertical.getValue();
                velocity.motionZ *= horizontal.getValue();
            }
        } else if (event.getPacket() instanceof SPacketExplosion) {
            if (horizontal.getValue() == 0 && vertical.getValue() == 0) event.setCancelled(true);
            SPacketExplosion velocity = (SPacketExplosion) event.getPacket();
            velocity.motionX *= horizontal.getValue();
            velocity.motionY *= vertical.getValue();
            velocity.motionZ *= horizontal.getValue();
        }
    }

    @EventTarget
    public void onEntityCollision(EntityEvent.EntityCollision event) {
        if (event.getEntity() == mc.player) {
            if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
                event.setCancelled(true);
                return;
            }
            event.setX(-event.getX() * horizontal.getValue());
            event.setY(0);
            event.setZ(-event.getZ() * horizontal.getValue());
        }
    }

}
