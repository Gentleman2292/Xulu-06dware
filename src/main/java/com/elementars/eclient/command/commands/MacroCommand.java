package com.elementars.eclient.command.commands;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.macro.Macro;
import org.lwjgl.input.Keyboard;

public class MacroCommand extends Command {
    public MacroCommand() {
        super("macro", "Manages macros", new String[]{"add"});
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("help")) {
                showSyntax(args[0]);
                return;
            }
        }
        if (args.length < 2) {
            sendChatMessage("Specify an option. Try doing .macro help to see command options");
            return;
        }
        if (args[1].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sendChatMessage("Please specify a key.");
                return;
            }
            if (args.length < 4) {
                sendChatMessage("Needs more arguments!");
                return;
            }
            try {
                String isOn = args[2];
                String name = "";
                for (String arg : args) {
                    if (!arg.equalsIgnoreCase("macro") && !arg.equalsIgnoreCase("add") && !arg.equalsIgnoreCase(isOn)) {
                        name = name + " " + arg;
                    }
                }
                name = name.substring(1);
                int key = Keyboard.getKeyIndex(isOn.toUpperCase());
                sendChatMessage("Message = " + name + ":Key = " + isOn + ":actual key = " + key);
                if (Keyboard.getKeyName(key) != null) {
                    if (!Xulu.MACRO_MANAGER.getMacros().contains(new Macro(name, key))) {
                        Xulu.MACRO_MANAGER.addMacro(name, key);
                    }
                    sendChatMessage("Added Macro with the key " + Keyboard.getKeyName(key));
                }
            } catch (StringIndexOutOfBoundsException e) {
                sendChatMessage("Unknown arguments!");
            }
        }else if (args[1].equalsIgnoreCase("del")) {
            if (args.length < 3) {
                sendChatMessage("Please specify a key.");
                return;
            }
            try {
                int key = Keyboard.getKeyIndex(args[2].toUpperCase());
                Xulu.MACRO_MANAGER.delMacro(key);
                sendChatMessage("Deleted Macro with the key " + args[2].toUpperCase());
            } catch (Exception e) {
                sendChatMessage("Error occured while removing macro!");
            }
        } else {
            sendChatMessage("Unknown arguments!");
        }
    }
}
