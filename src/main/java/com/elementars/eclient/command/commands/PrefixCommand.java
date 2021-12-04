package com.elementars.eclient.command.commands;

import com.elementars.eclient.command.Command;

public class PrefixCommand extends Command {
    public PrefixCommand() {
        super("prefix", "Changes the prefix for commands", new String[]{});
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            sendChatMessage("Specify what prefix you would like to change to.");
            sendChatMessage("Current prefix is: " + getPrefix());
            return;
        }
        if (args[1].equalsIgnoreCase("help")) {
            showSyntax(args[0]);
            return;
        }
        setPrefix(args[1]);
        sendChatMessage("Set the prefix to: " + getPrefix());
    }
}
