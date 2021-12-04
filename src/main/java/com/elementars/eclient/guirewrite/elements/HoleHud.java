package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.util.Pair;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HoleHud extends Element {

    BlockPos NORTH = new BlockPos(0, 0, -1);
    BlockPos EAST = new BlockPos(1, 0, 0);
    BlockPos SOUTH = new BlockPos(0, 0, 1);
    BlockPos WEST = new BlockPos(-1, 0, 0);

    public HoleHud() {
        super("HoleHud");
    }

    private static void preitemrender() {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale(1.0f, 1.0f, 0.01f);
    }

    private static void postitemrender() {
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }

    @Override
    public void onEnable() {
        width = 48;
        height = 48;
        super.onEnable();
    }

    @Override
    public void onRender() {
        if (mc.player == null || mc.world == null) return;
        List<ItemStack> items;
        switch (mc.getRenderViewEntity().getHorizontalFacing()) {
            case NORTH:
                this.itemrender(getNorth(), (int) this.x, (int) this.y);
                break;
            case EAST:
                this.itemrender(getEast(), (int) this.x, (int) this.y);
                break;
            case SOUTH:
                this.itemrender(getSouth(), (int) this.x, (int) this.y);
                break;
            case WEST:
                this.itemrender(getWest(), (int) this.x, (int) this.y);
                break;
        }
    }

    private boolean isBrockOrObby(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }

    private List<ItemStack> getEast() {
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        List<ItemStack> items = new ArrayList<>(
                Arrays.asList(
                        (isBrockOrObby(playerPos.add(EAST.x, EAST.y, EAST.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(EAST.x, EAST.y, EAST.z)).getBlock()) : new ItemStack(Items.AIR)),
                        (isBrockOrObby(playerPos.add(SOUTH.x, SOUTH.y, SOUTH.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(SOUTH.x, SOUTH.y, SOUTH.z)).getBlock()) : new ItemStack(Items.AIR)),
                        (isBrockOrObby(playerPos.add(WEST.x, WEST.y, WEST.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(WEST.x, WEST.y, WEST.z)).getBlock()) : new ItemStack(Items.AIR)),
                        (isBrockOrObby(playerPos.add(NORTH.x, NORTH.y, NORTH.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(NORTH.x, NORTH.y, NORTH.z)).getBlock()) : new ItemStack(Items.AIR))
                )
        );
        return items;
    }

    private List<ItemStack> getSouth() {
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        List<ItemStack> items = new ArrayList<>(
                Arrays.asList(
                        (isBrockOrObby(playerPos.add(SOUTH.x, SOUTH.y, SOUTH.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(SOUTH.x, SOUTH.y, SOUTH.z)).getBlock()) : new ItemStack(Items.AIR)),
                        (isBrockOrObby(playerPos.add(WEST.x, WEST.y, WEST.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(WEST.x, WEST.y, WEST.z)).getBlock()) : new ItemStack(Items.AIR)),
                        (isBrockOrObby(playerPos.add(NORTH.x, NORTH.y, NORTH.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(NORTH.x, NORTH.y, NORTH.z)).getBlock()) : new ItemStack(Items.AIR)),
                        (isBrockOrObby(playerPos.add(EAST.x, EAST.y, EAST.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(EAST.x, EAST.y, EAST.z)).getBlock()) : new ItemStack(Items.AIR))
                )
        );
        return items;
    }
    private List<ItemStack> getWest() {
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        List<ItemStack> items = new ArrayList<>(
                Arrays.asList(
                        (isBrockOrObby(playerPos.add(WEST.x, WEST.y, WEST.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(WEST.x, WEST.y, WEST.z)).getBlock()) : new ItemStack(Items.AIR)),
                        (isBrockOrObby(playerPos.add(NORTH.x, NORTH.y, NORTH.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(NORTH.x, NORTH.y, NORTH.z)).getBlock()) : new ItemStack(Items.AIR)),
                        (isBrockOrObby(playerPos.add(EAST.x, EAST.y, EAST.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(EAST.x, EAST.y, EAST.z)).getBlock()) : new ItemStack(Items.AIR)),
                        (isBrockOrObby(playerPos.add(SOUTH.x, SOUTH.y, SOUTH.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(SOUTH.x, SOUTH.y, SOUTH.z)).getBlock()) : new ItemStack(Items.AIR))
                )
        );
        return items;
    }
    private List<ItemStack> getNorth() {
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        List<ItemStack> items = new ArrayList<>(
                Arrays.asList(
                        (isBrockOrObby(playerPos.add(NORTH.x, NORTH.y, NORTH.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(NORTH.x, NORTH.y, NORTH.z)).getBlock()) : new ItemStack(Items.AIR)),
                        (isBrockOrObby(playerPos.add(EAST.x, EAST.y, EAST.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(EAST.x, EAST.y, EAST.z)).getBlock()) : new ItemStack(Items.AIR)),
                        (isBrockOrObby(playerPos.add(SOUTH.x, SOUTH.y, SOUTH.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(SOUTH.x, SOUTH.y, SOUTH.z)).getBlock()) : new ItemStack(Items.AIR)),
                        (isBrockOrObby(playerPos.add(WEST.x, WEST.y, WEST.z)) ? new ItemStack(mc.world.getBlockState(playerPos.add(WEST.x, WEST.y, WEST.z)).getBlock()) : new ItemStack(Items.AIR))
                )
        );
        return items;
    }


    private void itemrender(final List<ItemStack> items, final int x, final int y) {
        ArrayList<Pair<Integer, Integer>> coordinates = new ArrayList<>(
                Arrays.asList(
                        new Pair<>(x+16, y), //top
                        new Pair<>(x+32, y+16), //right
                        new Pair<>(x+16, y+32), //bottom
                        new Pair<>(x, y+16) //left
                )
        );
        for (int item = 0; item < 4; item++) {
            preitemrender();
            InvPreview.mc.getRenderItem().renderItemAndEffectIntoGUI(items.get(item), coordinates.get(item).getKey(), coordinates.get(item).getValue());
            //InvPreview.mc.getRenderItem().renderItemOverlays(InvPreview.mc.fontRenderer, items.get(item), coordinates.get(item).getKey(), coordinates.get(item).getValue());
            postitemrender();
        }
    }
}