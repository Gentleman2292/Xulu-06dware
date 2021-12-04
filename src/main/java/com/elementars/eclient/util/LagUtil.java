package com.elementars.eclient.util;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventReceivePacket;
import net.minecraft.network.play.server.SPacketTimeUpdate;

public class LagUtil {

    public static LagUtil INSTANCE;

    public LagUtil() {
        Xulu.EVENT_MANAGER.register(this);
    }

    private long timeLastTimeUpdate = -1;

    public long getLastTimeDiff() {
        if (timeLastTimeUpdate != -1) {
            return System.currentTimeMillis() - timeLastTimeUpdate;
        } else {
            return 0;
        }
    }

    @EventTarget
    public void onPacketPreceived(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            timeLastTimeUpdate = System.currentTimeMillis();
            INSTANCE.timeLastTimeUpdate = timeLastTimeUpdate;
        }
    }
}
