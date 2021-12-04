package com.elementars.eclient.module.combat;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.LocalPlayerUpdateEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Made for IngrosWare-Recode
 *
 * @author oHare
 * @since 6/14/2020
 **/
public class AutoTotem extends Module {

    public final Value<Integer> health = register(new Value<>("Health", this, 20, 1, 22));
    public final Value<Integer> delayA = register(new Value<>("Delay", this, 1, 0, 5));
    public final Value<Integer> delay = register(new Value<>("Offhand Delay", this, 5, 0, 20));

    public AutoTotem() {
        super("AutoTotem", "Automatically places totems in your offhand", Keyboard.KEY_NONE, Category.COMBAT, true);
    }

    public static boolean switchInProgress = false;
    public static int offhand_delay = 0;

    @Override
    public void onUpdate() {
        if (switchInProgress) return;
        if (mc.currentScreen instanceof GuiContainer) return;
        if (offhand_delay > 0) offhand_delay--;
        if (shouldTotem() && !(mc.player.getHeldItemOffhand() != ItemStack.EMPTY && mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)) {
            final int slot = getTotemSlot() < 9 ? getTotemSlot() + 36 : getTotemSlot();
            if (getTotemSlot() != -1) {
                this.slot = slot;
                switchInProgress = true;
                offhand_delay = delay.getValue();
            }
        }
    }

    boolean didFirst = false;
    int switchDelay1 = -1;
    int switchDelay2 = -1;
    int slot = -1;

    @EventTarget
    public void onPlayerUpdate(LocalPlayerUpdateEvent event) {
        if (switchInProgress) {
            if (switchDelay1 > 0) switchDelay1--;
            if (switchDelay2 > 0) switchDelay2--;
            if (!didFirst) {
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                switchDelay1 = delayA.getValue();
                didFirst = true;
                return;
            }
            if (switchDelay1 == 0) {
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                switchDelay1 = -1;
                switchDelay2 = delayA.getValue();
                return;
            }
            if (switchDelay2 == 0) {
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                switchDelay1 = -1;
                switchDelay2 = -1;
                slot = -1;
                switchInProgress = false;
                didFirst = false;
            }
        }
    }

    private boolean shouldTotem() {
        return (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= (Xulu.MODULE_MANAGER.getModuleT(Offhand.class).conserveGapples.getValue() ? !Surround.isExposed() && isFullArmor(mc.player) ? 6 : health.getValue() : health.getValue()) || mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA;
    }

    public static boolean isFullArmor(EntityPlayer entity) {
        boolean fullArmor = true;
        int diamondItems = 0;
        boolean hasBlast = false;
        for (ItemStack is : entity.getArmorInventoryList()) {
            if (is.isEmpty()) {
                fullArmor = false;
                break;
            } else {
                if (is.getItem() == Items.DIAMOND_HELMET) diamondItems++;
                if (is.getItem() == Items.DIAMOND_CHESTPLATE) diamondItems++;
                if (is.getItem() == Items.DIAMOND_LEGGINGS) diamondItems++;
                if (is.getItem() == Items.DIAMOND_BOOTS) diamondItems++;
                NBTTagList enchants = is.getEnchantmentTagList();
                List<String> enchantments = new ArrayList<>();
                if (enchants != null) {
                    for (int index = 0; index < enchants.tagCount(); ++index) {
                        short id = enchants.getCompoundTagAt(index).getShort("id");
                        short level = enchants.getCompoundTagAt(index).getShort("lvl");
                        Enchantment enc = Enchantment.getEnchantmentByID(id);
                        if (enc != null) {
                            enchantments.add(enc.getTranslatedName(level));
                        }
                    }
                }
                if (enchantments.contains("Blast Protection IV")) hasBlast = true;
            }
        }
        return fullArmor && diamondItems == 4 && hasBlast;
    }

    int getTotemSlot() {
        int totemSlot = -1;
        for (int i = 45; i > -1; i--) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                totemSlot = i;
                break;
            }
        }
        return totemSlot;
    }

    @Override
    public String getHudInfo() {
        int items = 0;
        for (int i = 45; i > -1; i--) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                items += mc.player.inventory.getStackInSlot(i).getCount();
            }
        }
        return items + "";
    }
}