package com.elementars.eclient.command.commands;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;

public class SaveCommand extends Command {
    public SaveCommand() {
        super("save", "Saves the config", new String[]{});
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("help")) {
                showSyntax(args[0]);
                return;
            }
        }
        Xulu.save();
        sendChatMessage("Config saved!");
    }
}
