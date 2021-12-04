package dev.xulu.newgui.elements.menu;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.render.OldGui;
import com.elementars.eclient.util.ColorUtils;
import dev.xulu.newgui.elements.Element;
import dev.xulu.newgui.elements.ModuleButton;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.newgui.util.FontUtil;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.Gui;

import java.awt.*;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit me
 *
 *  @author HeroCode
 */
public class ElementComboBoxEnum extends Element {
    /*
     * Konstrukor
     */
    public ElementComboBoxEnum(ModuleButton iparent, Value iset) {
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
            Xulu.cFontRenderer.drawStringWithShadow(set.getName(), (float) (x + 2), (float) y + 2, 0xffffffff);
        }else{
            FontUtil.drawStringWithShadow(set.getName(), x + 2, y + 2, 0xffffffff);
        }
        int clr1 = color;
        int clr2 = temp.getRGB();

        Gui.drawRect((int)x, (int)(y + 14), (int)(x + width), (int)(y + 15), ColorUtils.changeAlpha(0x77000000, 30));
        String s = Xulu.getTitle(set.getValue().toString());
        if (OldGui.customfont.getValue()) {
            Xulu.cFontRenderer.drawStringWithShadow(s, (float) (x + 8 + Xulu.cFontRenderer.getStringWidth(set.getName())), (float) y + 2, new Color(0xffffffff).darker().darker().getRGB());
        }else{
            FontUtil.drawStringWithShadow(s, x + 8 + FontUtil.getStringWidth(set.getName()), y + 2, new Color(0xffffffff).darker().darker().getRGB());
        }
    }
	/*
	if (comboextended) {
		Gui.drawRect((int)x, (int)(y + 15), (int)(x + width), (int)(y + height), 0xaa121212);
		double ay = y + 15;
		for (String sld : set.getOptions()) {
			String elementtitle = sld.substring(0, 1).toUpperCase() + sld.substring(1, sld.length());
			if (OldGui.customfont.getValBoolean()) {
				Eclient.cFontRenderer.drawCenteredString(elementtitle, (float) (x + width / 2), (float) (ay + 2), 0xffffffff);
			}else{
				FontUtil.drawCenteredString(elementtitle, x + width / 2, ay + 2, 0xffffffff);
			}


			if (sld.equalsIgnoreCase(set.getValString())) {
				Gui.drawRect((int)x, (int)ay, (int)(x + 1.5), (int)(ay + (OldGui.customfont.getValBoolean() ? Eclient.cFontRenderer.getHeight() : FontUtil.getFontHeight()) + 2), clr1);
			}
			*/ /*
			if (mouseX >= x && mouseX <= x + width && mouseY >= ay && mouseY < ay + (OldGui.customfont.getValBoolean() ? Eclient.cFontRenderer.getHeight() : FontUtil.getFontHeight()) + 2) {
				Gui.drawRect((int)(x + width - 1.2), (int)ay, (int)(x + width), (int)(ay + (OldGui.customfont.getValBoolean() ? Eclient.cFontRenderer.getHeight() : FontUtil.getFontHeight()) + 2), clr2);
			}
			ay += (OldGui.customfont.getValBoolean() ? Eclient.cFontRenderer.getHeight() : FontUtil.getFontHeight()) + 2;
		}
	}
	*/

    /*
     * 'true' oder 'false' bedeutet hat der Nutzer damit interagiert und
     * sollen alle anderen Versuche der Interaktion abgebrochen werden?
     */
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        String s = (set.getValue() instanceof String ? (String) set.getValue() : set.getValue().toString());
        if (mouseButton == 0) {
            if (isButtonHovered(mouseX, mouseY)) {
                try {
                    if (!set.getCorrectOption(s).toString().equalsIgnoreCase(set.getOptions().get(set.getOptions().size() - 1).toString())) {
                        set.setEnumValue(set.getOptions().get(set.getOptions().indexOf(set.getCorrectOption(s)) + 1).toString());
                    } else {
                        set.setEnumValue(set.getOptions().get(0).toString());
                    }
                } catch (Exception e) {
                    System.err.println("Error with invalid combo");
                    e.printStackTrace();
                    set.setEnumValue(set.getOptions().get(0).toString());
                }
                return true;
            }
        }

        if (mouseButton == 1) {
            if (isButtonHovered(mouseX, mouseY)) {
                try {
                    if (set.getOptions().listIterator(set.getOptions().indexOf(set.getCorrectOption(s))).hasPrevious())
                        set.setEnumValue(set.getOptions().listIterator(set.getOptions().indexOf(set.getCorrectOption(s))).previous().toString());
                    else
                        set.setEnumValue(set.getOptions().get(set.getOptions().size() - 1).toString());
                } catch (Exception e) {
                    System.err.println("Error with invalid combo");
                    e.printStackTrace();
                    set.setEnumValue(set.getOptions().get(0).toString());
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /*
     * Einfacher HoverCheck, ben  tigt damit die Combobox ge  ffnet und geschlossen werden kann
     */
    public boolean isButtonHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

	/*

			if (!comboextended)return false;
	double ay = y + 15;
			for (String slcd : set.getOptions()) {
		if (mouseX >= x && mouseX <= x + width && mouseY >= ay && mouseY <= ay + (OldGui.customfont.getValBoolean() ? Eclient.cFontRenderer.getHeight() : FontUtil.getFontHeight()) + 2) {
			//if(Client.setmgr.getSettingByName("Sound").getValBoolean())
			//Minecraft.getMinecraft().thePlayer.playSound("tile.piston.in", 20.0F, 20.0F);

			if(clickgui != null && clickgui.setmgr != null)
				//clickgui.setmgr.getSettingByName(set.getName()).setValString(slcd.toLowerCase());
				clickgui.setmgr.getSettingByMod(set.getParentMod(), set.getName()).setValString(slcd.toLowerCase());
			return true;
		}
		ay += (OldGui.customfont.getValBoolean() ? Eclient.cFontRenderer.getHeight() : FontUtil.getFontHeight()) + 2;
	}
	 */
}
