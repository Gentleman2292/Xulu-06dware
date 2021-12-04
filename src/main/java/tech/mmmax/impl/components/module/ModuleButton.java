package tech.mmmax.impl.components.module;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Bind;
import dev.xulu.settings.Value;
import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokRect;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import tech.mmmax.api.color.Colors;
import tech.mmmax.api.component.AbstractComponent;
import tech.mmmax.api.component.Metrics;
import tech.mmmax.api.component.custom.AbstractOutlined;
import tech.mmmax.api.font.FontManager;
import tech.mmmax.impl.MainFrame;
import tech.mmmax.impl.components.ComponentManager;
import tech.mmmax.impl.components.setting.AbstractSettingComponent;
import tech.mmmax.impl.components.setting.SettingFrame;
import tech.mmmax.impl.components.setting.impl.BooleanComponent;
import tech.mmmax.impl.components.setting.impl.KeybindComponent;
import tech.mmmax.impl.components.setting.impl.ModeComponent;
import tech.mmmax.impl.components.setting.impl.SliderComponent;

import java.awt.*;
import java.util.AbstractSet;
import java.util.ArrayList;

public class ModuleButton extends AbstractOutlined {

    public Module module;
    public MainFrame parent;

    public SettingFrame settingFrame;

    public ArrayList<AbstractSettingComponent> settingComponents;

    int textHeight = 16;

    TurokRect titleRect;


    public ModuleButton(int x, int y, int width, int height, Module module, MainFrame parent){
        rect = new TurokRect(x, y, width, Xulu.VALUE_MANAGER.getSettingsByMod(module).size() * Metrics.SETTING_HEIGHT + textHeight);
        this.parent = parent;
        this.module = module;
        anchorPoint = parent.anchorPoint;
        settingFrame = new SettingFrame(0, 0, module);
        settingFrame.exists = false;

        settingComponents = new ArrayList<>();
        int offset = textHeight;
        if (Xulu.VALUE_MANAGER.getValuesByMod(module) != null) {
            for (Value<?> value : Xulu.VALUE_MANAGER.getValuesByMod(module)) {

                if (value != null) {
                    AbstractSettingComponent component = null;
                    if (value.isBind()) {
                        component = new KeybindComponent(rect.getX(), rect.getY() + offset, rect.getWidth() - Metrics.SPACER_SIZE, (Value<Bind>) value);
                    }
                    if (value.isToggle()){
                        component = new BooleanComponent(rect.getX(), rect.getY() + offset, rect.getWidth() - Metrics.BASIC_SPACING, ((Value<Boolean>) value));
                    }
                    if (value.isMode()){
                        component = new ModeComponent(rect.getX(), rect.getY() + offset, rect.getWidth() - Metrics.BASIC_SPACING, ((Value<String>) value));
                    }
                    if (value.isNumber()){
                        component = new SliderComponent(rect.getX(), rect.getY() + offset, rect.getWidth() - Metrics.BASIC_SPACING, value);
                    }
                    if (component != null) {
                        offset += component.getHeight();
                        settingComponents.add(component);
                    }
                }
            }
        }
        rect.setHeight(offset);

     //   ComponentManager.INSTANCE.addComponents(settingFrame);
    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {
        super.draw(m, partialTicks);
        this.anchorPoint = parent.anchorPoint;

        //    labelRect = new TurokRect(textHeight, )
       // int centerX = (rect.getWidth() - TurokFontManager.getStringWidth(FontManager.MEDIUM_LARGE.font, module.getName())) / 2;
        titleRect = new TurokRect(rect.getX(), rect.getY(), rect.getWidth(), textHeight);
        int centerY = (titleRect.getHeight() - TurokFontManager.getStringHeight(FontManager.MEDIUM_LARGE.font, module.getName())) / 2;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        TurokFontManager.render(FontManager.MEDIUM_LARGE.font, module.getName(), rect.getX() + 4, rect.getY() + centerY, true, Colors.FONT_COLOR);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        int checkHeight = 6;
        int centerYCheck = (titleRect.getHeight() - checkHeight) / 2;
        TurokRect checkMark = new TurokRect(rect.getX() + rect.getWidth() - 4 - checkHeight, rect.getY() + centerYCheck, checkHeight, checkHeight);
        renderCheckmark(checkMark, module.isToggled());

        int offset = textHeight;

        for (AbstractSettingComponent settingComponent : settingComponents){
            settingComponent.rect.setX(rect.getX() + Metrics.BASIC_SPACING);
            settingComponent.rect.setY(rect.getY() + offset);
            offset += settingComponent.getHeight();
            settingComponent.draw(m, partialTicks);
        }

        //      TurokRenderGL.drawRoundedRect(switchRect, switchSize / 2);


    }



    @Override
    public void click(TurokMouse m, int button) {

        super.click(m, button);
        if (titleRect.collideWithMouse(m)){
            if (button == 0){
                module.toggle();
            }
        }

        for (AbstractSettingComponent settingComponent : settingComponents){
            settingComponent.click(m, button);
        }
    }

    @Override
    public void release(TurokMouse m, int state) {
        super.release(m, state);
        for (AbstractSettingComponent settingComponent : settingComponents){
            settingComponent.release(m, state);
        }
    }

    @Override
    public void key(int keyTyped, char character) {
        super.key(keyTyped, character);
        for (AbstractSettingComponent settingComponent : settingComponents){
            settingComponent.key(keyTyped, character);
        }
    }

    public void renderSettings(TurokMouse m, float partialTicks){

    }

    public void clickSettings(TurokMouse m, int button){

    }

    public static void renderCheckmark(TurokRect rect, boolean enabled){
        if (enabled){
            TurokRenderGL.color(Colors.ENABLED_COLOR);
            renderGradientVertical(rect, Colors.ENABLED_COLOR, Colors.ENABLED_COLOR.darker().darker());
        }
        TurokRenderGL.color(Colors.LINE_COLOR);
        TurokRenderGL.lineSize(Metrics.LINE_SIZE);
        TurokRenderGL.drawOutlineRect(rect);
    }

    public static void renderGradientVertical(TurokRect rect, Color top, Color bottom){
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glBegin(GL11.GL_QUADS);
        {
            TurokRenderGL.color(top);
            TurokRenderGL.addVertex(rect.getX(), rect.getY());
            TurokRenderGL.color(bottom);
            TurokRenderGL.addVertex(rect.getX(), rect.getY() + rect.getHeight());
            TurokRenderGL.color(bottom);
            TurokRenderGL.addVertex(rect.getX() + rect.getWidth(), rect.getY() +  rect.getHeight());
            TurokRenderGL.color(top);
            TurokRenderGL.addVertex(rect.getX() + rect.getWidth(), rect.getY());
        }
        GL11.glEnd();
    }
}
