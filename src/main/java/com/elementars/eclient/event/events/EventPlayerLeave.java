package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;
import com.mojang.authlib.GameProfile;

public class EventPlayerLeave extends Event {
    GameProfile gameProfile;

    public EventPlayerLeave(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

}
