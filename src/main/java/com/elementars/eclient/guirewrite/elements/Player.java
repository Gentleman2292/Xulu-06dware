package com.elementars.eclient.guirewrite.elements;


import com.elementars.eclient.guirewrite.Element;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

public class Player extends Element {

    private final Value<String> noLook = register(new Value<>("Look Mode", this, "Mouse", new String[]{
            "Mouse", "Free", "None"
    }));
    private final Value<Integer> scale = register(new Value<>("Scale", this, 30, 1, 100));

    public Player() {
        super("Player");
    }

    @Override
    public void onEnable() {
        width = 34;
        height = 63;
        super.onEnable();
    }

    @Override
    public void onRender() {
        ScaledResolution s = new ScaledResolution(mc);
        if (mc.player == null) return;
        if (mc.gameSettings.thirdPersonView != 0) return;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (noLook.getValue().equalsIgnoreCase("Free")) {
            drawPlayer(mc.player, (int) x, (int) y);
        } else {
            GuiInventory.drawEntityOnScreen((int) x + 17, (int) y + 60, 30, (noLook.getValue().equalsIgnoreCase("None") ? 0.0f : (float) (x) - Mouse.getX()), (noLook.getValue().equalsIgnoreCase("None") ? 0.0f : (float) (-s.getScaledHeight()) + Mouse.getY()), mc.player);
        }
        GlStateManager.popMatrix();
    }

    public void drawPlayer(final EntityPlayer player, final int x, final int y) {
        final EntityPlayer ent = player;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.rotate(0.0f, 0.0f, 5.0f, 0.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(x + 16), (float)(y + 55), 50.0f);
        GlStateManager.scale(-1f * scale.getValue(), 1f * scale.getValue(), 1f * scale.getValue());
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-(float)Math.atan(y / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        try {
            rendermanager.renderEntity((Entity)ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        }
        catch (Exception ex) {}
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.depthFunc(515);
        GlStateManager.resetColor();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }
}
