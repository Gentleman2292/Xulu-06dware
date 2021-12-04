package dev.xulu.clickgui.item.properties;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.render.ExeterGui;
import com.elementars.eclient.module.render.NewGui;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.Timer;
import com.elementars.eclient.util.XuluTessellator;
import dev.xulu.clickgui.item.Button;
import dev.xulu.newgui.elements.menu.ElementTextBox;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.newgui.util.FontUtil;
import dev.xulu.settings.Bind;
import dev.xulu.settings.TextBox;
import dev.xulu.settings.Value;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;

/**
 * @author Elementars
 * @since 6/2/2020 - 1:55 PM
 */
public class TextButton extends Button {

    private boolean listening;

    private Timer timer = new Timer();
    private boolean showCursor;

    private ElementTextBox.CurrentString currentString = new ElementTextBox.CurrentString("");

    public static class CurrentString
    {
        private String string;

        public CurrentString(final String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }

    public TextButton(final Value property) {
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
        String s = currentString.getString();
        if (listening) {
            if (timer.hasReached(500)) {
                showCursor = !showCursor;
                timer.reset();
            }
            if (showCursor)
                Gui.drawRect((int) x + (NewGui.customfont.getValue() ? Xulu.cFontRenderer.getStringWidth(s) : FontUtil.getStringWidth(s)) + 3, (int) y + 1, (int) x + (NewGui.customfont.getValue() ? Xulu.cFontRenderer.getStringWidth(s) : FontUtil.getStringWidth(s)) + 4, (int) y + FontUtil.getFontHeight() + 5, -1);
        } else {
            if (!s.equals(((Value<TextBox>) property).getValue().getText())) {
                currentString = new ElementTextBox.CurrentString(((TextBox) property.getValue()).getText());
            }
            showCursor = false;
        }
        if (ExeterGui.getCF()) {
            Xulu.cFontRenderer.drawStringWithShadow(s, this.x + 2.3f, this.y + 3.0f, this.getState() ? -1 : -1);
        } else {
            fontRenderer.drawStringWithShadow(s, this.x + 2.3f, this.y + 4.0f, this.getState() ? -1 : -1);
        }
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            if (ExeterGui.getSound())
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (mouseButton == 0) {
                currentString = new ElementTextBox.CurrentString(((TextBox)property.getValue()).getText());
                listening = true;
            }
        }
    }

    @Override
    public boolean keyTyped(final char typedChar, final int keyCode) {
        if (this.listening) {
            switch (keyCode) {
                case 1: {
                    return true;
                }
                case 28: {
                    this.enterString();
                    return true;
                }
                case 14: {
                    this.setString(removeLastChar(this.currentString.getString()));
                    return true;
                }
                default: {
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        this.setString(this.currentString.getString() + typedChar);
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    public static String removeLastChar(final String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    private void enterString() {
        property.setValue(new TextBox(currentString.getString()));
        //((TextBox)property.getValue()).setText(this.currentString.getString());
        this.setString(((TextBox) property.getValue()).getText());
        listening = false;
    }

    public void setString(final String newString) {
        this.currentString = new ElementTextBox.CurrentString(newString);
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
        return false;
    }
}

