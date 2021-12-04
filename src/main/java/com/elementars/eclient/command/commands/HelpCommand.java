package com.elementars.eclient.command.commands;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.command.CommandManager;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "Shows a list of commands", new String[]{});
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("help")) {
                showSyntax(args[0]);
                return;
            }
        }
        sendChatMessage("Here's a list of commands:");
        for (Command command : CommandManager.getCommands()) {
            sendChatMessage(command.getName() + " : " + command.getDescription());
        }
        sendChatMessage("Follow any command with help to see command options");
    }
}
