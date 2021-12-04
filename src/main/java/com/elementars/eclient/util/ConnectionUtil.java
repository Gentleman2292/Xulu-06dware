package com.elementars.eclient.util;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventPlayerConnect;
import com.elementars.eclient.event.events.EventReceivePacket;
import com.google.common.base.Strings;
import net.minecraft.network.play.server.SPacketPlayerListItem;

import java.util.Objects;
import java.util.UUID;

public class ConnectionUtil {
    public static ConnectionUtil INSTANCE;

    public ConnectionUtil() {
        Xulu.EVENT_MANAGER.register(this);
    }

    private void fireEvents(SPacketPlayerListItem.Action action, UUID uuid, String name) {
        if (uuid == null) {
            return;
        }
        switch (action) {
            case ADD_PLAYER: {
                EventPlayerConnect.Join eventPlayerConnect = new EventPlayerConnect.Join(uuid, name);
                eventPlayerConnect.call();
                break;
            }
            case REMOVE_PLAYER: {
                EventPlayerConnect.Leave eventPlayerConnect = new EventPlayerConnect.Leave(uuid, name);
                eventPlayerConnect.call();
                break;
            }
        }
    }

    @EventTarget
    public void onScoreboardEvent(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            final SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();
            if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals(packet.getAction())
                    && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals(packet.getAction())) {
                return;
            }

            packet
                    .getEntries()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(
                            data ->
                                    !Strings.isNullOrEmpty(data.getProfile().getName())
                                            || data.getProfile().getId() != null)
                    .forEach(
                            data -> {
                                final UUID id = data.getProfile().getId();
                                final String name = data.getProfile().getName();
                                fireEvents(packet.getAction(), id, name);
                            });
        }
    }
}
