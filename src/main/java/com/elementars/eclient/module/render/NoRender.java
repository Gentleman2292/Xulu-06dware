package com.elementars.eclient.module.render;

import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventReceivePacket;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

/**
 * Created by 086 on 4/02/2018.
 */
public class NoRender extends Module {

    private final Value<Boolean> mob = register(new Value<>("Mob", this, false));
    private final Value<Boolean> gentity = register(new Value<>("GEntity", this, false));
    public final Value<Boolean> armor = register(new Value<>("Armor", this, false));
    public final Value<Boolean> armorTrans = register(new Value<>("Armor Transparency", this, false));
    public final Value<Integer> alpha = register(new Value<>("Transparency", this, 255, 0, 255));
    private final Value<Boolean> object = register(new Value<>("Object", this, false));
    private final Value<Boolean> xp = register(new Value<>("XP", this, false));
    private final Value<Boolean> paint = register(new Value<>("Paintings", this, false));
    private final Value<Boolean> fire = register(new Value<>("Fire", this, true));

    public NoRender() {
        super("NoRender", "Prevents rendering of certain things", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @EventTarget
    public void onPacket(EventReceivePacket event) {
        Packet packet = event.getPacket();
        if ((packet instanceof SPacketSpawnMob && mob.getValue()) ||
                (packet instanceof SPacketSpawnGlobalEntity && gentity.getValue()) ||
                (packet instanceof SPacketSpawnObject && object.getValue()) ||
                (packet instanceof SPacketSpawnExperienceOrb && xp.getValue()) ||
                (packet instanceof SPacketSpawnPainting && paint.getValue()))
            event.setCancelled(true);
    }

    @SubscribeEvent
    public void onBlockOverlay(RenderBlockOverlayEvent event) {
        if (fire.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) event.setCanceled(true);
    }
}
