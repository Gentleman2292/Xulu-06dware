package dev.xulu.newgui.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.render.OldGui;
import com.elementars.eclient.util.ColorUtils;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.newgui.Panel;
import dev.xulu.newgui.elements.menu.*;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.newgui.util.FontUtil;
import dev.xulu.settings.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;


/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit me
 *
 *  @author HeroCode
 */
public class ModuleButton {
	public Module mod;
	public ArrayList<Element> menuelements;
	public Panel parent;
	public double x;
	public double y;
	public double width;
	public double height;
	public double height2;
	public boolean extended = false;
	public boolean listening = false;

	/*
	 * Konstrukor
	 */
	public ModuleButton(Module imod, Panel pl) {
		mod = imod;
		height = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2;
		height2 = Xulu.cFontRenderer.getHeight() + 2;
		parent = pl;
		menuelements = new ArrayList<>();
		/*
		 * Settings wurden zuvor in eine ArrayList eingetragen
		 * dieses SettingSystem hat 3 Konstruktoren je nach
		 *  verwendetem Konstruktor   ndert sich die Value
		 *  bei .isCheck() usw. so kann man ganz einfach ohne
		 *  irgendeinen Aufwand bestimmen welches Element
		 *  f  r ein Setting ben  tigt wird :>
		 */
		if (Xulu.VALUE_MANAGER.getSettingsByMod(imod) != null)
			for (Value s : Xulu.VALUE_MANAGER.getSettingsByMod(imod)) {
				if (s.isToggle()) {
					menuelements.add(new ElementCheckBox(this, s));
				} else if (s.isNumber()) {
					menuelements.add(new ElementSlider(this, s));
				} else if (s.isMode()) {
					menuelements.add(new ElementComboBox(this, s));
				} else if (s.isEnum()) {
					menuelements.add(new ElementComboBoxEnum(this, s));
				} else if (s.isBind() && !(mod instanceof com.elementars.eclient.guirewrite.Element)) {
					menuelements.add(new ElementKeyBind(this, s));
				} else if (s.isText()) {
					menuelements.add(new ElementTextBox(this, s));
				}
			}

	}

	/*
	 * Rendern des Elements 
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Color temp = ColorUtil.getClickGUIColor();
		int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 200).getRGB();
		if (OldGui.rainbowgui.getValue()) {
			color = ColorUtils.changeAlpha(parent.rgb, 200);
		}
		
		/*
		 * Ist das Module an, wenn ja dann soll
		 *  #ein neues Rechteck in Gr    e des Buttons den Knopf als Toggled kennzeichnen
		 *  #sich der Text anders f  rben
		 */
		int textcolor = 0xffafafaf;
		if (mod.isToggled()) {
			if (!OldGui.moduleSetting.getValue().equalsIgnoreCase("Text")) {
				if (OldGui.moduleSetting.getValue().equalsIgnoreCase("MiniButton")) {
					Gui.drawRect((int) x, (int) y + 1, (int) (x + width), (int) (y + height), ColorUtils.changeAlpha(color, 100));
					textcolor = 0xffefefef;
				} else {
					Gui.drawRect((int) x - 2, (int) y, (int) (x + width + 2), (int) (y + height + 1), color);
					textcolor = 0xffefefef;
				}
			}
		}
		
		/*
		 * Ist die Maus   ber dem Element, wenn ja dann soll der Button sich anders f  rben
		 */
		if (isHovered(mouseX, mouseY)) {
			if (!OldGui.moduleSetting.getValue().equalsIgnoreCase("Text")) {
				if (OldGui.moduleSetting.getValue().equalsIgnoreCase("MiniButton")) {
					Gui.drawRect((int) x, (int) y + 1, (int) (x + width), (int) (y + height), (mod.isToggled() && !mod.getCategory().equals(Category.HUD) ? ColorUtils.changeAlpha(0x55111111, 30) : ColorUtils.changeAlpha(ColorUtils.Colors.GRAY, 30))); //0x55111111
				} else {
					Gui.drawRect((int) (x - 2), (int) y, (int) (x + width + 2), (int) (y + height + 1), (mod.isToggled() && !mod.getCategory().equals(Category.HUD) ? ColorUtils.changeAlpha(0x55111111, 30) : ColorUtils.changeAlpha(ColorUtils.Colors.GRAY, 30))); //0x55111111
				}
			}
		}
		
