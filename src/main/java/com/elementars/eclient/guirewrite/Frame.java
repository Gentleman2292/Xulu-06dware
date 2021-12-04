package com.elementars.eclient.guirewrite;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.ModuleManager;
import com.elementars.eclient.util.Wrapper;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.newgui.util.FontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit me
 *
 *  @author HeroCode
 */
public class Frame {
	public String title;
	public double x;
	public double y;
	private double x2;
	private double y2;
	public double width;
	public double height;
	public boolean dragging;
	public boolean pinned;
	public boolean visible;
	public HUD hud;

	/*
	 * Konstrukor
	 */
	public Frame(String ititle, double ix, double iy, double iwidth, double iheight, boolean ipinned, HUD hud) {
		this.title = ititle;
		this.x = ix;
		this.y = iy;
		this.width = iwidth;
		this.height = iheight;
		this.pinned = ipinned;
		this.dragging = false;
		this.visible = true;
		this.hud = hud;
		setup();
	}

	/*
	 * Wird in NewGUI   berschrieben, sodass auch ModuleButtons hinzugef  gt werden k  nnen :3
	 */
	public void setup() {}
	int changeAlpha(int origColor, int userInputedAlpha) {
		origColor = origColor & 0x00ffffff; //drop the previous alpha value
		return (userInputedAlpha << 24) | origColor; //add the one the user inputted
	}
	/*
	 * Rendern des Elements.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.visible = ModuleManager.isModuleEnabled(this.title);
		if (!this.visible)
			return;

		if (this.dragging) {
			x = x2 + mouseX;
			y = y2 + mouseY;
			ScaledResolution sr = new ScaledResolution(Wrapper.getMinecraft());
			if (x < 0) x = 0;
			if (y < 0) y = 0;
			if (x > sr.getScaledWidth() - width) x = sr.getScaledWidth() - width;
			if (y > sr.getScaledHeight() - height) y = sr.getScaledHeight() - height;
		}
		if (Xulu.MODULE_MANAGER.getModuleByName(this.title) != null) {
			((Element)Xulu.MODULE_MANAGER.getModuleByName(this.title)).x = x;
			((Element)Xulu.MODULE_MANAGER.getModuleByName(this.title)).y = y;
		}
		if (this.dragging) {
			Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), changeAlpha(Color.lightGray.getRGB(), 100));
			Gui.drawRect((int) (x + 4), (int) (y + 2), (int) (x + 4.3), (int) (y + height - 2), changeAlpha(Color.lightGray.getRGB(), 100));
		} else {
			Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), changeAlpha(0xff121212, 100));
			Gui.drawRect((int) (x + 4), (int) (y + 2), (int) (x + 4.3), (int) (y + height - 2), changeAlpha(0xffaaaaaa, 100));
		}
		if (Xulu.MODULE_MANAGER.getModuleByName(this.title) != null) {
			Xulu.MODULE_MANAGER.getModuleByName(this.title).onRender();
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
		} else if (mouseButton == 2 && isHovered(mouseX, mouseY)) {
			((Element) Xulu.MODULE_MANAGER.getModuleByName(this.title)).onMiddleClick();
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
