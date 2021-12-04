package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.render.Chams;
import com.elementars.eclient.module.render.EnchantColor;
import com.elementars.eclient.module.render.ViewmodelChanger;
import com.elementars.eclient.util.Wrapper;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(RenderItem.class)
public class MixinRenderItem {

    @Shadow private void renderModel(IBakedModel model, int color, ItemStack stack) {}

    @ModifyArg(method = "renderEffect", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/RenderItem.renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"), index = 1)
    private int renderEffect(int oldValue) {
        return Xulu.MODULE_MANAGER.getModule(EnchantColor.class).isToggled() ? EnchantColor.getColor(1, 1).getRGB()
                : oldValue;
    }
    /*
    @Redirect(method = "renderItemModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V"))
    private void renderRightArmWear(RenderItem renderItem, ItemStack stack, IBakedModel model)  {
        if (Xulu.MODULE_MANAGER.getModule(ViewmodelChanger.class) != null && Xulu.MODULE_MANAGER.getModule(ViewmodelChanger.class).isToggled()) {
            GlStateManager.rotate(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).x.getValue() * 360, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).y.getValue() * 360, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).z.getValue() * 360, 0.0f, 0.0f, 1.0f);
            GlStateManager.translate(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).posX.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).posY.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).posZ.getValue());
            renderItem.renderItem(stack, model);
        }
    }
    */

    @Inject(method = "renderItemModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", shift = At.Shift.BEFORE))
    private void test(ItemStack stack, IBakedModel bakedmodel, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci)  {
        if (Xulu.MODULE_MANAGER.getModule(ViewmodelChanger.class) != null && Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).mode.getValue().isOK(leftHanded) && Xulu.MODULE_MANAGER.getModule(ViewmodelChanger.class).isToggled() && Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).item.getValue()) {
            if (!Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).pause.getValue() || (!Wrapper.getMinecraft().player.isHandActive() || isHandGood(Wrapper.getMinecraft().player.getActiveHand(), leftHanded))) {
                GlStateManager.scale(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).sizex.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).sizey.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).sizez.getValue());
                GlStateManager.rotate(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).x.getValue() * 360, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).y.getValue() * 360, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).z.getValue() * 360, 0.0f, 0.0f, 1.0f);
                GlStateManager.translate(Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).posX.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).posY.getValue(), Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).posZ.getValue());
            }
        }
    }

    private boolean isHandGood(EnumHand activeHand, boolean leftHandedRenderHand) {
        switch (activeHand) {
            case MAIN_HAND:
                return leftHandedRenderHand;
            case OFF_HAND:
                return !leftHandedRenderHand;
        }
        return false;
    }

    @Redirect(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V"))
    private void color(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
        if (Xulu.MODULE_MANAGER.getModule(ViewmodelChanger.class) != null && Xulu.MODULE_MANAGER.getModule(ViewmodelChanger.class).isToggled()) {
            GlStateManager.color(colorRed, colorGreen, colorBlue, Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).alpha.getValue());
        } else {
            GlStateManager.color(colorRed, colorGreen, colorBlue, colorAlpha);
        }
    }

    @Redirect(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/item/ItemStack;)V"))
    private void renderModelColor(RenderItem renderItem, IBakedModel model, ItemStack stack) {
        if (Xulu.MODULE_MANAGER.getModule(ViewmodelChanger.class) != null && Xulu.MODULE_MANAGER.getModule(ViewmodelChanger.class).isToggled()) {
            renderModel(model, new Color(1f, 1f, 1f, Xulu.MODULE_MANAGER.getModuleT(ViewmodelChanger.class).alpha.getValue()).getRGB(), stack);
        } else {
            renderModel(model, -1, stack);
        }
    }
}
