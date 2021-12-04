package com.elementars.eclient.command.commands;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.render.Search;
import com.elementars.eclient.module.render.Xray;
import net.minecraft.block.Block;

/**
 * @author Elementars
 * @since 5/27/2020 - 1:51 PM
 */
public class SearchCommand extends Command {
    public SearchCommand() {
        super("search", "Manages Search", new String[]{"add", "remove", "list"});
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
            sendChatMessage("Specify an option. Try doing .search help to see command options");
            return;
        }
        if (args[1].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sendChatMessage("Please specify a block.");
                return;
            }
            if (Search.addBlock(args[2])) {
                sendChatMessage("Added " + args[2] + " to Search!");
                mc.renderGlobal.loadRenderers();
            } else {
                sendChatMessage("Unknown block!");
            }
        }else if (args[1].equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                sendChatMessage("Please specify a block.");
                return;
            }
            if (Search.delBlock(args[2])) {
                sendChatMessage("Removed " + args[2] + " from Search!");
                Xulu.MODULE_MANAGER.getModuleT(Search.class).posList.clear();
                mc.renderGlobal.loadRenderers();
            } else {
                sendChatMessage("Unknown block!");
            }
        }else if (args[1].equalsIgnoreCase("list")) {
            sendChatMessage("Search blocks &7(" + Search.getBLOCKS().size() + ")&r: ");
            String out = "";
            boolean start = true;
            for (Block b : Search.getBLOCKS()) {
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
