package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.Xulu;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Inject(method = "renderPotionEffects", at = {@At("HEAD")}, cancellable = true)
    private void onRenderPotionEffects(CallbackInfo info) {
        if((boolean) Xulu.VALUE_MANAGER.getValueByName("Hide Potions").getValue()) {
            info.cancel();
        }
    }
}
