package com.elementars.eclient.module.combat;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.Timer;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

/**
 * @author Elementars
 * @since 9/2/2020 - 11:45 AM
 */
public class AutoArmor extends Module {
    private Timer timer = new Timer();
    private int[] bestArmorDamage;
    private int[] bestArmorSlots;

    private Value<Boolean> pif = register(new Value<>("Pickup If Full", this, true));
    private Value<Boolean> replace = register(new Value<>("Replace Empty", this, true));
    private Value<Boolean> preserve = register(new Value<>("Preserve Damaged", this, false));
    private Value<Integer> preserveDMG = register(new Value<>("Damage %", this, 5, 0, 100));
    private Value<Integer> ms = register(new Value<>("MS delay", this, 500, 0, 1000));

    public AutoArmor() {
        super("AutoArmor", "Automatically refills armor", Keyboard.KEY_NONE, Category.COMBAT, true);
    }

    @Override
    public void onUpdate() {

        if(mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof InventoryEffectRenderer)) return;

        searchSlots();

        for (int i = 0; i < 4; i ++) {
            if (bestArmorSlots[i] != -1) {
                int bestSlot = bestArmorSlots[i];

                if(bestSlot < 9) bestSlot += 36;

                if (!mc.player.inventory.armorItemInSlot(i).isEmpty()) {
                    //Shouldn't happen often unless if you use mode preserve so we'll just make sure we aren't interfering with other switches
                    if (mc.player.inventory.getFirstEmptyStack() == -1 && !AutoTotem.switchInProgress && !Offhand.switchInProgress && !MiddleClickPearl.switchInProgress && pif.getValue()) {
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 8 - i, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, bestSlot, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 8 - i, 0, ClickType.PICKUP, mc.player);
                        continue;
                    }
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 8 - i, 0, ClickType.QUICK_MOVE, mc.player);
                    if (!timer.hasReached(ms.getValue())) return;
                }
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, bestSlot, 0, ClickType.QUICK_MOVE, mc.player);
                timer.reset();
            }
        }
    }

    private void searchSlots() {
        bestArmorDamage = new int[4];
        bestArmorSlots = new int[4];
        Arrays.fill(bestArmorDamage, -1);
        Arrays.fill(bestArmorSlots, -1);

        for (int i = 0; i < bestArmorSlots.length; i++) {
            ItemStack itemStack = mc.player.inventory.armorItemInSlot(i);

            if (itemStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) itemStack.getItem();

                if (preserve.getValue()) {
                    float dmg = ((float) itemStack.getMaxDamage() - (float) itemStack.getItemDamage()) / (float) itemStack.getMaxDamage();
                    int percent = (int) (dmg * 100);
                    if (percent > preserveDMG.getValue()) { //keep it at -1 if it's below
                        bestArmorDamage[i] = armor.damageReduceAmount;
                    }
                } else {
                    bestArmorDamage[i] = armor.damageReduceAmount;
                }
            } else if (itemStack.isEmpty()) {
                if (!replace.getValue())
                    bestArmorDamage[i] = Integer.MAX_VALUE;
            }
        }

        for (int i = 0; i < 9 * 4; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

            if (itemStack.getCount() > 1) continue;

            if (itemStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) itemStack.getItem();
                int armorType = armor.armorType.ordinal() - 2;
                if (bestArmorDamage[armorType] < armor.damageReduceAmount) {
                    bestArmorDamage[armorType] = armor.damageReduceAmount;
                    bestArmorSlots[armorType] = i;
                }
            }
        }
    }
}
