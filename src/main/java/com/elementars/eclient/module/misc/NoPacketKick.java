package com.elementars.eclient.module.misc;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

/**
 * @author 086
 * @see com.elementars.eclient.mixin.mixins.MixinNetworkManager
 */
public class NoPacketKick extends Module {
    private static NoPacketKick INSTANCE;

    public NoPacketKick() {
        super("NoPacketKick", "Prevents packet kicks", Keyboard.KEY_NONE, Category.MISC, true);
        INSTANCE = this;
    }

    public static boolean isEnabled() {
        return INSTANCE.isToggled();
    }

}
