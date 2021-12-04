package com.elementars.eclient.module.movement;

import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventSendPacket;
import com.elementars.eclient.event.events.LocalPlayerUpdateEvent;
import com.elementars.eclient.event.events.MotionEvent;
import com.elementars.eclient.event.events.PlayerMoveEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import dev.xulu.settings.Value;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.input.Keyboard;

public class Step extends Module {

    private final Value<String> mode = register(new Value<>("Mode", this, "Normal", new String[]{
            "Normal"
    }));

    private final double[] oneblockPositions = {
            0.41999998688698,
            0.7531999805212
    };

    private final double[] onehalfblockPositions = {
            0.41999998688698,
            0.7531999805212,
            1.00133597911214,
            1.16610926093821,
            1.24918707874468,
            1.1707870772188
    };

    private final double[] twoblockPositions = {
            0.42,
            0.78,
            0.63,
            0.51,
            0.9,
            1.21,
            1.45,
            1.43
    };

    //private final double[] twoblockPositions = {0.4D, 0.75D, 0.5D, 0.41D, 0.83D, 1.16D, 1.41D, 1.57D, 1.58D, 1.42D};

    private int packets;

    public Step() {
        super("Step","Step up blocks", Keyboard.KEY_NONE, Category.MOVEMENT, true);
    }

    @EventTarget
    public void onWalkingUpdate(MotionEvent event) {

        if (!mc.player.collidedHorizontally && mode.getValue().equalsIgnoreCase("Normal")) return;
        if (!mc.player.onGround || mc.player.isOnLadder() || mc.player.isInWater() || mc.player.isInLava() || mc.player.movementInput.jump || mc.player.noClip) return;
        if (mc.player.moveForward == 0 && mc.player.moveStrafing == 0) return;

        if (mc.player.collidedHorizontally && mc.player.onGround) {
            this.packets++;
        }

        final double n = get_n_normal();

        if (mode.getValue().equalsIgnoreCase("Normal")) {

            if (n < 0 || n > 2) return;

            if (n == 2.0) {
                if (this.packets > this.twoblockPositions.length - 2) {
                    for (double pos : twoblockPositions) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + pos, mc.player.posZ, true));
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ);
                    packets = 0;
                }
            }
            if (n == 1.5) {
                if (this.packets > this.onehalfblockPositions.length - 2) {
                    for (double pos : onehalfblockPositions) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + pos, mc.player.posZ, true));
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + onehalfblockPositions[onehalfblockPositions.length - 1], mc.player.posZ);
                    packets = 0;
                }
            }
            if (n == 1.0) {
                if (this.packets > this.oneblockPositions.length - 2) {
                    for (double pos : oneblockPositions) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + pos, mc.player.posZ, true));
                    }
                    mc.player.setPosition(mc.player.posX, mc.player.posY + oneblockPositions[oneblockPositions.length - 1], mc.player.posZ);
                    packets = 0;
                }
            }

        }

    }

    public double get_n_normal() {

        mc.player.stepHeight = 0.5f;

        double max_y = -1;

        final AxisAlignedBB grow = mc.player.getEntityBoundingBox().offset(0, 0.05, 0).grow(0.05);

        if (!mc.world.getCollisionBoxes(mc.player, grow.offset(0, 2, 0)).isEmpty()) return 100;

        for (final AxisAlignedBB aabb : mc.world.getCollisionBoxes(mc.player, grow)) {

            if (aabb.maxY > max_y) {
                max_y = aabb.maxY;
            }

        }

        return max_y - mc.player.posY;

    }
}