package tech.mmmax.impl.components.setting.impl;

import dev.xulu.settings.Value;
import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.management.TurokFontManager;
import tech.mmmax.api.color.Colors;
import tech.mmmax.api.component.Metrics;
import tech.mmmax.impl.components.setting.AbstractSettingComponent;

public class ModeComponent extends AbstractSettingComponent {

    public Value<String> setting;

    public ModeComponent(int x, int y, int width, Value<String> setting) {
        super(x, y, width, Metrics.SETTING_HEIGHT);
        this.setting = setting;
    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {
        super.draw(m, partialTicks);
        String str = setting.getName();
        int centerX = (rect.getHeight() - TurokFontManager.getStringHeight(font, str)) / 2;
        TurokFontManager.render(font, str, rect.getX() + 2, rect.getY() + centerX, true, Colors.FONT_COLOR);

        int rightX = rect.getX() + rect.getWidth() - TurokFontManager.getStringWidth(font, setting.getValue()) - Metrics.BASIC_SPACING * 2;
        TurokFontManager.render(font, setting.getValue(), rightX, rect.getY() + centerX, true, Colors.FONT_COLOR);
    }

    @Override
    public void click(TurokMouse m, int button) {
        super.click(m, button);
        String s = (setting.getValue() instanceof String ? (String) setting.getValue() : setting.getValue().toString());

        if (rect.collideWithMouse(m)){
            try {
                if (!setting.getCorrectString(s).equalsIgnoreCase(setting.getOptions().get(setting.getOptions().size() - 1).toString())) {
                    setting.setValue(setting.getOptions().get(setting.getOptions().indexOf(setting.getCorrectString(s)) + 1));
                } else {
                    setting.setValue(setting.getOptions().get(0));
                }
            } catch (Exception e) {
              //  System.err.println("Error with invalid combo");
                e.printStackTrace();
                setting.setValue(setting.getOptions().get(0));
            }
        }
    }
}
