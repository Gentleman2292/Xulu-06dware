package dev.xulu.newgui.elements.menu;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.render.OldGui;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.Timer;
import dev.xulu.newgui.elements.Element;
import dev.xulu.newgui.elements.ModuleButton;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.newgui.util.FontUtil;
import dev.xulu.settings.Bind;
import dev.xulu.settings.TextBox;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ChatAllowedCharacters;

import java.awt.*;

/**
 * @author Elementars
 * @since 5/31/2020 - 9:21 PM
 */
public class ElementTextBox extends Element {

    private boolean listening;
    private Timer timer = new Timer();
    private boolean showCursor;

    private CurrentString currentString = new CurrentString("");

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

    /*
     * Konstrukor
     */
    public ElementTextBox(ModuleButton iparent, Value iset) {
        parent = iparent;
        set = iset;
        currentString = new CurrentString(((TextBox)iset.getValue()).getText());
        super.setup();
    }

    /*
     * Rendern des Elements
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Color temp = ColorUtil.getClickGUIColor();
        int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 150).getRGB();

        /*
         * Die Box und Umrandung rendern
         */
        Gui.drawRect((int)x, (int)y, (int)(x + width), (int)(y + height), ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 60));  //0xff1a1a1a

        Gui.drawRect((int)x, (int)(y + height - 1), (int)(x + width), (int)(y + height), ColorUtils.changeAlpha(0x77000000, 30));
        String s = currentString.getString();
        if (listening) {
            if (timer.hasReached(500)) {
                showCursor = !showCursor;
                timer.reset();
            }
            if (showCursor)
                Gui.drawRect((int) x + (OldGui.customfont.getValue() ? Xulu.cFontRenderer.getStringWidth(s) : FontUtil.getStringWidth(s)) + 2, (int) y, (int) x + (OldGui.customfont.getValue() ? Xulu.cFontRenderer.getStringWidth(s) : FontUtil.getStringWidth(s)) + 3, (int) y + FontUtil.getFontHeight() + 2, -1);
        } else {
            if (!s.equals(((Value<TextBox>) set).getValue().getText())) {
                currentString = new CurrentString(((TextBox) set.getValue()).getText());
            }
            showCursor = false;
        }
        if (OldGui.customfont.getValue()) {
            Xulu.cFontRenderer.drawStringWithShadow(s, (float) (x + 2), (float) y + 2, -1);
        }else{
            FontUtil.drawStringWithShadow(s, x + 2, y + 2, -1);
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isButtonHovered(mouseX, mouseY)) {
            if (mouseButton == 0) {
                currentString = new CurrentString(((TextBox)set.getValue()).getText());
                listening = true;
            }
            return true;
        }
        return false;
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
        set.setValue(new TextBox(currentString.getString()));
        //((TextBox)set.getValue()).setText(this.currentString.getString());
        this.setString(((TextBox) set.getValue()).getText());
        listening = false;
    }

    public void setString(final String newString) {
        this.currentString = new CurrentString(newString);
    }

    public boolean isButtonHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
