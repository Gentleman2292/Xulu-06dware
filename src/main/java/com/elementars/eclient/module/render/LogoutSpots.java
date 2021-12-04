package com.elementars.eclient.module.render;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventPlayerConnect;
import com.elementars.eclient.event.events.LocalPlayerUpdateEvent;
import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.*;
import com.google.common.collect.Sets;

import java.awt.*;
import java.util.Set;
import java.util.UUID;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class LogoutSpots extends Module {

    private final RenderUtils renderUtils = new RenderUtils();

    private final Value<Boolean> cf = register(new Value<>("Custom Font", this, false));
    private final Value<Boolean> render = register(new Value<>("Render", this, true));
    private final Value<Boolean> box = register(new Value<>("Box", this, false));
    private final Value<Boolean> coords = register(new Value<>("Coordinates", this, true));
    private final Value<Integer> max_distance = register(new Value<>("Max Distance", this, 320, 0, 1000));
    private final Value<Boolean> print_message = register(new Value<>("Print Message", this, true));
    private final Value<Boolean> watermark = register(new Value<>("Watermark", this, true));
    private final Value<String> color = register(new Value<>("Color", this, "White", ColorTextUtils.colors));
    private final Value<Integer> red = register(new Value<>("Red", this, 255, 0, 255));
    private final Value<Integer> green = register(new Value<>("Green", this, 0, 0, 255));
    private final Value<Integer> blue = register(new Value<>("Blue", this, 0, 0, 255));

    private final SpotSet spots = new SpotSet();

    public LogoutSpots() {
        super("LogoutSpot","show where a player logs out", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    private void reset() {
        synchronized (spots) {
            spots.clear();
        }
    }

    @Override
    public void onEnable() {
        EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        EVENT_BUS.unregister(this);
        reset();
    }

    @EventTarget
    public void onPlayerConnect(EventPlayerConnect.Join event) {
        /*
        synchronized (spots) {
            if (spots.removeIf(spot -> spot.getId().equals(event.getUuid()))) {
                if (print_message.getValue()) Command.sendChatMessage(String.format("%s has joined!", event.getName()));
            }
        }
        */
        synchronized (spots) {
            Pair<Boolean, LogoutPos> check = spots.removeIfReturn(spot -> spot.getId().equals(event.getUuid()));
            if (check.getKey()) {
                if (print_message.getValue()) {
                    final double x = check.getValue().player.lastTickPosX;
                    final double y = check.getValue().player.lastTickPosY;
                    final double z = check.getValue().player.lastTickPosZ;
                    if (watermark.getValue())
                        Command.sendChatMessage(ColorTextUtils.getColor(color.getValue()) + String.format("%s has joined (%s, %s, %s)!", event.getName(), (int) x, (int) y, (int) z));
                    else
                        Command.sendRawChatMessage(ColorTextUtils.getColor(color.getValue()) + String.format("%s has joined (%s, %s, %s)!", event.getName(), (int) x, (int) y, (int) z));
                }
            }
        }
    }
    
    @EventTarget
    public void onPlayerDisconnect(EventPlayerConnect.Leave event) {
        if (mc.world == null) {
            return;
        }

        EntityPlayer player = mc.world.getPlayerEntityByUUID(event.getUuid());
        if (player != null && mc.player != null && !mc.player.equals(player)) {
            AxisAlignedBB bb = player.getEntityBoundingBox();
            /*
            synchronized (spots) {
                if (spots.add(
                        new LogoutPos(
                                event.getUuid(),
                                event.getName(),
                                new Vec3d(bb.maxX, bb.maxY, bb.maxZ),
                                new Vec3d(bb.minX, bb.minY, bb.minZ), bb))) {
                    if (print_message.getValue()) Command.sendChatMessage(String.format("%s has disconnected!", event.getName()));
                }
            }
            */
            synchronized (spots) {
                if (spots.add(
                        new LogoutPos(
                                event.getUuid(),
                                player.getName(),
                                //event.getName(),
                                new Vec3d(bb.maxX, bb.maxY, bb.maxZ),
                                new Vec3d(bb.minX, bb.minY, bb.minZ), bb, player))) {
                    if (print_message.getValue())
                        if (watermark.getValue())
                            Command.sendChatMessage(ColorTextUtils.getColor(color.getValue()) + String.format("%s has disconnected!", player.getName()));
                        else
                            Command.sendRawChatMessage(ColorTextUtils.getColor(color.getValue()) + String.format("%s has disconnected!", player.getName()));
                }
            }
        }
    }

    public void renderNametag(EntityPlayer player, double x, double y, double z) {
        GlStateManager.pushMatrix();
        //GlStateManager.enableTexture2D();
        FontRenderer var13 = Wrapper.getMinecraft().fontRenderer;
        String name = (player.getName() + " " + (coords.getValue() ? ChatFormatting.GRAY + "(" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")" : ChatFormatting.GRAY + "" + Math.round(mc.player.getDistance(player))));
        name = name.replace(".0", "");
        float distance = mc.player.getDistance(player);
        float var15 = ((distance / 5 <= 2 ? 2.0F : (distance / 5) * ((0.05075472f * 10) + 1)) * 2.5f) * (0.05075472f / 10);
        //float var14 = scale.getValue() * getNametagSize(player); //0.016666668F

        GL11.glTranslated((float) x, (float) y + 2.5 + (distance / 5 > 2 ? distance / 12 - 0.7: 0), (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-mc.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        //GL11.glRotatef(mc.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(mc.renderManager.playerViewX, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, (float)0);
        GL11.glScalef(-var15, -var15, var15);

        //GlStateManager.enableTexture2D();
        //GlStateManager.disableLighting();
        //lStateManager.disableDepth();

        // Disable lightning and depth test
        renderUtils.disableGlCap(GL_LIGHTING, GL_DEPTH_TEST);

        // Enable blend
        renderUtils.enableGlCap(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        int width;
        if (cf.getValue()) {
            width = Xulu.cFontRenderer.getStringWidth(name) / 2 + 1;
        } else {
            width = var13.getStringWidth(name) / 2;
        }

        //int color = (isFriend || isEnemy) && friendMode.getValue().equalsIgnoreCase("Box") ? (isFriend ? new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB() : new Color(Ered.getValue(), Egreen.getValue(), Eblue.getValue()).getRGB()) : ColorUtils.Colors.BLACK;
        if (box.getValue()) Gui.drawRect(-width - 2, 10, width + 1, 20, ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 120));
        //if (outline.getValue()) XuluTessellator.drawRectOutline(-width - 2, 10, width + 1, 20, 0.5D, ColorUtils.changeAlpha(color, 150));
        if (cf.getValue()) {
            Xulu.cFontRenderer.drawStringWithShadow(name, -width, 10, new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB());
        } else {
            mc.fontRenderer.drawStringWithShadow(name, -width, 11, new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB());
        }


        // Reset caps
        renderUtils.resetCaps();

        // Reset color
        GlStateManager.resetColor();
        glColor4f(1F, 1F, 1F, 1F);

        // Pop
        glPopMatrix();
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (!render.getValue()) {
            return;
        }

        GlStateManager.pushMatrix();
        synchronized (spots) {
            spots.forEach(logoutPos -> {
                final double x = logoutPos.lastTickPosX + (logoutPos.posX - logoutPos.lastTickPosX) * mc.timer.renderPartialTicks
                        - mc.renderManager.renderPosX;
                final double y = logoutPos.lastTickPosY + (logoutPos.posY - logoutPos.lastTickPosY) * mc.timer.renderPartialTicks
                        - mc.renderManager.renderPosY;
                final double z = logoutPos.lastTickPosZ + (logoutPos.posZ - logoutPos.lastTickPosZ) * mc.timer.renderPartialTicks
                        - mc.renderManager.renderPosZ;
                final AxisAlignedBB entityBox = logoutPos.bb;
                final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                        entityBox.minX - logoutPos.posX + x - 0.05D,
                        entityBox.minY - logoutPos.posY + y,
                        entityBox.minZ - logoutPos.posZ + z - 0.05D,
                        entityBox.maxX - logoutPos.posX + x + 0.05D,
                        entityBox.maxY - logoutPos.posY + y + 0.15D,
                        entityBox.maxZ - logoutPos.posZ + z + 0.05D
                );
                XuluTessellator.drawBoundingBox(axisAlignedBB, 1.5f, new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB());
                renderNametag(logoutPos.player, x, y, z);
            });
        }
        GlStateManager.popMatrix();

        /*
        synchronized (spots) {
            spots.forEach(logoutPos -> {
                final AxisAlignedBB aaBB = new AxisAlignedBB(
                        logoutPos.getMins().x,
                        logoutPos.getMins().y,
                        logoutPos.getMins().z,
                        logoutPos.getMaxs().x,
                        logoutPos.getMaxs().y,
                        logoutPos.getMaxs().z);
                XuluTessellator.drawBoundingBox(aaBB, 1.5f, ColorUtils.Colors.RED);
            });
        }
        */
    }


    @EventTarget
    public void onPlayerUpdate(LocalPlayerUpdateEvent event) {
        if (max_distance.getValue() > 0) {
            /*
            synchronized (spots) {
                spots.removeIf(
                        pos ->
                                mc.player.getPositionVector().distanceTo(pos.getTopVec())
                                        > max_distance.getValue());
            }
            */
            synchronized (spots) {
                spots.removeIf(
                        pos ->
                                mc.player.getPositionVector().distanceTo(pos.getTopVec())
                                        > max_distance.getValue());
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        reset();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        reset();
    }

    public class LogoutPos {

        final UUID id;
        final String name;
        final Vec3d maxs;
        final Vec3d mins;
        final AxisAlignedBB bb;
        final EntityPlayer player;
        final double posX;
        final double posY;
        final double posZ;
        final double lastTickPosX;
        final double lastTickPosY;
        final double lastTickPosZ;

        private LogoutPos(UUID uuid, String name, Vec3d maxs, Vec3d mins, AxisAlignedBB bb, final EntityPlayer player) {
            this.id = uuid;
            this.name = name;
            this.maxs = maxs;
            this.mins = mins;
            this.bb = bb;
            this.player = player;
            posX = player.posX;
            posY = player.posY;
            posZ = player.posZ;
            lastTickPosX = player.lastTickPosX;
            lastTickPosY = player.lastTickPosY;
            lastTickPosZ = player.lastTickPosZ;
        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public AxisAlignedBB getBb() {
            return bb;
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Vec3d getMaxs() {
            return maxs;
        }

        public Vec3d getMins() {
            return mins;
        }

        public Vec3d getTopVec() {
            return new Vec3d(
                    (getMins().x + getMaxs().x) / 2.D, getMaxs().y, (getMins().z + getMaxs().z) / 2.D);
        }

        @Override
        public boolean equals(Object other) {
            return this == other
                    || (other instanceof LogoutPos && getId().equals(((LogoutPos) other).getId()));
        }

        @Override
        public int hashCode() {
            return getId().hashCode();
        }
    }
}