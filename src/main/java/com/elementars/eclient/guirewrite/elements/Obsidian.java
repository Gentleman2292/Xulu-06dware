package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.guirewrite.Element;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class Obsidian extends Element {
    
    public Obsidian() {
        super("Obsidian");
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
        width = 16;
        height = 16;
        super.onEnable();
    }

    @Override
    public void onRender() {
        int obsidian = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) obsidian += mc.player.getHeldItemOffhand().stackSize;
        ItemStack items = new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN), obsidian);
        this.itemrender(items);
    }

    private void itemrender(ItemStack itemStack) {
        preitemrender();
        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) this.x, (int) this.y);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, (int) this.x, (int) this.y);
        postitemrender();
    }
}
