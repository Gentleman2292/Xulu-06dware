package com.elementars.eclient.module.misc;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

/**
 * Created by 086 on 12/12/2017.
 * @see com.elementars.eclient.mixin.mixins.MixinEntityPlayerSP
 */
public class PortalChat extends Module {
    public PortalChat() {
        super("PortalChat", "Allows you to chat in portals", Keyboard.KEY_NONE, Category.MISC, true);
    }
}
