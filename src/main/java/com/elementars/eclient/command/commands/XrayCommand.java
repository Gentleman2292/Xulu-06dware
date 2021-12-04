package com.elementars.eclient.command.commands;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.render.Xray;
import net.minecraft.block.Block;

public class XrayCommand extends Command {
    public XrayCommand() {
        super("xray", "Manages Xray", new String[]{"add", "remove", "list"});
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
            sendChatMessage("Specify an option. Try doing .xray help to see command options");
            return;
        }
        if (args[1].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sendChatMessage("Please specify a block.");
                return;
            }
            if (Xray.addBlock(args[2])) {
                sendChatMessage("Added " + args[2] + " to XRAY!");
                mc.renderGlobal.loadRenderers();
            } else {
                sendChatMessage("Unknown block!");
            }
        }else if (args[1].equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                sendChatMessage("Please specify a block.");
                return;
            }
            if (Xray.delBlock(args[2])) {
                sendChatMessage("Removed " + args[2] + " from XRAY!");
                mc.renderGlobal.loadRenderers();
            } else {
                sendChatMessage("Unknown block!");
            }
        }else if (args[1].equalsIgnoreCase("list")) {
            sendChatMessage("Xray blocks &7(" + Xray.getBLOCKS().size() + ")&r: ");
            String out = "";
            boolean start = true;
            for (Block b : Xray.getBLOCKS()) {
                if (start)
                    out = b.getLocalizedName();
                else
                    out += ", " + b.getLocalizedName();
                start = false;
            }
            sendChatMessage(out);
        } else {
            sendChatMessage("Unknown arguments!");
        }
    }
}
