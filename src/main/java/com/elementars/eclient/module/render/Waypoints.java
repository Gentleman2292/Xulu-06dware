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
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.settings.Value;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Elementars
 * @since 6/24/2020 - 10:32 AM
 */
public class Waypoints extends Module {

    private final RenderUtils renderUtils = new RenderUtils();

    private final Value<Boolean> cf = register(new Value<>("Custom Font", this, false));
    private final Value<Boolean> render = register(new Value<>("Render", this, true));
    private final Value<String> mode = register(new Value<>("Mode", this, "Coordinates", new String[]{
            "Coordinates", "Distance", "Safe"
    }));
    private final Value<Integer> red = register(new Value<>("Red", this, 255, 0, 255));
    private final Value<Integer> green = register(new Value<>("Green", this, 0, 0, 255));
    private final Value<Integer> blue = register(new Value<>("Blue", this, 0, 0, 255));
    private final Value<Integer> alpha = register(new Value<>("Alpha", this, 150, 0, 255));

    public static final Set<Waypoint> WAYPOINTS = new HashSet<>();

    public Waypoints() {
        super("Waypoints","Shows locations of waypoints", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    @Override
    public void onEnable() {
        EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        EVENT_BUS.unregister(this);
    }

    private void renderNametag2(Waypoint waypoint) {
        String name = waypoint.getName() + (mode.getValue().equalsIgnoreCase("Safe") ? "" : " " + (mode.getValue().equalsIgnoreCase("Coordinates") ? ChatFormatting.GRAY + "(" + (int)waypoint.getPos().x + ", " + (int)waypoint.getPos().y + ", " + (int)waypoint.getPos().z + ")" : ChatFormatting.GRAY + "" + Math.round(mc.player.getDistance(waypoint.getPos().x, waypoint.getPos().y, waypoint.getPos().z))));
        Plane pos = VectorUtils.toScreen(waypoint.getPos().getX() + .5, waypoint.getPos().getY() + 1.5, waypoint.getPos().getZ() + .5);
        if (cf.getValue()) {
            Xulu.cFontRenderer.drawStringWithShadow(name, (float) pos.getX() - Xulu.cFontRenderer.getStringWidth(name) / 2, (float) pos.getY() - Xulu.cFontRenderer.getHeight() / 2, new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB());
        } else {
            fontRenderer.drawStringWithShadow(name, (float) pos.getX() - fontRenderer.getStringWidth(name) / 2, (float) pos.getY() - fontRenderer.FONT_HEIGHT / 2, new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB());
        }
    }

    @Override
    public void onRender() {
        synchronized (WAYPOINTS) {
            WAYPOINTS.forEach(waypoint -> {
                if (mc.player.dimension == waypoint.dimension) {
                    renderNametag2(waypoint);
                }
            });
        }
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (!render.getValue()) {
            return;
        }

        GlStateManager.pushMatrix();
        synchronized (WAYPOINTS) {
            WAYPOINTS.forEach(waypoint -> {
                if (mc.player.dimension == waypoint.dimension) {
                    XuluTessellator.prepare(GL_QUADS);
                    XuluTessellator.drawBox(waypoint.getPos(), new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()).getRGB(), 63);
                    XuluTessellator.release();
                }
            });
        }
        GlStateManager.popMatrix();
    }

    public static class Waypoint {

        final UUID id;
        final String name;
        final BlockPos pos;
        final AxisAlignedBB bb;
        final int dimension;

        public Waypoint(final UUID id, final String name, final BlockPos pos, final AxisAlignedBB bb, final int dimension) {
            this.id = id;
            this.name = name;
            this.pos = pos;
            this.bb = bb;
            this.dimension = dimension;
        }

        public AxisAlignedBB getBb() {
            return bb;
        }

        public UUID getId() {
            return id;
        }

        public BlockPos getPos() {
            return pos;
        }

        public String getName() {
            return name;
        }

        public int getDimension() {
            return dimension;
        }

        @Override
        public boolean equals(Object other) {
            return this == other
                    || (other instanceof Waypoint && getId().equals(((Waypoint) other).getId()));
        }

        @Override
        public int hashCode() {
            return getId().hashCode();
        }
    }
}