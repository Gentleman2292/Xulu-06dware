package com.elementars.eclient.util;

import dev.xulu.settings.Bind;
import dev.xulu.settings.Value;

import java.util.ArrayList;

/**
 * @author Elementars
 * @since 5/31/2020 - 8:46 PM
 */
public class ValueList extends ArrayList<Value<?>> {
    @Override
    public boolean isEmpty() {
        if (super.isEmpty()) return true;
        boolean test = true;
        for (Value<?> value : this) {
            if (!(value.getValue() instanceof Bind)) {
                test = false;
                break;
            }
        }
        return test;
    }
}