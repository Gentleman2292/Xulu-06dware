package com.elementars.eclient.module.combat;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.Wrapper;
import net.minecraft.item.ItemExpBottle;
import org.lwjgl.input.Keyboard;


public class EXPFast extends Module {
    public EXPFast() {
        super("EXPFast", "XP fast zoom", Keyboard.KEY_NONE, Category.COMBAT, true);
    }
    public void onUpdate() {
        if(!this.isToggled()) {
            return;
        }
        if(Wrapper.getMinecraft().player.inventory.getCurrentItem().getItem() instanceof ItemExpBottle) {
            Wrapper.getMinecraft().rightClickDelayTimer = 0;
        }
    }
}
