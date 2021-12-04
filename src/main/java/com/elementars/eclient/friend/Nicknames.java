package com.elementars.eclient.friend;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Elementars
 * @since 7/30/2020 - 11:33 AM
 */
public class Nicknames {
    private static Map<String, String> aliases = new HashMap<>();
    public static void addNickname(String name, String nick) {
        aliases.put(name, nick);
    }
    public static void removeNickname(String name) {
        aliases.remove(name);
    }
    public static String getNickname(String name) {
        return aliases.get(name);
    }
    public static boolean hasNickname(String name) {
        return aliases.containsKey(name);
    }
    public static Map<String, String> getAliases() {
        return aliases;
    }
}
