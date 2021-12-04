package tech.mmmax.impl.components.setting.impl;

import dev.xulu.settings.Value;
import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokRect;
import tech.mmmax.api.color.Colors;
import tech.mmmax.api.component.Metrics;
import tech.mmmax.impl.components.module.ModuleButton;
import tech.mmmax.impl.components.setting.AbstractSettingComponent;

public class BooleanComponent extends AbstractSettingComponent {

    Value<Boolean> setting;

    public BooleanComponent(int x, int y, int width, Value<Boolean> setting) {
        super(x, y, width, Metrics.SETTING_HEIGHT);
        this.setting = setting;
    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {
        super.draw(m, partialTicks);
        int centerY = (rect.getHeight() - TurokFontManager.getStringHeight(font, setting.getName())) / 2;
        TurokFontManager.render(font, setting.getName(), rect.getX() + Metrics.BASIC_SPACING, rect.getY() + centerY, true, Colors.FONT_COLOR);
        int checkHeight = 6;
        int centerYCheck = (rect.getHeight() - checkHeight) / 2;

        TurokRect check = new TurokRect(rect.getX() + rect.getWidth() - 4 - checkHeight, rect.getY() + centerYCheck, checkHeight, checkHeight);
        ModuleButton.renderCheckmark(check, setting.getValue());
    }

    @Override
    public void click(TurokMouse m, int button) {
        super.click(m, button);
        if (rect.collideWithMouse(m)) {
            this.setting.setValue(!setting.getValue());
        }
    }
}
