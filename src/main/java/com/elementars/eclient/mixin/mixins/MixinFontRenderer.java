package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.module.core.Global;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.client.renderer.GlStateManager.enableAlpha;

@Mixin(FontRenderer.class)
public class MixinFontRenderer {

    @Shadow private int renderString(String text, float x, float y, int color, boolean dropShadow)
    { return 0;}

    @Redirect(
            method = "drawStringWithShadow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;FFIZ)I"
            )
    )
    private int drawStringMaybeWithShadow(FontRenderer fontRenderer, String text, float x, float y, int color, boolean dropShadow) {
        if (Global.textShadow.getValue()) {
            return fontRenderer.drawString(text, x, y, color, true);
        } else {
            return fontRenderer.drawString(text, x, y, color, false);
        }
    }

    @Redirect(
            method = "drawString(Ljava/lang/String;FFIZ)I",
            at = @At(
                    value = "INVOKE",
                    ordinal = 0,
                    target = "Lnet/minecraft/client/gui/FontRenderer;renderString(Ljava/lang/String;FFIZ)I"
            )
    )
    private int drawStringMore(FontRenderer fontRenderer, String text, float x, float y, int color, boolean dropShadow) {
        if (Global.shortShadow != null && Global.shortShadow.getValue()) {
            x -= 0.4f;
            y -= 0.4f;
        }
        return this.renderString(text, x, y, color, dropShadow);
    }

}
