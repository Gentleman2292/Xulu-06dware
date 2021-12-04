package com.elementars.eclient.command.commands;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;

public class AboutCommand extends Command {
    public AboutCommand() {
        super("about", "Shows general information", new String[]{});
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("help")) {
                showSyntax(args[0]);
                return;
            }
        }
        sendChatMessage("Xulu " + Xulu.version + " by " + Xulu.creator + " and John200410");
        sendChatMessage("Do .help to see a list of commands");
    }
}
