package com.elementars.eclient.module.misc;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

public class LiquidInteract extends Module {

    public static LiquidInteract INSTANCE;

    public LiquidInteract() {
        super("LiquidInteract", "Allows you to place blocks on liquids", Keyboard.KEY_NONE, Category.MISC, true);
        INSTANCE = this;
    }
}
