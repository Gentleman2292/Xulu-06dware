package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.event.events.EventClickBlock;
import com.elementars.eclient.event.events.EventPlayerDamageBlock;
import com.elementars.eclient.event.events.EventResetBlockRemoving;
import com.elementars.eclient.module.player.TpsSync;
import com.elementars.eclient.util.LagCompensator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by 086 on 3/10/2018.
 */
@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Redirect(method = "onPlayerDamageBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)F"))
    float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos) {
        return state.getPlayerRelativeBlockHardness(player, worldIn, pos) * (TpsSync.isSync() ? (LagCompensator.INSTANCE.getTickRate() / 20f) : 1);
    }

    @Inject(method = "onPlayerDamageBlock", at = @At(value = "HEAD"), cancellable = true)
    public void test(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable info) {
        EventPlayerDamageBlock eventPlayerDamageBlock = new EventPlayerDamageBlock(posBlock, directionFacing);
        eventPlayerDamageBlock.call();
        if (eventPlayerDamageBlock.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "clickBlock", at = @At(value = "HEAD"), cancellable = true)
    public void onClickBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable info) {
        EventClickBlock eventClickBlock = new EventClickBlock(posBlock, directionFacing);
        eventClickBlock.call();
        if (eventClickBlock.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "resetBlockRemoving", at = @At("HEAD"), cancellable = true)
    public void onBlockDestroyed(CallbackInfo info) {
        EventResetBlockRemoving eventDestroyBlock = new EventResetBlockRemoving();
        eventDestroyBlock.call();
        if (eventDestroyBlock.isCancelled()) {
            info.cancel();
        }
    }


}
