package dev.xulu.clickgui.item.properties;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.render.ExeterGui;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.Helper;
import com.elementars.eclient.util.XuluTessellator;
import dev.xulu.clickgui.ClickGui;
import dev.xulu.clickgui.Panel;
import dev.xulu.clickgui.item.Item;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.Value;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class NumberSlider extends Item implements Helper
{

    private boolean dragging;
    
    public NumberSlider(final Value numberProperty) {
        super(numberProperty.getName());
        setValue(numberProperty);
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        String displayval;
        if (property.getValue() instanceof Integer) {
            displayval = "" + Math.round((int) property.getValue() * 100D) / 100D;
        }
        else if (property.getValue() instanceof Short) {
            displayval = "" + Math.round((short) property.getValue() * 100D) / 100D;
        }
        else if (property.getValue() instanceof Long) {
            displayval = "" + Math.round((long) property.getValue() * 100D) / 100D;
        }
        else if (property.getValue() instanceof Float) {
            displayval = "" + Math.round((float) property.getValue() * 100D) / 100D;
        }
        else if (property.getValue() instanceof Double) {
            displayval = "" + Math.round((double) property.getValue() * 100D) / 100D;
        } else {
            displayval = "";
        }
        double percentBar;
        if (property.getValue() instanceof Integer) {
            double value = (int) property.getValue();
            percentBar = (value - (int)property.getMin()) / ((int)property.getMax() - (int)property.getMin());
        }
        else if (property.getValue() instanceof Short) {
            double value = (short) property.getValue();
            percentBar = (value - (short)property.getMin()) / ((short)property.getMax() - (short)property.getMin());
        }
        else if (property.getValue() instanceof Long) {
            double value = (long) property.getValue();
            percentBar = (value - (long)property.getMin()) / ((long)property.getMax() - (long)property.getMin());
        }
        else if (property.getValue() instanceof Float) {
            percentBar = ((float) property.getValue() - (float)property.getMin()) / ((float)property.getMax() - (float)property.getMin());
        }
        else if (property.getValue() instanceof Double) {
            percentBar = ((double) property.getValue() - (double)property.getMin()) / ((double)property.getMax() - (double)property.getMin());
        } else {
            percentBar = 0;
        }
        //XuluTessellator.drawRectDouble(this.x, this.y, this.x + this.getValueWidth(), this.y + this.height, this.isHovering(mouseX, mouseY) ? -1721964477 : 2002577475);
        XuluTessellator.drawRectGradient(this.x, this.y, this.x + (percentBar * (this.width + 7.4f)), this.y + this.height, ColorUtils.changeAlpha(ColorUtil.getClickGUIColor().getRGB(), 200), -1);
        if (this.isHovering(mouseX, mouseY)) {
            XuluTessellator.drawRectGradient(this.x, this.y, this.x + this.width, this.y + this.height, ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 25), -1);
        }
        if (ExeterGui.getCF()) {
            Xulu.cFontRenderer.drawStringWithShadow(String.format("%s\u00A77 %s", this.getLabel(), displayval), this.x + 2.3f, this.y + 3.0f, -1);
        } else {
            fontRenderer.drawStringWithShadow(String.format("%s\u00A77 %s", this.getLabel(), displayval), this.x + 2.3f, this.y + 4.0f, -1);
        }
        if (dragging) {
            Number val;
            if (property.getValue() instanceof Integer) {
                int diff = (int) property.getMax() - (int) property.getMin();
                val= (int) property.getMin() + (MathHelper.clamp((mouseX - x) / (this.width + 7.4f), 0, 1)) * diff;
            }else if (property.getValue() instanceof Short) {
                short diff = (short) ((short) property.getMax() - (short) property.getMin());
                val= (short) property.getMin() + (MathHelper.clamp((mouseX - x) / (this.width + 7.4f), 0, 1)) * diff;
            } else if (property.getValue() instanceof Long) {
                long diff = (long) property.getMax() - (long) property.getMin();
                val= (long) property.getMin() + (MathHelper.clamp((mouseX - x) / (this.width + 7.4f), 0, 1)) * diff;
            } else if (property.getValue() instanceof Float) {
                float diff = (float) property.getMax() - (float) property.getMin();
                val = (float) property.getMin() + (MathHelper.clamp((mouseX - x) / (this.width + 7.4f), 0, 1)) * diff;
            } else if (property.getValue() instanceof Double) {
                double diff = (double) property.getMax() - (double) property.getMin();
                val = (double) property.getMin() + (MathHelper.clamp((mouseX - x) / (this.width + 7.4f), 0, 1)) * diff;
            } else {
                val = 0d;
            }

            Number type;
            if (property.getValue() instanceof Integer) {
                type = val.intValue();
            } else if (property.getValue() instanceof Short) {
                type = val.shortValue();
            } else if (property.getValue() instanceof Long) {
                type = val.longValue();
            } else if (property.getValue() instanceof Float) {
                type = val.floatValue();
            } else if (property.getValue() instanceof Double) {
                type = val.doubleValue();
            } else {
                type = 0;
            }
            property.setValue(type);
        }
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (this.isHovering(mouseX, mouseY) && mouseButton == 0) {
            if (ExeterGui.getSound())
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            this.dragging = true;
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.dragging = false;
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    private boolean isHovering(final int mouseX, final int mouseY) {
        for (final Panel panel : ClickGui.getClickGui().getPanels()) {
            if (panel.drag) {
                return false;
            }
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
    
    private float getValueWidth() {
        return ((Number)this.property.getMax()).floatValue() - ((Number)this.property.getMin()).floatValue() + ((Number)this.property.getValue()).floatValue();
    }
}
