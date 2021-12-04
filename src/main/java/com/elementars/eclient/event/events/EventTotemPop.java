package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;
import net.minecraft.entity.player.EntityPlayer;

public class EventTotemPop extends Event {
    EntityPlayer player;

    public EventTotemPop(EntityPlayer entityPlayerIn) {
        this.player = entityPlayerIn;
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}
