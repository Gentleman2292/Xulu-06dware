//
// Decompiled by Procyon v0.5.36
//

package com.elementars.eclient.module.movement;

import java.util.Arrays;
import java.util.Base64;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

import com.elementars.eclient.event.Event;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.PlayerMoveEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.settings.Value;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import org.lwjgl.input.Keyboard;

public class Strafe extends Module {
    private static /* synthetic */ String[] llIlIIl;
    private static /* synthetic */ int[] llIllII;
    private /* synthetic */ double motionSpeed;
    private /* synthetic */ int currentState;
    @EventHandler
    private /* synthetic */ Listener<PlayerMoveEvent> packetEventListener;
    private /* synthetic */ double prevDist;

    Value<Double> multiplier = register(new Value<>("Multiplier", this, 1.0, 0.1, 2.0));
    Value<Boolean> autoSprint = register(new Value<>("Auto Sprint", this, false));
    Value<Boolean> accelerationTimer = register(new Value<>("Acceleration Timer", this, false));
    Value<Integer> timerSpeed = register(new Value<>("Timer Speed", this, 1, 0, 10));
    Value<Boolean> speedDetect = register(new Value<>("Speed Detect", this, true));
    Value<Boolean> jumpDetect = register(new Value<>("Leaping Detect", this, true));
    Value<Double> extraYBoost = register(new Value<>("Extra Y Boost", this, 0.0, 0.0, 1.0));
    Value<Boolean> chat = register(new Value<>("Toggle msgs", this, false));

    public Strafe() {
        super("Strafe", "Speed mode strafe", Keyboard.KEY_NONE, Category.MOVEMENT, true);
    }

    @Override
    public void onEnable() {
        if (chat.getValue()) {
            sendDebugMessage(ChatFormatting.GREEN + "Enabled!");
        }
    }

    @Override
    public void onDisable() {
        if (chat.getValue()) {
            sendDebugMessage(ChatFormatting.RED + "Disabled!");
        }
    }

    @Override
    public String getHudInfo() {
        return "Speed";
    }

    static {
        lIIlIIIII();
        lIIIllllI();
    }

    private static void lIIlIIIII() {
        (llIllII = new int[10])[0] = ((0xD5 ^ 0x80) & ~(0x6 ^ 0x53));
        Strafe.llIllII[1] = " ".length();
        Strafe.llIllII[2] = "  ".length();
        Strafe.llIllII[3] = "   ".length();
        Strafe.llIllII[4] = (0xB0 ^ 0xBA);
        Strafe.llIllII[5] = (0x14 ^ 0x7D ^ (0x4D ^ 0x20));
        Strafe.llIllII[6] = (57 + 30 + 7 + 36 ^ 134 + 15 - 64 + 50);
        Strafe.llIllII[7] = (0xD5 ^ 0x8D ^ (0x3F ^ 0x61));
        Strafe.llIllII[8] = (0x51 ^ 0x11 ^ (0x4A ^ 0xD));
        Strafe.llIllII[9] = (71 + 178 - 231 + 172 ^ 69 + 149 - 198 + 162);
    }

    
    private static int lIIlIIIIl(float var0, float var1) {
        float var2;
        return (var2 = var0 - var1) == 0.0F ? 0 : (var2 < 0.0F ? -1 : 1);
    }


    private static boolean lIIlIlIII(final int lllIIlIIlIIIlll) {
        return lllIIlIIlIIIlll <= 0;
    }

    private static int lIIlIIllI(float var0, float var1) {
        float var2;
        return (var2 = var0 - var1) == 0.0F ? 0 : (var2 < 0.0F ? -1 : 1);
    }


    private static boolean lIIlIlIIl(final int lllIIlIIlIIIlIl) {
        return lllIIlIIlIIIlIl > 0;
    }

    @Override
    public void onUpdate() {
        if (isNull(Strafe.mc.player)) {
            return;
        }
        this.prevDist = Math.sqrt((Strafe.mc.player.posX - Strafe.mc.player.prevPosX) * (Strafe.mc.player.posX - Strafe.mc.player.prevPosX) + (Strafe.mc.player.posZ - Strafe.mc.player.prevPosZ) * (Strafe.mc.player.posZ - Strafe.mc.player.prevPosZ));
        if (lIIlIIlII(((boolean)this.accelerationTimer.getValue()) ? 1 : 0)) {
            Strafe.mc.timer.tickLength = 50.0f / this.timerSpeed.getValue();
        }
        else if (lIIlIIlII(lIIlIIIIl(Strafe.mc.timer.tickLength, 50.0f))) {
            Strafe.mc.timer.tickLength = 50.0f;
        }
        if (lIIlIIlIl(Strafe.mc.player.isSprinting() ? 1 : 0) && lIIlIIlII(((boolean)this.autoSprint.getValue()) ? 1 : 0)) {
            Strafe.mc.player.setSprinting((boolean)(Strafe.llIllII[1] != 0));
            Strafe.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Strafe.mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }
    }

