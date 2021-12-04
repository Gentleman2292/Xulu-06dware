package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;
import net.minecraft.entity.player.EntityPlayer;

public class EventUseItem extends Event {

    EntityPlayer player;

    public EventUseItem(EntityPlayer entityPlayer) {
        this.player = entityPlayer;
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}
