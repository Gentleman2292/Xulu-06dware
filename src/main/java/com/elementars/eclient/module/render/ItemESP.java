package com.elementars.eclient.module.render;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.*;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;

/**
 * @author Elementars
 */
public class ItemESP extends Module {
    public final Value<String> mode = register(new Value<>("Mode", this, "Box", new ArrayList<>(
            Arrays.asList("Box", "Text", "Shader", "Chams")
    )));
    private final Value<Integer> outlinewidth = register(new Value<>("Shader Width", this, 1, 1, 10));
    private final Value<Boolean> rainbow = register(new Value<>("Rainbow", this, false));
    private final Value<Boolean> items = register(new Value<>("Items", this, true));
    private final Value<Boolean> xpbottles = register(new Value<>("EXP Bottles", this, true));
    private final Value<Boolean> pearls = register(new Value<>("Pearls", this, true));
    private final Value<Integer> red = register(new Value<>("Red", this, 255, 0, 255));
    private final Value<Integer> green = register(new Value<>("Green", this, 0, 0, 255));
    private final Value<Integer> blue = register(new Value<>("Blue", this, 0, 0, 255));
    private final Value<Integer> alpha = register(new Value<>("Alpha", this, 80, 0, 255));
    private final Value<Integer> alphaF = register(new Value<>("Full Alpha", this, 80, 0, 255));
    private final Value<Integer> pred = register(new Value<>("Pearl Red", this, 255, 0, 255));
    private final Value<Integer> pgreen = register(new Value<>("Pearl Green", this, 255, 0, 255));
    private final Value<Integer> pblue = register(new Value<>("Pearl Blue", this, 255, 0, 255));
    private final Value<Integer> palpha = register(new Value<>("Pearl Alpha", this, 255, 0, 255));
    private final Value<RenderMode> outline = register(new Value<>("Render Mode", this, RenderMode.SOLID, RenderMode.values()));
    private final Value<Integer> width = register(new Value<>("Width", this, 1, 1, 10));

    public ItemESP() {
        super("ItemESP", "Highlights items", Keyboard.KEY_NONE, Category.RENDER, true);
        INSTANCE = this;
    }

    private enum RenderMode {
        SOLID, OUTLINE, FULL
    }

    public static ItemESP INSTANCE;

