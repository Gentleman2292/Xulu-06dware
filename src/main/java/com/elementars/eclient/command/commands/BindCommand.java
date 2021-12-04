package com.elementars.eclient.command.commands;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {
    public BindCommand() {
        super("bind", "binds a module to a key", new String[]{});
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            sendChatMessage("Please specify which module you want bound");
            return;
        }
        if (args[1].equalsIgnoreCase("help")) {
            showSyntax(args[0]);
            return;
        }
        if (args.length < 3) {
            sendChatMessage("Please specify the key you would like to bind");
            return;
        }
        Module m = Xulu.MODULE_MANAGER.getModuleByName(args[1]);
        if (m == null) {
            sendChatMessage("Module not found.");
            return;
        }
        m.setKey(Keyboard.getKeyIndex(args[2].toUpperCase()));
        sendChatMessage(m.getDisplayName() + " bound to " + args[2].toUpperCase());
    }
}
