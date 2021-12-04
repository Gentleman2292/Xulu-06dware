package com.elementars.eclient.util;

/**
 * @author Elementars
 * @since 7/6/2020 - 11:37 AM
 */
public class BoolSwitch {
    boolean value;

    public BoolSwitch(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void toggle() {
        this.value = !this.value;
    }
}
