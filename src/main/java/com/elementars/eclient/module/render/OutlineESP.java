package com.elementars.eclient.module.render;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.Event;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventModelRender;
import com.elementars.eclient.event.events.EventPostRenderLayers;
import com.elementars.eclient.event.events.EventRenderEntity;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.*;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.Value;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class OutlineESP extends Module {

    public static boolean feQE;
    public static boolean krOE;

    public final Value<String> mode = register(new Value<>("Mode", this, "Outline", new ArrayList<>(
            Arrays.asList("Outline", "Wireframe", "Solid", "Shader")
    )));
    public final Value<Float> width = register(new Value<>("Line Width", this, 1f, 0f, 10f));
    public final Value<Boolean> chams = register(new Value<>("Chams", this, false));
    public final Value<Boolean> onTop = register(new Value<>("OnTop", this, true));
    public final Value<Boolean> players = register(new Value<>("Players", this, true));
    public final Value<Boolean> animals = register(new Value<>("Animals", this, true));
    public final Value<Boolean> mobs = register(new Value<>("Mobs", this, true));
    public final Value<Boolean> crystals = register(new Value<>("Crystals", this, true));
    public final Value<String> color = register(new Value<>("Color Mode", this, "Tracers", new String[]{
            "ClickGui", "Tracers", "Target", "Rainbow"
    }));
    public final Value<Boolean> renderEntities = register(new Value<>("Render Entities", this, true));
    public final Value<Boolean> renderCrystals = register(new Value<>("Render Crystals", this, true));
    public final Value<Boolean> friends = register(new Value<>("Friends", this, true));
    private final Value<Integer> red = register(new Value<>("Target Red", this, 255, 0, 255));
    private final Value<Integer> green = register(new Value<>("Target Green", this, 0, 0, 255));
    private final Value<Integer> blue = register(new Value<>("TargetBlue", this, 0, 0, 255));
    public static Value<Boolean> future;

    public OutlineESP() {
        super("OutlineESP", "Outlines entities", Keyboard.KEY_NONE, Category.RENDER, true);
        future = register(new Value<>("Future Colors", this, true));
    }

    boolean fancyGraphics;
    float gamma;

    ICamera camera = new Frustum();

    @EventTarget
    public void onModelRender(EventModelRender event) {
        Vec3d view = MathUtil.interpolateEntity(mc.player, event.getPartialTicks());
        camera.setPosition(view.x, view.y, view.z);
        if (!camera.isBoundingBoxInFrustum(event.entity.getEntityBoundingBox())) return;
        if (event.getEventState() == Event.State.PRE) {
            fancyGraphics = mc.gameSettings.fancyGraphics;
            mc.gameSettings.fancyGraphics = false;

            gamma = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = 10000f;
            if (onTop.getValue() && renderEntities.getValue()) {
                event.modelBase.render(
                        event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor
                );
            }
            if (mode.getValue().equalsIgnoreCase("Shader")) {
                if (event.entity instanceof EntityOtherPlayerMP && players.getValue()) {
                    event.entity.setGlowing(true);
                } else if (animals.getValue() && EntityUtil.isPassive(event.entity)) {
                    event.entity.setGlowing(true);
                } else if (mobs.getValue() && !EntityUtil.isPassive(event.entity)) {
                    event.entity.setGlowing(true);
                } else {
                    event.entity.setGlowing(false);
                }
            } else {
                event.entity.setGlowing(false);
                if (!mode.getValue().equalsIgnoreCase("Outline")) {
                    if (event.entity instanceof EntityOtherPlayerMP && players.getValue()) {
                        GL11.glPushMatrix();
                        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                        GL11.glPolygonOffset(1f, -100000f);
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        if (mode.getValue().equalsIgnoreCase("Solid")) {
                            GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
                        } else {
                            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                        }
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glEnable(GL11.GL_LINE_SMOOTH);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        OutlineUtils.setColor(getColor((EntityOtherPlayerMP)event.entity));
                        GL11.glLineWidth(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        GL11.glPopAttrib();
                        GL11.glPolygonOffset(1f, 100000f);
                        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                        GL11.glPopMatrix();
                    } else if (animals.getValue() && EntityUtil.isPassive(event.entity)) {
                        GL11.glPushMatrix();
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glEnable(GL11.GL_LINE_SMOOTH);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        OutlineUtils.setColor(OutlineESP.getEntityColor(event.entity));
                        GL11.glLineWidth(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        GL11.glPopAttrib();
                        GL11.glPopMatrix();
                    } else if (mobs.getValue() && !EntityUtil.isPassive(event.entity)) {
                        GL11.glPushMatrix();
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glEnable(GL11.GL_LINE_SMOOTH);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        OutlineUtils.setColor(OutlineESP.getEntityColor(event.entity));
                        GL11.glLineWidth(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        GL11.glPopAttrib();
                        GL11.glPopMatrix();
                    }
                } else {
                    if (event.entity instanceof EntityOtherPlayerMP && players.getValue()) {
                        Color n = getColor((EntityOtherPlayerMP)event.entity);
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        OutlineUtils.renderOne(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        OutlineUtils.renderTwo();
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        OutlineUtils.renderThree();
                        OutlineUtils.renderFour(n);
                        OutlineUtils.setColor(n);
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        OutlineUtils.renderFive();
                        OutlineUtils.setColor(Color.WHITE);
                    } else if (animals.getValue() && EntityUtil.isPassive(event.entity)) {
                        Color n;
                        if (future.getValue())
                            n = new Color(0, 196, 0);
                        else
                            n = new Color(5, 255, 240);
                        GL11.glLineWidth(5.0F);
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        OutlineUtils.renderOne(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        OutlineUtils.renderTwo();
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        OutlineUtils.renderThree();
                        OutlineUtils.renderFour(n);
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        OutlineUtils.renderFive();
                    } else if (mobs.getValue() && !EntityUtil.isPassive(event.entity) && !(event.entity instanceof EntityPlayer)) {
                        Color n;
                        if (future.getValue())
                            n = new Color(191, 57, 59);
                        else
                            n = new Color(255, 0, 102);
                        GL11.glLineWidth(5.0F);
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        OutlineUtils.renderOne(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        OutlineUtils.renderTwo();
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        OutlineUtils.renderThree();
                        OutlineUtils.renderFour(n);
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        OutlineUtils.renderFive();
                    }
                }
            }
            if (!onTop.getValue() && renderEntities.getValue()) {
                event.modelBase.render(
                        event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor
                );
            }
            OutlineUtils.setColor(Color.WHITE);
            mc.gameSettings.fancyGraphics = fancyGraphics;
            mc.gameSettings.gammaSetting = gamma;
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void renderPost(EventPostRenderLayers event) {
        if (!event.renderer.bindEntityTexture(event.entity)) return;
        Vec3d view = MathUtil.interpolateEntity(mc.player, event.getPartialTicks());
        camera.setPosition(view.x, view.y, view.z);
        if (!camera.isBoundingBoxInFrustum(event.entity.getEntityBoundingBox())) return;
        if (event.getEventState() == Event.State.PRE) {
            fancyGraphics = mc.gameSettings.fancyGraphics;
            mc.gameSettings.fancyGraphics = false;

            gamma = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = 10000f;
            if (onTop.getValue() && renderEntities.getValue()) {
                event.modelBase.render(
                        event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn
                );
            }
            if (mode.getValue().equalsIgnoreCase("Shader")) {
                if (event.entity instanceof EntityOtherPlayerMP && players.getValue()) {
                    event.entity.setGlowing(true);
                } else if (animals.getValue() && EntityUtil.isPassive(event.entity)) {
                    event.entity.setGlowing(true);
                } else if (mobs.getValue() && !EntityUtil.isPassive(event.entity)) {
                    event.entity.setGlowing(true);
                } else {
                    event.entity.setGlowing(false);
                }
            } else {
                event.entity.setGlowing(false);
                if (!mode.getValue().equalsIgnoreCase("Outline")) {
                    if (event.entity instanceof EntityOtherPlayerMP && players.getValue()) {
                        GL11.glPushMatrix();
                        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                        GL11.glPolygonOffset(1f, -100000f);
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        if (mode.getValue().equalsIgnoreCase("Solid")) {
                            GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
                        } else {
                            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                        }
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glEnable(GL11.GL_LINE_SMOOTH);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        OutlineUtils.setColor(getColor((EntityOtherPlayerMP)event.entity));
                        GL11.glLineWidth(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        GL11.glPopAttrib();
                        GL11.glPolygonOffset(1f, 100000f);
                        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                        GL11.glPopMatrix();
                    } else if (animals.getValue() && EntityUtil.isPassive(event.entity)) {
                        GL11.glPushMatrix();
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glEnable(GL11.GL_LINE_SMOOTH);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        OutlineUtils.setColor(OutlineESP.getEntityColor(event.entity));
                        GL11.glLineWidth(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        GL11.glPopAttrib();
                        GL11.glPopMatrix();
                    } else if (mobs.getValue() && !EntityUtil.isPassive(event.entity)) {
                        GL11.glPushMatrix();
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glEnable(GL11.GL_LINE_SMOOTH);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        OutlineUtils.setColor(OutlineESP.getEntityColor(event.entity));
                        GL11.glLineWidth(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        GL11.glPopAttrib();
                        GL11.glPopMatrix();
                    }
                } else {
                    if (event.entity instanceof EntityOtherPlayerMP && players.getValue()) {
                        Color n = getColor((EntityOtherPlayerMP)event.entity);
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        OutlineUtils.renderOne(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        OutlineUtils.renderTwo();
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        OutlineUtils.renderThree();
                        OutlineUtils.renderFour(n);
                        OutlineUtils.setColor(n);
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        OutlineUtils.renderFive();
                        OutlineUtils.setColor(Color.WHITE);
                    } else if (animals.getValue() && EntityUtil.isPassive(event.entity)) {
                        Color n;
                        if (future.getValue())
                            n = new Color(0, 196, 0);
                        else
                            n = new Color(5, 255, 240);
                        GL11.glLineWidth(5.0F);
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        OutlineUtils.renderOne(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        OutlineUtils.renderTwo();
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        OutlineUtils.renderThree();
                        OutlineUtils.renderFour(n);
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        OutlineUtils.renderFive();
                    } else if (mobs.getValue() && !EntityUtil.isPassive(event.entity) && !(event.entity instanceof EntityPlayer)) {
                        Color n;
                        if (future.getValue())
                            n = new Color(191, 57, 59);
                        else
                            n = new Color(255, 0, 102);
                        GL11.glLineWidth(5.0F);
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        OutlineUtils.renderOne(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        OutlineUtils.renderTwo();
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        OutlineUtils.renderThree();
                        OutlineUtils.renderFour(n);
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                        OutlineUtils.renderFive();
                    }
                }
            }
            if (!onTop.getValue() && renderEntities.getValue()) {
                event.modelBase.render(
                        event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn
                );
            }
            OutlineUtils.setColor(Color.WHITE);
            mc.gameSettings.fancyGraphics = fancyGraphics;
            mc.gameSettings.gammaSetting = gamma;
            event.setCancelled(true);
        }
    }

    private Color getColor(EntityPlayer entity) {
        if (Friends.isFriend(entity.getName()) && friends.getValue()) {
            return new Color(0.27f, 0.7f, 0.92f);
        }
        if (color.getValue().equalsIgnoreCase("ClickGui")) {
            return ColorUtil.getClickGUIColor();
        } else if (color.getValue().equalsIgnoreCase("Tracers")) {
            final float distance = mc.player.getDistance(entity);
            if (distance <= 32.0f) {
                return new Color(1.0f - ((distance / 32.0f) / 2), distance / 32.0f, 0.0f);
            }
            else {
                return new Color(0.0f, 0.9f, 0.0f);
            }
        } else if (color.getValue().equalsIgnoreCase("Target")) {
            if (TargetPlayers.targettedplayers.containsKey(entity.getName())) {
                return new Color(red.getValue() / 255f, green.getValue() / 255f, blue.getValue() / 255f);
            } else {
                return ColorUtil.getClickGUIColor();
            }
        } else if (color.getValue().equalsIgnoreCase("Rainbow")) {
            return new Color(Xulu.rgb);
        } else {
            return new Color(1f, 1f, 1f);
        }
    }

    public static void renderNormal2(final float n) {
        for (final Entity entity : Wrapper.getMinecraft().world.loadedEntityList) {
            if (entity == Wrapper.getMinecraft().getRenderViewEntity() || !(entity instanceof EntityPlayer)) {
                continue;
            } else {
                final Render entityRenderObject = Wrapper.getMinecraft().getRenderManager().getEntityRenderObject(entity);
                RenderLivingBase renderer = entityRenderObject instanceof RenderLivingBase ? (RenderLivingBase) entityRenderObject : null;
                    /*
                    RenderHelper.enableStandardItemLighting();
                    final int combinedLight = Wrapper.getMinecraft().world.getCombinedLight(entity.getPosition(), 0);
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) (combinedLight % 65536), (float) (combinedLight / 65536));
                    GlStateManager.pushMatrix();
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    final Render entityRenderObject = Wrapper.getMinecraft().getRenderManager().getEntityRenderObject(entity);
                    final double n2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * n;
                    final double n3 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * n;
                    final double n4 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * n;
                    OutlineESP.feQE = true;
                    OutlineESP.krOE = true;
                    entityRenderObject.doRender(entity, n2 - mc.renderManager.renderPosX, n3 - mc.renderManager.renderPosY, n4 - mc.renderManager.renderPosZ, entity.rotationYaw, n);
                    OutlineESP.krOE = false;
                    OutlineESP.feQE = false;
                    GlStateManager.popMatrix();

                     */
                final double n2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * n;
                final double n3 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * n;
                final double n4 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * n;
                if (renderer != null)
                    renderer.doRender((EntityPlayer) entity, n2 - mc.renderManager.renderPosX, n3 - mc.renderManager.renderPosY, n4 - mc.renderManager.renderPosZ, entity.rotationYaw, mc.getRenderPartialTicks());
                //mc.renderGlobal.renderManager.renderEntityStatic(entity, n, false);
            }
        }
    }

    public static void renderColor2(final float n) {
        for (final Entity entity : Wrapper.getMinecraft().world.loadedEntityList) {
            if (entity == Wrapper.getMinecraft().getRenderViewEntity() || !(entity instanceof EntityPlayer)) {
                continue;
            } else {
                final Render entityRenderObject = Wrapper.getMinecraft().getRenderManager().getEntityRenderObject(entity);
                RenderLivingBase renderer = entityRenderObject instanceof RenderLivingBase ? (RenderLivingBase) entityRenderObject : null;
                    /*
                    RenderHelper.enableStandardItemLighting();
                    final int combinedLight = Wrapper.getMinecraft().world.getCombinedLight(entity.getPosition(), 0);
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) (combinedLight % 65536), (float) (combinedLight / 65536));
                    GlStateManager.pushMatrix();
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    final Render entityRenderObject = Wrapper.getMinecraft().getRenderManager().getEntityRenderObject(entity);
                    final double n2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * n;
                    final double n3 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * n;
                    final double n4 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * n;
                    OutlineESP.feQE = true;
                    OutlineESP.krOE = true;
                    entityRenderObject.doRender(entity, n2 - mc.renderManager.renderPosX, n3 - mc.renderManager.renderPosY, n4 - mc.renderManager.renderPosZ, entity.rotationYaw, n);
                    OutlineESP.krOE = false;
                    OutlineESP.feQE = false;
                    GlStateManager.popMatrix();

                     */
                if (entity instanceof EntityPlayer) {
                    GL11.glColor3f(5, 255, 240);
                } else if (entity instanceof EntityEnderCrystal) {
                    OutlineUtils2.setColor(ColorUtil.getClickGUIColor());
                } else if (EntityUtil.isPassive(entity)) {
                    if (future.getValue()) {
                        OutlineUtils2.setColor(new Color(0, 196, 0));
                    }
                    else {
                        OutlineUtils2.setColor(new Color(5, 255, 240));
                    }
                } else if (!EntityUtil.isPassive(entity) || entity instanceof EntitySpider) {
                    if (future.getValue()) {
                        OutlineUtils2.setColor(new Color(191, 57, 59));
                    }
                    else {
                        OutlineUtils2.setColor(new Color(255, 0, 102));
                    }
                } else {
                    OutlineUtils2.setColor(new Color(1.0f, 1.0f, 0.0f));
                }
                final double n2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * n;
                final double n3 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * n;
                final double n4 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * n;
                if (renderer != null)
                    renderer.doRender((EntityPlayer) entity, n2, n3, n4, entity.rotationYaw, mc.getRenderPartialTicks());
            }
        }
    }

    public static void renderNormal(final float n) {
        for (final Entity entity : Wrapper.getMinecraft().world.loadedEntityList) {
            if (entity == Wrapper.getMinecraft().getRenderViewEntity() || !(entity instanceof AbstractClientPlayer)) {
                continue;
            } else {
                AbstractClientPlayer player = (AbstractClientPlayer) entity;
                /*
                RenderHelper.enableStandardItemLighting();
                final int combinedLight = Wrapper.getMinecraft().world.getCombinedLight(entity.getPosition(), 0);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) (combinedLight % 65536), (float) (combinedLight / 65536));
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                final Render entityRenderObject = Wrapper.getMinecraft().getRenderManager().getEntityRenderObject(entity);
                */
                final double n2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * n;
                final double n3 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * n;
                final double n4 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * n;
                /*
                OutlineESP.feQE = true;
                OutlineESP.krOE = true;
                entityRenderObject.doRender(entity, n2 - mc.renderManager.renderPosX, n3 - mc.renderManager.renderPosY, n4 - mc.renderManager.renderPosZ, entity.rotationYaw, n);
                OutlineESP.krOE = false;
                OutlineESP.feQE = false;
                GlStateManager.popMatrix();
                */

                //mc.renderGlobal.renderManager.renderEntityStatic(entity, n, false);
                mc.renderGlobal.renderManager.playerRenderer.doRender(player, n2 - mc.renderManager.renderPosX, n3 - mc.renderManager.renderPosY, n4 - mc.renderManager.renderPosZ, entity.rotationYaw, n);
            }
        }
    }

    public static void renderColor(final float n) {
        for (final Entity entity : Wrapper.getMinecraft().world.loadedEntityList) {
            if (entity == Wrapper.getMinecraft().getRenderViewEntity() || !(entity instanceof AbstractClientPlayer)) {
                continue;
            } else {
                AbstractClientPlayer player = (AbstractClientPlayer) entity;
                /*
                RenderHelper.enableStandardItemLighting();
                final int combinedLight = Wrapper.getMinecraft().world.getCombinedLight(entity.getPosition(), 0);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) (combinedLight % 65536), (float) (combinedLight / 65536));
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                final Render entityRenderObject = Wrapper.getMinecraft().getRenderManager().getEntityRenderObject(entity);
                 */
                final double n2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * n;
                final double n3 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * n;
                final double n4 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * n;
                if (entity instanceof EntityPlayer) {
                    GL11.glColor3f(5, 255, 240);
                } else if (entity instanceof EntityEnderCrystal) {
                    OutlineUtils2.setColor(ColorUtil.getClickGUIColor());
                } else if (EntityUtil.isPassive(entity)) {
                    if (future.getValue()) {
                        OutlineUtils2.setColor(new Color(0, 196, 0));
                    }
                    else {
                        OutlineUtils2.setColor(new Color(5, 255, 240));
                    }
                } else if (!EntityUtil.isPassive(entity) || entity instanceof EntitySpider) {
                    if (future.getValue()) {
                        OutlineUtils2.setColor(new Color(191, 57, 59));
                    }
                    else {
                        OutlineUtils2.setColor(new Color(255, 0, 102));
                    }
                } else {
                    OutlineUtils2.setColor(new Color(1.0f, 1.0f, 0.0f));
                }
                    /*
                    OutlineESP.feQE = true;
                    OutlineESP.krOE = true;
                    entityRenderObject.doRender(entity, n2 - mc.renderManager.renderPosX, n3 - mc.renderManager.renderPosY, n4 - mc.renderManager.renderPosZ, entity.rotationYaw, n);
                    OutlineESP.krOE = false;
                    OutlineESP.feQE = false;

                     */
                //mc.renderGlobal.renderManager.renderEntityStatic(entity, n, false);
                mc.renderGlobal.renderManager.playerRenderer.doRender(player, n2 - mc.renderManager.renderPosX, n3 - mc.renderManager.renderPosY, n4 - mc.renderManager.renderPosZ, entity.rotationYaw, n);
            }
        }
    }

    public static void oisD(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public static Color getEntityColor(Entity entity) {
        if (entity instanceof EntityPlayer) {
            if (Friends.isFriend(entity.getName())) {
                return new Color(0.27f, 0.7f, 0.92f);
            }
            else {
                final float distance = mc.player.getDistance(entity);
                if (distance <= 32.0f) {
                    return new Color(1.0f - ((distance / 32.0f) / 2), distance / 32.0f, 0.0f);
                }
                else {
                    return new Color(0.0f, 0.9f, 0.0f);
                }
            }
        } else if (entity instanceof EntityEnderCrystal) {
            return ColorUtil.getClickGUIColor();
        } else if (EntityUtil.isPassive(entity)) {
            if (future.getValue()) {
                return new Color(0, 196, 0);
            }
            else {
                return new Color(5, 255, 240);
            }
        } else if (!EntityUtil.isPassive(entity) || entity instanceof EntitySpider) {
            if (future.getValue()) {
                return new Color(191, 57, 59);
            }
            else {
                return new Color(255, 0, 102);
            }
        } else {
            return new Color(1.0f, 1.0f, 0.0f);
        }
    }
}
