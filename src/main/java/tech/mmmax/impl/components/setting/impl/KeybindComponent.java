package tech.mmmax.impl.components.setting.impl;

import dev.xulu.settings.Bind;
import dev.xulu.settings.Value;
import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.util.TurokRect;
import org.lwjgl.input.Keyboard;
import tech.mmmax.api.color.Colors;
import tech.mmmax.api.component.Metrics;
import tech.mmmax.impl.components.setting.AbstractSettingComponent;

public class KeybindComponent extends AbstractSettingComponent {

    public Value<Bind> setting;

    boolean binding;

    public KeybindComponent(int x, int y, int width, Value<Bind> bind){
        super(x, y, width, Metrics.SETTING_HEIGHT);
        this.setting = bind;
        binding = false;
    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {
        super.draw(m, partialTicks);
        String string = binding ? "Binding..." : setting.getName() + ": " + Keyboard.getKeyName(setting.getValue().getNum());
        int centerY = (rect.getHeight() - TurokFontManager.getStringHeight(font, string)) / 2;
        TurokFontManager.render(font, string, rect.getX() + Metrics.BASIC_SPACING, rect.getY() + centerY, true, Colors.FONT_COLOR);
    }

    @Override
    public void click(TurokMouse m, int button) {
        super.click(m, button);
        if (rect.collideWithMouse(m)){
            binding = !binding;
        }
    }

    @Override
    public void key(int keyTyped, char character) {
        super.key(keyTyped, character);
        if (binding){
            if (keyTyped == Keyboard.KEY_DELETE){
                setting.getValue().setNum(0);
            } else {
                setting.getValue().setNum(keyTyped);
            }
            binding = false;
        }
    }
}
