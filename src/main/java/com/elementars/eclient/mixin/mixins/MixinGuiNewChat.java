package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.ModuleManager;
import com.elementars.eclient.module.render.Chat;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({GuiNewChat.class})
public abstract class MixinGuiNewChat {
    @Redirect(
            method = "drawChat",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"
            )
    )
    private void drawRectBackgroundClean(int var1, int var2, int var3, int var4, int var5) {
        if (!ModuleManager.isModuleEnabled("Chat") || !((Xulu.VALUE_MANAGER.<Boolean>getValueByMod(Xulu.MODULE_MANAGER.getModuleT(Chat.class), "No Chat Background")).getValue())) {
            Gui.drawRect(var1, var2, var3, var4, var5);
        }
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int drawStringWithShadowMaybe(FontRenderer fontRenderer, String message, float x, float y, int color) {
        if (!Chat.INSTANCE.isToggled()) return fontRenderer.drawStringWithShadow(message, x, y, color);
        if (Chat.INSTANCE.customFont.getValue()) {
            if (Chat.nochatshadow.getValue()) {
                return (int) Xulu.cFontRenderer.drawString(message, x, y, color);
            }
            return (int) Xulu.cFontRenderer.drawStringWithShadow(message, x, y, color);
        } else {
            if (Chat.nochatshadow.getValue()) {
                return fontRenderer.drawString(message, (int) x, (int) y, color);
            }
            return fontRenderer.drawStringWithShadow(message, x, y, color);
        }
    }
}
