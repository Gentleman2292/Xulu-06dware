package com.elementars.eclient.mixin.mixins;

import javax.annotation.Nullable;

import com.elementars.eclient.cape.Capes;
import com.elementars.eclient.module.render.Cape;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer{

    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();
    private Minecraft minecraft = Minecraft.getMinecraft();

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        if (Cape.isEnabled()) {
            NetworkPlayerInfo info = this.getPlayerInfo();
            if (info != null) {
                if (Capes.isCapeUser(info.getGameProfile().getName())) {
                    callbackInfoReturnable.setReturnValue(new ResourceLocation("textures/eclient/cape.png"));
                }
            }
        }
    }
}