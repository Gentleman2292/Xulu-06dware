package com.elementars.eclient.macro;

import com.elementars.eclient.command.CommandManager;
import com.elementars.eclient.util.Wrapper;
import net.minecraft.network.play.client.CPacketChatMessage;

public class Macro {
    private String macro;
    private int key;

    public Macro(String msg, int key) {
        this.macro = msg;
        this.key = key;
    }

    public String getMacro() {
        return macro;
    }

    public int getKey() {
        return key;
    }

    public void runMacro() {
        if (this.macro.startsWith(".")) {
            CommandManager.runCommand(this.macro.substring(1));
        }else {
            if (Wrapper.getMinecraft().getConnection() != null) {
                Wrapper.getMinecraft().getConnection().sendPacket(new CPacketChatMessage(this.macro));
            }
        }
    }
}
