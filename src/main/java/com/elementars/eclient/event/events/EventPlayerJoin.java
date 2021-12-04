package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;
import com.mojang.authlib.GameProfile;

public class EventPlayerJoin extends Event {
    GameProfile gameProfile;

    public EventPlayerJoin(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

}
