package com.elementars.eclient.module.player;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

/**
 * Created by 086 on 9/04/2018.
 */
public class TpsSync extends Module {

    private static TpsSync INSTANCE;

    public TpsSync() {
        super("TpsSync", "Syncs client with the tps", Keyboard.KEY_NONE, Category.PLAYER, true);
        INSTANCE = this;
    }

    public static boolean isSync() {
        return INSTANCE.isToggled();
    }

}
