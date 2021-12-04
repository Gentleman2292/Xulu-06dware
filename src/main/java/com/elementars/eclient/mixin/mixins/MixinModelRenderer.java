package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.render.ViewmodelChanger;
import com.elementars.eclient.util.Wrapper;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Elementars
 * @since 7/19/2020 - 5:25 PM
 */
@Mixin(ModelRenderer.class)
public class MixinModelRenderer {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;callList(I)V", shift = At.Shift.BEFORE))
    private void test(float scale, CallbackInfo ci) {
        if (ModelRenderer.class.cast(this) == Wrapper.getMinecraft().renderManager.playerRenderer.getMainModel().bipedRightArm) {
            if (Xulu.MODULE_MANAGER.getModule(ViewmodelChanger.class) != null && Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).hand.getValue() && Xulu.MODULE_MANAGER.getModule(ViewmodelChanger.class).isToggled()) {
                GlStateManager.scale(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).sizex.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).sizey.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).sizez.getValue());
                GlStateManager.translate(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).posX.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).posY.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).posZ.getValue());
            }
        } else if (ModelRenderer.class.cast(this) == Wrapper.getMinecraft().renderManager.playerRenderer.getMainModel().bipedRightArmwear) {
            if (Xulu.MODULE_MANAGER.getModule(ViewmodelChanger.class) != null && Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).hand.getValue() && Xulu.MODULE_MANAGER.getModule(ViewmodelChanger.class).isToggled()) {
                GlStateManager.scale(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).sizex.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).sizey.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).sizez.getValue());
                GlStateManager.translate(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).posX.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).posY.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).posZ.getValue());
            }
        }
    }
}
