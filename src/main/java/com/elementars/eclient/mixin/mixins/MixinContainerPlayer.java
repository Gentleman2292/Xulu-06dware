package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.event.events.CloseInventoryEvent;
import com.elementars.eclient.module.render.ExtraTab;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Elementars
 * @since 6/30/2020 - 4:08 PM
 */
@Mixin(ContainerPlayer.class)
public class MixinContainerPlayer {
    @Inject(method = "onContainerClosed", at = @At("HEAD"), cancellable = true)
    public void getPlayerName(EntityPlayer playerIn, CallbackInfo ci) {
        CloseInventoryEvent event = new CloseInventoryEvent();
        event.call();
        if (event.isCancelled()) ci.cancel();
    }
}
