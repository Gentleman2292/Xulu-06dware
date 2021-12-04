package com.elementars.eclient.util;

import com.elementars.eclient.event.ArrayHelper;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Elementars
 * @since 6/19/2020 - 4:56 PM
 */
public class ListHelper {

    public static String longest(@Nonnull List<String> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) return null;
        String s = list.get(0);
        for (String st : list) {
            if (st.length() > s.length())
                s = st;
        }
        return s;
    }

    public static String longest(@Nonnull String[] list) {
        Objects.requireNonNull(list);
        List<String> list1 = Arrays.asList(list);
        if (list1.isEmpty()) return null;
        String s = list1.get(0);
        for (String st : list) {
            if (st.length() > s.length())
                s = st;
        }
        return s;
    }
}
