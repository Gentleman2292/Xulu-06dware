package com.elementars.eclient.module.player;

import java.util.HashMap;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.Pair;
import dev.xulu.settings.Value;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.input.Keyboard;

public class AutoReplenish extends Module {
    Value<String> mode;
    Value<Integer> threshold;
    Value<Integer> tickdelay;
    private int delay_step;

    public AutoReplenish() {
        super("AutoReplenish", "Automatically replenishes stacks in the hotbar", Keyboard.KEY_NONE, Category.PLAYER, true);
        this.mode = register(new Value<>("Mode", this, "All", new String[]{
                "All", "Crystals", "Xp", "Both"
        }));
        this.threshold = register(new Value<>("Threshold", this, 32, 1, 63));
        this.tickdelay = register(new Value<>("Delay", this, 2, 1, 10));
        this.delay_step = 0;
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (this.delay_step < this.tickdelay.getValue()) {
            ++this.delay_step;
            return;
        }
        this.delay_step = 0;
        final Pair<Integer, Integer> slots = this.findReplenishableHotbarSlot();
        if (slots == null) {
            return;
        }
        final int inventorySlot = slots.getKey();
        final int hotbarSlot = slots.getValue();
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        mc.playerController.windowClick(0, hotbarSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        mc.playerController.updateController();
    }

    private Pair<Integer, Integer> findReplenishableHotbarSlot() {
        Pair<Integer, Integer> returnPair = null;
        for (final Map.Entry<Integer, ItemStack> hotbarSlot : this.get_hotbar().entrySet()) {
            final ItemStack stack = hotbarSlot.getValue();
            if (!stack.isEmpty) {
                if (stack.getItem() == Items.AIR) {
                    continue;
                }
                if (!stack.isStackable()) {
                    continue;
                }
                switch (mode.getValue()) {
                    case "Crystals":
                        if (stack.getItem() != Items.END_CRYSTAL) {
                            continue;
                        }
                        break;
                    case "Xp":
                        if (stack.getItem() != Items.EXPERIENCE_BOTTLE) {
                            continue;
                        }
                        break;
                    case "Both":
                        if (stack.getItem() != Items.END_CRYSTAL || stack.getItem() != Items.EXPERIENCE_BOTTLE) {
                            continue;
                        }
                        break;
                }
                if (stack.stackSize >= stack.getMaxStackSize()) {
                    continue;
                }
                if (stack.stackSize > this.threshold.getValue()) {
                    continue;
                }
                final int inventorySlot = this.findCompatibleInventorySlot(stack);
                if (inventorySlot == -1) {
                    continue;
                }
                returnPair = new Pair<Integer, Integer>(inventorySlot, hotbarSlot.getKey());
            }
        }
        return returnPair;
    }

    private int findCompatibleInventorySlot(final ItemStack hotbarStack) {
        int inventorySlot = -1;
        int smallestStackSize = 999;
        for (final Map.Entry<Integer, ItemStack> entry : this.get_inventory().entrySet()) {
            final ItemStack inventoryStack = entry.getValue();
            if (!inventoryStack.isEmpty) {
                if (inventoryStack.getItem() == Items.AIR) {
                    continue;
                }
                if (!this.isCompatibleStacks(hotbarStack, inventoryStack)) {
                    continue;
                }
                final int currentStackSize = ((ItemStack)mc.player.inventoryContainer.getInventory().get((int)entry.getKey())).stackSize;
                if (smallestStackSize <= currentStackSize) {
                    continue;
                }
                smallestStackSize = currentStackSize;
                inventorySlot = entry.getKey();
            }
        }
        return inventorySlot;
    }

    private boolean isCompatibleStacks(final ItemStack stack1, final ItemStack stack2) {
        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        }
        if (stack1.getItem() instanceof ItemBlock && stack2.getItem() instanceof ItemBlock) {
            final Block block1 = ((ItemBlock)stack1.getItem()).getBlock();
            final Block block2 = ((ItemBlock)stack2.getItem()).getBlock();
            if (!block1.material.equals(block2.material)) {
                return false;
            }
        }
        return stack1.getDisplayName().equals(stack2.getDisplayName()) && stack1.getItemDamage() == stack2.getItemDamage();
    }

    private Map<Integer, ItemStack> get_inventory() {
        return this.get_inv_slots(9, 35);
    }

    private Map<Integer, ItemStack> get_hotbar() {
        return this.get_inv_slots(36, 44);
    }

    private Map<Integer, ItemStack> get_inv_slots(int current, final int last) {
        final Map<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        while (current <= last) {
            fullInventorySlots.put(current, (ItemStack)mc.player.inventoryContainer.getInventory().get(current));
            ++current;
        }
        return fullInventorySlots;
    }
}
