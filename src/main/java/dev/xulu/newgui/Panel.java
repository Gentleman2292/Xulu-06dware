package dev.xulu.newgui;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.module.render.OldGui;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.XuluTessellator;
import dev.xulu.newgui.elements.Element;
import dev.xulu.newgui.elements.ModuleButton;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.newgui.util.FontUtil;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit me
 *
 *  @author HeroCode
 */
public class Panel {
	public String title;
	public double x;
	public double y;
	private double x2;
	private double y2;
	public double width;
	public double height;
	public boolean dragging;
	public boolean extended;
	public boolean visible;
	public ArrayList<ModuleButton> Elements = new ArrayList<>();
	public NewGUI clickgui;

	/*
	 * Konstrukor
	 */
	public Panel(String ititle, double ix, double iy, double iwidth, double iheight, boolean iextended, NewGUI parent) {
		this.title = ititle;
		this.x = ix;
		this.y = iy;
		this.width = iwidth;
		this.height = iheight;
		this.extended = iextended;
		this.dragging = false;
		this.visible = true;
		this.clickgui = parent;
		setup();
	}

	/*
	 * Wird in NewGUI   berschrieben, sodass auch ModuleButtons hinzugef  gt werden k  nnen :3
	 */
	public void setup() {}


	public int rgb;

	public int updateRainbow(int IN) {
		float hue2 = Color.RGBtoHSB(new Color(IN).getRed(), new Color(IN).getGreen(), new Color(IN).getBlue(), null)[0];
		hue2 += OldGui.rainbowspeed.getValue() / 1000f;
		if (hue2 > 1) hue2 -= 1;
		return Color.HSBtoRGB(hue2, Global.rainbowSaturation.getValue() / 255f, Global.rainbowLightness.getValue() / 255f);
	}

	/*
	 * Rendern des Elements.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (!this.visible)
			return;

		if (this.dragging) {
			x = x2 + mouseX;
			y = y2 + mouseY;
		}
		rgb = Xulu.rgb;
		
		Color temp = ColorUtil.getClickGUIColor();
		int outlineColor = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 200).getRGB();
		if (OldGui.rainbowgui.getValue()) {
			outlineColor = ColorUtils.changeAlpha(rgb, 200);
		}
		int trueOutline = ColorUtils.changeAlpha(outlineColor, 225);
		
		Gui.drawRect((int)x, (int)y, (int)(x + width), (int)(y + height), ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 225)); //0xff121212
		Gui.drawRect((int)x, (int)y, (int)(x + width), (int)(y + height), outlineColor);
		Gui.drawRect((int)(x + 4),(int)(y + 2), (int)(x + 4.3), (int)(y + height - 2), 0xffaaaaaa);
		if (extended)
			Gui.drawRect((int)(x),(int)(y + height - 1), (int)(x + width), (int)(y + height), ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 30));
		//if (OldGui.outline.getValue()) XuluTessellator.drawRectOutline(x - 2, y - 2, x + width + 2, y + width, x, y, x + width, y + width, ColorUtils.changeAlpha(outlineColor, 100));
		if (OldGui.outline.getValue()) {
			if (extended)
				XuluTessellator.drawRectOutline((int) x - 1, (int) y - 1, (int) (x + width) + 1, (int) (y + height), (int) x, (int) y, (int) (x + width), (int) (y + height), trueOutline);
			else
				XuluTessellator.drawRectOutline((int) x, (int) y, (int) (x + width), (int) (y + height), 1, trueOutline);
		}
		//Gui.drawRect((int)(x - 4 + width), (int)(y + 2), (int)(x - 4.3 + width), (int)(y + height - 2), 0xffaaaaaa);
		if (OldGui.customfont.getValue()) {
			Xulu.cFontRenderer.drawStringWithShadow(title, (float) (x + 4), (float) ((y + height / 2) - 4), 0xffefefef);
		}else{
			FontUtil.drawStringWithShadow(title, x + 4, y + height / 2 - 4, 0xffefefef);
		}
		/*
		if(Eclient.settingsManager.getSettingByName("Design").getValString().equalsIgnoreCase("New")){
			Gui.drawRect((int)(x - 2), (int)y, (int)x, (int)(y + height), outlineColor);
			if (Eclient.CustomFont) {
				Eclient.cFontRenderer.drawStringWithShadow(title, x + 2, (y + height / 2 - Eclient.cFontRenderer.getHeight()/2) - 1, 0xffefefef);
			}else{
				FontUtil.drawStringWithShadow(title, x + 2, y + height / 2 - FontUtil.getFontHeight()/2, 0xffefefef);
			}
		}else if(Eclient.settingsManager.getSettingByName("Design").getValString().equalsIgnoreCase("JellyLike")){
			Gui.drawRect((int)(x + 4),(int)(y + 2), (int)(x + 4.3), (int)(y + height - 2), 0xffaaaaaa);
			//Gui.drawRect((int)(x - 4 + width), (int)(y + 2), (int)(x - 4.3 + width), (int)(y + height - 2), 0xffaaaaaa);
			if (Eclient.CustomFont) {
				Eclient.cFontRenderer.drawCenteredStringWithShadow(title, (float) (x + width / 2), (float) ((y + height / 2) - 4), 0xffefefef);
			}else{
				FontUtil.drawTotalCenteredStringWithShadow(title, x + width / 2, y + height / 2, 0xffefefef);
			}
		}
		*/
		
