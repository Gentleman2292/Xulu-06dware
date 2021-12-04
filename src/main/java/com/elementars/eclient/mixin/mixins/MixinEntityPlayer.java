package com.elementars.eclient.mixin.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryEnderChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Elementars
 * @version Xulu v1.2.0
 * @since 6/12/2020 - 9:49 PM
 */
@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase{
    @Shadow public InventoryEnderChest enderChest;
}
