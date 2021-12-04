package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.event.events.EventDrinkPotion;
import com.elementars.eclient.event.events.EventUseItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemPotion.class)
public class MixinItemPotion {
    @Inject(method = "onItemUseFinish", at = {@At("HEAD")}, cancellable = true)
    private void onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving, CallbackInfoReturnable info) {
        EventDrinkPotion event = new EventDrinkPotion(entityLiving, stack);
        event.call();
    }
}
