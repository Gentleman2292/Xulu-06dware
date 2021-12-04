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
public class ElementCheckBox extends Element {
	/*
	 * Konstrukor
	 */
	public ElementCheckBox(ModuleButton iparent, Value iset) {
		parent = iparent;
		set = iset;
		super.setup();
	}

	/*
	 * Rendern des Elements 
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (OldGui.toggleSetting.getValue().equalsIgnoreCase("Checkbox")) {
			Color temp = ColorUtil.getClickGUIColor();
			if (OldGui.rainbowgui.getValue()) {
				temp = (new Color(Xulu.rgb)).darker();
			}
			int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 225).getRGB();

			/*
			 * Die Box und Umrandung rendern
			 */
			Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 60));  //0xff1a1a1a

			/*
			 * Titel und Checkbox rendern.
			 */
			if (OldGui.customfont.getValue()) {
				Xulu.cFontRenderer.drawStringWithShadow(setstrg, (float) (x + width - Xulu.cFontRenderer.getStringWidth(setstrg)), (float) (y + height / 2 - 1.5) - 3, 0xffffffff);
			} else {
				FontUtil.drawString(setstrg, x + width - FontUtil.getStringWidth(setstrg), y + FontUtil.getFontHeight() / 2 - 1.5, 0xffffffff);
			}
			Gui.drawRect((int) (x + 1), (int) (y + 1), (int) (x + 11), (int) (y + 11), (Boolean) set.getValue() ? color : ColorUtils.changeAlpha(0xff000000, 150));
			if (isCheckHovered(mouseX, mouseY))
				Gui.drawRect((int) (x + 1), (int) (y + 1), (int) (x + 11), (int) (y + 11), ColorUtils.changeAlpha(0x55111111, 30));
		}
		else if (OldGui.toggleSetting.getValue().equalsIgnoreCase("Full-box")) {
			Color temp = ColorUtil.getClickGUIColor().darker();
			if (OldGui.rainbowgui.getValue()) {
				temp = (new Color(Xulu.rgb)).darker();
			}
			int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 200).getRGB();
			Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), (boolean) set.getValue() ? ColorUtils.changeAlpha(color, 225) : ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 60));  //0xff1a1a1a
			if (OldGui.customfont.getValue()) {
				Xulu.cFontRenderer.drawStringWithShadow(setstrg, (float) (x + (width / 2) - (Xulu.cFontRenderer.getStringWidth(setstrg) / 2)), (float) (y + Xulu.cFontRenderer.getHeight() / 2 - 1.5), 0xffffffff);
			} else {
				FontUtil.drawStringWithShadow(setstrg, x + (width / 2) - (FontUtil.getStringWidth(setstrg) / 2), y + FontUtil.getFontHeight() / 2 - 1.5, 0xffffffff);
			}
			Gui.drawRect((int)(x),(int)(y + height - 1), (int)(x + width), (int)(y + height), ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 30));
		}
	}

	/*
	 * 'true' oder 'false' bedeutet hat der Nutzer damit interagiert und
	 * sollen alle anderen Versuche der Interaktion abgebrochen werden?
	 */
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && (OldGui.toggleSetting.getValue().equalsIgnoreCase("Checkbox") ? isCheckHovered(mouseX, mouseY) : isHovered(mouseX, mouseY))) {
			set.setValue(!((boolean) set.getValue()));
			return true;
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/*
	 * Einfacher HoverCheck, ben tigt damit die Value ge ndert werden kann
	 */
	public boolean isCheckHovered(int mouseX, int mouseY) {
		return mouseX >= x + 1 && mouseX <= x + 11 && mouseY >= y + 1 && mouseY <= y + 11;
	}
}
