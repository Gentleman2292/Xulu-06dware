package dev.xulu.newgui.elements.menu;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.render.OldGui;
import com.elementars.eclient.util.ColorUtils;
import dev.xulu.newgui.elements.Element;
import dev.xulu.newgui.elements.ModuleButton;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.newgui.util.FontUtil;
import dev.xulu.settings.Bind;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

/**
 * @author Elementars
 * @since 5/31/2020 - 9:21 PM
 */
public class ElementKeyBind extends Element {

    private boolean listening;

    /*
     * Konstrukor
     */
    public ElementKeyBind(ModuleButton iparent, Value iset) {
        parent = iparent;
        set = iset;
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

        if (OldGui.customfont.getValue()) {
            Xulu.cFontRenderer.drawStringWithShadow(setstrg, (float) (x + 2), (float) y + 2, 0xffffffff);
        }else{
            FontUtil.drawStringWithShadow(setstrg, x + 2, y + 2, 0xffffffff);
        }
        int clr1 = color;
        int clr2 = temp.getRGB();

        Gui.drawRect((int)x, (int)(y + height - 1), (int)(x + width), (int)(y + height), ColorUtils.changeAlpha(0x77000000, 30));
        String s = listening ? "..." : Keyboard.getKeyName(((Bind) set.getValue()).getNum());
        if (OldGui.customfont.getValue()) {
            Xulu.cFontRenderer.drawStringWithShadow(s, (float) (x + 8 + Xulu.cFontRenderer.getStringWidth(setstrg)), (float) y + 2, new Color(0xffffffff).darker().darker().getRGB());
        }else{
            FontUtil.drawStringWithShadow(s, x + 8 + FontUtil.getStringWidth(setstrg), y + 2, new Color(0xffffffff).darker().darker().getRGB());
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isButtonHovered(mouseX, mouseY)) {
            if (mouseButton == 0) {
                listening = true;
            }
            return true;
        }
        return false;
    }

    public boolean keyTyped(char typedChar, int keyCode) throws IOException {
        if (listening) {
            if (keyCode != Keyboard.KEY_ESCAPE) {
                parent.mod.setKey(keyCode);
            } else {
                parent.mod.setKey(Keyboard.KEY_NONE);
            }
            listening = false;
            return true;
        }
        return super.keyTyped(typedChar, keyCode);
    }

    public boolean isButtonHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
