package com.elementars.eclient.module.combat;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventSendPacket;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.EntityUtil;
import com.elementars.eclient.util.LagCompensator;
import dev.xulu.settings.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

import java.util.Iterator;
import java.util.Random;

import static com.elementars.eclient.module.combat.AutoCrystal.calculateLookAt;

/**
 * Created by 086 on 12/12/2017.
 * Last Updated 5 August 2019 by hub
 */
public class Aura extends Module {
    
    final Value<Boolean> players = register(new Value<>("Players", this, true));
    final Value<Boolean> animals = register(new Value<>("Animals", this, false));
    final Value<Boolean> mobs = register(new Value<>("Mobs", this, false));
    final Value<Double> range = register(new Value<>("Range", this, 5.5d, 1d, 10d));
    final Value<Boolean> wait = register(new Value<>("Wait", this, true));
    final Value<Boolean> walls = register(new Value<>("Walls", this, false));
    final Value<Boolean> rotate = register(new Value<>("Rotate", this, true));
    final Value<Boolean> sharpness = register(new Value<>("32k Switch", this, false));
    public Aura() {
        super("Aura", "Hits people", Keyboard.KEY_NONE, Category.COMBAT, true);
    }

    boolean isSpoofingAngles = false;
    double yaw;
    double pitch;

    @Override
    public void onUpdate() {
        if (mc.player.isDead) {
            return;
        }
        if (shouldPause()) {
            resetRotation();
            return;
        }
        boolean shield = mc.player.getHeldItemOffhand().getItem().equals(Items.SHIELD) && mc.player.getActiveHand() == EnumHand.OFF_HAND;
        boolean gap = mc.player.getHeldItemOffhand().getItem().equals(Items.GOLDEN_APPLE) && mc.player.getActiveHand() == EnumHand.OFF_HAND;
        if (mc.player.isHandActive() && !shield && !gap) {
            return;
        }

        if (wait.getValue()) {
            if (mc.player.getCooledAttackStrength(getLagComp()) < 1) {
                return;
            } else if (mc.player.ticksExisted % 2 != 0) {
                return;
            }
        }

        for (Entity target : Minecraft.getMinecraft().world.loadedEntityList) {
            if (!EntityUtil.isLiving(target)) {
                continue;
            }
            if (target == mc.player) {
                continue;
            }
            if (mc.player.getDistance(target) > range.getValue()) {
                continue;
            }
            if (((EntityLivingBase) target).getHealth() <= 0) {
                continue;
            }
            if (((EntityLivingBase) target).hurtTime != 0 && wait.getValue()) {
                continue;
            }
            if (!walls.getValue() && (!mc.player.canEntityBeSeen(target) && !canEntityFeetBeSeen(target))) {
                continue; // If walls is on & you can't see the feet or head of the target, skip. 2 raytraces needed
            }
            if (players.getValue() && target instanceof EntityPlayer && !Friends.isFriend(target.getName())) {
                attack(target);
                return;
            } else {
                if (EntityUtil.isPassive(target) ? animals.getValue() : (EntityUtil.isMobAggressive(target) && mobs.getValue())) {
                    attack(target);
                    return;
                }
            }
        }
        resetRotation();
    }

    private boolean checkSharpness(ItemStack stack) {

        if (stack.getTagCompound() == null) {
            return false;
        }

        NBTTagList enchants = (NBTTagList) stack.getTagCompound().getTag("ench");
		
		if (enchants == null) {
			return false;
		}
			

        for (int i = 0; i < enchants.tagCount(); i++) {
            NBTTagCompound enchant = ((NBTTagList) enchants).getCompoundTagAt(i);
            if (enchant.getInteger("id") == 16) {
                int lvl = enchant.getInteger("lvl");
                if (lvl >= 16) {
                    return true;
                }
                break;
            }
        }

        return false;

    }

    private void attack(Entity e) {

        if (sharpness.getValue()) {

            if (!checkSharpness(mc.player.getHeldItemMainhand())) {

                int newSlot = -1;

                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (stack == ItemStack.EMPTY) {
                        continue;
                    }
                    if (checkSharpness(stack)) {
                        newSlot = i;
                        break;
                    }
                }

                if (newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                }

            }

        }

        if (rotate.getValue()) {
            lookAtPacket(e.posX, e.posY, e.posZ, mc.player);
        }

        mc.playerController.attackEntity(mc.player, e);
        mc.player.swingArm(EnumHand.MAIN_HAND);

    }

    private float getLagComp() {
        if (wait.getValue()) {
            return -(20 - LagCompensator.INSTANCE.getTickRate());
        }
        return 0.0F;
    }

    private boolean canEntityFeetBeSeen(Entity entityIn) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posX + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ), false, true, false) == null;
    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }

    private void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    private void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    @Override
    public void onDisable() {
        resetRotation();
    }

    @EventTarget
    public void onSend(EventSendPacket event) {
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer && rotate.getValue()) {
            if (isSpoofingAngles) {
                ((CPacketPlayer) packet).yaw = (float) yaw;
                ((CPacketPlayer) packet).pitch = (float) pitch;
            }
        }
    }

    private boolean shouldPause() {
        if (Xulu.MODULE_MANAGER.getModule(Surround.class).isToggled() && Surround.isExposed() && Xulu.MODULE_MANAGER.getModuleT(Surround.class).findObiInHotbar() != -1) {
            return true;
        }
        if (Xulu.MODULE_MANAGER.getModule(AutoTrap.class).isToggled()) {
            return true;
        }
        if (Xulu.MODULE_MANAGER.getModule(HoleFill.class).isToggled()) {
            return true;
        }
        if (Xulu.MODULE_MANAGER.getModule(HoleBlocker.class).isToggled() && HoleBlocker.isExposed() && Xulu.MODULE_MANAGER.getModuleT(Surround.class).findObiInHotbar() != -1) {
            return true;
        }
        return false;
    }
}
