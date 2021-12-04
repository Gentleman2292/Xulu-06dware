package com.elementars.eclient.command.commands;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.Wrapper;
import dev.xulu.settings.Value;

public class SetCommand extends Command {
    public SetCommand() {
        super("set", "Sets the settings of a module", new String[]{Xulu.MODULE_MANAGER.getModules().toString()});
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
        if (args.length < 4) {
            sendChatMessage("Please enter a value you would like to set");
            return;
        }
        Module m = Xulu.MODULE_MANAGER.getModuleByName(args[1]);
        if (m == null) {
            sendChatMessage("Module not found!");
            return;
        }
        Value s = null;
        for (Value v : Xulu.VALUE_MANAGER.getValuesByMod(m)) {
            if (v.getName().equalsIgnoreCase(args[2])) {
                s = v;
            }
        }
        if (s == null) {
            sendChatMessage("Setting not found!");
            return;
        }
        if (s.getParentMod() != m) {
            sendChatMessage(m.getDisplayName() + " has no setting " + s.getName());
            return;
        }
        try {
            if (s.isToggle()) {
                s.setValue(Boolean.parseBoolean(args[3]));
                sendChatMessage("Set " + s.getName() + " to " + args[3].toUpperCase());
            }
            else if (s.isMode()) {
                if (s.getOptions().contains(args[3])) {
                    s.setValue(args[3]);
                    sendChatMessage("Set " + s.getName() + " to " + args[3].toUpperCase());
                }else {
                    sendChatMessage("Option " + args[3] + " not found!");
                }
            }
            else if (s.isNumber()) {
                if (Wrapper.getFileManager().determineNumber(s.getValue()).equalsIgnoreCase("INTEGER")) {
                    s.setValue(Integer.parseInt(args[3]));
                }
                else if (Wrapper.getFileManager().determineNumber(s.getValue()).equalsIgnoreCase("FLOAT")) {
                    s.setValue(Float.parseFloat(args[3]));
                }
                else if (Wrapper.getFileManager().determineNumber(s.getValue()).equalsIgnoreCase("DOUBLE")) {
                    s.setValue(Double.parseDouble(args[3]));
                }
                else {
                    sendChatMessage("UNKNOWN NUMBER VALUE");
                }
                sendChatMessage("Set " + s.getName() + " to " + args[3]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendChatMessage("Error occured when setting value.");
        }
    }
}