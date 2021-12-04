package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.events.EventThrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityThrowable.class)
public class MixinEntityThrowable {


    @Inject(method = "shoot(Lnet/minecraft/entity/Entity;FFFFF)V", at = @At(value = "HEAD"), cancellable = true)
    private void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy, CallbackInfo info) {
        EventThrow eventThrow = new EventThrow(entityThrower, EntityThrowable.class.cast(this), rotationYawIn);
        eventThrow.call();
        if (eventThrow.isCancelled()) {
            info.cancel();
        }

    }


}
