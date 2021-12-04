package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.render.Chams;
import com.elementars.eclient.module.render.OutlineESP;
import com.elementars.eclient.util.OutlineUtils;
import com.elementars.eclient.util.Wrapper;
import dev.xulu.newgui.util.ColorUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(RenderEnderCrystal.class)
public abstract class MixinRenderEnderCrystal {
    @Shadow public abstract void doRender(EntityEnderCrystal entity, double x, double y, double z, float entityYaw, float partialTicks);
    @Shadow public ModelBase modelEnderCrystal;
    @Shadow public ModelBase modelEnderCrystalNoBase;


    @Final
    @Shadow private static ResourceLocation ENDER_CRYSTAL_TEXTURES;

    @Redirect(method = "doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void render1(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (Xulu.MODULE_MANAGER.getModuleT(Chams.class).isToggled() && Chams.crystals.getValue() && Chams.mode.getValue().equalsIgnoreCase("ESP")) return;
        if (!Xulu.MODULE_MANAGER.getModuleT(OutlineESP.class).isToggled() || Xulu.MODULE_MANAGER.getModuleT(OutlineESP.class).renderCrystals.getValue()) {
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Redirect(method = "doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V", ordinal = 1))
    private void render2(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (Xulu.MODULE_MANAGER.getModuleT(Chams.class).isToggled() && Chams.crystals.getValue() && Chams.mode.getValue().equalsIgnoreCase("ESP")) return;
        if (!Xulu.MODULE_MANAGER.getModuleT(OutlineESP.class).isToggled() || Xulu.MODULE_MANAGER.getModuleT(OutlineESP.class).renderCrystals.getValue()) {
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V", at = @At("RETURN"), cancellable = true)
    public void IdoRender(EntityEnderCrystal entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callback) {
        OutlineESP outlineESP = Xulu.MODULE_MANAGER.getModuleT(OutlineESP.class);
        Chams chams = Xulu.MODULE_MANAGER.getModuleT(Chams.class);
        if (outlineESP == null || chams == null) return;
        if (chams.isToggled() && chams.crystals.getValue() && Chams.mode.getValue().equalsIgnoreCase("ESP")) {
            Color c = chams.rainbow.getValue() ? new Color(Xulu.rgb) : new Color(chams.r.getValue(), chams.g.getValue(), chams.b.getValue());
            GL11.glPushMatrix();
            float f = (float) entity.innerRotation + partialTicks;
            GlStateManager.translate(x, y, z);
            Wrapper.getMinecraft().renderManager.renderEngine.bindTexture(ENDER_CRYSTAL_TEXTURES);
            float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
            f1 = f1 * f1 + f1;
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1f, -10000000f);
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            if (!chams.lines.getValue()) {
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
            GL11.glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, chams.a.getValue() / 255f);
            if (chams.lines.getValue()) GL11.glLineWidth(chams.width.getValue());
            if (entity.shouldShowBottom()) {
                this.modelEnderCrystal.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
            } else {
                this.modelEnderCrystalNoBase.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
            }
            GL11.glPopAttrib();
            GL11.glPolygonOffset(1f, 100000f);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPopMatrix();
        } else if (chams.isToggled() && chams.crystals.getValue() && Chams.mode.getValue().equalsIgnoreCase("Walls")) {
            GL11.glPushMatrix();
            float f = (float) entity.innerRotation + partialTicks;
            GlStateManager.translate(x, y, z);
            Wrapper.getMinecraft().renderManager.renderEngine.bindTexture(ENDER_CRYSTAL_TEXTURES);
            float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
            f1 = f1 * f1 + f1;
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(chams.Wr.getValue() / 255f, chams.Wg.getValue() / 255f, chams.Wb.getValue() / 255f, 1f);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            if (entity.shouldShowBottom()) {
                this.modelEnderCrystal.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
            } else {
                this.modelEnderCrystalNoBase.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(chams.Vr.getValue() / 255f, chams.Vg.getValue() / 255f, chams.Vb.getValue() / 255f, 1f);
            if (entity.shouldShowBottom()) {
                this.modelEnderCrystal.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
            } else {
                this.modelEnderCrystalNoBase.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
            }
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1f, 1f, 1f, 1f);
            GlStateManager.popMatrix();
        }
        if (outlineESP.mode.getValue().equalsIgnoreCase("Shader") && outlineESP.crystals.getValue() && outlineESP.isToggled()) {
            entity.setGlowing(true);
        } else {
            entity.setGlowing(false);
            if (outlineESP.crystals.getValue() && outlineESP.isToggled()) {
                if (outlineESP.mode.getValue().equalsIgnoreCase("Outline")){
                    float f = (float) entity.innerRotation + partialTicks;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x, y, z);
                    Wrapper.getMinecraft().renderManager.renderEngine.bindTexture(ENDER_CRYSTAL_TEXTURES);
                    float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
                    f1 = f1 * f1 + f1;
                    GL11.glLineWidth(5.0F);
                    if (entity.shouldShowBottom()) {
                        this.modelEnderCrystal.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
                    } else {
                        this.modelEnderCrystalNoBase.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
                    }
                    OutlineUtils.renderOne(outlineESP.width.getValue());
                    if (entity.shouldShowBottom()) {
                        this.modelEnderCrystal.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
                    } else {
                        this.modelEnderCrystalNoBase.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
                    }
                    OutlineUtils.renderTwo();
                    if (entity.shouldShowBottom()) {
                        this.modelEnderCrystal.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
                    } else {
                        this.modelEnderCrystalNoBase.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
                    }
                    OutlineUtils.renderThree();
                    OutlineUtils.renderFour(outlineESP.color.getValue().equalsIgnoreCase("Rainbow") ? new Color(Xulu.rgb) : ColorUtil.getClickGUIColor());
                    if (entity.shouldShowBottom()) {
                        this.modelEnderCrystal.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
                    } else {
                        this.modelEnderCrystalNoBase.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
                    }
                    OutlineUtils.renderFive();
                    GlStateManager.popMatrix();
                } else {
                    float f = (float) entity.innerRotation + partialTicks;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x, y, z);
                    Wrapper.getMinecraft().renderManager.renderEngine.bindTexture(ENDER_CRYSTAL_TEXTURES);
                    float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
                    f1 = f1 * f1 + f1;
                    //GL11.glPushMatrix();
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    OutlineUtils.setColor(ColorUtil.getClickGUIColor());
                    GL11.glLineWidth(outlineESP.width.getValue());
                    if (entity.shouldShowBottom()) {
                        this.modelEnderCrystal.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
                    } else {
                        this.modelEnderCrystalNoBase.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
                    }
                    GL11.glPopAttrib();
                    GL11.glPopMatrix();
                }
            }
        }
    }
}
