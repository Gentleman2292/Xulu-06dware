package com.elementars.eclient.module.combat;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.ModuleManager;
import com.elementars.eclient.util.BlockInteractionHelper;
import dev.xulu.settings.Value;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.world.GameType;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockAir;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.mojang.realmsclient.gui.ChatFormatting;
import org.lwjgl.input.Keyboard;

public class HoleBlocker extends Module {
    private ArrayList<String> options;

    private final Value<Boolean> triggerable = register(new Value<>("Triggerable", this, true));
    private final Value<Integer> timeoutTicks = register(new Value<>("TimeoutTicks", this, 40, 1, 100));
    private final Value<Integer> blocksPerTick = register(new Value<>("BlocksPerTick", this, 4, 1, 9));
    private final Value<Integer> tickDelay = register(new Value<>("TickDelay", this, 0, 0, 10));
    private final Value<Boolean> rotate = register(new Value<>("Rotate", this, true));
    private final Value<Boolean> noGlitchBlocks = register(new Value<>("NoGlitchBlocks", this, true));
    private final Value<Boolean> announceUsage = register(new Value<>("AnnounceUsage", this, true));
    private int playerHotbarSlot;
    private int lastHotbarSlot;
    private int offsetStep;
    private int delayStep;
    private int totalTicksRunning;
    private boolean firstRun;
    private boolean isSneaking;

    public HoleBlocker() {
        super("HoleBlocker", "Blocks others from entering your hole", Keyboard.KEY_NONE, Category.COMBAT, true);
        this.playerHotbarSlot = -1;
        this.lastHotbarSlot = -1;
        this.offsetStep = 0;
        this.delayStep = 0;
        this.totalTicksRunning = 0;
        this.isSneaking = false;
    }

    double oldY;

    @Override
    public void onEnable() {
        if (HoleBlocker.mc.player == null) {
            this.disable();
            return;
        }
        oldY = mc.player.posY;
        this.firstRun = true;
        this.playerHotbarSlot = HoleBlocker.mc.player.inventory.currentItem;
        this.lastHotbarSlot = -1;
        if (this.announceUsage.getValue()) {
            sendDebugMessage(ChatFormatting.GREEN.toString() + "Enabled!");
        }
    }

