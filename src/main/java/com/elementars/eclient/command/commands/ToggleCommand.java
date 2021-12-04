package com.elementars.eclient.command.commands;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.Module;

public class ToggleCommand extends Command {
    public ToggleCommand() {
        super("t", "Toggles modules", new String[]{});
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("help")) {
                showSyntax(args[0]);
                return;
            }
        }
        if (args.length <= 1) {
            sendChatMessage("Not enough arguments!");
            return;
        }
        if (Xulu.MODULE_MANAGER.getModuleByName(args[1]) != null) {
            Module module = Xulu.MODULE_MANAGER.getModuleByName(args[1]);
            module.toggle();
            sendChatMessage(module.getName() + " toggled " + (module.isToggled() ? Command.SECTIONSIGN() + "aON": Command.SECTIONSIGN() + "cOFF"));
        }else{
            sendChatMessage("Module not found.");
        }
    }
}
