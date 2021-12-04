package com.elementars.eclient.module.misc;

import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventReceivePacket;
import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @since 9/28/2020 - 9:52 PM
 */
public class Time extends Module {

    long oldTime;

    private final Value<Long> time = register(new Value<>("Time", this, 0L, 0L, 24000L));

    public Time() {
        super("Time", "Clientside sets the time", Keyboard.KEY_NONE, Category.MISC, true);
    }

    @EventTarget
    public void onTime(EventReceivePacket event) {
        if(event.getPacket() instanceof SPacketTimeUpdate) {
            this.oldTime = ((SPacketTimeUpdate) event.getPacket()).getWorldTime();
            event.setCancelled(true);
        }
    }

    @Override
    public void onUpdate() {
        if (mc.world == null) return;
        mc.world.setWorldTime(time.getValue());
    }

    @Override
    public void onEnable() {
        this.oldTime = mc.world.getWorldTime();
    }

    @Override
    public void onDisable() {
        mc.world.setWorldTime(this.oldTime);
    }
}
