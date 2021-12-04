/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package com.elementars.eclient.util;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;

public final class MovementUtils implements Helper {
    

    public static float getSpeed() {
        return (float) Math.sqrt(Wrapper.getMinecraft().player.motionX * Wrapper.getMinecraft().player.motionX + Wrapper.getMinecraft().player.motionZ * Wrapper.getMinecraft().player.motionZ);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static boolean isMoving() {
        return Wrapper.getMinecraft().player != null && (Wrapper.getMinecraft().player.movementInput.moveForward != 0F || Wrapper.getMinecraft().player.movementInput.moveStrafe != 0F);
    }

    public static boolean hasMotion() {
        return Wrapper.getMinecraft().player.motionX != 0D && Wrapper.getMinecraft().player.motionZ != 0D && Wrapper.getMinecraft().player.motionY != 0D;
    }

    public static void strafe(final float speed) {
        if(!isMoving())
            return;

        final double yaw = getDirection();
        Wrapper.getMinecraft().player.motionX = -Math.sin(yaw) * speed;
        Wrapper.getMinecraft().player.motionZ = Math.cos(yaw) * speed;
    }

    public static void forward(final double length) {
        final double yaw = Math.toRadians(Wrapper.getMinecraft().player.rotationYaw);
        Wrapper.getMinecraft().player.setPosition(Wrapper.getMinecraft().player.posX + (-Math.sin(yaw) * length), Wrapper.getMinecraft().player.posY, Wrapper.getMinecraft().player.posZ + (Math.cos(yaw) * length));
    }

    public static double[] forward2(final double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static double getDirection() {
        float rotationYaw = Wrapper.getMinecraft().player.rotationYaw;

        if(Wrapper.getMinecraft().player.moveForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if(Wrapper.getMinecraft().player.moveForward < 0F)
            forward = -0.5F;
        else if(Wrapper.getMinecraft().player.moveForward > 0F)
            forward = 0.5F;

        if(Wrapper.getMinecraft().player.moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if(Wrapper.getMinecraft().player.moveStrafing < 0F)
            rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.player != null && mc.player.isPotionActive(Potion.getPotionById(1))) {
            final int amplifier = mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }

        return baseSpeed;
    }

    public static double getDirectionStrafe() {
        return Math.toRadians(Minecraft.getMinecraft().player.rotationYaw);
    }

    public static double getSpeedStrafe() {
        return Math.sqrt(Math.pow(Minecraft.getMinecraft().player.motionX, 2) + Math.pow(Minecraft.getMinecraft().player.motionX, 2));
    }

    public static void setSpeedStrafe(double speed) {
        Minecraft.getMinecraft().player.motionX = -Math.sin(getDirection()) * speed;
        Minecraft.getMinecraft().player.motionZ = Math.cos(getDirection()) * speed;
    }
}