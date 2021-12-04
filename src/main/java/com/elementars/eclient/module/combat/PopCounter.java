package com.elementars.eclient.module.combat;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventReceivePacket;
import com.elementars.eclient.event.events.EventTotemPop;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.ColorTextUtils;
import com.elementars.eclient.util.TargetPlayers;
import dev.xulu.settings.Value;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Elementars
 */
public class PopCounter extends Module {

    public ConcurrentHashMap<EntityPlayer, Integer> popMap = new ConcurrentHashMap<>();
    private final Value<Boolean> onlyTargets = register(new Value<>("Only Targets", this, true));
    private final Value<Boolean> chat = register(new Value<>("Send Message", this, true));
    private final Value<Boolean> watermark = register(new Value<>("Watermark", this, true));
    private final Value<String> color = register(new Value<>("Color", this, "White", ColorTextUtils.colors));
    private final Value<String> ncolor = register(new Value<>("Number Color", this, "White", ColorTextUtils.colors));

    public PopCounter() {
        super("PopCounter", "Counts how many times your enemy pops", Keyboard.KEY_NONE, Category.COMBAT, true);
        INSTANCE = this;
    }

    public static PopCounter INSTANCE;

    @Override
    public void onUpdate() {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.getHealth() == 0.0f  && popMap.containsKey(player)) {
                if (chat.getValue())
                    sendChatMessage(player.getName() + " has died!");
                popMap.remove(player);
            }
        }
    }

    @EventTarget
    public void onPop(EventTotemPop event) {
        if (TargetPlayers.targettedplayers.containsKey(event.getPlayer().getName()) || !onlyTargets.getValue()) {
            int pops = popMap.getOrDefault(event.getPlayer(), 0) + 1;
            if (chat.getValue())
                sendChatMessage(event.getPlayer().getName() + " has popped " + ColorTextUtils.getColor(ncolor.getValue()) + pops + ColorTextUtils.getColor(color.getValue()) + " times!");
            popMap.put(event.getPlayer(), pops);
        }
    }

    @EventTarget
    public void onPacket(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                EntityPlayer entity = (EntityPlayer) packet.getEntity(mc.world);
                EventTotemPop eventTotemPop = new EventTotemPop(entity);
                eventTotemPop.call();
            }
        }
    }

    public void sendChatMessage(String message) {
        if (watermark.getValue()) {
            Command.sendChatMessage(ColorTextUtils.getColor(color.getValue()) + message);
        } else {
            Command.sendRawChatMessage(ColorTextUtils.getColor(color.getValue()) + message);
        }
    }
}
