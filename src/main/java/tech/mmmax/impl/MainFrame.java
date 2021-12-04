package tech.mmmax.impl;

import com.elementars.eclient.module.render.NewGui;
import me.rina.turok.hardware.mouse.TurokMouse;
import tech.mmmax.api.color.Colors;
import tech.mmmax.api.component.Metrics;
import tech.mmmax.api.component.custom.AbstractFrameComponent;
import tech.mmmax.impl.components.category.CategoryPanel;
import tech.mmmax.impl.components.module.ModulePanel;

import java.awt.*;

public class MainFrame extends AbstractFrameComponent {

    public CategoryPanel categoryPanel;
    ModulePanel modulePanel;

    public MainFrame(int x, int y, int width, int height) {
        super(x, y, width, height);
        anchorPoint = header;
        categoryPanel = new CategoryPanel(insideRect.getX() + Metrics.BASIC_SPACING, insideRect.getY() + (Metrics.BASIC_SPACING * 3), insideRect.getWidth() - (Metrics.BASIC_SPACING * 2), this);
        modulePanel = new ModulePanel(insideRect.getX() + Metrics.BASIC_SPACING, categoryPanel.rect.getY() + categoryPanel.getHeight(), categoryPanel.rect.getWidth(), insideRect.getHeight() - categoryPanel.getHeight() - (Metrics.BASIC_SPACING * 4), this);
        //ComponentManager.INSTANCE.addComponents(categoryPanel, modulePanel);

    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {
        super.draw(m, partialTicks);
        anchorPoint = header;
        Colors.ENABLED_COLOR = new Color(NewGui.red.getValue(), NewGui.green.getValue(), NewGui.blue.getValue());

        categoryPanel.draw(m, partialTicks);
        modulePanel.draw(m, partialTicks);

    }

    @Override
    public void click(TurokMouse m, int button) {
        super.click(m, button);
        categoryPanel.click(m, button);
        modulePanel.click(m, button);
    }

    @Override
    public void release(TurokMouse m, int state) {
        super.release(m, state);
        categoryPanel.release(m, state);
        modulePanel.release(m, state);
    }

    @Override
    public void key(int keyTyped, char character) {
        super.key(keyTyped, character);
        categoryPanel.key(keyTyped, character);
        modulePanel.key(keyTyped, character);
    }
}
