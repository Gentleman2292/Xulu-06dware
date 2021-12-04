package com.elementars.eclient.command.commands;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.player.SelfLogoutSpot;

public class LogspotCommand extends Command {
    public LogspotCommand() {
        super("logspot", "Shows your logout spot from a given server", new String[]{});
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            sendChatMessage("Please specify a server IP");
            return;
        }
        if (SelfLogoutSpot.INSTANCE.logoutMap.isEmpty() || SelfLogoutSpot.INSTANCE.logoutMap.get(args[1]) == null) {
            sendChatMessage("Your logout spot is not saved for that server!");
        } else {
            sendChatMessage("Your logout spot is - " + SelfLogoutSpot.INSTANCE.logoutMap.get(args[1]));
        }
    }
}
