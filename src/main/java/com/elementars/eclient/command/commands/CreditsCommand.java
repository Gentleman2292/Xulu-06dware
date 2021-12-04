package com.elementars.eclient.command.commands;

import com.elementars.eclient.command.Command;

public class CreditsCommand extends Command {

    public CreditsCommand() {
        super("credits", "Shows the people who helped come up with ideas for modules and ect.", new String[]{});
    }

    String[] credits = new String[] {
            "Sago",
            "WeWide",
            "Nemac",
            "Jumpy/Xdolf",
            "Naughty",
            "John",
            "Mtnl"
    };

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("help")) {
                showSyntax(args[0]);
                return;
            }
        }
        sendChatMessage("Here's a list of people who helped brainstorm ideas for the client:");
        String out = "";
        boolean start = true;
        for (String s : credits) {
            if (start)
                out = s;
            else
                out = out + ", " + s;
            start = false;
        }
        sendChatMessage(out);
    }
}
