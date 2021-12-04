package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.module.render.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Block.class, priority = 9999)
public class MixinBlock {
    @Inject(method = "isFullCube", at = @At("HEAD"), cancellable = true)
    public void isFullCube(IBlockState state, CallbackInfoReturnable<Boolean> callback) {
        try
        {
            if (Xray.INSTANCE != null && Xray.INSTANCE.isToggled()) {
                callback.setReturnValue(Xray.shouldXray(Block.class.cast(this)));
                callback.cancel();
            }
        }catch (Exception e) {

        }
    }


    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
    public void shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> callback) {
        try {
            if (Xray.INSTANCE != null && Xray.INSTANCE.isToggled()) {
                callback.setReturnValue(Xray.shouldXray(Block.class.cast(this)));
            }
        }catch (Exception e) {
            //e
        }
    }

    @Inject(method = "isOpaqueCube", at = @At("HEAD"), cancellable = true)
    public void isOpaqueCube(IBlockState state, CallbackInfoReturnable<Boolean> callback) {
        try {
            if (Xray.INSTANCE != null && Xray.INSTANCE.isToggled()) {
                callback.setReturnValue(Xray.shouldXray(Block.class.cast(this)));
            }
        } catch (Exception e) {
            //e
        }
    }
}
