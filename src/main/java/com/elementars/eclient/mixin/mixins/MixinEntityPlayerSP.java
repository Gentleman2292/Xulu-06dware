package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.Event;
import com.elementars.eclient.event.events.EventPreMotionUpdates;
import com.elementars.eclient.event.events.MotionEvent;
import com.elementars.eclient.event.events.MotionEventPost;
import com.elementars.eclient.event.events.PlayerMoveEvent;
import com.elementars.eclient.module.ModuleManager;
import com.elementars.eclient.module.misc.AntiSound;
import com.elementars.eclient.module.render.OffhandSwing;
import com.elementars.eclient.util.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by 086 on 12/12/2017.
 */
@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends MixinAbstractClientPlayer {

    @Shadow public abstract void move(MoverType type, double x, double y, double z);

    @Shadow public abstract void swingArm(EnumHand hand);

    @Final public NetHandlerPlayClient connection;

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;closeScreen()V"))
    public void closeScreen(EntityPlayerSP entityPlayerSP) {
        if (ModuleManager.isModuleEnabled("PortalChat")) return;
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    public void closeScreen(Minecraft minecraft, GuiScreen screen) {
        if (Xulu.MODULE_MANAGER.getModuleByName("PortalChat").isToggled()) return;
    }

//    @ModifyArgs(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
//    public void move(Args args) {
//        MoverType type = args.get(0);
//        double x = args.get(1);<
//        double y = args.get(2);
//        double z = args.get(3);
//        PlayerMoveEvent event = new PlayerMoveEvent(type, x, y, z);
//        KamiMod.EVENT_BUS.post(event);
//        if (event.isCancelled()) {
//            x = y = z = 0;
//        } else {
//            x = event.getX();
//            y = event.getY();
//            z = event.getZ();
//        }
//        args.set(1, x);
//        args.set(2, y);
//        args.set(3, z);
//    }

    @Inject(method = "onUpdate", at = @At("RETURN"), cancellable = true)
    private void onUpdatePost(CallbackInfo callbackInfo) {
        if (Wrapper.getMinecraft().world.isBlockLoaded(new BlockPos(Wrapper.getMinecraft().player.posX, 0.0D, Wrapper.getMinecraft().player.posZ)))
        {
            EventPreMotionUpdates preMotion = new EventPreMotionUpdates(Wrapper.getMinecraft().player.rotationYaw, Wrapper.getMinecraft().player.rotationPitch, Wrapper.getMinecraft().player.posY);
            preMotion.call();
            if (preMotion.isCancelled()) {
                callbackInfo.cancel();
            }
        }
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void onUpdateWalkingPlayer(CallbackInfo info) {
        MotionEvent event = new MotionEvent();
        event.call();
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    public void onUpdateWalkingPlayerPost(CallbackInfo info) {
        MotionEventPost event = new MotionEventPost();
        event.call();
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MoverType type, double x, double y, double z, CallbackInfo info) {
        PlayerMoveEvent event = new PlayerMoveEvent(type, x, y, z);
        event.setState(Event.State.PRE);
        event.call();
        if (event.isCancelled()) {
            super.move(type, event.getX(), event.getY(), event.getZ());
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("RETURN"), cancellable = true)
    public void moveReturn(MoverType type, double x, double y, double z, CallbackInfo info) {
        PlayerMoveEvent event = new PlayerMoveEvent(type, x, y, z);
        event.setState(Event.State.POST);
        event.call();
    }

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    public void swingArm(EnumHand enumHand, CallbackInfo info) {
        try {
            if (Xulu.MODULE_MANAGER.getModule(OffhandSwing.class).isToggled()) {
                super.swingArm(EnumHand.OFF_HAND);
                Wrapper.getMinecraft().player.connection.sendPacket(new CPacketAnimation(enumHand));
                info.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Redirect(method = "notifyDataManagerChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundHandler;playSound(Lnet/minecraft/client/audio/ISound;)V"))
    private void playElytraSound(SoundHandler soundHandler, ISound sound) {
        if (!Xulu.MODULE_MANAGER.getModule(AntiSound.class).isToggled() || !Xulu.MODULE_MANAGER.getModuleT(AntiSound.class).elytra.getValue()) {
            soundHandler.playSound(sound);
        }
    }
}
