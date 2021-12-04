package com.elementars.eclient.command.commands;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.player.AntiVoid;
import com.elementars.eclient.module.player.SelfLogoutSpot;

/**
 * @author Elementars
 * @since 6/23/2020 - 3:32 PM
 */
public class AntiVoidCommand extends Command {
    public AntiVoidCommand() {
        super("antivoid", "Shows if you have logged from antivoid on a server", new String[]{});
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1) {
            sendChatMessage("Please specify a server IP");
            return;
        }
        if (AntiVoid.INSTANCE.ipList.isEmpty() || !AntiVoid.INSTANCE.ipList.contains(args[1])) {
            sendChatMessage("You did not trigger AntiVoid on this server!");
        } else {
            sendChatMessage("You did fall below the Y level in AntiVoid! Be careful!");
        }
    }
}