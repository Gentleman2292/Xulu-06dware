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
import net.minecraft.util.math.MathHelper;

import java.awt.*;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit me
 *
 *  @author HeroCode
 */
public class ElementSlider extends Element {
	public boolean dragging;

	/*
	 * Konstrukor
	 */
	public ElementSlider(ModuleButton iparent, Value iset) {
		parent = iparent;
		set = iset;
		dragging = false;
		super.setup();
	}

	/*
	 * Rendern des Elements 
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (OldGui.sliderSetting.getValue().equalsIgnoreCase("Line")) {
			String displayval;
			if (set.getValue() instanceof Integer) {
				displayval = "" + Math.round((int) set.getValue() * 100D) / 100D;
			}
			else if (set.getValue() instanceof Short) {
				displayval = "" + Math.round((short) set.getValue() * 100D) / 100D;
			}
			else if (set.getValue() instanceof Long) {
				displayval = "" + Math.round((long) set.getValue() * 100D) / 100D;
			}
			else if (set.getValue() instanceof Float) {
				displayval = "" + Math.round((float) set.getValue() * 100D) / 100D;
			}
			else if (set.getValue() instanceof Double) {
				displayval = "" + Math.round((double) set.getValue() * 100D) / 100D;
			} else {
				displayval = "";
			}
			boolean hoveredORdragged = isSliderHovered(mouseX, mouseY) || dragging;

			Color temp = ColorUtil.getClickGUIColor();
			if (OldGui.rainbowgui.getValue()) {
				temp = (new Color(Xulu.rgb)).darker();
			}
			int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), hoveredORdragged ? 225 : 225).getRGB();
			int color2 = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), hoveredORdragged ? 225 : 225).getRGB();

			//selected = iset.getValDouble() / iset.getMax();
			double percentBar;
			if (set.getValue() instanceof Integer) {
				double value = (int) set.getValue();
				percentBar = (value - (int)set.getMin()) / ((int)set.getMax() - (int)set.getMin());
			}
			else if (set.getValue() instanceof Short) {
				double value = (short) set.getValue();
				percentBar = (value - (short)set.getMin()) / ((short)set.getMax() - (short)set.getMin());
			}
			else if (set.getValue() instanceof Long) {
				double value = (long) set.getValue();
				percentBar = (value - (long)set.getMin()) / ((long)set.getMax() - (long)set.getMin());
			}
			else if (set.getValue() instanceof Float) {
				percentBar = ((float) set.getValue() - (float)set.getMin()) / ((float)set.getMax() - (float)set.getMin());
			}
			else if (set.getValue() instanceof Double) {
				percentBar = ((double) set.getValue() - (double)set.getMin()) / ((double)set.getMax() - (double)set.getMin());
			} else {
				percentBar = 0;
			}

			/*
			 * Die Box und Umrandung rendern
			 */
			Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 30)); //0xff1a1a1a

			/*
			 * Den Text rendern
			 */
			if (OldGui.customfont.getValue()) {
				Xulu.cFontRenderer.drawString(setstrg, (float) (x + 2), (float) (y + 2), 0xffffffff);
				Xulu.cFontRenderer.drawString(displayval, (float) (x + width - Xulu.cFontRenderer.getStringWidth(displayval)), (float) (y + 2), 0xffffffff);
			} else {
				FontUtil.drawString(setstrg, x + 2, y + 2, 0xffffffff);
				FontUtil.drawString(displayval, x + width - FontUtil.getStringWidth(displayval), y + 2, 0xffffffff);
			}

			/*
			 * Den Slider rendern
			 */
			Gui.drawRect((int) x, (int) (y + 12), (int) (x + width), (int) (y + 13.5), ColorUtils.changeAlpha(0xff101010, 30));
			Gui.drawRect((int) x, (int) (y + 12), (int) (x + (percentBar * width)), (int) (y + 13.5), color);

			if (percentBar > 0 && percentBar < 1)
				Gui.drawRect((int) (x + (percentBar * width) - 1), (int) (y + 12), (int) (x + Math.min((percentBar * width), width)), (int) (y + 13.5), color2);


			/*
			 * Neue Value berechnen, wenn dragging
			 */
			if (this.dragging) {
				Double val;
				if (set.getValue() instanceof Integer) {
					int diff = (int) set.getMax() - (int) set.getMin();
					val= (int) set.getMin() + (MathHelper.clamp((mouseX - x) / width, 0, 1)) * diff;
				}else if (set.getValue() instanceof Short) {
					short diff = (short) ((short) set.getMax() - (short) set.getMin());
					val= (short) set.getMin() + (MathHelper.clamp((mouseX - x) / width, 0, 1)) * diff;
				} else if (set.getValue() instanceof Long) {
					long diff = (long) set.getMax() - (long) set.getMin();
					val= (long) set.getMin() + (MathHelper.clamp((mouseX - x) / width, 0, 1)) * diff;
				} else if (set.getValue() instanceof Float) {
					float diff = (float) set.getMax() - (float) set.getMin();
					val = (float) set.getMin() + (MathHelper.clamp((mouseX - x) / width, 0, 1)) * diff;
				} else if (set.getValue() instanceof Double) {
					double diff = (double) set.getMax() - (double) set.getMin();
					val = (double) set.getMin() + (MathHelper.clamp((mouseX - x) / width, 0, 1)) * diff;
				} else {
					val = 0d;
				}

				Number type;
				if (set.getValue() instanceof Integer) {
					type = val.intValue();
				} else if (set.getValue() instanceof Short) {
					type = val.shortValue();
				} else if (set.getValue() instanceof Long) {
					type = val.longValue();
				} else if (set.getValue() instanceof Float) {
					type = val.floatValue();
				} else if (set.getValue() instanceof Double) {
					type = val.doubleValue();
				} else {
					type = 0;
				}
				set.setValue(type);
			}
		}
		else if (OldGui.sliderSetting.getValue().equalsIgnoreCase("Box")) {
			String displayval;
			if (set.getValue() instanceof Integer) {
				displayval = "" + Math.round((int)set.getValue() * 100D) / 100D;
			}
			else if (set.getValue() instanceof Short) {
				displayval = "" + Math.round((short)set.getValue() * 100D) / 100D;
			}
			else if (set.getValue() instanceof Long) {
				displayval = "" + Math.round((long)set.getValue() * 100D) / 100D;
			}
			else if (set.getValue() instanceof Float) {
				displayval = "" + Math.round((float)set.getValue() * 100D) / 100D;
			}
			else if (set.getValue() instanceof Double) {
				displayval = "" + Math.round((double)set.getValue() * 100D) / 100D;
			} else {
				displayval = "";
			}
			boolean hoveredORdragged = (OldGui.sliderSetting.getValue().equalsIgnoreCase("Line") ? isSliderHovered(mouseX, mouseY) : isHovered(mouseX, mouseY)) || dragging;

			Color temp = ColorUtil.getClickGUIColor().darker();
			if (OldGui.rainbowgui.getValue()) {
				temp = (new Color(Xulu.rgb).darker());
			}
			int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), hoveredORdragged ? 50 : 30).getRGB();
			int color2 = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), hoveredORdragged ? 255 : 230).getRGB();

			//selected = iset.getValDouble() / iset.getMax();
			double percentBar;
			if (set.getValue() instanceof Integer) {
				double value = (int) set.getValue();
				percentBar = (value - (int) set.getMin()) / ((int) set.getMax() - (int) set.getMin());
			}
			else if (set.getValue() instanceof Short) {
				double value = (short) set.getValue();
				percentBar = (value - (short) set.getMin()) / ((short) set.getMax() - (short) set.getMin());
			}
			else if (set.getValue() instanceof Long) {
				double value = (long) set.getValue();
				percentBar = (value - (long) set.getMin()) / ((long) set.getMax() - (long) set.getMin());
			}
			else if (set.getValue() instanceof Float) {
				percentBar = ((float)set.getValue() - (float)set.getMin()) / ((float)set.getMax() - (float)set.getMin());
			}
			else if (set.getValue() instanceof Double) {
				percentBar = ((double)set.getValue() - (double)set.getMin()) / ((double)set.getMax() - (double)set.getMin());
			} else {
				percentBar = 0;
			}

			/*
			 * Die Box und Umrandung rendern
			 */
			Gui.drawRect((int) (x + (percentBar * width)), (int) y, (int) (x + width), (int) (y + height), ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 60)); //0xff1a1a1a

			/*
			 * Den Text rendern
			 */

			/*
			 * Den Slider rendern
			 */
			//Gui.drawRect((int) x, (int) (y + 12), (int) (x + width), (int) (y + 13.5), 0xff101010);
			Gui.drawRect((int) x, (int) (y), (int) (x + (percentBar * width)), (int) (y + height), ColorUtils.changeAlpha(color, 225));

			//if (percentBar > 0 && percentBar < 1)
				//Gui.drawRect((int) (x + (percentBar * width) - 1), (int) (y), (int) (x + Math.min((percentBar * width), width)), (int) (y + height), color2);

			if (OldGui.customfont.getValue()) {
				Xulu.cFontRenderer.drawStringWithShadow(setstrg, (float) (x + 2), (float) (y + 2), 0xffffffff);
				Xulu.cFontRenderer.drawStringWithShadow(displayval, (float) (x + width - Xulu.cFontRenderer.getStringWidth(displayval)), (float) (y + 2), 0xffffffff);
			} else {
				FontUtil.drawStringWithShadow(setstrg, x + 2, y + 2, 0xffffffff);
				FontUtil.drawStringWithShadow(displayval, x + width - FontUtil.getStringWidth(displayval), y + 2, 0xffffffff);
			}

			/*
			 * Neue Value berechnen, wenn dragging
			 */
			if (this.dragging) {
				Double val;
				if (set.getValue() instanceof Integer) {
					int diff = (int) set.getMax() - (int) set.getMin();
					val= (int) set.getMin() + (MathHelper.clamp((mouseX - x) / width, 0, 1)) * diff;
				}else if (set.getValue() instanceof Short) {
					short diff = (short) ((short) set.getMax() - (short) set.getMin());
					val= (short) set.getMin() + (MathHelper.clamp((mouseX - x) / width, 0, 1)) * diff;
				} else if (set.getValue() instanceof Long) {
					long diff = (long) set.getMax() - (long) set.getMin();
					val= (long) set.getMin() + (MathHelper.clamp((mouseX - x) / width, 0, 1)) * diff;
				} else if (set.getValue() instanceof Float) {
					float diff = (float) set.getMax() - (float) set.getMin();
					val = (float) set.getMin() + (MathHelper.clamp((mouseX - x) / width, 0, 1)) * diff;
				} else if (set.getValue() instanceof Double) {
					double diff = (double) set.getMax() - (double) set.getMin();
					val = (double) set.getMin() + (MathHelper.clamp((mouseX - x) / width, 0, 1)) * diff;
				} else {
					val = 0d;
				}

				Number type;
				if (set.getValue() instanceof Integer) {
					type = val.intValue();
				} else if (set.getValue() instanceof Short) {
					type = val.shortValue();
				} else if (set.getValue() instanceof Long) {
					type = val.longValue();
				} else if (set.getValue() instanceof Float) {
					type = val.floatValue();
				} else if (set.getValue() instanceof Double) {
					type = val.doubleValue();
				} else {
					type = 0;
				}
				set.setValue(type);
			}
		}
	}

	/*
	 * 'true' oder 'false' bedeutet hat der Nutzer damit interagiert und
	 * sollen alle anderen Versuche der Interaktion abgebrochen werden?
	 */
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && (OldGui.sliderSetting.getValue().equalsIgnoreCase("Line") ? isSliderHovered(mouseX, mouseY) : isHovered(mouseX, mouseY))) {
			this.dragging = true;
			return true;
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/*
	 * Wenn die Maus losgelassen wird soll aufgeh  rt werden die Slidervalue zu ver  ndern
	 */
	public void mouseReleased(int mouseX, int mouseY, int state) {
		this.dragging = false;
	}

	/*
	 * Einfacher HoverCheck, ben  tigt damit dragging auf true gesetzt werden kann
	 */
	public boolean isSliderHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y + 11 && mouseY <= y + 14;
	}
}