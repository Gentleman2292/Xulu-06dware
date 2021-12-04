package com.elementars.eclient.module.core;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.font.CFontManager;
import com.elementars.eclient.font.XFontRenderer;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.TextBox;
import dev.xulu.settings.Value;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Elementars
 * @since 8/13/2020 - 11:22 AM
 */
public class CustomFont extends Module {



    public static Value<String> customFontMode;
    public static Value<TextBox> FONT;
    public static Value<Integer> FONT_STYLE;
    public static Value<Integer> FONT_SIZE;
    public static Value<Boolean> antiAlias;
    public static Value<Boolean> metrics;
    public static Value<Boolean> shadow;
    public static Value<Integer> fontOffset;

    public CustomFont() {
        super("CustomFont", "Custom in game text rendering", Keyboard.KEY_NONE, Category.CORE, false);
        customFontMode = register(new Value<>("Mode", this, "Normal", new ArrayList<>(
                Arrays.asList("Normal", "Xdolf", "Rainbow")
        )));
        FONT = register(new Value<>("Font", this, new TextBox("Verdana")))
                .newValueFilter(textBox -> Xulu.getCorrectFont(textBox.getText()) != null)
                .withFilterError("Not a font! (Case-sensitive)")
                .onChanged(textBoxOnChangedValue -> updateFont(textBoxOnChangedValue.getNew().getText(), FONT_STYLE.getValue(), FONT_SIZE.getValue(), antiAlias.getValue(), metrics.getValue()))
                .visibleWhen(textBox -> !customFontMode.getValue().equalsIgnoreCase("Rainbow"));
        FONT_STYLE = register(new Value<>("Font Style", this, 0, 0, 2))
                .onChanged(integerOnChangedValue -> updateFont(FONT.getValue().getText(), integerOnChangedValue.getNew(), FONT_SIZE.getValue(), antiAlias.getValue(), metrics.getValue()))
                .visibleWhen(integer -> !customFontMode.getValue().equalsIgnoreCase("Rainbow"));
        FONT_SIZE = register(new Value<>("Font Size", this, 18, 5, 50))
                .onChanged(integerOnChangedValue -> updateFont(FONT.getValue().getText(), FONT_STYLE.getValue(), integerOnChangedValue.getNew(), antiAlias.getValue(), metrics.getValue()))
                .visibleWhen(integer -> !customFontMode.getValue().equalsIgnoreCase("Rainbow"));
        antiAlias = register(new Value<>("Anti Alias", this, true))
                .visibleWhen(integer -> customFontMode.getValue().equalsIgnoreCase("Normal") || customFontMode.getValue().equalsIgnoreCase("Xdolf"))
                .onChanged(booleanOnChangedValue -> updateFont(FONT.getValue().getText(), FONT_STYLE.getValue(), FONT_SIZE.getValue(), booleanOnChangedValue.getNew(), metrics.getValue()));
        metrics = register(new Value<>("Metrics", this, true))
                .visibleWhen(integer -> customFontMode.getValue().equalsIgnoreCase("Normal"))
                .onChanged(booleanOnChangedValue -> updateFont(FONT.getValue().getText(), FONT_STYLE.getValue(), FONT_SIZE.getValue(), antiAlias.getValue(), booleanOnChangedValue.getNew()));
        shadow = register(new Value<>("Shadow", this, true))
                .visibleWhen(aBoolean -> customFontMode.getValue().equalsIgnoreCase("Normal"));
        fontOffset = register(new Value<>("Font Offset", this, 0, -5, 5))
                .visibleWhen(integer -> !customFontMode.getValue().equalsIgnoreCase("Rainbow"));
    }

    public static void updateFont(String newName, int style, int size, boolean antialias, boolean metrics) {
        switch (customFontMode.getValue()) {
            case "Normal":
                try{
                    if (newName.equalsIgnoreCase("Comfortaa Regular")) {
                        CFontManager.customFont = new com.elementars.eclient.font.custom.CustomFont(new Font("Comfortaa Regular", style, size), antialias, metrics);
                        return;
                    }
                    CFontManager.customFont = new com.elementars.eclient.font.custom.CustomFont(new Font(Xulu.getCorrectFont(newName), style, size), antialias, metrics);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Xdolf":
                try{
                    if (newName.equalsIgnoreCase("Comfortaa Regular")) {
                        CFontManager.xFontRenderer = new XFontRenderer(new Font("Comfortaa Regular", style, size * 2), antialias, 8);
                        return;
                    }
                    CFontManager.xFontRenderer = new XFontRenderer(new Font(Xulu.getCorrectFont(newName), style, size * 2), antialias, 8);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
