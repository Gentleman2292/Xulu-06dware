package com.elementars.eclient.module.render;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.*;
import dev.xulu.settings.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.http.util.EntityUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

import static java.awt.Color.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by 086 on 11/12/2017.
 */
public class Tracers extends Module {
    
    private final Value<Boolean> spine = register(new Value<>("Spine", this, false));
    private final Value<Boolean> players = register(new Value<>("Players", this, true));
    private final Value<Boolean> friends = register(new Value<>("Friends", this, true));
    private final Value<Boolean> animals = register(new Value<>("Animals", this, false));
    private final Value<Boolean> mobs = register(new Value<>("Mobs", this, false));
    private final Value<Float> range = register(new Value<>("Range", this, 200f, 1f, 1000f));
    private final Value<Float> opacity = register(new Value<>("Opacity", this, 1f, 0f, 1f));

    public Tracers() {
        super("Tracers", "Draws a line to entities in render distance", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    @Override
    public void onEnable() {
        EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        EVENT_BUS.unregister(this);
    }

    HueCycler cycler = new HueCycler(3600);

    @Override
    public void onWorldRender(RenderEvent event) {
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().world.loadedEntityList.stream()
                .filter(EntityUtil::isLiving)
                .filter(entity -> !EntityUtil.isFakeLocalPlayer(entity))
                .filter(entity -> (entity instanceof EntityPlayer ? players.getValue() && mc.player != entity : (EntityUtil.isPassive(entity) ? animals.getValue() : mobs.getValue())))
                .filter(entity -> mc.player.getDistance(entity) < range.getValue())
                .forEach(entity -> {
                    int colour = getColour(entity);
                    if (colour == ColorUtils.Colors.RAINBOW) {
                        if (!friends.getValue()) return;
                        colour = cycler.current();
                    }
                    final float r = ((colour >>> 16) & 0xFF) / 255f;
                    final float g = ((colour >>> 8) & 0xFF) / 255f;
                    final float b = (colour & 0xFF) / 255f;
                    drawLineToEntity(entity, r, g, b, (float) opacity.getValue());
                });
        GlStateManager.popMatrix();
    }

    @Override
    public void onUpdate() {
        cycler.next();
    }

    private void drawRainbowToEntity(Entity entity, float opacity) {
        Vec3d eyes = new Vec3d(0, 0, 1)
                .rotatePitch(-(float)Math
                        .toRadians(Minecraft.getMinecraft().player.rotationPitch))
                .rotateYaw(-(float)Math
                        .toRadians(Minecraft.getMinecraft().player.rotationYaw));
        double[] xyz = interpolate(entity);
        double posx = xyz[0];
        double posy = xyz[1];
        double posz = xyz[2];
        double posx2 = eyes.x;
        double posy2 = eyes.y + mc.player.getEyeHeight();
        double posz2 = eyes.z;

        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1.5f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        cycler.reset();
        cycler.setNext(opacity);
        GlStateManager.disableLighting();
        GL11.glLoadIdentity();
        mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());

        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex3d(posx, posy, posz);
            GL11.glVertex3d(posx2, posy2, posz2);
            cycler.setNext(opacity);
            GL11.glVertex3d(posx2, posy2, posz2);
            GL11.glVertex3d(posx2, posy2, posz2);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor3d(1d,1d,1d);
        GlStateManager.enableLighting();
    }

    private int getColour(Entity entity) {
        if (entity instanceof EntityPlayer) {
            if (Friends.isFriend(entity.getName())) {
                return new Color(0.27f, 0.7f, 0.92f).getRGB();
            }
            else {
                final float distance = mc.player.getDistance(entity);
                if (distance <= 32.0f) {
                    return new Color(1.0f - ((distance / 32.0f) / 2), distance / 32.0f, 0.0f).getRGB();
                }
                else {
                    return new Color(0.0f, 0.9f, 0.0f).getRGB();
                }
            }
        }else{
            if (EntityUtil.isPassive(entity)) return ColorUtils.Colors.GREEN;
            else
                return ColorUtils.Colors.RED;
        }
    }

    public static double interpolate(double now, double then) {
        return then + (now - then) * mc.getRenderPartialTicks();
    }

    public static double[] interpolate(Entity entity) {
        double posX = interpolate(entity.posX, entity.lastTickPosX) - mc.getRenderManager().renderPosX;
        double posY = interpolate(entity.posY, entity.lastTickPosY) - mc.getRenderManager().renderPosY;
        double posZ = interpolate(entity.posZ, entity.lastTickPosZ) - mc.getRenderManager().renderPosZ;
        return new double[] { posX, posY, posZ };
    }

    public static void drawLineToEntity(Entity e, float red, float green, float blue, float opacity){
        double[] xyz = interpolate(e);
        drawLine(xyz[0],xyz[1],xyz[2], e.height, red, green, blue, opacity);
    }

    public static void drawLine(double posx, double posy, double posz, double up, float red, float green, float blue, float opacity)
    {
        Vec3d eyes = new Vec3d(0, 0, 1)
                .rotatePitch(-(float)Math
                        .toRadians(Minecraft.getMinecraft().player.rotationPitch))
                .rotateYaw(-(float)Math
                        .toRadians(Minecraft.getMinecraft().player.rotationYaw));

        drawLineFromPosToPos(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, posx, posy, posz, up, red, green, blue, opacity);
    }

    public static void drawLineFromPosToPos(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, float red, float green, float blue, float opacity){
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1.5f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, opacity);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        //GlStateManager.disableLighting();
        GL11.glLoadIdentity();
        final boolean bobbing = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());
        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex3d(posx, posy, posz);
            GL11.glVertex3d(posx2, posy2, posz2);
            if (Xulu.MODULE_MANAGER.getModuleT(Tracers.class).spine.getValue()) {
                GL11.glVertex3d(posx2, posy2, posz2);
                GL11.glVertex3d(posx2, posy2 + up, posz2);
            }
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        GL11.glColor3d(1d, 1d, 1d);
        //GlStateManager.enableLighting();
        mc.gameSettings.viewBobbing = bobbing;
    }

    private enum EntityType {
        PLAYER, HOSTILE, ANIMAL, INVALID
    }

    private EntityType getType(Entity entity) {
        if (EntityUtil.isDrivenByPlayer(entity) || EntityUtil.isFakeLocalPlayer(entity)) return EntityType.INVALID;
        if (EntityUtil.isPlayer(entity)) return EntityType.PLAYER;
        if (EntityUtil.isPassive(entity)) return EntityType.ANIMAL;
        if (!EntityUtil.isPassive(entity) || entity instanceof EntitySpider) return EntityType.HOSTILE;
        return EntityType.HOSTILE;
    }

    private class EntityRelations implements Comparable<EntityRelations> {

        private final Entity entity;
        private final EntityType entityType;

        public EntityRelations(Entity entity) {
            Objects.requireNonNull(entity);
            this.entity = entity;
            this.entityType = getType(entity);
        }

        public Entity getEntity() {
            return entity;
        }

        public EntityType getEntityType() {
            return entityType;
        }

        public Color getColor() {
            switch (entityType) {
                case PLAYER:
                    return YELLOW;
                case HOSTILE:
                    return RED;
                default:
                    return GREEN;
            }
        }

        public float getDepth() {
            switch (entityType) {
                case PLAYER:
                    return 15.f;
                case HOSTILE:
                    return 10.f;
                case ANIMAL:
                    return 5.f;
                default:
                    return 0.f;
            }
        }

        public boolean isOptionEnabled() {
            switch (entityType) {
                case PLAYER:
                    return players.getValue();
                case HOSTILE:
                    return mobs.getValue();
                default:
                    return animals.getValue();
            }
        }

        @Override
        public int compareTo(EntityRelations o) {
            return getEntityType().compareTo(o.getEntityType());
        }
    }
}
