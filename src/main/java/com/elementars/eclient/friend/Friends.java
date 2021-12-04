package com.elementars.eclient.friend;

import io.netty.util.internal.ConcurrentSet;

import java.util.ArrayList;

public class Friends {
    private static ConcurrentSet<Friend> friends = new ConcurrentSet<>();
    public static void addFriend(String name){
        friends.add(new Friend(name));
    }
    public static void delFriend(String name) {
        friends.remove(getFriendByName(name));
    }
    public static Friend getFriendByName(String name) {
        for (Friend f : friends) {
            if (f.username.equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }
    public static ConcurrentSet<Friend> getFriends() {
        return friends;
    }
    public static boolean isFriend(String name) {
        return friends.stream().anyMatch(friend -> friend.username.equalsIgnoreCase(name));
    }
}
