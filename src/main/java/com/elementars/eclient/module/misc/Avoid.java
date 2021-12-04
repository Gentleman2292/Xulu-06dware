package com.elementars.eclient.module.misc;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @since 7/20/2020 - 12:32 AM
 */
public class Avoid extends Module {

    public static Value<Boolean> cactus;
    public static Value<Boolean> fire;
    public static Value<Boolean> lava;
    public static Value<Boolean> webs;

    public Avoid() {
        super("Avoid", "Avoids interactions with certain things", Keyboard.KEY_NONE, Category.MISC, true);
        cactus = register(new Value<>("Cactus", this,true));
        fire = register(new Value<>("Fire", this,true));
        lava = register(new Value<>("Lava", this,true));
        webs = register(new Value<>("Webs", this,true));
    }
}
