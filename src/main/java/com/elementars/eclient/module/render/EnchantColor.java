package com.elementars.eclient.module.render;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class EnchantColor extends Module {

    private final Value<String> mode = register(new Value<>("Mode", this, "Color", new String[]{"Color", "Rainbow"}));
    private final Value<Integer> red = register(new Value<>("Red", this, 255, 0, 255));
    private final Value<Integer> green = register(new Value<>("Green", this, 255, 0, 255));
    private final Value<Integer> blue = register(new Value<>("Blue", this, 255, 0, 255));

    public EnchantColor() {
        super("EnchantColor", "Changes the color of the enchantment effect", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    public static Color getColor(long offset, float fade){
        if (Xulu.MODULE_MANAGER.getModuleT(EnchantColor.class).mode.getValue().equalsIgnoreCase("Color")) {
            return new Color(Xulu.MODULE_MANAGER.getModuleT(EnchantColor.class).red.getValue(), Xulu.MODULE_MANAGER.getModuleT(EnchantColor.class).green.getValue(), Xulu.MODULE_MANAGER.getModuleT(EnchantColor.class).blue.getValue());
        }
        float hue = (float) (System.nanoTime() + offset) / 1.0E10F % 1.0F;
        long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, 1.0F, 1.0F)).intValue()), 16);
        Color c = new Color((int) color);
        return new Color(c.getRed()/255.0F*fade, c.getGreen()/255.0F*fade, c.getBlue()/255.0F*fade, c.getAlpha()/255.0F);
    }
}
