package com.elementars.eclient.module.render;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

/**
 * Created by 086 on 24/12/2017.
 * @see com.elementars.eclient.mixin.mixins.MixinGuiScreen
 */
public class ShulkerPreview extends Module {
    public ShulkerPreview() {
        super("ShulkerPreview", "Peeks into shulker boxes in the inventory", Keyboard.KEY_NONE, Category.RENDER, true);
    }
}
