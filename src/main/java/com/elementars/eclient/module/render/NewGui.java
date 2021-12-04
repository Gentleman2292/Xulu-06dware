package com.elementars.eclient.module.render;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.newgui.NewGUI;
import dev.xulu.newgui.Panel;
import dev.xulu.settings.Value;
import org.lwjgl.input.Keyboard;
import tech.mmmax.impl.ClickGuiCSGO;

import java.util.ArrayList;
import java.util.Arrays;

public class NewGui extends Module {

    public static Value<Boolean> customfont;
    public static Value<Boolean> rainbowgui;
    public static Value<Integer> rainbowspeed;
    public static Value<Boolean> blur;
    public static Value<Boolean> outline;
    public static Value<String> moduleSetting;
    public static Value<String> toggleSetting;
    public static Value<String> sliderSetting;
    public static Value<Integer> bgAlpha;
    public static Value<Integer> red;
    public static Value<Integer> green;
    public static Value<Integer> blue;
    public static Value<Boolean> resetGui;

    public static Value<Float> rainbowSat;
    public static Value<Float> rainbowBri;
    public static Value<Boolean> lineRainbow;
    public static Value<Integer> redL;
    public static Value<Integer> greenL;
    public static Value<Integer> blueL;
    public static Value<Integer> redR;
    public static Value<Integer> greenR;
    public static Value<Integer> blueR;

    public NewGui() {
        super("NewGui", "New gui for the client", Keyboard.KEY_Y, Category.CORE, false);
       // resetGui = register(new Value<>("Reset Gui", this, false));
      //  customfont = register(new Value<>("Custom Font", this, false));
      //  rainbowspeed = register(new Value<>("Rainbow Speed", this, 20, 1, 50));
        //blur = register(new Value<>("Blur", this, true));
        //outline = register(new Value<>("Outline", this, true));
        //moduleSetting = register(new Value<>("Module Setting", this, "Normal", new ArrayList<>(
          //      Arrays.asList("Normal", "MiniButton", "Text")
        //)));
        //toggleSetting = register(new Value<>("Toggle Setting", this, "Full-box", new ArrayList<>(
          //      Arrays.asList("Checkbox", "Full-box")
        //)));
        //sliderSetting = register(new Value<>("Slider Setting", this, "Box", new ArrayList<>(
         //       Arrays.asList("Line", "Box")
        //)));
       bgAlpha = register(new Value<>("Background alpha", this, 130, 0, 255));
        red = register(new Value<>("GuiRed", this, 255, 0, 255));
        green = register(new Value<>("GuiGreen", this, 26, 0, 255));
        blue = register(new Value<>("GuiBlue", this, 42, 0, 255));
        redL = register(new Value<>("LineRedLeft", this, 255, 0, 255));
        greenL = register(new Value<>("LineGreenLeft", this, 26, 0, 255));
        blueL = register(new Value<>("LineBlueLeft", this, 42, 0, 255));
        redR = register(new Value<>("LineRedRight", this, 255, 0, 255));
        greenR = register(new Value<>("LineGreenRight", this, 26, 0, 255));
        blueR = register(new Value<>("LineBlueRight", this, 42, 0, 255));
        lineRainbow = register(new Value<>("LineRainbow", this, true));
        rainbowSat = register(new Value<>("RainbowSat", this, 1f, 0f, 1f));
        rainbowBri = register(new Value<>("RainbowBri", this, 1f, 0f, 1f));


    }

    @Override
    public void onEnable() {
        if (ClickGuiCSGO.INSTANCE != null) {
            mc.displayGuiScreen(ClickGuiCSGO.INSTANCE);
        } else ClickGuiCSGO.INSTANCE = new ClickGuiCSGO();
        this.toggle();
    }

    public static void resetGui() {
        if (resetGui.getValue()) {

            int startY = 10;
            for (Panel panel : NewGUI.getPanels()) {
                panel.x = 10;
                panel.y = startY;
                startY += 23;
            }
            resetGui.setValue(false);
        }
    }
}
