package com.elementars.eclient.command.commands;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("reload", "Reloads the config", new String[]{});
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("help")) {
                showSyntax(args[0]);
                return;
            }
        }
        Xulu.load();
        sendChatMessage("Config reloaded!");
    }
}
