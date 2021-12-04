package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.event.events.EventUseItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEnderPearl.class)
public class MixinItemEnderPearl {

    @Inject(method = "onItemRightClick", at = {@At("HEAD")}, cancellable = true)
    private void useitemrightclick(World worldIn, EntityPlayer playerIn, EnumHand hand, CallbackInfoReturnable info) {
        EventUseItem event = new EventUseItem(playerIn);
        event.call();
    }
}
