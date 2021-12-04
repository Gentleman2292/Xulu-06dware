package com.elementars.eclient.event.events;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.command.CommandManager;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.ModuleManager;
import com.elementars.eclient.util.*;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class KeyRegistry implements Helper {

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        /*

         */
        ModuleManager.onKey();

    }

    @SubscribeEvent
    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (Wrapper.getMinecraft().world != null
                && event.getEntity().getEntityWorld().isRemote
                && event.getEntityLiving().equals(Wrapper.getPlayer())) {
            LocalPlayerUpdateEvent ev = new LocalPlayerUpdateEvent(event.getEntityLiving());
            ev.call();
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.isCanceled()) return;

        RenderGameOverlayEvent.ElementType target = RenderGameOverlayEvent.ElementType.EXPERIENCE;
        if (!Wrapper.getPlayer().isCreative() && Wrapper.getPlayer().getRidingEntity() instanceof AbstractHorse)
            target = RenderGameOverlayEvent.ElementType.HEALTHMOUNT;

        if (event.getType() == target) {
            Xulu.MODULE_MANAGER.onRender();
            GL11.glPushMatrix();
            GL11.glPopMatrix();
            XuluTessellator.releaseGL();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null) return;
        Xulu.MODULE_MANAGER.onUpdate();
        TargetPlayers.onUpdate();
        RainbowUtils.updateRainbow();
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        TargetPlayers.onAttack(event);
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) return;
        Xulu.MODULE_MANAGER.onWorldRender(event);
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatEvent event) {
        if (event.getMessage().startsWith(Command.getPrefix())) {
            String message = event.getMessage();
            event.setCanceled(true);
            CommandManager.runCommand(message.substring(Command.getPrefix().length()));
        }
    }

    @SubscribeEvent
    public void onRenderPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO && Xulu.MODULE_MANAGER.getModuleByName("BossStack").isToggled()) {
            event.setCanceled(true);
        }
    }
    /*
    @SubscribeEvent
    private void onUnload(net.minecraftforge.event.world.ChunkEvent.Unload event) {
        UnloadChunkEvent unloadChunkEvent = new UnloadChunkEvent(event.getChunk());
        unloadChunkEvent.call();
    }
    */
}
