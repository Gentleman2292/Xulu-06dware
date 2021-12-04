package com.elementars.eclient.module.combat;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventMiddleClick;
import com.elementars.eclient.event.events.LocalPlayerUpdateEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.misc.MCF;
import com.elementars.eclient.module.player.AntiVoid;
import dev.xulu.settings.Value;
import dev.xulu.settings.ValueManager;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @since 6/22/2020 - 10:35 PM
 */
public class MiddleClickPearl extends Module {

    private final Value<String> mode = register(new Value<>("Mode", this, "Switch", new String[]{
            "Switch", "Instant"
    }));
    private final Value<Integer> delayA = register(new Value<>("Click Delay", this, 1, 0, 5))
            .visibleWhen(integer -> mode.getValue().equalsIgnoreCase("Switch"));
    private final Value<Integer> delay = register(new Value<>("Switch Delay", this, 10, 0, 80))
            .visibleWhen(integer -> mode.getValue().equalsIgnoreCase("Switch"));

    public MiddleClickPearl() {
        super("MiddleClickPearl", "Throws a pearl without it being in your hotbar", Keyboard.KEY_NONE, Category.COMBAT, true);
    }

    private int slot = -1;
    private int slot2 = -1;
    int time;
    boolean hasClicked = false;

    @EventTarget
    public void onMiddleClick(EventMiddleClick event) {
        if (mc.currentScreen instanceof GuiContainer) return;
        final RayTraceResult ray = MCF.mc.objectMouseOver;
        if(ray.typeOfHit == RayTraceResult.Type.ENTITY) return;
        if(ray.typeOfHit == RayTraceResult.Type.BLOCK) return;
        if (mode.getValue().equalsIgnoreCase("Instant")) {
            throwPearl();
            return;
        }
        slot = getSlot() < 9 ? getSlot() + 36 : getSlot();
        slot2 = getSlot2() < 9 ? getSlot2() + 36 : getSlot2();
        if (getSlot() == -1) {
            sendDebugMessage("No pearl found!");
            return;
        }
        if (getSlot() != -1 && getSlot2() != -1 && mc.player.getHeldItemMainhand().getItem() != Items.ENDER_PEARL) {
            mc.addScheduledTask(() ->{
                switchInProgress = true;
                isThrowingPearl = true;
            });
            hasClicked = true;
        }
    }

    boolean isFinishingTask = false;
    boolean isThrowingPearl = false;
    public static boolean switchInProgress = false;
    boolean didFirst = false;
    int switchDelay1 = -1;
    int switchDelay2 = -1;

    @Override
    public void onUpdate() {
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
                mc.playerController.windowClick(0, slot2, 0, ClickType.PICKUP, mc.player);
                switchDelay1 = -1;
                switchDelay2 = delayA.getValue();
                return;
            }
            if (switchDelay2 == 0) {
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                switchDelay1 = -1;
                switchDelay2 = -1;
                switchInProgress = false;
                didFirst = false;
                if (isThrowingPearl) {
                    mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    isThrowingPearl = false;
                }
                if (isFinishingTask) {
                    slot = -1;
                    slot2 = -1;
                    time = 0;
                    hasClicked = false;
                    isFinishingTask = false;
                    mc.playerController.updateController();
                }
            }
        }
    }

    @EventTarget
    public void onPlayerUpdate(LocalPlayerUpdateEvent event) {
        if (mc.player.getHeldItemMainhand().getItem() == Items.ENDER_PEARL && hasClicked) {
            time++;
            if (time < delay.getValue()) return;
            mc.addScheduledTask(() ->{
                switchInProgress = true;
                isFinishingTask = true;
            });
        } else if (getSlot() == -1 && hasClicked) {
            mc.addScheduledTask(() ->{
                switchInProgress = true;
                isFinishingTask = true;
            });
        }
    }


    int getSlot() {
        int slot = -1;
        for (int i = 45; i > -1; i--) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ENDER_PEARL) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    int getSlot2() {
        int slot = -1;
        for (int i = 45; i > -1; i--) {
            if (mc.player.inventory.getStackInSlot(i) == mc.player.getHeldItemMainhand()) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    private void throwPearl() {
        final int pearlSlot = findHotbarBlock(ItemEnderPearl.class);
        final boolean offhand = mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL;
        if (pearlSlot != -1 || offhand) {
            final int oldslot = mc.player.inventory.currentItem;
            if (!offhand) {
                switchToHotbarSlot(pearlSlot, false);
            }
            mc.playerController.processRightClick(mc.player, mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (!offhand) {
                switchToHotbarSlot(oldslot, false);
            }
        }
    }

    public static void switchToHotbarSlot(final int slot, final boolean silent) {
        if (mc.player.inventory.currentItem == slot || slot < 0) {
            return;
        }
        if (silent) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.playerController.updateController();
        }
        else {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }

    public static void switchToHotbarSlot(final Class clazz, final boolean silent) {
        final int slot = findHotbarBlock(clazz);
        if (slot > -1) {
            switchToHotbarSlot(slot, silent);
        }
    }

    public static int findHotbarBlock(final Class clazz) {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (clazz.isInstance(stack.getItem())) {
                    return i;
                }
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (clazz.isInstance(block)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
}