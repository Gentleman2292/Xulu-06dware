package com.elementars.eclient.command.commands;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.friend.Friend;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.friend.Nicknames;

public class NicknameCommand extends Command {
    public NicknameCommand() {
        super("nickname", "adds or deletes friends", new String[]{"set", "remove"});
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            sendChatMessage("Usage: .nickname set/remove (name) (nickname)");
            return;
        }
        if (args[1].equalsIgnoreCase("help")) {
            showSyntax(args[0]);
        }
        if (args.length < 3) {
            sendChatMessage("Specify a username");
            return;
        }
        if (args.length < 4 && !args[1].equalsIgnoreCase("remove")) {
            sendChatMessage("Specify a nickname");
            return;
        }
        if (args[1].equalsIgnoreCase("set")) {
            Nicknames.addNickname(args[2], args[3]);
            sendChatMessage("Set nickname for &b" + args[2]);
        }
        else if (args[1].equalsIgnoreCase("remove")) {
            if (Nicknames.hasNickname(args[2])) {
                Nicknames.removeNickname(args[2]);
                sendChatMessage("Nickname has been removed for &b" + args[2]);
            } else {
                sendChatMessage("&b" + args[2] + "&f doesn't have a nickname");
            }
        }
        else {
            sendChatMessage("Unknown attribute '" + args[1] + "'");
        }
    }
}
