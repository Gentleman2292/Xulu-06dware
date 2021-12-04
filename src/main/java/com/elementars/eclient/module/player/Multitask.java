package com.elementars.eclient.module.player;

import com.elementars.eclient.event.Event;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.AllowInteractEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

public class Multitask extends Module {
    public Multitask() {
        super("Multitask", "Credit to luchadora client", Keyboard.KEY_NONE, Category.PLAYER, true);
    }

    @EventTarget
    public void onUseItem(AllowInteractEvent event) {
        event.usingItem = false;
    }
}