		if (this.extended && !Elements.isEmpty()) {
			double startY = y + height;
			//int epanelcolor = Eclient.settingsManager.getSettingByName("Design").getValString().equalsIgnoreCase("New") ? 0xff232323 : Eclient.settingsManager.getSettingByName("Design").getValString().equalsIgnoreCase("JellyLike") ? 0xbb151515 : 0;;
			int epanelcolor = ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 	OldGui.bgAlpha.getValue()); // 0xbb151515
			int mcolor = ColorUtils.changeAlpha(ColorUtil.getClickGUIColor().getRGB(), 30); // 0xbb151515
			for (ModuleButton et : Elements) {
				/*
				if(Eclient.settingsManager.getSettingByName("Design").getValString().equalsIgnoreCase("New")){
					Gui.drawRect((int)(x - 2), (int)startY, (int)(x + width), (int)(startY + et.height + 1), outlineColor);
				}
				*/
				if (OldGui.rainbowgui.getValue())
					trueOutline = ColorUtils.changeAlpha(rgb = updateRainbow(rgb), 225);
				Gui.drawRect((int)x, (int)startY, (int)(x + width), (int)(startY + et.height + 1), epanelcolor);
				if (OldGui.outline.getValue()) {
					if (Elements.indexOf(et) == Elements.size() - 1 && !et.extended) { //if this is the last module and it doesn't have settings open
						XuluTessellator.drawRectOutline((int) x - 1, (int) startY, (int) (x + width) + 1, (int) (startY + et.height + 1) + 1, (int) x, (int) startY, (int) (x + width), (int) (startY + et.height + 1), trueOutline);
					} else {
						XuluTessellator.drawRectOutline((int) x - 1, (int) startY, (int) (x + width) + 1, (int) (startY + et.height + 1), (int) x, (int) startY, (int) (x + width), (int) (startY + et.height + 1), trueOutline);
					}
				}
				if (OldGui.moduleSetting.getValue().equalsIgnoreCase("MiniButton")) Gui.drawRect((int)x + 2, 	(int)startY + 1, (int)(x + width) - 2, (int)(startY + et.height), mcolor);
				et.x = x + 2;
				et.y = startY;
				et.width = width - 4;
				et.drawScreen(mouseX, mouseY, partialTicks);
				startY += et.height + 1;
				if (et.extended) {
					for (Element e : et.menuelements) {
						if (!e.set.isVisible()) continue;
						Gui.drawRect((int) x, (int) startY, (int) (x + width), (int) (startY + et.height + 1), epanelcolor);
						if (OldGui.outline.getValue()) {
							if (Elements.indexOf(et) == Elements.size() - 1 && et.menuelements.indexOf(e) == et.menuelements.size() - 1) { //if this is the last setting
								XuluTessellator.drawRectOutline((int) x - 1, (int) startY, (int) (x + width) + 1, (int) (startY + et.height + 1) + 1, (int) x, (int) startY, (int) (x + width), (int) (startY + et.height + 1), trueOutline);
							} else {
								XuluTessellator.drawRectOutline((int) x - 1, (int) startY, (int) (x + width) + 1, (int) (startY + et.height + 1), (int) x, (int) startY, (int) (x + width), (int) (startY + et.height + 1), trueOutline);
							}
						}
						startY += et.height + 1;
					}
				}
			}
			Gui.drawRect((int)x, (int)(startY + 1), (int)(x + width), (int)(startY + 1), epanelcolor);
		
		}
	}

	/*
	 * Zum Bewegen und Extenden des Panels
	 * usw.
	 */
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!this.visible) {
			return false;
		}
		if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
			x2 = this.x - mouseX;
			y2 = this.y - mouseY;
			dragging = true;
			return true;
		} else if (mouseButton == 1 && isHovered(mouseX, mouseY)) {
			extended = !extended;
			return true;
		} else if (extended) {
			for (ModuleButton et : Elements) {
				if (et.mouseClicked(mouseX, mouseY, mouseButton)) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Damit das Panel auch losgelassen werden kann 
	 */
	public void mouseReleased(int mouseX, int mouseY, int state) {
		if (!this.visible) {
			return;
		}
		if (state == 0) {
			this.dragging = false;
		}
	}

	/*
	 * HoverCheck
	 */
	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
}
