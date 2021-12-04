package com.elementars.eclient.module.misc;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import org.lwjgl.input.Keyboard;

public class Timer extends Module {

    private final Value<Float> tickSpeed = register(new Value<>("Speed", this, 4f, 0f, 10f));

    public Timer() {
        super("Timer", "Modifies the game speed", Keyboard.KEY_NONE, Category.MISC, true);
    }

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50f;
    }

    @Override
    public void onUpdate() {
        mc.timer.tickLength = 50f / (this.tickSpeed.getValue() == 0f ? 0.1f : this.tickSpeed.getValue());
    }
}
