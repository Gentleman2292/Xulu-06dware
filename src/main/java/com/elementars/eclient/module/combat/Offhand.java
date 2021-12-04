package com.elementars.eclient.module.combat;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.LocalPlayerUpdateEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.player.PacketSwing;
import com.google.common.collect.Maps;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.util.Map;

/**
 * Made for IngrosWare-Recode
 *
 * @author oHare
 * @since 6/14/2020
 **/
public class Offhand extends Module {

    private Map<String, Item> itemMap = Maps.newHashMap();
    private final Value<String> item = this.register(new Value<>("Item", this, "Crystals", new String[]{"Crystals", "Gapples"}));
    public final Value<Boolean> conserveGapples = register(new Value<>("Conserve Gap", this, true));
    private final Value<Boolean> gapOnSword = register(new Value<>("Gap On Sword", this, false));
    private final Value<Integer> delay = this.register(new Value<>("Delay", this, 1, 0, 5));

    public Offhand() {
        super("Offhand", "Automatically places items in your offhand", Keyboard.KEY_NONE, Category.COMBAT, true);
        this.itemMap.put("Crystals", Items.END_CRYSTAL);
        this.itemMap.put("Gapples", Items.GOLDEN_APPLE);
    }

    public static boolean switchInProgress = false;

    @Override
    public void onUpdate() {
        if (switchInProgress) return;
        if (mc.currentScreen instanceof GuiContainer) return;
        if (AutoTotem.offhand_delay != 0) return;
        if (isOk() && mc.player.getHeldItemOffhand().getItem() != itemMap.get(getItem())) {
            final int slot = getSlot() < 9 ? getSlot() + 36 : getSlot();
            if (getSlot() != -1) {
                this.slot = slot;
                switchInProgress = true;
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
                switchDelay1 = delay.getValue();
                didFirst = true;
                return;
            }
            if (switchDelay1 == 0) {
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                switchDelay1 = -1;
                switchDelay2 = delay.getValue();
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

    private boolean isOk() {
        return (mc.player.getHealth() + mc.player.getAbsorptionAmount()) > getModuleT(AutoTotem.class).health.getValue() && mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA;
    }

    String getItem() {
        if (gapOnSword.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
            return "Gapples";
        } else {
            return item.getValue();
        }
    }

    int getSlot() {
        int slot = -1;
        for (int i = 45; i > -1; i--) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == itemMap.get(getItem())) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    @Override
    public String getHudInfo() {
        int items = 0;
        for (int i = 45; i > -1; i--) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == itemMap.get(item.getValue())) {
                items += mc.player.inventory.getStackInSlot(i).getCount();
            }
        }
        return items + "";
    }
}