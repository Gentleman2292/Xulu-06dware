package com.elementars.eclient.command.commands;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.command.SetBox;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;

public class SetStringCommand extends Command {
    public SetStringCommand() {
        super("setstring", "Sets a string easier", new String[]{});
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            sendChatMessage("Please specify a module");
            return;
        }
        if (args[1].equalsIgnoreCase("help")) {
            showSyntax(args[0]);
            return;
        }
        if (args.length < 3) {
            sendChatMessage("Please specify which setting you would like to change");
            return;
        }

        Module m = Xulu.MODULE_MANAGER.getModuleByName(args[1]);
        if (m == null) {
            sendChatMessage("Module not found!");
            return;
        }
        Value s = Xulu.VALUE_MANAGER.getValueByMod(m, args[2]);
        if (s == null) {
            sendChatMessage("Setting not found!");
            return;
        }
        if (s.getParentMod() != m) {
            sendChatMessage(m.getDisplayName() + " has no setting " + s.getName());
            return;
        }
        if (s.isMode()) {
            SetBox.initTextBox(s);
        } else {
            sendChatMessage(s.getName() + " is not a text setting!");
        }
    }
}