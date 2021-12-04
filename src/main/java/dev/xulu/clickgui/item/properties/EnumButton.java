package dev.xulu.clickgui.item.properties;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.render.ExeterGui;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.XuluTessellator;
import dev.xulu.clickgui.item.Button;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.Value;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class EnumButton extends Button
{
    
    public EnumButton(final Value property) {
        super(property.getName(), null);
        setValue(property);
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        //XuluTessellator.drawRectDouble(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height, this.getState() ? (this.isHovering(mouseX, mouseY) ? -1711586750 : 2012955202) : (this.isHovering(mouseX, mouseY) ? -2009910477 : 288568115));
        XuluTessellator.drawRectGradient(this.x, this.y, this.x + this.width + 7.4f, this.y + this.height, this.getState() ? ColorUtils.changeAlpha(ColorUtil.getClickGUIColor().getRGB(), 200) : 290805077, -1);
        if (this.isHovering(mouseX, mouseY)) {
            if (this.getState()) {
                XuluTessellator.drawRectGradient(this.x, this.y, this.x + this.width, this.y + this.height, ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 25), -1);
            } else {
                XuluTessellator.drawRectGradient(this.x, this.y, this.x + this.width, this.y + this.height, ColorUtils.changeAlpha(ColorUtils.Colors.WHITE, 25), -1);
            }
        }
        if (ExeterGui.getCF()) {
            Xulu.cFontRenderer.drawStringWithShadow(String.format("%s\u00A77 %s", this.getLabel(), Xulu.getTitle(property.getValue().toString())), this.x + 2.3f, this.y + 3.0f, this.getState() ? -1 : -5592406);
        } else {
            fontRenderer.drawStringWithShadow(String.format("%s\u00A77 %s", this.getLabel(), Xulu.getTitle(property.getValue().toString())), this.x + 2.3f, this.y + 4.0f, this.getState() ? -1 : -5592406);
        }
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            if (ExeterGui.getSound())
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            String s = (property.getValue() instanceof String ? (String) property.getValue() : property.getValue().toString());
            if (mouseButton == 0) {
                try {
                    if (!property.getCorrectOption(s).toString().equalsIgnoreCase(property.getOptions().get(property.getOptions().size() - 1).toString())) {
                        property.setEnumValue(property.getOptions().get(property.getOptions().indexOf(property.getCorrectOption(s)) + 1).toString());
                    } else {
                        property.setEnumValue(property.getOptions().get(0).toString());
                    }
                } catch (Exception e) {
                    System.err.println("Error with invalid combo");
                    e.printStackTrace();
                    property.setEnumValue(property.getOptions().get(0).toString());
                }
            }
            else if (mouseButton == 1) {
                try {
                    if (property.getOptions().listIterator(property.getOptions().indexOf(property.getCorrectOption(s))).hasPrevious())
                        property.setEnumValue(property.getOptions().listIterator(property.getOptions().indexOf(property.getCorrectOption(s))).previous().toString());
                    else
                        property.setEnumValue(property.getOptions().get(property.getOptions().size() - 1).toString());
                } catch (Exception e) {
                    System.err.println("Error with invalid combo");
                    e.printStackTrace();
                    property.setEnumValue(property.getOptions().get(0).toString());
                }
            }
        }
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    @Override
    public void toggle() {
    }
    
    @Override
    public boolean getState() {
        return true;
    }
}
