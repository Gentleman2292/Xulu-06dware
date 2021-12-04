package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.events.AllowInteractEvent;
import com.elementars.eclient.event.events.EventKey;
import com.elementars.eclient.event.events.EventMiddleClick;
import com.elementars.eclient.module.core.TitleScreenShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class, priority = 9999)
public class MixinMinecraft {

    @Shadow public EntityRenderer entityRenderer;

    @Shadow public WorldClient world;

    @Shadow public GuiScreen currentScreen;

    @Shadow public GameSettings gameSettings;

    @Shadow public Framebuffer framebuffer;

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void displayCrashReport(Minecraft minecraft, CrashReport crashReport) {
        Xulu.save();
    }

    @Inject(method = "middleClickMouse", at = @At("HEAD"))
    private void middleClickMouse(CallbackInfo callback) {
        EventMiddleClick eventMiddleClick = new EventMiddleClick();
        eventMiddleClick.call();
    }

    @Inject(method = "shutdownMinecraftApplet", at = @At("HEAD"))
    private void stopClient(CallbackInfo callbackInfo) {
        Xulu.INSTANCE.stopClient();
    }

    @Inject(method = "runTickKeyboard", at = @At(value = "INVOKE", remap = false, target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 0, shift = At.Shift.BEFORE))
    private void onKeyboard(CallbackInfo callbackInfo) {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
        if (Keyboard.getEventKeyState()) {
            EventKey eventKey = new EventKey(i);
            eventKey.call();
        }
    }

    @Inject(method = "getLimitFramerate", at = @At("HEAD"), cancellable = true)
    private void getFrameRateXulu(CallbackInfoReturnable<Integer> cir) {
        try {
            if (Xulu.MODULE_MANAGER.getModule(TitleScreenShader.class).isToggled()) {
                cir.setReturnValue(this.world == null && this.currentScreen != null ? TitleScreenShader.fps.getValue() : this.gameSettings.limitFramerate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Redirect(method = "sendClickBlockToController", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
    private boolean isHandActiveWrapper(EntityPlayerSP playerSP)
    {
        AllowInteractEvent event = new AllowInteractEvent(playerSP.isHandActive());
        event.call();
        return event.usingItem;
    }

    @Redirect(method = "rightClickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z", ordinal = 0))
    private boolean isHittingBlockWrapper(PlayerControllerMP playerControllerMP)
    {
        AllowInteractEvent event = new AllowInteractEvent(playerControllerMP.getIsHittingBlock());
        event.call();
        return event.usingItem;
    }
}
