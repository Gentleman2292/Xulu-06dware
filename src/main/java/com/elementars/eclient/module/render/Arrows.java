package com.elementars.eclient.module.render;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.*;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Objects;

import static java.awt.Color.*;
import static net.minecraft.client.renderer.GlStateManager.*;

public class Arrows extends Module {

    enum Mode {
        ARROWS,
        LINES,
        BOTH,
        ;
    }

    public Arrows() {
        super("Arrows", "2d Tracers", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    public final Value<Mode> mode = register(new Value<>("Mode", this, Mode.ARROWS, Mode.values()));
    public final Value<Integer> sizeX = register(new Value<>("Dimension X", this, 450, 0, 1000));
    public final Value<Integer> sizeY = register(new Value<>("Dimension Y", this, 285, 0, 1000));
    public final Value<Float> sizeT = register(new Value<>("Triangle Size", this, 2f, 1f, 5f));
    public final Value<Boolean> rainbow = register(new Value<>("Rainbow Players", this, false));
    public final Value<String> colorMode = register(new Value<>("Color Mode", this, "RGB", new String[]{
            "RGB", "Tracers"
    }));
    public final Value<Integer> red = register(new Value<>("Player Red", this, 0, 0, 255))
            .visibleWhen(t -> colorMode.getValue().equalsIgnoreCase("RGB"));
    public final Value<Integer> green = register(new Value<>("Player Green", this, 255, 0, 255))
            .visibleWhen(t -> colorMode.getValue().equalsIgnoreCase("RGB"));
    public final Value<Integer> blue = register(new Value<>("Player Blue", this, 0, 0, 255))
            .visibleWhen(t -> colorMode.getValue().equalsIgnoreCase("RGB"));
    public final Value<Integer> Fr = register(new Value<>("Friend Red", this, 0, 0, 255));
    public final Value<Integer> Fg = register(new Value<>("Friend Green", this, 200, 0, 255));
    public final Value<Integer> Fb = register(new Value<>("Friend Blue", this, 255, 0, 255));
    public final Value<Integer> alpha = register(new Value<>("Alpha", this, 255, 0, 255));
    public final Value<Boolean> outline = register(new Value<>("Outline", this, false));
    public final Value<Boolean> black = register(new Value<>("Black Outline", this, true))
            .visibleWhen(aBoolean -> outline.getValue());
    public final Value<Boolean> antialias = register(new Value<>("Antialias", this, true));
    public final Value<Boolean> players = register(new Value<>("Players", this, true));
    public final Value<Boolean> hostile = register(new Value<>("Mobs", this, true));
    public final Value<Boolean> friendly = register(new Value<>("Animals", this, true));


    public void onRender() {
        ScaledResolution sr = new ScaledResolution(mc);

        enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();
        GlStateManager.enableAlpha();

        if (antialias.getValue()) {
            GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
        }

        final Mode dm = mode.getValue();

        final double cx = sr.getScaledWidth_double() / 2.f;
        final double cy = sr.getScaledHeight_double() / 2.f;

        mc.world.loadedEntityList.stream()
                .filter(entity -> !Objects.equals(entity, mc.player))
                .filter(EntityLivingBase.class::isInstance)
                .map(EntityRelations::new)
                .filter(er -> !er.getRelationship().equals(RelationState.INVALID))
                .filter(EntityRelations::isOptionEnabled)
                .forEach(
                        er -> {
                            Entity entity = er.getEntity();
                            RelationState relationship = er.getRelationship();

                            Vec3d entityPos =
                                    EntityUtil.getInterpolatedEyePos(entity, mc.getRenderPartialTicks());
                            Plane screenPos = VectorUtils.toScreen(entityPos);

                            Color color = er.getColor();
                            if (colorMode.getValue().equalsIgnoreCase("Tracers")) {
                                final float distance = mc.player.getDistance(entity);
                                if (distance <= 32.0f) {
                                    Color c = new Color(1.0f - ((distance / 32.0f) / 2), distance / 32.0f, 0.0f);
                                    GL11.glColor4f(c.getRed() / 255f,
                                            c.getGreen() / 255f,
                                            c.getBlue() / 255f,
                                            alpha.getValue() / 255f);
                                }
                                else {
                                    Color c = new Color(0.0f, 0.9f, 0.0f);
                                    GL11.glColor4f(c.getRed() / 255f,
                                            c.getGreen() / 255f,
                                            c.getBlue() / 255f,
                                            alpha.getValue() / 255f);
                                }
                            } else {
                                GL11.glColor4f(color.getRed() / 255f,
                                        color.getGreen() / 255f,
                                        color.getBlue() / 255f,
                                        alpha.getValue() / 255f);
                            }

                            GlStateManager.translate(0, 0, er.getDepth());

                            if (dm.equals(Mode.BOTH) || dm.equals(Mode.ARROWS)) {
                                if (!screenPos.isVisible()) {
                                    // get position on ellipse

                                    // dimensions of the ellipse
                                    final double dx = cx - sizeX.getValue();
                                    final double dy = cy - sizeY.getValue();

                                    // ellipse = x^2/a^2 + y^2/b^2 = 1
                                    // e = (pos - C) / d
                                    //  C = center vector
                                    //  d = dimensions
                                    double ex = (screenPos.getX() - cx) / dx;
                                    double ey = (screenPos.getY() - cy) / dy;

                                    // normalize
                                    // n = u/|u|
                                    double m = Math.abs(Math.sqrt(ex * ex + ey * ey));
                                    double nx = ex / m;
                                    double ny = ey / m;

                                    // scale
                                    // p = C + dot(n,d)
                                    double x = cx + nx * dx;
                                    double y = cy + ny * dy;

                                    // --------------------
                                    // now rotate triangle

                                    // point - center
                                    // w = <px - cx, py - cy>
                                    double wx = x - cx;
                                    double wy = y - cy;

                                    // u = <w, 0>
                                    double ux = sr.getScaledWidth_double();
                                    double uy = 0.D;

                                    // |u|
                                    double mu = Math.sqrt(ux * ux + uy * uy);
                                    // |w|
                                    double mw = Math.sqrt(wx * wx + wy * wy);

                                    // theta = dot(u,w)/(|u|*|w|)
                                    double ang = Math.toDegrees(Math.acos((ux * wx + uy * wy) / (mu * mw)));

                                    // don't allow NaN angles
                                    if (ang == Float.NaN) {
                                        ang = 0;
                                    }

                                    // invert
                                    if (y < cy) {
                                        ang *= -1;
                                    }

                                    // normalize
                                    ang = (float) AngleHelper.normalizeInDegrees(ang);

                                    // --------------------

                                    int size = relationship.equals(RelationState.PLAYER) ? 8 : 5;

                                    pushMatrix();

                                    GlStateManager.translate(x, y, 0);
                                    GlStateManager.rotate((float) ang, 0.f, 0.f, size / 2.f);

                                    if (colorMode.getValue().equalsIgnoreCase("Tracers")) {
                                        final float distance = mc.player.getDistance(entity);
                                        if (distance <= 32.0f) {
                                            Color c = new Color(1.0f - ((distance / 32.0f) / 2), distance / 32.0f, 0.0f);
                                            GL11.glColor4f(c.getRed() / 255f,
                                                    c.getGreen() / 255f,
                                                    c.getBlue() / 255f,
                                                    alpha.getValue() / 255f);
                                        }
                                        else {
                                            Color c = new Color(0.0f, 0.9f, 0.0f);
                                            GL11.glColor4f(c.getRed() / 255f,
                                                    c.getGreen() / 255f,
                                                    c.getBlue() / 255f,
                                                    alpha.getValue() / 255f);
                                        }
                                    } else {
                                        GL11.glColor4f(color.getRed() / 255f,
                                                color.getGreen() / 255f,
                                                color.getBlue() / 255f,
                                                alpha.getValue() / 255f);
                                    }

                                    glBegin(GL11.GL_TRIANGLES);
                                    {
                                        GL11.glVertex2d(0, 0);
                                        GL11.glVertex2d(-size, -size / sizeT.getValue());
                                        GL11.glVertex2d(-size, size / sizeT.getValue());
                                    }
                                    glEnd();

                                    if (outline.getValue()) {
                                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                                        GL11.glLineWidth(1.5f);
                                        if (black.getValue())
                                            GL11.glColor4f(0.f, 0.f, 0.f, 1.f);
                                        glBegin(GL11.GL_TRIANGLES);
                                        {
                                            GL11.glVertex2d(0, 0);
                                            GL11.glVertex2d(-size, -size / sizeT.getValue());
                                            GL11.glVertex2d(-size, size / sizeT.getValue());
                                        }
                                        glEnd();
                                        GL11.glPopAttrib();
                                    }

                                    popMatrix();
                                }
                            }

                            if (dm.equals(Mode.BOTH) || dm.equals(Mode.LINES)) {
                                glBegin(GL11.GL_LINES);
                                {
                                    GL11.glVertex2d(cx, cy);
                                    GL11.glVertex2d(screenPos.getX(), screenPos.getY());
                                }
                                glEnd();
                            }

                            GlStateManager.translate(0, 0, -er.getDepth());
                        });

        GlStateManager.enableTexture2D();
        disableBlend();

        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
    }

    public enum RelationState {
        PLAYER,
        HOSTILE,
        FRIENDLY,
        INVALID
    }

    private class EntityRelations implements Comparable<EntityRelations> {

        private final Entity entity;
        private final RelationState relationship;

        public EntityRelations(Entity entity) {
            Objects.requireNonNull(entity);
            this.entity = entity;
            if (EntityUtil.isFakeLocalPlayer(entity)) {
                this.relationship = RelationState.INVALID;
            } else if (entity instanceof EntityPlayer) {
                this.relationship = RelationState.PLAYER;
            } else if (EntityUtil.isPassive(entity)) {
                this.relationship = RelationState.FRIENDLY;
            } else {
                this.relationship = RelationState.HOSTILE;
            }
        }

        public Entity getEntity() {
            return entity;
        }

        public RelationState getRelationship() {
            return relationship;
        }

        public Color getColor() {
            switch (relationship) {
                case PLAYER:
                    if (Friends.isFriend(getEntity().getName())) {
                        return new Color(Fr.getValue(), Fg.getValue(), Fb.getValue());
                    } else {
                        if (rainbow.getValue()) {
                            return new Color(Xulu.rgb);
                        } else {
                            return new Color(red.getValue(), green.getValue(), blue.getValue());
                        }
                    }
                case HOSTILE:
                    return RED;
                default:
                    return YELLOW;
            }
        }

        public float getDepth() {
            switch (relationship) {
                case PLAYER:
                    return 15.f;
                case HOSTILE:
                    return 10.f;
                default:
                    return 0.f;
            }
        }

        public boolean isOptionEnabled() {
            switch (relationship) {
                case PLAYER:
                    return players.getValue();
                case HOSTILE:
                    return hostile.getValue();
                default:
                    return friendly.getValue();
            }
        }

        @Override
        public int compareTo(EntityRelations o) {
            return getRelationship().compareTo(o.getRelationship());
        }
    }
}