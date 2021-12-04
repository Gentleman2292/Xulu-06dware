package com.elementars.eclient.command.commands;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.util.Wrapper;

public class YawCommand extends Command {
    public YawCommand() {
        super("setyaw", "Sets the yaw of the player", new String[]{});
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            sendChatMessage("Please specify the yaw!");
            return;
        }
        Wrapper.getMinecraft().player.rotationYaw = Integer.valueOf(args[1]);
    }
}