    private static int lIIlIIlll(double var0, double var2) {
        double var4;
        return (var4 = var0 - var2) == 0.0D ? 0 : (var4 < 0.0D ? -1 : 1);
    }


    private static boolean isNull(final Object lllIIlIIlIIllIl) {
        return lllIIlIIlIIllIl == null;
    }

    private static boolean lIIlIIlII(final int lllIIlIIlIIlIll) {
        return lllIIlIIlIIlIll != 0;
    }

    @EventTarget
    public void onMove(PlayerMoveEvent event) {
        if (event.getEventState() != Event.State.PRE) return;
        double lllIIlIIlllIlII;
        double lllIIlIIlllIIIl;
        double lllIIlIIlllIIII;
        double lllIIlIIllIllll;
        if (!isNull(Strafe.mc.player)) {
            switch (this.currentState) {
                case 0: {
                    this.currentState += Strafe.llIllII[1];
                    this.prevDist = 0.0;
                    break;
                }
                case 2: {
                    lllIIlIIlllIlII = 0.40123128 + this.extraYBoost.getValue();
                    if ((!lIIlIIlIl(lIIlIIllI(Strafe.mc.player.moveForward, 0.0f)) || lIIlIIlII(lIIlIIllI(Strafe.mc.player.moveStrafing, 0.0f))) && lIIlIIlII(Strafe.mc.player.onGround ? 1 : 0)) {
                        if (lIIlIIlII(Strafe.mc.player.isPotionActive(MobEffects.JUMP_BOOST) ? 1 : 0) && lIIlIIlII(((boolean)this.jumpDetect.getValue()) ? 1 : 0)) {
                            lllIIlIIlllIlII += (Strafe.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + Strafe.llIllII[1]) * 0.1f;
                        }
                        event.setY(mc.player.motionY = lllIIlIIlllIlII);
                        this.motionSpeed *= 2.149;
                        break;
                    }
                    else {
                        break;
                    }
                }
                case 3: {
                    this.motionSpeed = this.prevDist - 0.76 * (this.prevDist - this.getBaseMotionSpeed());
                    break;
                }
                default: {
                    if ((!lIIlIlIII(Strafe.mc.world.getCollisionBoxes((Entity)Strafe.mc.player, Strafe.mc.player.getEntityBoundingBox().offset(0.0, Strafe.mc.player.motionY, 0.0)).size()) || lIIlIIlII(Strafe.mc.player.collidedVertically ? 1 : 0)) && lIIlIlIIl(this.currentState)) {
                        if (lIIlIIlIl(lIIlIIllI(Strafe.mc.player.moveForward, 0.0f)) && lIIlIIlIl(lIIlIIllI(Strafe.mc.player.moveStrafing, 0.0f))) {
                            currentState = Strafe.llIllII[0];
                        }
                        else {
                            currentState = Strafe.llIllII[1];
                        }
                    }
                    this.motionSpeed = this.prevDist - this.prevDist / 159.0;
                    break;
                }
            }
            this.motionSpeed = Math.max(this.motionSpeed, this.getBaseMotionSpeed());
            lllIIlIIlllIIIl = Strafe.mc.player.movementInput.moveForward;
            lllIIlIIlllIIII = Strafe.mc.player.movementInput.moveStrafe;
            lllIIlIIllIllll = Strafe.mc.player.rotationYaw;
            if (lIIlIIlIl(lIIlIIlll(lllIIlIIlllIIIl, 0.0)) && lIIlIIlIl(lIIlIIlll(lllIIlIIlllIIII, 0.0))) {
                event.setX(0.0);
                event.setZ(0.0);
            }
            if (lIIlIIlII(lIIlIIlll(lllIIlIIlllIIIl, 0.0)) && lIIlIIlII(lIIlIIlll(lllIIlIIlllIIII, 0.0))) {
                lllIIlIIlllIIIl *= Math.sin(0.7853981633974483);
                lllIIlIIlllIIII *= Math.cos(0.7853981633974483);
            }
            event.setX((lllIIlIIlllIIIl * this.motionSpeed * -Math.sin(Math.toRadians(lllIIlIIllIllll)) + lllIIlIIlllIIII * this.motionSpeed * Math.cos(Math.toRadians(lllIIlIIllIllll))) * (this.multiplier.getValue() * 0.99));
            event.setZ((lllIIlIIlllIIIl * this.motionSpeed * Math.cos(Math.toRadians(lllIIlIIllIllll)) - lllIIlIIlllIIII * this.motionSpeed * -Math.sin(Math.toRadians(lllIIlIIllIllll))) * (this.multiplier.getValue() * 0.99));
            this.currentState += Strafe.llIllII[1];
        }
        event.setCancelled(true);
    }

