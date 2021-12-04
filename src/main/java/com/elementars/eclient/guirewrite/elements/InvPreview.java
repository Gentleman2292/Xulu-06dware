package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.guirewrite.Element;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.elementars.eclient.util.ColorUtils.changeAlpha;

public class InvPreview extends Element {
    private static final ResourceLocation box;
    private Value<String> background = register(new Value<>("Background", this, "Texture", new String[]{
            "Texture", "Transparent", "None"
    }));

    public InvPreview() {
        super("InvPreview");
    }

    private static void preboxrender() {
        GL11.glPushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.clear(256);
        GlStateManager.enableBlend();
    }

    private static void postboxrender() {
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GL11.glPopMatrix();
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
        width = 162;
        height = 54;
        super.onEnable();
    }


    @Override
    public void onRender() {
        final NonNullList<ItemStack> items = (NonNullList<ItemStack>) InvPreview.mc.player.inventory.mainInventory;
        if (background.getValue().equalsIgnoreCase("Texture")) {
            this.boxrender((int) this.x, (int) this.y);
        } else if (background.getValue().equalsIgnoreCase("Transparent")) {
            Gui.drawRect((int) x, (int) y, (int) x + (int) width, (int) y + (int) height, changeAlpha(0xff121212, 100));
        }
        this.itemrender(items, (int) this.x, (int) this.y);
    }

    private void boxrender(final int x, final int y) {
        preboxrender();
        InvPreview.mc.renderEngine.bindTexture(InvPreview.box);
        InvPreview.mc.ingameGUI.drawTexturedModalRect(x, y, 7, 17, 162, 54);
        postboxrender();
    }

    private void itemrender(final NonNullList<ItemStack> items, final int x, final int y) {
        for (int size = items.size(), item = 9; item < size; ++item) {
            final int slotx = x + 1 + item % 9 * 18;
            final int sloty = y + 1 + (item / 9 - 1) * 18;
            preitemrender();
            InvPreview.mc.getRenderItem().renderItemAndEffectIntoGUI((ItemStack)items.get(item), slotx, sloty);
            InvPreview.mc.getRenderItem().renderItemOverlays(InvPreview.mc.fontRenderer, (ItemStack)items.get(item), slotx, sloty);
            postitemrender();
        }
    }

    static {
        box = new ResourceLocation("textures/gui/container/generic_54.png");
    }
}