    @Override
    public void onDisable() {
        mc.world.loadedEntityList.stream()
                .filter(entity -> (entity instanceof EntityItem && this.items.getValue()) || (entity instanceof EntityExpBottle && this.xpbottles.getValue()) || (entity instanceof EntityEnderPearl && this.pearls.getValue()))
                .forEach(entity ->
                    entity.setGlowing(false)
                );
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        int r;
        int g;
        int b;
        if (this.rainbow.getValue()) {
            r = RainbowUtils.r;
            g = RainbowUtils.g;
            b = RainbowUtils.b;
        } else {
            r = this.red.getValue();
            g = this.green.getValue();
            b = this.blue.getValue();
        }
        XuluTessellator.prepare(GL11.GL_QUADS);
        mc.world.loadedEntityList.stream()
                .filter(entity -> (entity instanceof EntityItem && this.items.getValue()) || (entity instanceof EntityExpBottle && this.xpbottles.getValue()) || (entity instanceof EntityEnderPearl && this.pearls.getValue()))
                .forEach(entity -> {
                    if (this.mode.getValue().equalsIgnoreCase("text")) {
                        drawText(entity);
                        return;
                    }
                    if (this.mode.getValue().equalsIgnoreCase("shader")) {
                        entity.setGlowing(true);
                        return;
                    }
                    if (this.mode.getValue().equalsIgnoreCase("chams")) {
                        return;
                    }
                    entity.setGlowing(false);
                    final RenderManager renderManager = mc.renderManager;
                    final Timer timer = mc.timer;
                    final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks
                            - renderManager.renderPosX;
                    final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks
                            - renderManager.renderPosY;
                    final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks
                            - renderManager.renderPosZ;
                    final AxisAlignedBB entityBox = entity.getEntityBoundingBox();
                    final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                            entityBox.minX - entity.posX + x - 0.05D,
                            entityBox.minY - entity.posY + y,
                            entityBox.minZ - entity.posZ + z - 0.05D,
                            entityBox.maxX - entity.posX + x + 0.05D,
                            entityBox.maxY - entity.posY + y + 0.15D,
                            entityBox.maxZ - entity.posZ + z + 0.05D
                    );
                    if (entity instanceof EntityEnderPearl) {
                        if (this.rainbow.getValue()) {
                            switch (outline.getValue()) {
                                case SOLID:
                                    XuluTessellator.drawBox2(axisAlignedBB, RainbowUtils.r, RainbowUtils.g, RainbowUtils.b, this.palpha.getValue(), GeometryMasks.Quad.ALL);
                                    break;
                                case OUTLINE:
                                    XuluTessellator.drawBoundingBox(axisAlignedBB, this.width.getValue(), RainbowUtils.r, RainbowUtils.g, RainbowUtils.b, this.palpha.getValue());
                                    break;
                                case FULL:
                                    XuluTessellator.drawFullBoxAA(axisAlignedBB, this.width.getValue(), RainbowUtils.r, RainbowUtils.g, RainbowUtils.b, this.palpha.getValue(), alphaF.getValue());
                                    break;
                            }
                        } else {
                            switch (outline.getValue()) {
                                case SOLID:
                                    XuluTessellator.drawBox2(axisAlignedBB, pred.getValue(), pgreen.getValue(), pblue.getValue(), this.palpha.getValue(), GeometryMasks.Quad.ALL);
                                    break;
                                case OUTLINE:
                                    XuluTessellator.drawBoundingBox(axisAlignedBB, this.width.getValue(), pred.getValue(), pgreen.getValue(), pblue.getValue(), this.palpha.getValue());
                                    break;
                                case FULL:
                                    XuluTessellator.drawFullBoxAA(axisAlignedBB, this.width.getValue(), pred.getValue(), pgreen.getValue(), pblue.getValue(), this.palpha.getValue(), alphaF.getValue());
                                    break;
                            }
                        }
                    } else {
                        switch (outline.getValue()) {
                            case SOLID:
                                XuluTessellator.drawBox2(axisAlignedBB, r, g, b, this.alpha.getValue(), GeometryMasks.Quad.ALL);
                                break;
                            case OUTLINE:
                                XuluTessellator.drawBoundingBox(axisAlignedBB, this.width.getValue(), r, g, b, this.alpha.getValue());
                                break;
                            case FULL:
                                XuluTessellator.drawFullBoxAA(axisAlignedBB, this.width.getValue(), r, g, b, this.alpha.getValue(), alphaF.getValue());
                                break;
                        }
                    }
                });
        XuluTessellator.release();
    }

    private void drawText(Entity entityIn) {
        GlStateManager.pushMatrix();

        double scale = 1.D;
        String name = (entityIn instanceof EntityItem ? ((EntityItem) entityIn).getItem().getDisplayName() : entityIn instanceof EntityEnderPearl ? "Thrown Ender Pearl" : entityIn instanceof EntityExpBottle ? "Thrown Exp Bottle" : "null");

        Vec3d interp = EntityUtil.getInterpolatedRenderPos(entityIn, mc.getRenderPartialTicks());
        //float yAdd = entityIn.height + 0.5F - (entityIn.isSneaking() ? 0.25F : 0.0F);
        float yAdd = (entityIn.height / 2) + 0.5F;
        double x = interp.x;
        double y = interp.y + yAdd;
        double z = interp.z;

        float viewerYaw = mc.getRenderManager().playerViewY;
        float viewerPitch = mc.getRenderManager().playerViewX;
        boolean isThirdPersonFrontal = mc.getRenderManager().options.thirdPersonView == 2;
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);

        float f = mc.player.getDistance(entityIn);
        float m = (f / 8f) * (float) (Math.pow(1.2589254f, scale));
        GlStateManager.scale(m, m, m);

        FontRenderer fontRendererIn = mc.fontRenderer;
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        String str = name + (entityIn instanceof EntityItem ? " x" + ((EntityItem) entityIn).getItem().getCount() : "");
        int i = fontRendererIn.getStringWidth(str) / 2;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        //fontRendererIn.drawString(str, -i, 10, entityIn instanceof EntityPlayer ? Friends.isFriend(entityIn.getName()) ? 0x11ee11 : 0xffffff : 0xffffff);
        if (Xulu.CustomFont) {
            Xulu.cFontRenderer.drawStringWithShadow(str, -i, 9, ColorUtils.Colors.WHITE);
        } else {
            GlStateManager.enableTexture2D();
            fontRendererIn.drawStringWithShadow(str, -i, 9, ColorUtils.Colors.WHITE);
            GlStateManager.disableTexture2D();
        }
        GlStateManager.glNormal3f(0.0F, 0.0F, 0.0F);
        GlStateManager.popMatrix();
    }

    public void render1(final float n) {
        RenderHelper.enableStandardItemLighting();
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                EntityItem item = (EntityItem) entity;
                GL11.glPushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                mc.getRenderManager().renderShadow = false;
                mc.getRenderManager().renderEntityStatic(item, n, true);
                GL11.glPopMatrix();
            }
        }
    }

    public void render2(final float n) {
        RenderHelper.enableStandardItemLighting();
        int r;
        int g;
        int b;
        if (this.rainbow.getValue()) {
            r = RainbowUtils.r;
            g = RainbowUtils.g;
            b = RainbowUtils.b;
        } else {
            r = this.red.getValue();
            g = this.green.getValue();
            b = this.blue.getValue();
        }
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                EntityItem item = (EntityItem) entity;
                GlStateManager.pushMatrix();
                OutlineUtils2.setColor(new Color(r, g, b));
                mc.getRenderManager().renderShadow = false;
                mc.getRenderManager().renderEntityStatic(item, n, true);
                GlStateManager.popMatrix();
            }
        }
    }
}
