package com.elementars.eclient.command.commands;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.friend.Friend;
import com.elementars.eclient.friend.Friends;

public class FriendCommand extends Command {
    public FriendCommand() {
        super("friend", "adds or deletes friends", new String[]{"add", "del"});
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            sendChatMessage("Try .friend add or .friend del");
            return;
        }
        if (args[1].equalsIgnoreCase("help")) {
            showSyntax(args[0]);
        }
        if (args.length < 3) {
            sendChatMessage("Specify a username");
            return;
        }
        Friend friend = new Friend(args[2]);
        if (args[1].equalsIgnoreCase("add")) {
            if (!Friends.getFriends().contains(friend)) {
                Friends.addFriend(friend.getUsername());
                sendChatMessage(friend.getUsername() + " has been friended");
            }else{
                sendChatMessage(friend.getUsername() + " is already friended!");
            }
        }
        else if (args[1].equalsIgnoreCase("del")) {
            if (Friends.getFriendByName(friend.getUsername()) != null) {
                Friends.delFriend(friend.getUsername());
                sendChatMessage(friend.getUsername() + " has been unfriended");
            }else{
                sendChatMessage(friend.getUsername() + " isn't a friend");
            }
        }
        else {
            sendChatMessage("Unknown attribute '" + args[1] + "'");
        }
    }
}
