package com.elementars.eclient.module.combat;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventDrinkPotion;
import com.elementars.eclient.event.events.EventReceivePacket;
import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.EntityUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.Entity;
import java.util.HashSet;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Set;

public class StrengthDetectW extends Module {
    public Set<EntityPlayer> strengthedPlayers;
    public Set<EntityPlayer> renderPlayers;

    public StrengthDetectW() {
        super("StrengthDetectW", "test", Keyboard.KEY_NONE, Category.COMBAT, true);
    }

    @Override
    public void onEnable() {
        this.strengthedPlayers = new HashSet<EntityPlayer>();
        this.renderPlayers = new HashSet<EntityPlayer>();
        Xulu.EVENT_MANAGER.register(this);
        EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        Xulu.EVENT_MANAGER.unregister(this);
        EVENT_BUS.unregister(this);
    }

    @Override
    public void onUpdate() {
        if (StrengthDetect.mc.player == null) {
            return;
        }
        for (final EntityPlayer ent : StrengthDetect.mc.world.playerEntities) {
            if (EntityUtil.isLiving((Entity)ent)) {

                if (((EntityLivingBase)ent).getHealth() <= 0.0f) {
                    continue;
                }
                if (ent.isPotionActive(MobEffects.STRENGTH) && !this.strengthedPlayers.contains(ent)) {
                    Command.sendChatMessage("§4[" + ent.getDisplayNameString() + "]§r is now strong");
                    this.strengthedPlayers.add(ent);
                }
                if (this.strengthedPlayers.contains(ent) && !ent.isPotionActive(MobEffects.STRENGTH)) {
                    Command.sendChatMessage("§3[" + ent.getDisplayNameString() + "]§r is no longer strong");
                    this.strengthedPlayers.remove(ent);
                }
                this.checkRender();
            }
        }
    }

    @SubscribeEvent
    public void onUseItem(LivingEntityUseItemEvent.Finish event) {
        Command.sendChatMessage("finish use item");
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (event.getItem().getItem() == Items.POTIONITEM) {
                for (PotionEffect potioneffect : PotionUtils.getEffectsFromStack(event.getItem())) {
                    if (potioneffect.getPotion().equals(MobEffects.STRENGTH)) {
                        strengthedPlayers.add(player);
                    }
                }
            }
        }
    }

    @EventTarget
    public void onItem(EventDrinkPotion event) {
        Command.sendChatMessage("drink event");
        if (event.getEntityLivingBase() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLivingBase();
            if (event.getStack().getItem() == Items.POTIONITEM) {
                for (PotionEffect potioneffect : PotionUtils.getEffectsFromStack(event.getStack())) {
                    if (potioneffect.getPotion().equals(MobEffects.STRENGTH)) {
                        strengthedPlayers.add(player);
                    }
                }
            }
        }
    }

    @EventTarget
    public void onPacket(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketEntityEffect) {
            Command.sendChatMessage("OO");
            SPacketEntityEffect packet = (SPacketEntityEffect) event.getPacket();
            if (Potion.getPotionById(packet.getEffectId()) == MobEffects.STRENGTH) {
                Command.sendChatMessage("is this strength");
                EntityPlayer player = mc.world.getEntityByID(packet.getEntityId()) instanceof EntityPlayer ? (EntityPlayer) mc.world.getEntityByID(packet.getEntityId()) : null;
                if (player != null) {
                    Command.sendChatMessage("we got a player right?");
                    strengthedPlayers.add(player);
                }
            }
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            Command.sendChatMessage("status");
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 9 && packet.getEntity(world) instanceof EntityPlayer) {
                Command.sendChatMessage("use item status");
                EntityPlayer player = (EntityPlayer) packet.getEntity(world);
                if (player.getHeldItemMainhand().getItem() == Items.POTIONITEM) {
                    Command.sendChatMessage("holding a potion?");
                    for (PotionEffect potioneffect : PotionUtils.getEffectsFromStack(player.getHeldItemMainhand())) {
                        if (potioneffect.getPotion().equals(MobEffects.STRENGTH)) {
                            Command.sendChatMessage("we got strength");
                            strengthedPlayers.add(player);
                        }
                    }
                }
            }
        }
    }

    public void checkRender() {
        try {
            this.renderPlayers.clear();
            for (final EntityPlayer ent : StrengthDetect.mc.world.playerEntities) {
                if (EntityUtil.isLiving((Entity)ent)) {
                    if (((EntityLivingBase)ent).getHealth() <= 0.0f) {
                        continue;
                    }
                    this.renderPlayers.add(ent);
                }
            }
            for (final EntityPlayer ent : this.strengthedPlayers) {
                if (!this.renderPlayers.contains(ent)) {
                    Command.sendChatMessage("§3[" + ent.getDisplayNameString() + "]§r is (probably) no longer strong");
                    this.strengthedPlayers.remove(ent);
                }
            }
        }
        catch (Exception ex) {}
    }

    @Override
    public void onWorldRender(final RenderEvent event) {
        if (mc.getRenderManager().options == null) {
            return;
        }
        final boolean isThirdPersonFrontal = mc.getRenderManager().options.thirdPersonView == 2;
        final float viewerYaw = mc.getRenderManager().playerViewY;
        for (final EntityPlayer e : this.strengthedPlayers) {
            if (e.getName() == StrengthDetect.mc.player.getName()) {
                return;
            }
            GlStateManager.pushMatrix();
            final Vec3d pos = EntityUtil.getInterpolatedPos((Entity)e, event.getPartialTicks());
            GlStateManager.translate(pos.x - StrengthDetect.mc.getRenderManager().renderPosX, pos.y - StrengthDetect.mc.getRenderManager().renderPosY, pos.z - StrengthDetect.mc.getRenderManager().renderPosZ);
            GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(-viewerYaw, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1), 1.0f, 0.0f, 0.0f);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GL11.glColor3f(1.0f, 0.2f, 0.2f);
            GlStateManager.disableTexture2D();
            GL11.glLineWidth(4.0f);
            GL11.glEnable(2848);
            GL11.glBegin(2);
            GL11.glVertex2d((double)(-e.width / 2.0f), 0.0);
            GL11.glVertex2d((double)(-e.width / 2.0f), (double)e.height);
            GL11.glVertex2d((double)(e.width / 2.0f), (double)e.height);
            GL11.glVertex2d((double)(e.width / 2.0f), 0.0);
            GL11.glEnd();
            GlStateManager.popMatrix();
        }
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GlStateManager.disableDepth();
        GlStateManager.enableCull();
        GlStateManager.glLineWidth(1.0f);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
    }
}
