package com.elementars.eclient.module.combat;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.enemy.Enemies;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.BlockInteractionHelper;
import com.elementars.eclient.util.BoolSwitch;
import com.elementars.eclient.util.TargetPlayers;
import dev.xulu.settings.Value;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.material.Material;

import java.util.*;

import net.minecraft.init.Blocks;
import net.minecraft.block.Block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import org.lwjgl.input.Keyboard;

public class HoleFill extends Module
{
    private final Value<Integer> range = register(new Value<>("Range", this, 5, 1, 10));
    private final Value<Integer> yRange = register(new Value<>("YRange", this, 2, 1, 10));
    private final Value<Boolean> rotate = register(new Value<>("Rotate", this, true));
    private final Value<Boolean> triggerable = register(new Value<>("Triggerable", this, true));
    private final Value<Integer> waitTick = register(new Value<>("TickDelay", this, 1, 0, 10));
    private final Value<Boolean> useEC = register(new Value<>("UseEnderchests", this, false));
    public final Value<Boolean> noGlitchBlocks = register(new Value<>("NoGlitchBlocks", this, true));
    private final Value<Boolean> pre = register(new Value<>("Prioritize Enemies", this, false));
    private final Value<Boolean> chat = register(new Value<>("Chat", this, false));
    private ArrayList<BlockPos> holes;
    private List<Block> whiteList;
    BlockPos pos;
    private int waitCounter;

    public HoleFill() {
        super("HoleFill", "Fills holes", Keyboard.KEY_NONE, Category.COMBAT, true);
        this.holes = new ArrayList<BlockPos>();
        this.whiteList = Arrays.asList(Blocks.OBSIDIAN);
    }

    int delay;

    @Override
    public void onUpdate() {
        if (this.triggerable.getValue()) {
            if (delay > 0) {
                --delay;
            } else {
                this.toggle();
            }
        }
        this.holes = new ArrayList<BlockPos>();
        if (this.useEC.getValue()) {
            if (!this.whiteList.contains(Blocks.ENDER_CHEST)) {
                this.whiteList.add(Blocks.ENDER_CHEST);
            }
        }
        else this.whiteList.remove(Blocks.ENDER_CHEST);
        final Iterable<BlockPos> blocks = BlockPos.getAllInBox(HoleFill.mc.player.getPosition().add(-this.range.getValue(), -this.yRange.getValue(), -this.range.getValue()), HoleFill.mc.player.getPosition().add((double)this.range.getValue(), (double)this.yRange.getValue(), (double)this.range.getValue()));
        for (final BlockPos pos : blocks) {
            if (!HoleFill.mc.world.getBlockState(pos).getMaterial().blocksMovement() && !HoleFill.mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement()) {
                final boolean solidNeighbours = (HoleFill.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK | HoleFill.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN) && (HoleFill.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK | HoleFill.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN) && (HoleFill.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK | HoleFill.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN) && (HoleFill.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK | HoleFill.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN) && HoleFill.mc.world.getBlockState(pos.add(0, 0, 0)).getMaterial() == Material.AIR && HoleFill.mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial() == Material.AIR && HoleFill.mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial() == Material.AIR;
                if (!solidNeighbours) {
                    continue;
                }
                this.holes.add(pos);
            }
        }
        int newSlot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = HoleFill.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (this.whiteList.contains(block)) {
                        newSlot = i;
                        break;
                    }
                }
            }
        }
        if (newSlot == -1) {
            return;
        }
        final int oldSlot = HoleFill.mc.player.inventory.currentItem;
        BoolSwitch test = new BoolSwitch(false);
        TargetPlayers.targettedplayers.keySet().stream()
                .map(s -> mc.world.getPlayerEntityByName(s))
                .filter(Objects::nonNull)
                .filter(player -> mc.player.getDistance(player) <= range.getValue())
                .min(Comparator.comparing(player -> mc.player.getDistance(player)))
                .ifPresent(closest -> {
                    test.setValue(true);
                    this.holes.sort(Comparator.comparing(closest::getDistanceSq));
                });
        if (!test.isValue()) {
            mc.world.playerEntities.stream()
                    .filter(player -> !Friends.isFriend(player.getName()))
                    .filter(player -> mc.player.getDistance(player) <= range.getValue())
                    .min(Comparator.comparing(player -> mc.player.getDistance(player)))
                    .ifPresent(closest -> this.holes.sort(Comparator.comparing(closest::getDistanceSq)));
            if (pre.getValue()) {
                mc.world.playerEntities.stream()
                        .filter(player -> !Friends.isFriend(player.getName()) && Enemies.isEnemy(player.getName()))
                        .filter(player -> mc.player.getDistance(player) <= range.getValue())
                        .min(Comparator.comparing(player -> mc.player.getDistance(player)))
                        .ifPresent(closest -> this.holes.sort(Comparator.comparing(closest::getDistanceSq)));
            }
        }
        if (this.waitTick.getValue() > 0.0) {
            if (this.waitCounter < this.waitTick.getValue()) {
                HoleFill.mc.player.inventory.currentItem = newSlot;
                this.holes.forEach(this::place);
                HoleFill.mc.player.inventory.currentItem = oldSlot;
                return;
            }
            this.waitCounter = 0;
        }
    }

    public void onEnable() {
        delay = 20;
        if (HoleFill.mc.player != null && this.chat.getValue()) {
            sendDebugMessage(ChatFormatting.GREEN + "Enabled!");
        }
    }

    public void onDisable() {
        if (HoleFill.mc.player != null && this.chat.getValue()) {
            sendDebugMessage(ChatFormatting.RED + "Disabled!");
        }
    }

    private void place(final BlockPos blockPos) {
        for (final Entity entity : HoleFill.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos))) {
            if (entity instanceof EntityLivingBase) {
                return;
            }
        }
        placeBlockScaffold(blockPos, this.rotate.getValue());
        ++this.waitCounter;
    }
    
    public static boolean placeBlockScaffold(final BlockPos pos, final boolean rotate) {
        final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (BlockInteractionHelper.canBeClicked(neighbor)) {
                final Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (rotate) {
                    BlockInteractionHelper.faceVectorPacketInstant(hitVec);
                }
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                processRightClickBlock(neighbor, side2, hitVec);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.rightClickDelayTimer = 0;
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                if (Xulu.MODULE_MANAGER.getModuleT(HoleFill.class).noGlitchBlocks.getValue() && !Surround.mc.playerController.getCurrentGameType().equals(GameType.CREATIVE)) {
                    Surround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, neighbor, side2));
                }
                return true;
            }
        }
        return false;
    }
    
    private static PlayerControllerMP getPlayerController() {
        return mc.playerController;
    }
    

    public static void processRightClickBlock(final BlockPos pos, final EnumFacing side, final Vec3d hitVec) {
        getPlayerController().processRightClickBlock(mc.player, mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
    }
}