		/*
		 * Den Namen des Modules in die Mitte (x und y) rendern
		 */
		if (OldGui.moduleSetting.getValue().equalsIgnoreCase("MiniButton")) {
			if (OldGui.customfont.getValue()) {
				Xulu.cFontRenderer.drawStringWithShadow(mod.getName(), (float) (x + 2), (float) ((y + 1 + height2 / 2) - 4), textcolor);
				if (Xulu.VALUE_MANAGER.getValuesByMod(mod) != null) {
					Xulu.cFontRenderer.drawStringWithShadow((extended ? "." : "..."), (float) (x + width - 10), (float) ((y + 1 + height2 / 2) - 4), textcolor);
				}
			} else {
				FontUtil.drawStringWithShadow(mod.getName(), x + 2, y + 1 + height / 2 - 4, textcolor);
				if (Xulu.VALUE_MANAGER.getValuesByMod(mod) != null) {
					FontUtil.drawStringWithShadow((extended ? "." : "..."), x + width - 7, y + 1 + height / 2 - 4, textcolor);
				}
			}
		} else if (OldGui.moduleSetting.getValue().equalsIgnoreCase("Text"))  {
			if (OldGui.customfont.getValue()) {
				Xulu.cFontRenderer.drawStringWithShadow((isHovered(mouseX, mouseY) ? ChatFormatting.BOLD : "") + mod.getName(), (float) (x + 2), (float) ((y + 1 + height2 / 2) - 4), (mod.isToggled() && !mod.getCategory().equals(Category.HUD) ? ColorUtil.getClickGUIColor().getRGB() : textcolor));
				if (Xulu.VALUE_MANAGER.getValuesByMod(mod) != null) {
					Xulu.cFontRenderer.drawStringWithShadow((extended ? ">" : "V"), (float) (x + width - 6), (float) ((y + 1 + height2 / 2) - 4), textcolor);
				}
			} else {
				FontUtil.drawStringWithShadow((isHovered(mouseX, mouseY) ? ChatFormatting.BOLD : "") + mod.getName(), x + 2, y + 1 + height / 2 - 4, (mod.isToggled() && !mod.getCategory().equals(Category.HUD) ? ColorUtil.getClickGUIColor().getRGB() : textcolor));
				if (Xulu.VALUE_MANAGER.getValuesByMod(mod) != null) {
					FontUtil.drawStringWithShadow((extended ? ">" : "V"), x + width - 5, y + 1 + height / 2 - 4, textcolor);
				}
			}
		} else {
			if (OldGui.customfont.getValue()) {
				Xulu.cFontRenderer.drawStringWithShadow(mod.getName(), (float) (x + 2), (float) ((y + 1 + height2 / 2) - 4), textcolor);
				if (Xulu.VALUE_MANAGER.getValuesByMod(mod) != null) {
					Xulu.cFontRenderer.drawStringWithShadow((extended ? ">" : "V"), (float) (x + width - 6), (float) ((y + 1 + height2 / 2) - 4), textcolor);
				}
			} else {
				FontUtil.drawStringWithShadow(mod.getName(), x + 2, y + 1 + height / 2 - 4, textcolor);
				if (Xulu.VALUE_MANAGER.getValuesByMod(mod) != null) {
					FontUtil.drawStringWithShadow((extended ? ">" : "V"), x + width - 5, y + 1 + height / 2 - 4, textcolor);
				}
			}
		}
	}

	/*
	 * 'true' oder 'false' bedeutet hat der Nutzer damit interagiert und
	 * sollen alle anderen Versuche der Interaktion abgebrochen werden?
	 */
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!isHovered(mouseX, mouseY))
			return false;

		/*
		 * Rechtsklick, wenn ja dann Module togglen, 
		 */
		if (mouseButton == 0) {
			mod.toggle();
			
			//if(Eclient.settinsManager.getSettingByName("Sound").getValBoolean())
			//Minecraft.getMinecraft().player.playSound("random.click", 0.5f, 0.5f);
		} else if (mouseButton == 1) {
			/*
			 * Wenn ein Settingsmenu existiert dann sollen alle Settingsmenus 
			 * geschlossen werden und dieses ge  ffnet/geschlossen werden
			 */
			if (menuelements != null && menuelements.size() > 0) {
				boolean b = !this.extended;
				Xulu.newGUI.closeAllSettings();
				this.extended = b;
				
				//if(Eclient.settinsManager.getSettingByName("Sound").getValBoolean())
				//if(extended) Minecraft.getMinecraft().player.playSound("tile.piston.out", 1f, 1f);else Minecraft.getMinecraft().thePlayer.playSound("tile.piston.in", 1f, 1f);
			}
		}
		/* else if (mouseButton == 2 && (mod.getName().equalsIgnoreCase("HudEditor") || !mod.getCategory().equals(Category.HUD))) {
			listening = true;
		}
		*/
		return true;
	}

	public boolean keyTyped(char typedChar, int keyCode) throws IOException {
		/*
		 * Wenn listening, dann soll der n  chster Key (abgesehen 'ESCAPE') als Keybind f  r mod
		 * danach soll nicht mehr gewartet werden!
		 */
		/*
		if (listening) {
			if (keyCode != Keyboard.KEY_ESCAPE) {
				Command.sendChatMessage("Bound '" + mod.getName() + "'" + " to '" + Keyboard.getKeyName(keyCode) + "'");
				mod.setKey(keyCode);
			} else {
				Command.sendChatMessage("Unbound '" + mod.getName() + "'");
				mod.setKey(Keyboard.KEY_NONE);
			}
			listening = false;
			return true;
		}
		*/
		for (Element e : menuelements) {
			if (!e.set.isVisible()) continue;
			if (e.keyTyped(typedChar, keyCode)) return true;
		}
		return false;
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

}