    private static void lIIIllllI() {
        (llIlIIl = new String[Strafe.llIllII[8]])[Strafe.llIllII[0]] = lIIIllIII("n9pHF6SFvkOs6iUr+fnXgA==", "GmCTC");
        Strafe.llIlIIl[Strafe.llIllII[1]] = lIIIllIII("4noHmwJ5F40+cu8qBPcyzA==", "CVFaT");
        Strafe.llIlIIl[Strafe.llIllII[2]] = lIIIllIII("R+hGwU+dCgQQcUdIkD9ZYaUO+QBhMxiN", "RjGgZ");
        Strafe.llIlIIl[Strafe.llIllII[3]] = lIIIllIII("Dk9SQuIPQSn5I8lWMj8Z+w==", "dWNML");
        Strafe.llIlIIl[Strafe.llIllII[5]] = lIIIllIII("rPWGh7vSeiSJWWJOJQfq5wdZ8fI6Y9G+", "QkkkG");
        Strafe.llIlIIl[Strafe.llIllII[6]] = lIIIllIII("6BSD78RsHX6yVgm/4JINjBgTGCxZfgXF", "rXpxu");
        Strafe.llIlIIl[Strafe.llIllII[7]] = lIIIlllIl("ENR8rJxJYtA86kRMf8iVlQ==", "RTxXY");
    }

    private double getBaseMotionSpeed() {
        double lllIIlIIllllllI = 0.272;
        if (lIIlIIlII(Strafe.mc.player.isPotionActive(MobEffects.SPEED) ? 1 : 0) && lIIlIIlII(((boolean)this.speedDetect.getValue()) ? 1 : 0)) {
            final int lllIIlIlIIIIIII = Objects.requireNonNull(Strafe.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            lllIIlIIllllllI *= 1.0 + 0.2 * lllIIlIlIIIIIII;
        }
        return lllIIlIIllllllI;
    }

    private static boolean lIIlIIlIl(final int lllIIlIIlIIlIIl) {
        return lllIIlIIlIIlIIl == 0;
    }

    private static String lIIIllIII(final String lllIIlIIlIlllll, final String lllIIlIIllIIIII) {
        try {
            final SecretKeySpec lllIIlIIllIIlII = new SecretKeySpec(MessageDigest.getInstance("MD5").digest(lllIIlIIllIIIII.getBytes(StandardCharsets.UTF_8)), "Blowfish");
            final Cipher lllIIlIIllIIIll = Cipher.getInstance("Blowfish");
            lllIIlIIllIIIll.init(Strafe.llIllII[2], lllIIlIIllIIlII);
            return new String(lllIIlIIllIIIll.doFinal(Base64.getDecoder().decode(lllIIlIIlIlllll.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        }
        catch (Exception lllIIlIIllIIIlI) {
            lllIIlIIllIIIlI.printStackTrace();
            return null;
        }
    }

    private static String lIIIlllIl(final String lllIIlIIlIlIlII, final String lllIIlIIlIlIIIl) {
        try {
            final SecretKeySpec lllIIlIIlIlIlll = new SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance("MD5").digest(lllIIlIIlIlIIIl.getBytes(StandardCharsets.UTF_8)), Strafe.llIllII[9]), "DES");
            final Cipher lllIIlIIlIlIllI = Cipher.getInstance("DES");
            lllIIlIIlIlIllI.init(Strafe.llIllII[2], lllIIlIIlIlIlll);
            return new String(lllIIlIIlIlIllI.doFinal(Base64.getDecoder().decode(lllIIlIIlIlIlII.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        }
        catch (Exception lllIIlIIlIlIlIl) {
            lllIIlIIlIlIlIl.printStackTrace();
            return null;
        }
    }
}
