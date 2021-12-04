package com.elementars.eclient.guirewrite;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import org.lwjgl.input.Keyboard;

public class HudEditor extends Module {


    public HudEditor() {
        super("HudEditor", "Editor for HUD elements", Keyboard.KEY_NONE, Category.CORE, false);
    }

    @Override
    public void setup() {
        this.setKey(Keyboard.KEY_NONE);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Xulu.hud);
        this.toggle();
    }
}