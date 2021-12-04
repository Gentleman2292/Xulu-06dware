package com.elementars.eclient.module.render;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 */
public class Cape extends Module {
    public Cape() {
        super("Capes", "Shows Xulu capes", Keyboard.KEY_NONE, Category.CORE, true);
        INSTANCE = this;
    }

    public static Cape INSTANCE;

    public static boolean isEnabled() { return INSTANCE.isToggled(); }
}
