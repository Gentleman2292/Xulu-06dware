package com.elementars.eclient.module.render;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @since 6/23/2020 - 6:27 PM
 */
public class OffhandSwing extends Module {
    public OffhandSwing() {
        super("OffhandSwing", "Swings your offhand instead", Keyboard.KEY_NONE, Category.RENDER, true);
    }
}
