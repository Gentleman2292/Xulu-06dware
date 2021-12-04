package com.elementars.eclient.module.movement;

import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.LocalPlayerUpdateEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

public class HoleTP extends Module {

    private static final float DEFAULT_STEP_HEIGHT = 0.6f;

    float lastStep;


    private final BlockPos[] xd = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};

    public HoleTP() {
        super("HoleTP","Reverse step for holes", Keyboard.KEY_NONE, Category.MOVEMENT, true);
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            wasOnGround = mc.player.onGround;
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.stepHeight = DEFAULT_STEP_HEIGHT;
        }

        if (mc.player != null) {
            if (mc.player.getRidingEntity() != null) {
                mc.player.getRidingEntity().stepHeight = 1;
            }
        }
    }

    private void updateStepHeight(EntityPlayer player) {
        player.stepHeight = player.onGround ? 1.2f : DEFAULT_STEP_HEIGHT;
    }

    private boolean wasOnGround = false;

    private boolean shouldUnstep(BlockPos pos) {
        boolean should = true;
        for (final BlockPos position : xd) {
            if (mc.world.getBlockState(pos.add(position)).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(pos.add(position)).getBlock() != Blocks.OBSIDIAN) {
                should = false;
            }
        }
        return should;
    }

    private void unstep(EntityPlayer player) {
        if (!shouldUnstep(new BlockPos(player.getPositionVector()))) return;
        AxisAlignedBB range = player.getEntityBoundingBox().expand(0, -1.2f, 0)
                .contract(0, player.height, 0);

        if (!player.world.collidesWithAnyBlock(range)) {
            return;
        }

        List<AxisAlignedBB> collisionBoxes = player.world.getCollisionBoxes(player, range);
        AtomicReference<Double> newY = new AtomicReference<>(0D);
        collisionBoxes.forEach(box -> newY.set(Math.max(newY.get(), box.maxY)));
        player.setPositionAndUpdate(player.posX, newY.get(), player.posZ);
    }

    private void updateUnstep(EntityPlayer player) {
        try {
            if (wasOnGround && !player.onGround && player.motionY <= 0) {
                unstep(player);
            }
        } finally {
            wasOnGround = player.onGround;
        }
    }

    @EventTarget
    public void onLocalPlayerUpdate(LocalPlayerUpdateEvent event) {
        EntityPlayer player = (EntityPlayer) event.getEntityLivingBase();
        if (player == null) {
            return;
        }
        updateUnstep(player);
    }
}