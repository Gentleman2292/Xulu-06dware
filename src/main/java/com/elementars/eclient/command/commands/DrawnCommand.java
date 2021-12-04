package com.elementars.eclient.command.commands;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.Module;

public class DrawnCommand extends Command {
    public DrawnCommand() {
        super("drawn", "toggles if a module is drawn on array list", new String[]{});
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            sendChatMessage("Please specify which module you want drawn/undrawn");
            return;
        }
        if (args[1].equalsIgnoreCase("help")) {
            showSyntax(args[0]);
            return;
        }
        Module m = Xulu.MODULE_MANAGER.getModuleByName(args[1]);
        if (m == null) {
            sendChatMessage("Module not found.");
            return;
        }
        m.setDrawn(!m.isDrawn());
        sendChatMessage(m.getDisplayName() + (m.isDrawn() ? " drawn" : " undrawn"));
    }
}
