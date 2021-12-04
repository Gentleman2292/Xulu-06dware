package com.elementars.eclient.util;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;

public class SettingLookup {
    public static Value getSettingFromMod(Module module, String name) {
        for (Value setting : Xulu.VALUE_MANAGER.getValues()) {
            if (Xulu.VALUE_MANAGER.getSettingsByMod(module).contains(setting) && setting.getName().equals(name)) {
                return setting;
            }
        }
        System.err.println("["+ Xulu.name + "] Error Setting NOT found: '" + name +"'!");
        return null;
    }
}
