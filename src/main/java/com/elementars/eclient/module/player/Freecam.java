package com.elementars.eclient.module.player;

import com.elementars.eclient.event.Event;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventReceivePacket;
import com.elementars.eclient.event.events.EventSendPacket;
import com.elementars.eclient.event.events.PlayerMoveEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.Wrapper;
import dev.xulu.settings.Value;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;


import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;

public class Freecam extends Module {

    private double posX, posY, posZ;
    private float pitch, yaw;

    private double startPosX, startPosY, startPosZ;
    private float startPitch, startYaw;

    private EntityOtherPlayerMP clonedPlayer;
    private boolean isRidingEntity;
    private Entity ridingEntity;
    private final Value<Boolean> cancelPackets = register(new Value<>("Cancel Packets", this, true));
    private final Value<Integer> speed = register(new Value<>("Speed", this, 11, 1, 20));
    private final Value<Integer> vspeed = register(new Value<>("V Speed", this, 7, 1, 20));

    public Freecam() {
        super("Freecam", "Allows you to fly out of your body", Keyboard.KEY_NONE, Category.PLAYER, true);
    }

    public void onUpdate() {
        if (!Wrapper.getMinecraft().player.onGround) {
            Wrapper.getMinecraft().player.motionY = -0.2;
        }
        /*
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            Wrapper.getMinecraft().player.setPosition(Wrapper.getMinecraft().player.posX, Wrapper.getMinecraft().player.posY + this.speed.getValDouble() / 10, Wrapper.getMinecraft().player.posZ);
        }
        if (Wrapper.getMinecraft().player.isSneaking()) {
            Wrapper.getMinecraft().player.setPosition(Wrapper.getMinecraft().player.posX, Wrapper.getMinecraft().player.posY - this.speed.getValDouble() / 10, Wrapper.getMinecraft().player.posZ);

        }

        if (mc.gameSettings.keyBindForward.isPressed()
                || mc.gameSettings.keyBindBack.isPressed()
                || mc.gameSettings.keyBindLeft.isPressed()
                || mc.gameSettings.keyBindRight.isPressed()) {
            // mc.thePlayer.setSpeed(0.7f);
            // mc.timer.timerSpeed = (mc.thePlayer.ticksExisted % 2 == 0 ? 3.4f : 1f);

            this.playersSpeed(this.speed.getValDouble());
        } else {
            Wrapper.getMinecraft().player.motionX = 0.0;
            Wrapper.getMinecraft().player.motionZ = 0.0;
        }
        */

        //Command.sendChatMessage(mc.player.motionX + "");
        Wrapper.getMinecraft().player.onGround = true;
        Wrapper.getMinecraft().player.motionY = 0f;
        Wrapper.getMinecraft().player.noClip = true;

        Wrapper.getMinecraft().player.capabilities.isFlying = true;
        Wrapper.getMinecraft().player.capabilities.setFlySpeed((float) speed.getValue() / 100f);

        Wrapper.getMinecraft().player.onGround = false;
        Wrapper.getMinecraft().player.fallDistance = 0;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY += vspeed.getValue() / 10f;
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY += -vspeed.getValue() / 10f;
        }
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        if (Wrapper.getMinecraft().player != null) {
            isRidingEntity = Wrapper.getMinecraft().player.getRidingEntity() != null;

            if (Wrapper.getMinecraft().player.getRidingEntity() == null) {
                posX = Wrapper.getMinecraft().player.posX;
                posY = Wrapper.getMinecraft().player.posY;
                posZ = Wrapper.getMinecraft().player.posZ;
            } else {
                ridingEntity = Wrapper.getMinecraft().player.getRidingEntity();
                Wrapper.getMinecraft().player.dismountRidingEntity();
            }

            pitch = Wrapper.getMinecraft().player.rotationPitch;
            yaw = Wrapper.getMinecraft().player.rotationYaw;

            clonedPlayer = new EntityOtherPlayerMP(Wrapper.getMinecraft().world, Wrapper.getMinecraft().getSession().getProfile());
            clonedPlayer.copyLocationAndAnglesFrom(Wrapper.getMinecraft().player);
            clonedPlayer.rotationYawHead = Wrapper.getMinecraft().player.rotationYawHead;
            Wrapper.getMinecraft().world.addEntityToWorld(-100, clonedPlayer);
            Wrapper.getMinecraft().player.noClip = true;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        if (Wrapper.getMinecraft().player != null) {
            Wrapper.getMinecraft().player.setPositionAndRotation(posX, posY, posZ, yaw, pitch);
            Wrapper.getMinecraft().world.removeEntityFromWorld(-100);
            clonedPlayer = null;
            posX = posY = posZ = 0.D;
            pitch = yaw = 0.f;
            mc.player.capabilities.isFlying = false;
            Wrapper.getMinecraft().player.capabilities.setFlySpeed(0.05F);
            Wrapper.getMinecraft().player.noClip = false;
            Wrapper.getMinecraft().player.motionX = Wrapper.getMinecraft().player.motionY = Wrapper.getMinecraft().player.motionZ = 0.f;

            if (isRidingEntity) {
                Wrapper.getMinecraft().player.startRiding(ridingEntity, true);
            }
        }
        Wrapper.getMinecraft().renderGlobal.loadRenderers();
        super.onDisable();
    }

    @EventTarget
    public void onPacketSent(EventSendPacket event) {
        if (this.cancelPackets.getValue() && (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput)) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onPacketRecived(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            startPosX = packet.getX();
            startPosY = packet.getY();
            startPosZ = packet.getZ();
            startPitch = packet.getPitch();
            startYaw = packet.getYaw();
        }
    }

    @SubscribeEvent
    public void onPush(PlayerSPPushOutOfBlocksEvent event) {
        event.setCanceled(true);
    }

    private void playersSpeed(double speed) {
        if (Wrapper.getMinecraft().player != null) {
            MovementInput movementInput = Wrapper.getMinecraft().player.movementInput;
            double forward = movementInput.moveForward;
            double strafe = movementInput.moveStrafe;
            float yaw = Wrapper.getMinecraft().player.rotationYaw;
            if ((forward == 0.0D) && (strafe == 0.0D)) {
                Wrapper.getMinecraft().player.motionX = 0.0D;
                Wrapper.getMinecraft().player.motionZ = 0.0D;
            } else {
                if (forward != 0.0D) {
                    if (strafe > 0.0D) {
                        yaw += (forward > 0.0D ? -45 : 45);
                    } else if (strafe < 0.0D) {
                        yaw += (forward > 0.0D ? 45 : -45);
                    }
                    strafe = 0.0D;
                    if (forward > 0.0D) {
                        forward = 1.0D;
                    } else if (forward < 0.0D) {
                        forward = -1.0D;
                    }
                }
                Wrapper.getMinecraft().player.motionX = (forward * speed * Math.cos(Math.toRadians(yaw + 90.0F))
                        + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
                Wrapper.getMinecraft().player.motionZ = (forward * speed * Math.sin(Math.toRadians(yaw + 90.0F))
                        - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
            }
        }
    }

    @EventTarget
    public void onMove(PlayerMoveEvent event) {
        if (event.getEventState() != Event.State.PRE) return;
        mc.player.noClip = true;
    }

}