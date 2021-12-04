package com.elementars.eclient.module.combat;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

/**
 * Created by 086 on 9/04/2018.
 */
public class AutoLog extends Module {
    private Value<Integer> health = register(new Value<>("Health", this, 6, 0, 36));
    private boolean shouldLog = false;
    long lastLog = System.currentTimeMillis();
    
    public AutoLog() {
        super("AutoLog", "Automatically Logs", Keyboard.KEY_NONE, Category.COMBAT, true);
    }

    @Override
    public void onEnable() {
        //MinecraftForge.EVENT_BUS.register(this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        //MinecraftForge.EVENT_BUS.unregister(this);
        super.onDisable();
    }

    @SubscribeEvent
    private void onEntity(EntityJoinWorldEvent event) {
        if (mc.player == null) return;
        if (event.getEntity() instanceof EntityEnderCrystal) {
            if (mc.player.getHealth() - AutoCrystal.calculateDamage((EntityEnderCrystal) event.getEntity(), mc.player) < health.getValue()) {
                log();
            }
        }
    }

    @Override
    public void onUpdate() {
        if (shouldLog) {
            shouldLog = false;
            if (System.currentTimeMillis() - lastLog < 2000) return;
            Minecraft.getMinecraft().getConnection().handleDisconnect(new SPacketDisconnect(new TextComponentString("AutoLogged")));
        }
    }

    private void log() {
        Xulu.MODULE_MANAGER.getModuleByName("AutoReconnect").disable();
        shouldLog = true;
        lastLog = System.currentTimeMillis();
    }

}
