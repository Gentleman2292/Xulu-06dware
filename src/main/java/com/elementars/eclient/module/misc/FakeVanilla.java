package com.elementars.eclient.module.misc;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

/**
 * Created by 086 on 9/04/2018.
 */
public class FakeVanilla extends Module {
    public FakeVanilla() {
        super("FakeVanilla", "Fakes Vanilla", Keyboard.KEY_NONE, Category.MISC, true);
    }
}
