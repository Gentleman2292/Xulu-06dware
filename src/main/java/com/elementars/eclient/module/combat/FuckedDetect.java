package com.elementars.eclient.module.combat;

import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.*;
import dev.xulu.settings.Value;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class FuckedDetect extends Module {
    
    public FuckedDetect() {
        super("FuckedDetector", "Detects when people are fucked", Keyboard.KEY_NONE, Category.COMBAT, true);
    }
    
    private final Value<Integer> red = register(new Value<>("Red", this, 255, 0, 255));
    private final Value<Integer> green = register(new Value<>("Green", this, 255, 0, 255));
    private final Value<Integer> blue = register(new Value<>("Blue", this, 255, 0, 255));
    private final Value<Integer> alpha = register(new Value<>("Alpha", this, 70, 0, 255));
    private final Value<Integer> distance = register(new Value<>("Draw Distance", this, 20, 0, 30));
    private final Value<Boolean> drawFriends = register(new Value<>("Draw Friends", this, false));
    private final Value<Boolean> drawOwn = register(new Value<>("Draw Own", this, false));
    private final Value<String> renderMode = register(new Value<>("RenderMode", this, "Solid", new ArrayList<>(
            Arrays.asList("Solid", "Flat", "Outline", "Full")
    )));

    public Set<EntityPlayer> fuckedPlayers;

    @Override
    public void onEnable() {
        fuckedPlayers = new HashSet<EntityPlayer>();
    }

    @Override
    public void onUpdate() {
        if (mc.player.isDead || mc.player == null || !this.isToggled()) {
            return;
        }
        getList();
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        for (EntityPlayer e : this.fuckedPlayers) {
            this.drawBlock(new BlockPos(e.posX,e.posY,e.posZ), this.red.getValue(), this.green.getValue(), this.blue.getValue());
        }
    }

    private void drawBlock(final BlockPos renderBlock, final int r, final int g, final int b) {
        if (renderMode.getValue().equalsIgnoreCase("Solid")) {
            XuluTessellator.prepare(GL11.GL_QUADS);
            XuluTessellator.drawBox(renderBlock, r, g, b, alpha.getValue(), GeometryMasks.Quad.ALL);
            XuluTessellator.release();
        }
        else if (renderMode.getValue().equalsIgnoreCase("Flat")) {
            XuluTessellator.prepare(GL11.GL_QUADS);
            XuluTessellator.drawBox(renderBlock, r, g, b, alpha.getValue(), GeometryMasks.Quad.DOWN);
            XuluTessellator.release();
        }
        else if (renderMode.getValue().equalsIgnoreCase("Outline")) {
            final IBlockState iBlockState2 = mc.world.getBlockState(renderBlock);
            final Vec3d interp2 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
            XuluTessellator.drawBoundingBox(iBlockState2.getSelectedBoundingBox(mc.world, renderBlock).grow(0.0020000000949949026D).offset(-interp2.x, -interp2.y, -interp2.z), 1.5f, r, g, b, alpha.getValue());
        }
        else if (renderMode.getValue().equalsIgnoreCase("Full")) {
            final IBlockState iBlockState3 = mc.world.getBlockState(renderBlock);
            final Vec3d interp3 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
            XuluTessellator.drawFullBox(iBlockState3.getSelectedBoundingBox(mc.world, renderBlock).grow(0.0020000000949949026D).offset(-interp3.x, -interp3.y, -interp3.z), renderBlock, 1.5f, r, g, b, alpha.getValue(), 255);
        }
        
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)time);
    }

    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    public Boolean checkHole(EntityPlayer ent) {
        BlockPos pos = new BlockPos(ent.posX, ent.posY-1, ent.posZ);
        if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR) {
            if (canPlaceCrystal(pos.south()) || (canPlaceCrystal(pos.south().south()) && mc.world.getBlockState(pos.add(0, 1, 1)).getBlock() == Blocks.AIR)) {
                return true;
            } else if (canPlaceCrystal(pos.east()) || (canPlaceCrystal(pos.east().east()) && mc.world.getBlockState(pos.add(1, 1, 0)).getBlock() == Blocks.AIR)) {
                return true;
            } else if (canPlaceCrystal(pos.west()) || (canPlaceCrystal(pos.west().west()) && mc.world.getBlockState(pos.add(-1, 1, 0)).getBlock() == Blocks.AIR)) {
                return true;
            } else
                return canPlaceCrystal(pos.north()) || (canPlaceCrystal(pos.north().north()) && mc.world.getBlockState(pos.add(0, 1, -1)).getBlock() == Blocks.AIR);
        }
        return false;
    }

    public Set<EntityPlayer> getList() {
        this.fuckedPlayers.clear();
        for (EntityPlayer ent : mc.world.playerEntities) {
            if (!EntityUtil.isLiving(ent) || ent.getHealth() <= 0.0f) continue;
            if (checkHole(ent)) {
                if (!this.drawOwn.getValue() && ent.getName() == mc.player.getName()) continue;
                if (!this.drawFriends.getValue() && Friends.isFriend(ent.getName())) continue;
                if (mc.player.getDistance(ent) > this.distance.getValue()) continue;

                this.fuckedPlayers.add(ent);
            }
        }
        return this.fuckedPlayers;
    }
}

