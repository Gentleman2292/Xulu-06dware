package com.elementars.eclient.module.misc;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class HopperNuker extends Module {
    public ArrayList<BlockPos> hoppers = new ArrayList<>();
    int pickaxeSlot;

    public HopperNuker() {
        super("HopperNuker", "Nuker for hoppers", Keyboard.KEY_NONE, Category.MISC, true);
    }

    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            final Iterable<BlockPos> blocks = (Iterable<BlockPos>) BlockPos.getAllInBox(HopperNuker.mc.player.getPosition().add(-5, -5, -5), HopperNuker.mc.player.getPosition().add(5, 5, 5));
            for (final BlockPos pos : blocks) {
                if (mc.world.getBlockState(pos).getBlock() == Blocks.HOPPER) {
                    pickaxeSlot = -1;
                    for (int i = 0; i < 9; i++) {

                        if (pickaxeSlot != -1) {
                            break;
                        }

                        ItemStack stack = mc.player.inventory.getStackInSlot(i);

                        if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemPickaxe)) {
                            continue;
                        }

                        ItemPickaxe pickaxe = ((ItemPickaxe) stack.getItem());
                        pickaxeSlot = i;

                    }
                    if (pickaxeSlot != -1) {
                        mc.player.inventory.currentItem = pickaxeSlot;
                    }
                    mc.playerController.onPlayerDamageBlock(pos, mc.player.getHorizontalFacing());
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                }
            }
        }
    }
}
