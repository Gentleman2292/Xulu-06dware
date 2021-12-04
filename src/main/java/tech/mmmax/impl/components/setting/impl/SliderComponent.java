package tech.mmmax.impl.components.setting.impl;

import dev.xulu.settings.Value;
import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokRect;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import tech.mmmax.api.color.Colors;
import tech.mmmax.api.component.Metrics;
import tech.mmmax.impl.components.setting.AbstractSettingComponent;

public class SliderComponent extends AbstractSettingComponent {
    Value setting;
    TurokRect slider;
    TurokRect sliderBar;
    double val;
    boolean dragging;
    public SliderComponent(int x, int y, int width, Value setting) {
        super(x, y, width, Metrics.SETTING_HEIGHT);
        this.setting = setting;
        int sliderWidth = (int) (rect.width / 2.4);
        int sliderX = rect.getWidth() - sliderWidth - Metrics.BASIC_SPACING;
        slider = new TurokRect(rect.getX() + sliderX, rect.getY(), sliderWidth, rect.getHeight());

    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {
        super.draw(m, partialTicks);
        if (setting != null) {
            int centerY = (rect.getHeight() - TurokFontManager.getStringHeight(font, setting.getName())) / 2;
            TurokFontManager.render(font, setting.getName(), rect.getX() + Metrics.BASIC_SPACING, rect.getY() + centerY, true, Colors.FONT_COLOR);

            int sliderWidth = (int) (rect.width / 2.4);
            int sliderX = rect.getWidth() - sliderWidth - Metrics.BASIC_SPACING;
            slider = new TurokRect(rect.getX() + sliderX, rect.getY() + 1, sliderWidth, rect.getHeight() - 2);

            sliderBar = new TurokRect(slider.getX(), slider.getY(), ((int) (getPercent(getValDouble(setting), getMinDouble(setting), getMaxDouble(setting)) * slider.getWidth())), slider.getHeight());

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            TurokRenderGL.color(Colors.ENABLED_COLOR);
            TurokRenderGL.drawSolidRect(sliderBar);
            TurokRenderGL.color(Colors.FONT_COLOR);
            TurokRenderGL.drawOutlineRect(slider);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            int centerX = (slider.getWidth() - TurokFontManager.getStringWidth(font, setting.getValue().toString())) / 2;
            int centerYSlider = (slider.getHeight()  - TurokFontManager.getStringHeight(font, setting.getValue().toString())) / 2;
            TurokFontManager.render(font, setting.getValue().toString(), slider.getX() +  centerX, slider.getY() + centerYSlider, true, Colors.FONT_COLOR);
            if (dragging) {
                Number val;
                if (setting.getValue() instanceof Integer) {
                    int diff = (int) setting.getMax() - (int) setting.getMin();
                    val= (int) setting.getMin() + (MathHelper.clamp((m.getX() - slider.x) / (this.slider.width + 7.4f), 0, 1)) * diff;
                }else if (setting.getValue() instanceof Short) {
                    short diff = (short) ((short) setting.getMax() - (short) setting.getMin());
                    val= (short) setting.getMin() + (MathHelper.clamp((m.getX() - slider.x) / (this.slider.width + 7.4f), 0, 1)) * diff;
                } else if (setting.getValue() instanceof Long) {
                    long diff = (long) setting.getMax() - (long) setting.getMin();
                    val= (long) setting.getMin() + (MathHelper.clamp((m.getX() - slider.x) / (slider.width + 7.4f), 0, 1)) * diff;
                } else if (setting.getValue() instanceof Float) {
                    float diff = (float) setting.getMax() - (float) setting.getMin();
                    val = (float) setting.getMin() + (MathHelper.clamp((m.getX() - slider.x) / (this.slider.width + 7.4f), 0, 1)) * diff;
                } else if (setting.getValue() instanceof Double) {
                    double diff = (double) setting.getMax() - (double) setting.getMin();
                    val = (double) setting.getMin() + (MathHelper.clamp((m.getX() - slider.x) / (this.slider.width + 7.4f), 0, 1)) * diff;
                } else {
                    val = 0d;
                }

                Number type;
                if (setting.getValue() instanceof Integer) {
                    type = val.intValue();
                } else if (setting.getValue() instanceof Short) {
                    type = val.shortValue();
                } else if (setting.getValue() instanceof Long) {
                    type = val.longValue();
                } else if (setting.getValue() instanceof Float) {
                    type = val.floatValue();
                } else if (setting.getValue() instanceof Double) {
                    type = val.doubleValue();
                } else {
                    type = 0;
                }
                setting.setValue(type);
            }
        }
    }

    @Override
    public void click(TurokMouse m, int button) {
        super.click(m, button);
        if (slider.collideWithMouse(m)) dragging = true;
    }

    @Override
    public void release(TurokMouse m, int state) {
        super.release(m, state);
        dragging = false;
    }

    double getValDouble(Value val){
        double displayval;
        if (val.getValue() instanceof Integer) {
            int iVal = ((int) val.getValue());

            displayval = iVal;
        }
        else if (val.getValue() instanceof Short) {
            short sVal = ((short) val.getValue());

            displayval = sVal;
        }
        else if (val.getValue() instanceof Long) {
            long lVal = ((long) val.getValue());

            displayval = lVal;
        }
        else if (val.getValue() instanceof Float) {
            float fVal = ((float) val.getValue());
            displayval = fVal;
        }
        else if (val.getValue() instanceof Double) {
            displayval = (double) val.getValue();
        } else {
            displayval = 0;
        }
        return displayval;
    }

    double getMinDouble(Value val){
        double displayval;
        if (val.getMin() instanceof Integer) {
            int iVal = ((int) val.getMin());

            displayval = iVal;
        }
        else if (val.getMin() instanceof Short) {
            short sVal = ((short) val.getMin());

            displayval = sVal;
        }
        else if (val.getMin() instanceof Long) {
            long lVal = ((long) val.getMin());

            displayval = lVal;
        }
        else if (val.getMin() instanceof Float) {
            float fVal = ((float) val.getMin());
            displayval = fVal;
        }
        else if (val.getMin() instanceof Double) {
            displayval = (double) val.getMin();
        } else {
            displayval = 0;
        }
        return displayval;
    }

    double getMaxDouble(Value val){
        double displayval;
        if (val.getMax() instanceof Integer) {
            int iVal = ((int) val.getMax());

            displayval = iVal;
        }
        else if (val.getMax() instanceof Short) {
            short sVal = ((short) val.getMax());

            displayval = sVal;
        }
        else if (val.getMax() instanceof Long) {
            long lVal = ((long) val.getMax());

            displayval = lVal;
        }
        else if (val.getMax() instanceof Float) {
            float fVal = ((float) val.getMax());
            displayval = fVal;
        }
        else if (val.getMax() instanceof Double) {
            displayval = (double) val.getMax();
        } else {
            displayval = 0;
        }
        return displayval;
    }

    void setValDouble(Value val, double newVal){
        if (val.getMax() instanceof Integer) {
            int iVal = ((int) val.getMax());

            val.setValue(iVal);
        }
        else if (val.getMax() instanceof Short) {
            short sVal = ((short) val.getMax());

            val.setValue(sVal);
        }
        else if (val.getMax() instanceof Long) {
            long lVal = ((long) val.getMax());

            val.setValue(lVal);
        }
        else if (val.getMax() instanceof Float) {
            float fVal = ((float) val.getMax());
            val.setValue(fVal);
        }
        else if (val.getMax() instanceof Double) {
            val.setValue((double) val.getMax());
        } else {
        //    displayval = 0;
        }
    }



    double getPercent(double val, double min, double max){
        double percent = (val - min) / (max - min);
        return percent;
    }
}
