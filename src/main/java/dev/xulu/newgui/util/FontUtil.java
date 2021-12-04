package dev.xulu.newgui.util;

import com.elementars.eclient.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StringUtils;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit me
 *
 *  @author HeroCode
 */
public class FontUtil implements Helper {

	public static int getStringWidth(String text) {
		return fontRenderer.getStringWidth(StringUtils.stripControlCodes(text));
	}

	public static int getFontHeight() {
		return fontRenderer.FONT_HEIGHT;
	}

	public static void drawString(String text, double x, double y, int color) {
		fontRenderer.drawString(text, (int)x, (int)y, color);
	}

	public static void drawStringWithShadow(String text, double x, double y, int color) {
		fontRenderer.drawStringWithShadow(text, (float) x, (float) y, color);
	}

	public static void drawCenteredString(String text, double x, double y, int color) {
		drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
	}

	public static void drawCenteredStringWithShadow(String text, double x, double y, int color) {
		drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
	}

	public static void drawTotalCenteredString(String text, double x, double y, int color) {
		drawString(text, x - fontRenderer.getStringWidth(text) / 2, y - fontRenderer.FONT_HEIGHT / 2, color);
	}

	public static void drawTotalCenteredStringWithShadow(String text, double x, double y, int color) {
		drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2, y - fontRenderer.FONT_HEIGHT / 2F, color);
	}
}
