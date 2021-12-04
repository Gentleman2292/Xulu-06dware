package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.render.EnchantColor;
import com.elementars.eclient.module.render.NoRender;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {
    @Redirect(method = "renderEnchantedGlint", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/GlStateManager.color(FFFF)V", ordinal = 1))
    private static void renderEnchantedGlint(float red, float green, float blue, float alpha) {
        GlStateManager.color(Xulu.MODULE_MANAGER.getModule(EnchantColor.class).isToggled() ? EnchantColor.getColor(1, 1).getRed() : red, Xulu.MODULE_MANAGER.getModule(EnchantColor.class).isToggled() ? EnchantColor.getColor(1, 1).getGreen() : green, Xulu.MODULE_MANAGER.getModule(EnchantColor.class).isToggled() ? EnchantColor.getColor(1, 1).getBlue() : blue, Xulu.MODULE_MANAGER.getModule(EnchantColor.class).isToggled() ? EnchantColor.getColor(1, 1).getAlpha() : alpha);
    }

    @Inject(method = "renderEnchantedGlint", at = @At(value = "HEAD"), cancellable = true)
    private static void renderGlint(RenderLivingBase<?> p_188364_0_, EntityLivingBase p_188364_1_, ModelBase model, float p_188364_3_, float p_188364_4_, float p_188364_5_, float p_188364_6_, float p_188364_7_, float p_188364_8_, float p_188364_9_, CallbackInfo info) {
        if (Xulu.MODULE_MANAGER.getModuleT(NoRender.class).armor.getValue() && Xulu.MODULE_MANAGER.getModule(NoRender.class).isToggled()) {
            info.cancel();
        }
    }

    @Inject(method = "renderArmorLayer", at = @At(value = "HEAD"), cancellable = true)
    private void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo info) {
        if (Xulu.MODULE_MANAGER.getModuleT(NoRender.class).armor.getValue() && Xulu.MODULE_MANAGER.getModule(NoRender.class).isToggled()) {
            info.cancel();
        }
    }

    @Redirect(method = "renderArmorLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", ordinal = 1))
    private void color(float red, float green, float blue, float alpha) {
        NoRender noRender = Xulu.MODULE_MANAGER.getModuleT(NoRender.class);
        if (noRender.armorTrans.getValue() && noRender.isToggled()) {
            GlStateManager.color(red, green, blue, noRender.alpha.getValue() / 255f);
        } else {
            GlStateManager.color(red, green, blue, alpha);
        }
    }

    @Redirect(method = "renderArmorLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", ordinal = 2))
    private void color2(float red, float green, float blue, float alpha) {
        NoRender noRender = Xulu.MODULE_MANAGER.getModuleT(NoRender.class);
        if (noRender.armorTrans.getValue() && noRender.isToggled()) {
            GlStateManager.color(red, green, blue, noRender.alpha.getValue() / 255f);
        } else {
            GlStateManager.color(red, green, blue, alpha);
        }
    }

}
