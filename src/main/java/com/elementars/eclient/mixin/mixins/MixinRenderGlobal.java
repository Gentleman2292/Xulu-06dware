package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.module.misc.AntiSound;
import com.elementars.eclient.module.render.Chams;
import com.elementars.eclient.module.render.OutlineESP;
import com.elementars.eclient.module.render.StorageESP;
import com.elementars.eclient.util.*;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.*;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.injection.*;

import java.awt.*;

@Mixin(value = { RenderGlobal.class }, priority = 9999)
public class MixinRenderGlobal {

    @Shadow public ShaderGroup entityOutlineShader;

    @Shadow public boolean entityOutlinesRendered;

    @Shadow public WorldClient world;

    @Redirect(method = "broadcastSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;playSound(DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FFZ)V"))
    private void playWitherSpawn(WorldClient worldClient, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
        if (!Xulu.MODULE_MANAGER.getModule(AntiSound.class).isToggled() || !Xulu.MODULE_MANAGER.getModuleT(AntiSound.class).witherSpawn.getValue()) {
            world.playSound(x, y, z, soundIn, category, volume, pitch, distanceDelay);
        }
    }
    @Redirect(method = "playEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;playSound(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FFZ)V", ordinal = 22))
    private void playWitherShoot(WorldClient worldClient, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
        if (!Xulu.MODULE_MANAGER.getModule(AntiSound.class).isToggled() || !Xulu.MODULE_MANAGER.getModuleT(AntiSound.class).wither.getValue()) {
            world.playSound(pos, soundIn, category, volume, pitch, distanceDelay);
        }
    }

    @Inject(method = "renderEntities" , at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;preRenderDamagedBlocks()V", shift = At.Shift.BEFORE) })
    public void renderEntities(final Entity entity, final ICamera camera, final float n, final CallbackInfo callbackInfo) {
        /*
        if (Xulu.MODULE_MANAGER.getModule(Chams.class) != null && Xulu.MODULE_MANAGER.getModule(Chams.class).isToggled()) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1f, -100000f);
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(0f, 1f, 1f, 0.25f);
            GL11.glLineWidth(1.5f);
            renderNormal(n);
            GL11.glPopAttrib();
            GL11.glPolygonOffset(1f, 100000f);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPopMatrix();
        }
        */

        if (Xulu.MODULE_MANAGER.getModuleByName("StorageESP") != null && Xulu.MODULE_MANAGER.getModuleByName("StorageESP").isToggled() && ((String) Xulu.VALUE_MANAGER.getValueByMod(Xulu.MODULE_MANAGER.getModuleByName("StorageESP"), "Mode").getValue()).equalsIgnoreCase("Shader")) {
            StorageESP storageESP = (StorageESP) Xulu.MODULE_MANAGER.getModuleByName("StorageESP");
            StorageESP.renderNormal(n);
            OutlineUtils2.VZWQ(Xulu.VALUE_MANAGER.<Float>getValueByMod(storageESP, "Line Width").getValue());
            StorageESP.renderNormal(n);
            OutlineUtils2.JLYv();
            StorageESP.renderColor(n);
            OutlineUtils2.feKn();
            OutlineUtils2.mptE(null);
            StorageESP.renderColor(n);
            OutlineUtils2.VdOT();
        }
    }


    public void renderNormal(final float n) {
        RenderHelper.enableStandardItemLighting();
        for (final Entity e : Wrapper.getMinecraft().world.loadedEntityList) {
            GL11.glPushMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            Wrapper.getMinecraft().renderGlobal.renderManager.renderEntity(e, e.posX - Wrapper.getMinecraft().renderManager.renderPosX, e.posY - Wrapper.getMinecraft().renderManager.renderPosY, e.posZ - Wrapper.getMinecraft().renderManager.renderPosZ, e.rotationYaw, n, false);
            GL11.glPopMatrix();
        }
    }
}