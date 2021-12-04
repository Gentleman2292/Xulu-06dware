package com.elementars.eclient.module.core;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.ColorTextUtils;
import dev.xulu.settings.Value;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;

public class Global extends Module {

    public static Value<Integer> rainbowspeed;
    public static Value<Integer> rainbowspeed2;
    public static Value<Boolean> textShadow;
    public static Value<Boolean> shortShadow;
    public static Value<Boolean> showLag;
    public static Value<String> lagColor;
    public static Value<String> command1;
    public static Value<String> command2;
    public static Value<String> command3;
    public static Value<Boolean> direction;
    public static Value<Boolean> coordinates;
    public static Value<Integer> rainbowAmount;
    public static Value<Integer> rainbowSaturation;
    public static Value<Integer> rainbowLightness;
    public static Value<Integer> hudAlpha;

    public Global() {
        super("Global", "Stores global settings", Keyboard.KEY_NONE, Category.CORE, false);
        rainbowAmount = register(new Value<>("Gradient Amt", this, 5, 1, 20));
        hudAlpha = register(new Value<>("Hud Alpha", this, 255, 0, 255));
        direction = register(new Value<>("Direction", this, true));
        coordinates = register(new Value<>("Coordinates", this, false));
        showLag = register(new Value<>("Show Lag", this, true));
        lagColor = register(new Value<>("Lag Color", this, "Default", new String[]{"Default", "Gui Color"}));
        register(new Value<>("Rainbow Watermark", this, false));
        register(new Value<>("Hide Potions", this, true));
        rainbowspeed2 = register(new Value<>("Rainbow Speed", this, 5, 1, 100));
        rainbowspeed = register(new Value<>("Block Rainbow Speed", this, 2, 1, 50));
        rainbowSaturation = register(new Value<>("Rainbow Sat.", this, 255, 0, 255));
        rainbowLightness = register(new Value<>("Rainbow Light.", this, 255, 0, 255));
        textShadow = register(new Value<>("Text Shadow", this, true));
        shortShadow = register(new Value<>("Short Shadow", this, false));
        command1 = register(new Value<>("Watermark Color", this, "Purple", ColorTextUtils.colors));
        command2 = register(new Value<>("Bracket Color", this, "Purple", ColorTextUtils.colors));
        command3 = register(new Value<>("Bracket Type", this, "[]", new String[]{"[]", "<>", "()", "{}", "-==-"}));
    }

    @Override
    public void onEnable() {
        disable();
    }
}
