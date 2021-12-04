package com.elementars.eclient.module.misc;

import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventSendPacket;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.network.play.client.CPacketChatMessage;
import org.lwjgl.input.Keyboard;

/**
 * Created by 086 on 8/04/2018.
 */
public class CustomChat extends Module {

    private final Value<Boolean> commands = register(new Value<>("Commands", this, false));
    private final Value<Boolean> mode = register(new Value<>("2b2t Mode", this, false));

    public CustomChat() {
        super("CustomChat", "Appends XULU to the end of your chat messages", Keyboard.KEY_NONE, Category.MISC, true);
    }

    //private final String KAMI_SUFFIX = " \u23D0 \u1D0B\u1D00\u1D0D\u026A";
    private final String suffix1 = " \u23D0 \u166D \u144C \u14AA \u144C";
    private final String suffix2 = " | X U L U";

    @EventTarget
    public void onPacket(EventSendPacket event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/") && !commands.getValue()) return;
            s += (this.mode.getValue() ? suffix2 : suffix1);
            if (s.length() >= 256) s = s.substring(0,256);
            ((CPacketChatMessage) event.getPacket()).message = s;
        }
    }


}