    @Override
    public void onDisable() {
        if (HoleBlocker.mc.player == null) {
            return;
        }
        if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
            HoleBlocker.mc.player.inventory.currentItem = this.playerHotbarSlot;
        }
        if (this.isSneaking) {
            HoleBlocker.mc.player.connection.sendPacket(new CPacketEntityAction(HoleBlocker.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        this.playerHotbarSlot = -1;
        this.lastHotbarSlot = -1;
        if (this.announceUsage.getValue()) {
            sendDebugMessage(ChatFormatting.RED.toString() + "Disabled!");
        }
    }

    @Override
    public void onUpdate() {
        if (HoleBlocker.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }
        if (this.triggerable.getValue() && this.totalTicksRunning >= this.timeoutTicks.getValue()) {
            this.totalTicksRunning = 0;
            this.disable();
            return;
        }
        if (mc.player.posY != oldY) {
            this.disable();
            return;
        }
        if (!this.firstRun) {
            if (this.delayStep < this.tickDelay.getValue()) {
                ++this.delayStep;
                return;
            }
            this.delayStep = 0;
        }
        if (this.firstRun) {
            this.firstRun = false;
        }
        int blocksPlaced = 0;
        while (blocksPlaced < this.blocksPerTick.getValue()) {
            Vec3d[] offsetPattern = new Vec3d[0];
            int maxSteps = 0;
            String facing = mc.player.getHorizontalFacing().getName().toUpperCase();
            if (facing.equalsIgnoreCase("NORTH")) {
                offsetPattern = Offsets.NORTH;
                maxSteps = Offsets.NORTH.length;
            } else if (facing.equalsIgnoreCase("EAST")) {
                offsetPattern = Offsets.EAST;
                maxSteps = Offsets.EAST.length;
            } else if (facing.equalsIgnoreCase("SOUTH")) {
                offsetPattern = Offsets.SOUTH;
                maxSteps = Offsets.SOUTH.length;
            } else if (facing.equalsIgnoreCase("WEST")) {
                offsetPattern = Offsets.WEST;
                maxSteps = Offsets.WEST.length;
            } else {
                offsetPattern = Offsets.EAST;
                maxSteps = Offsets.EAST.length;
            }
            if (this.offsetStep >= maxSteps) {
                this.offsetStep = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos(offsetPattern[this.offsetStep]);
            final BlockPos targetPos = new BlockPos(HoleBlocker.mc.player.getPositionVector()).add(offsetPos.x, offsetPos.y, offsetPos.z);
            if (this.placeBlock(targetPos)) {
                ++blocksPlaced;
            }
            ++this.offsetStep;
        }
        if (blocksPlaced > 0) {
            if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                HoleBlocker.mc.player.inventory.currentItem = this.playerHotbarSlot;
                this.lastHotbarSlot = this.playerHotbarSlot;
            }
            if (this.isSneaking) {
                HoleBlocker.mc.player.connection.sendPacket(new CPacketEntityAction(HoleBlocker.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
        }
        ++this.totalTicksRunning;
    }

    private boolean placeBlock(final BlockPos pos) {
        final Block block = HoleBlocker.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        for (final Entity entity : HoleBlocker.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                return false;
            }
        }
        final EnumFacing side = BlockInteractionHelper.getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!BlockInteractionHelper.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = HoleBlocker.mc.world.getBlockState(neighbour).getBlock();
        final int obiSlot = this.findObiInHotbar();
        if (obiSlot == -1) {
            this.disable();
        }
        if (this.lastHotbarSlot != obiSlot) {
            HoleBlocker.mc.player.inventory.currentItem = obiSlot;
            this.lastHotbarSlot = obiSlot;
        }
        if ((!this.isSneaking && BlockInteractionHelper.blackList.contains(neighbourBlock)) || BlockInteractionHelper.shulkerList.contains(neighbourBlock)) {
            HoleBlocker.mc.player.connection.sendPacket(new CPacketEntityAction(HoleBlocker.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        if (this.rotate.getValue()) {
            BlockInteractionHelper.faceVectorPacketInstant(hitVec);
        }
        HoleBlocker.mc.playerController.processRightClickBlock(HoleBlocker.mc.player, HoleBlocker.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        HoleBlocker.mc.player.swingArm(EnumHand.MAIN_HAND);
        HoleBlocker.mc.rightClickDelayTimer = 4;
        if (this.noGlitchBlocks.getValue() && !HoleBlocker.mc.playerController.getCurrentGameType().equals(GameType.CREATIVE)) {
            HoleBlocker.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, neighbour, opposite));
        }
        return true;
    }

    private int findObiInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = HoleBlocker.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block instanceof BlockObsidian) {
                        slot = i;
                        break;
                    }
                }
            }
        }
        return slot;
    }

    private static class Offsets
    {
        private static final Vec3d[] NORTH;
        private static final Vec3d[] EAST;
        private static final Vec3d[] SOUTH;
        private static final Vec3d[] WEST;

        static {
            NORTH = new Vec3d[] { new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(0.0, 2.0, 0.0) };
            EAST = new Vec3d[] { new Vec3d(-1.0, 1.0, 0.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 0.0) };
            SOUTH = new Vec3d[] { new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(0.0, 2.0, 0.0) };
            WEST = new Vec3d[] { new Vec3d(1.0, 1.0, 0.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 0.0) };
        }
    }

    public static boolean isExposed() {
        if (mc.world == null) return false;
        BlockPos playerPos = new BlockPos(mc.player.getPositionVector());
        switch (mc.player.getHorizontalFacing()) {
            case NORTH:
                for (Vec3d vec3d : Offsets.NORTH) {
                    if (mc.world.getBlockState(playerPos.add(vec3d.x, vec3d.y, vec3d.z)).getBlock() == Blocks.AIR)
                        return true;
                }
                break;
            case SOUTH:
                for (Vec3d vec3d : Offsets.SOUTH) {
                    if (mc.world.getBlockState(playerPos.add(vec3d.x, vec3d.y, vec3d.z)).getBlock() == Blocks.AIR)
                        return true;
                }
                break;
            case EAST:
                for (Vec3d vec3d : Offsets.EAST) {
                    if (mc.world.getBlockState(playerPos.add(vec3d.x, vec3d.y, vec3d.z)).getBlock() == Blocks.AIR)
                        return true;
                }
                break;
            case WEST:
                for (Vec3d vec3d : Offsets.WEST) {
                    if (mc.world.getBlockState(playerPos.add(vec3d.x, vec3d.y, vec3d.z)).getBlock() == Blocks.AIR)
                        return true;
                }
                break;
        }
        return false;
    }
}
