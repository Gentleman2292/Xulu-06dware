package com.elementars.eclient.mixin.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Elementars
 * @version Xulu v1.2.0
 * @since 6/12/2020 - 9:51 PM
 */
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity{
    @Shadow
    public void swingArm(EnumHand hand) {

    }
}
