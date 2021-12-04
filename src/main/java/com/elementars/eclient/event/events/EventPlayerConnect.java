package com.elementars.eclient.event.events;

import com.elementars.eclient.event.Event;

import java.util.UUID;

public class EventPlayerConnect extends Event {
    UUID uuid;
    String name;

    public EventPlayerConnect(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public static class Join extends EventPlayerConnect {
        public Join(UUID uuid, String name) {
            super(uuid, name);
        }
    }

    public static class Leave extends EventPlayerConnect {
        public Leave(UUID uuid, String name) {
            super(uuid, name);
        }
    }
}
