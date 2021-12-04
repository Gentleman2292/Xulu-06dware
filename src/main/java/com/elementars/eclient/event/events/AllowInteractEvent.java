package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;

public class AllowInteractEvent extends Event {

    public boolean usingItem;

    public AllowInteractEvent(boolean usingItem) {
        this.usingItem = usingItem;
    }
}
