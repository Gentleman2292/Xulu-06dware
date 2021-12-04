package com.elementars.eclient.module.render;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.Event;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventModelPlayerRender;
import com.elementars.eclient.event.events.EventModelRender;
import com.elementars.eclient.event.events.EventPostRenderLayers;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.EntityUtil;
import com.elementars.eclient.util.MathUtil;
import com.elementars.eclient.util.OutlineUtils;
import dev.xulu.settings.Value;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Created by 086 on 12/12/2017.
 */
public class Chams extends Module {

    public static Value<String> mode;
    public final Value<Boolean> hand;
    public final Value<Boolean> lines;
    public final Value<Float> width;
    public final Value<Boolean> friendColor;
    public final Value<Boolean> rainbow;
    public final Value<Integer> r;
    public final Value<Integer> g;
    public final Value<Integer> b;
    public final Value<Integer> a;
    private static Value<Boolean> players;
    private static Value<Boolean> animals;
    private static Value<Boolean> mobs;
    public static Value<Boolean> crystals;

    //walls
    public final Value<Integer> Vr;
    public final Value<Integer> Vg;
    public final Value<Integer> Vb;
    public final Value<Integer> Wr;
    public final Value<Integer> Wg;
    public final Value<Integer> Wb;

    public Chams() {
        super("Chams", "See entities through walls", Keyboard.KEY_NONE, Category.RENDER, true);
        mode = register(new Value<>("Mode", this, "ESP", new String[]{
                "ESP", "Normal", "Walls"
        }));
        Vr = register(new Value<>("Visible Red", this, 255, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("Walls"));
        Vg = register(new Value<>("Visible Green", this, 0, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("Walls"));
        Vb = register(new Value<>("Visible Blue", this, 0, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("Walls"));
        Wr = register(new Value<>("Wall Red", this, 0, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("Walls"));
        Wg = register(new Value<>("Wall Green", this, 255, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("Walls"));
        Wb = register(new Value<>("Wall Blue", this, 0, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("Walls"));
        hand = register(new Value<>("Hand", this, true))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        lines = register(new Value<>("Lines", this, false))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        width = register(new Value<>("Width", this, 1f, 0f, 10f))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        friendColor = register(new Value<>("Friends", this, true))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        rainbow = register(new Value<>("Rainbow", this, false))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        r = register(new Value<>("Red", this, 0, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        g = register(new Value<>("Green", this, 255, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        b = register(new Value<>("Blue", this, 255, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        a = register(new Value<>("Alpha", this, 63, 0, 255))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP"));
        players = register(new Value<>("Players", this, true));
        animals = register(new Value<>("Animals", this, false));
        mobs = register(new Value<>("Mobs", this, false));
        crystals = register(new Value<>("Crystals", this, true))
                .visibleWhen(value -> mode.getValue().equalsIgnoreCase("ESP") || mode.getValue().equalsIgnoreCase("Walls"));
    }

    public static boolean renderChams(Entity entity) {
        return mode.getValue().equalsIgnoreCase("ESP") ? false : (entity instanceof EntityPlayer ? players.getValue() : (EntityUtil.isPassive(entity) ? animals.getValue() : mobs.getValue()));
    }

    @EventTarget
    public void renderPre(EventModelRender event) {
        if (mode.getValue().equalsIgnoreCase("Walls")) {
            if (event.entity instanceof EntityOtherPlayerMP && !players.getValue()) return;
            if (EntityUtil.isPassive(event.entity) && !animals.getValue()) return;
            if (!EntityUtil.isPassive(event.entity) && !mobs.getValue()) return;
            GlStateManager.pushMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(Wr.getValue() / 255f, Wg.getValue() / 255f, Wb.getValue() / 255f, 1f);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(Vr.getValue() / 255f, Vg.getValue() / 255f, Vb.getValue() / 255f, 1f);
            event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1f, 1f, 1f, 1f);
            GlStateManager.popMatrix();
            event.setCancelled(true);
        } else if (mode.getValue().equalsIgnoreCase("ESP")) {
            Color c = friendColor.getValue() && Friends.isFriend(event.entity.getName()) ? new Color(0.27f, 0.7f, 0.92f) : rainbow.getValue() ? new Color(Xulu.rgb) : new Color(r.getValue(), g.getValue(), b.getValue());
            if (event.getEventState() == Event.State.PRE) {
                if (!(event.entity instanceof EntityOtherPlayerMP)) {
                    if (EntityUtil.isPassive(event.entity) && animals.getValue()) {
                        GL11.glPushMatrix();
                        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                        GL11.glPolygonOffset(1f, -100000f);
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        if (!lines.getValue()) {
                            GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
                        } else {
                            GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
                        }
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glEnable(GL11.GL_LINE_SMOOTH);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, a.getValue() / 255f);
                        if (lines.getValue()) GL11.glLineWidth(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        GL11.glPopAttrib();
                        GL11.glPolygonOffset(1f, 100000f);
                        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                        GL11.glPopMatrix();
                        event.setCancelled(true);
                    } else if (!EntityUtil.isPassive(event.entity) && mobs.getValue()) {
                        GL11.glPushMatrix();
                        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                        GL11.glPolygonOffset(1f, -100000f);
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        if (!lines.getValue()) {
                            GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
                        } else {
                            GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
                        }
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glEnable(GL11.GL_LINE_SMOOTH);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, a.getValue() / 255f);
                        if (lines.getValue()) GL11.glLineWidth(width.getValue());
                        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleFactor);
                        GL11.glPopAttrib();
                        GL11.glPolygonOffset(1f, 100000f);
                        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                        GL11.glPopMatrix();
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventTarget
    public void renderPost(EventPostRenderLayers event) {
        if (mode.getValue().equalsIgnoreCase("ESP")) {
            if (!event.renderer.bindEntityTexture(event.entity)) return;
            Color c = friendColor.getValue() && Friends.isFriend(event.entity.getName()) ? new Color(0.27f, 0.7f, 0.92f) : rainbow.getValue() ? new Color(Xulu.rgb) : new Color(r.getValue(), g.getValue(), b.getValue());
            if (event.getEventState() == Event.State.PRE) {
                if (event.entity instanceof EntityOtherPlayerMP && players.getValue()) {
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                    GL11.glPolygonOffset(1f, -100000f);
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    if (!lines.getValue()) {
                        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
                    } else {
                        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
                    }
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, a.getValue() / 255f / 2f);
                    if (lines.getValue()) GL11.glLineWidth(width.getValue());
                    event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scaleIn);
                    GL11.glPopAttrib();
                    GL11.glPolygonOffset(1f, 100000f);
                    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    @EventTarget
    public void onPlayerModel(EventModelPlayerRender event) {
        if (mode.getValue().equalsIgnoreCase("ESP") && players.getValue()) {
            Color c = friendColor.getValue() && Friends.isFriend(event.entity.getName()) ? new Color(0.27f, 0.7f, 0.92f) : rainbow.getValue() ? new Color(Xulu.rgb) : new Color(r.getValue(), g.getValue(), b.getValue());
            switch (event.getEventState()) {
                case PRE:
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                    GL11.glPolygonOffset(1f, -10000000f);
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    if (!lines.getValue()) {
                        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
                    } else {
                        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
                    }
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, a.getValue() / 255f / 2f);
                    if (lines.getValue()) GL11.glLineWidth(width.getValue());
                    break;
                case POST:
                    GL11.glPopAttrib();
                    GL11.glPolygonOffset(1f, 10000000f);
                    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                    GL11.glPopMatrix();
                    break;
            }
        }
    }

}
