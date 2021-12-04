package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.event.events.EventRenderBlock;
import com.elementars.eclient.module.render.Xray;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Elementars
 * @since 5/27/2020 - 10:39 AM
 */
@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer {
    @Inject(method = "renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z", at = @At("HEAD"), cancellable = true)
    public void renderModel(IBlockAccess worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn, BufferBuilder buffer, boolean checkSides, long rand, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        /*
        if (Xray.INSTANCE.isToggled()) {
            if (Xray.shouldXray(stateIn.getBlock())) {
                callbackInfoReturnable.setReturnValue(false);
                //callbackInfoReturnable.cancel();
            }
        }
        */
        EventRenderBlock eventRenderBlock = new EventRenderBlock(worldIn, modelIn, stateIn, posIn, buffer, checkSides, rand);
        eventRenderBlock.call();
        if (eventRenderBlock.isCancelled()) {
            callbackInfoReturnable.setReturnValue(eventRenderBlock.isRenderable());
        }
    }
}
