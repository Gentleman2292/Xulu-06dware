package com.elementars.eclient.module.render;

import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.EntityUtil;
import com.elementars.eclient.util.GeometryMasks;
import com.elementars.eclient.util.XuluTessellator;
import dev.xulu.settings.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * @author 086
 */
public class EyeFinder extends Module {

    private final Value<Boolean> players = register(new Value<>("Players", this, true));
    private final Value<Boolean> mobs = register(new Value<>("Mobs", this, false));
    private final Value<Boolean> animals = register(new Value<>("Animals", this, false));

    public EyeFinder() {
        super("EyeFinder", "Draws line at an entity's eyes", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        mc.world.loadedEntityList.stream()
                .filter(EntityUtil::isLiving)
                .filter(entity -> mc.player != entity)
                .map(entity -> (EntityLivingBase) entity)
                .filter(entityLivingBase -> !entityLivingBase.isDead)
                .filter(entity -> (players.getValue() && entity instanceof EntityPlayer) || (EntityUtil.isPassive(entity) ? animals.getValue() : mobs.getValue()))
                .forEach(this::drawLine);
    }

    private void drawLine(EntityLivingBase e) {
        RayTraceResult result = e.rayTrace(6, Minecraft.getMinecraft().getRenderPartialTicks());
        if (result == null) return;
        Vec3d eyes = e.getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks());

        GlStateManager.enableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();

        double posx = eyes.x - mc.getRenderManager().renderPosX;
        double posy = eyes.y - mc.getRenderManager().renderPosY;
        double posz = eyes.z - mc.getRenderManager().renderPosZ;
        double posx2 = result.hitVec.x - mc.getRenderManager().renderPosX;
        double posy2 = result.hitVec.y - mc.getRenderManager().renderPosY;
        double posz2 = result.hitVec.z - mc.getRenderManager().renderPosZ;
        GL11.glColor4f(.2f, .1f, .3f, .8f);
        GlStateManager.glLineWidth(1.5f);

        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex3d(posx, posy, posz);
            GL11.glVertex3d(posx2, posy2, posz2);
            GL11.glVertex3d(posx2, posy2, posz2);
            GL11.glVertex3d(posx2, posy2, posz2);
        }
        GL11.glEnd();

        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            XuluTessellator.prepare(GL11.GL_QUADS);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            BlockPos b = result.getBlockPos();
            float x = b.x - .01f;
            float y = b.y - .01f;
            float z = b.z - .01f;
            XuluTessellator.drawBox(XuluTessellator.getBufferBuilder(), x, y, z, 1.01f, 1.01f, 1.01f, 51, 25, 73, 200, GeometryMasks.Quad.ALL);
            XuluTessellator.release();
        }

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
    }
}
