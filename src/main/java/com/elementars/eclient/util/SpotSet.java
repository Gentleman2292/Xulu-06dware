package com.elementars.eclient.util;

import com.elementars.eclient.module.render.LogoutSpots;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Elementars
 * @since 6/23/2020 - 1:57 PM
 */
public class SpotSet extends HashSet<LogoutSpots.LogoutPos> {
    public Pair<Boolean, LogoutSpots.LogoutPos> removeIfReturn(Predicate<? super LogoutSpots.LogoutPos> filter) {
        Set<LogoutSpots.LogoutPos> oldlist = new HashSet<>(this);
        boolean removed = removeIf(filter);
        if (removed) {
            LogoutSpots.LogoutPos logoutPos = null;
            for (LogoutSpots.LogoutPos pos : oldlist) {
                if (!this.contains(pos)) {
                    logoutPos = pos;
                    break;
                }
            }
            return new Pair<>(true, logoutPos);
        }
        return new Pair<>(false, null);
    }
}
