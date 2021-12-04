package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.combat.SelfWeb;
import com.elementars.eclient.module.misc.Avoid;
import com.elementars.eclient.module.misc.LiquidInteract;
import com.elementars.eclient.util.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by 086 on 16/12/2017.
 */
@SideOnly(Side.CLIENT)
@Mixin(value = BlockLiquid.class, priority = 9999)
public class MixinBlockLiquid {

    @Inject(method = "modifyAcceleration", at = @At("HEAD"), cancellable = true)
    public void modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion, CallbackInfoReturnable<Vec3d> returnable) {
        if (Xulu.MODULE_MANAGER.getModuleByName("Velocity").isToggled()) {
            returnable.setReturnValue(motion);
            returnable.cancel();
        }
    }

    @Inject(method = "canCollideCheck", at = @At("RETURN"), cancellable = true, require = 1)
    private void IcanCollide(IBlockState state, boolean hitIfLiquid, CallbackInfoReturnable<Boolean> returnable) {
        returnable.setReturnValue(LiquidInteract.INSTANCE.isToggled());
    }

    @Inject(method = "getCollisionBoundingBox", at = @At("HEAD"), cancellable = true)
    private void getCollision(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> cir) {
        if (Xulu.MODULE_MANAGER.getModule(Avoid.class).isToggled() && Avoid.lava.getValue() && (blockState.getBlock() == Blocks.LAVA || blockState.getBlock() == Blocks.FLOWING_LAVA) && !(Wrapper.getMinecraft().world.getBlockState(new BlockPos(Wrapper.getMinecraft().player.getPositionVector()).add(0, 1, 0)).getBlock() == Blocks.LAVA || Wrapper.getMinecraft().world.getBlockState(new BlockPos(Wrapper.getMinecraft().player.getPositionVector()).add(0, 1, 0)).getBlock() == Blocks.FLOWING_LAVA)) {
            cir.setReturnValue(Block.FULL_BLOCK_AABB);
            cir.cancel();
        }
    }

}
