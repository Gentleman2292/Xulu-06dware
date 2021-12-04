package com.elementars.eclient.util;

import java.util.ArrayList;
import java.util.List;

/***
 * @author Elementars
 */
public class EnumUtil {

    public static <T extends Enum<T>> ArrayList<String> enumConverter(Class<T> clazz) {
        ArrayList<String> options = new ArrayList<>();
        List<T> list = java.util.Arrays.asList(clazz.getEnumConstants());
        list.forEach(element -> options.add(toTitle(element.name())));
        return options;
    }

    public static String toTitle(String in) {
        in = Character.toUpperCase(in.toLowerCase().charAt(0)) + in.toLowerCase().substring(1);
        return in;
    }
}
