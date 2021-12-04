package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.combat.PopCounter;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.ColourHolder;
import com.elementars.eclient.util.Pair;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.settings.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author Elementars
 * @since 9/25/2020 - 9:01 AM
 */
public class Target extends Element {

    private final Value<Boolean> cf = register(new Value<>("Custom Font", this, false));

    public Target() {
        super("Target");
    }

    private EntityPlayer target;

    @Override
    public void onEnable() {
        width = 200;
        height = 100;
        super.onEnable();
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) return;
        List<EntityPlayer> players = new ArrayList<>(mc.world.playerEntities);
        players.removeIf(player -> player == mc.player);
        players.sort(Comparator.comparing(entityPlayer -> mc.player.getDistance(entityPlayer)));
        if (!players.isEmpty()) {
            target = players.get(0);
        } else {
            target = null;
        }
        super.onUpdate();
    }

    public String getPing(float ping) {
        if (ping > 200) {
            return "c";
        }
        else if (ping > 100) {
            return "e";
        }
        else {
            return "a";
        }
    }

    private String getDistance(double distance) {
        if (distance < 15d) {
            return "c";
        } else {
            return "a";
        }
    }

    private static RenderItem itemRender = Minecraft.getMinecraft()
            .getRenderItem();

    //Hole hud
    BlockPos NORTH = new BlockPos(0, 0, -1);
    BlockPos EAST = new BlockPos(1, 0, 0);
    BlockPos SOUTH = new BlockPos(0, 0, 1);
    BlockPos WEST = new BlockPos(-1, 0, 0);

    private boolean isBrockOrObby(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }

    private List<ItemStack> getNorth(EntityPlayer player) {
        BlockPos playerPos = new BlockPos(player.posX, player.posY, player.posZ);
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
            mc.getRenderItem().renderItemAndEffectIntoGUI(items.get(item), coordinates.get(item).getKey(), coordinates.get(item).getValue());
            postitemrender();
        }
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
    //end hole hud

    @Override
    public void onRender() {
        if (mc.player == null || mc.world == null) return;
        if (target != null) {
            Gui.drawRect((int) x, (int) y, (int) x + (int) width, (int) y + (int) height, ColorUtils.changeAlpha(Color.BLACK.getRGB(), 50));
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            try {
                GuiInventory.drawEntityOnScreen((int) x + 30, (int) y + 90, 45, 0.f, 0.f, target);
            } catch (Exception e) {
                e.printStackTrace();
            }
            GlStateManager.popMatrix();
            if (cf.getValue()) {
                Xulu.cFontRenderer.drawStringWithShadow(target.getName(), x + 62,  y + 3, -1);
                Xulu.cFontRenderer.drawStringWithShadow((mc.getConnection() == null || mc.getConnection().getPlayerInfo(target.entityUniqueID) == null ? ChatFormatting.RED + "-1" : Command.SECTIONSIGN() + getPing(mc.getConnection().getPlayerInfo(target.entityUniqueID).getResponseTime()) + mc.getConnection().getPlayerInfo(target.entityUniqueID).getResponseTime()) + "ms", x + 62,  y + 18, -1);
                Xulu.cFontRenderer.drawStringWithShadow((PopCounter.INSTANCE.popMap.containsKey(target) ? ChatFormatting.GREEN + "" + PopCounter.INSTANCE.popMap.get(target) + " Pops" : ChatFormatting.RED + "0 Pops"), x + 62,  y + 33, -1);
                Xulu.cFontRenderer.drawStringWithShadow(Command.SECTIONSIGN() + getDistance(mc.player.getDistance(target)) + (int) mc.player.getDistance(target) + " blocks away", x + 62,  y + 48, -1);
            } else {
                fontRenderer.drawStringWithShadow(target.getName(), (float) x + 62, (float) y + 3, -1);
                fontRenderer.drawStringWithShadow((mc.getConnection() == null || mc.getConnection().getPlayerInfo(target.entityUniqueID) == null ? ChatFormatting.RED + "-1" : Command.SECTIONSIGN() + getPing(mc.getConnection().getPlayerInfo(target.entityUniqueID).getResponseTime()) + mc.getConnection().getPlayerInfo(target.entityUniqueID).getResponseTime()) + "ms", (float) x + 62,  (float) y + 18, -1);
                fontRenderer.drawStringWithShadow((PopCounter.INSTANCE.popMap.containsKey(target) ? ChatFormatting.GREEN + "" + PopCounter.INSTANCE.popMap.get(target) + " Pops" : ChatFormatting.RED + "0 Pops"), (float) x + 62,  (float) y + 33, -1);
                fontRenderer.drawStringWithShadow(Command.SECTIONSIGN() + getDistance(mc.player.getDistance(target)) + (int) mc.player.getDistance(target) + " blocks away", (int) x + 62,  (int) y + 48, -1);
            }
            float health = MathHelper.clamp(MathHelper.ceil(target.getHealth()), 0, 20);
            float percentBar = (20f - health) / 20f;
            float red = 1 - percentBar;
            Gui.drawRect((int) x, (int) y + (int) height - 3, (int) (x + (red * width)), (int) y + (int) height, ColorUtils.changeAlpha(ColourHolder.toHex((int) (percentBar * 255), (int) (red * 255), 0), 200));

            //hole
            itemrender(getNorth(target), (int) x + (int) width - 52, (int) y + 4);

            //armor
            GlStateManager.pushMatrix();
            GlStateManager.enableTexture2D();

            int iteration = 0;
            for (ItemStack is : target.inventory.armorInventory) {
                iteration++;
                if (is.isEmpty()) continue;
                int x_2 = (int) x - 90 + (9 - iteration) * 20 + 2 - 12 + 60;
                int y_2 = (int) y + (int) height - 24;
                GlStateManager.enableDepth();

                itemRender.zLevel = 200F;
                itemRender.renderItemAndEffectIntoGUI(is, x_2, y_2);
                itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x_2, y_2, "");
                itemRender.zLevel = 0F;

                GlStateManager.enableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();

                String s2 = is.getCount() > 1 ? is.getCount() + "" : "";
                mc.fontRenderer.drawStringWithShadow(s2, x_2 + 19 - 2 - mc.fontRenderer.getStringWidth(s2), (int) y + 9, 0xffffff);
                //damage
                float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                float red2 = 1 - green;
                int dmg = 100 - (int) (red2 * 100);
                if (cf.getValue()) {
                    Xulu.cFontRenderer.drawStringWithShadow(dmg + "", x_2 + 8 - Xulu.cFontRenderer.getStringWidth(dmg + "") / 2, y_2 - 11, ColourHolder.toHex((int) (red2 * 255), (int) (green * 255), 0));
                } else {
                    fontRenderer.drawStringWithShadow(dmg + "", x_2 + 9 - fontRenderer.getStringWidth(dmg + "") / 2, y_2 - 11, ColourHolder.toHex((int) (red2 * 255), (int) (green * 255), 0));
                }
            }

            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
        }
    }
}
