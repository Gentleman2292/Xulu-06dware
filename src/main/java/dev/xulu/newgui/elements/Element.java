package dev.xulu.newgui.elements;

import dev.xulu.newgui.NewGUI;
import dev.xulu.newgui.util.FontUtil;
import dev.xulu.settings.Value;

import java.io.IOException;

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit me
 *
 *  @author HeroCode
 */
public class Element {
	public NewGUI clickgui;
	public ModuleButton parent;
	public Value set;
	public double offset;
	public double x;
	public double y;
	public double width;
	public double height;
	
	public String setstrg;
	
	public boolean comboextended;
	
	public void setup(){
		clickgui = parent.parent.clickgui;
	}
	
	public void update(){
		/*
		 * Richtig positionieren! Offset wird von NewGUI aus bestimmt, sodass
		 * nichts ineinander gerendert wird
		 */
		//x = parent.x + parent.width + 2;
		//y = parent.y + offset;
		//width = parent.width + 10;
		//height = 15;

		x = parent.x - 2;
		y = parent.y + offset;
		width = parent.width + 4;
		height = parent.height + 1;
		
		/*
		 * Title der Box bestimmen und falls n  tig die Breite der CheckBox
		 * erweitern
		 */
		String sname = set.getName();
		if(set.isToggle()){
			setstrg = sname.substring(0, 1).toUpperCase() + sname.substring(1, sname.length());
			double textx = x + width - FontUtil.getStringWidth(setstrg);
			if (textx < x + 13) {
				width += (x + 13) - textx + 1;
			}
		}else if(set.isMode()){
			//height = comboextended ? set.getOptions().size() * (FontUtil.getFontHeight() + 2) + 15 : 15;
			
			setstrg = sname.substring(0, 1).toUpperCase() + sname.substring(1, sname.length());
			/*
			int longest = FontUtil.getStringWidth(setstrg);
			for (Object o : set.getOptions()) {
				String s;
				if (!(o instanceof String)) {
					s = o.toString();
				} else {
					s = (String) o;
				}
				int temp = FontUtil.getStringWidth(s);
				if (temp > longest) {
					longest = temp;
				}
			}
			double textx = x + width - longest;
			if (textx < x) {
				width += x - textx + 1;
			}
			*/
		}else if(set.isNumber()){
			setstrg = sname.substring(0, 1).toUpperCase() + sname.substring(1, sname.length());
			//String displayval = "" + Math.round(set.getValue() * 100D)/ 100D;
			String displaymax;
			if (set.getValue() instanceof Integer) {
				displaymax = "" + Math.round((int) set.getMax() * 100D)/ 100D;
			}
			else if (set.getValue() instanceof Short) {
				displaymax = "" + Math.round((short) set.getMax() * 100D)/ 100D;
			}
			else if (set.getValue() instanceof Long) {
				displaymax = "" + Math.round((long) set.getMax() * 100D)/ 100D;
			}
			else if (set.getValue() instanceof Float) {
				displaymax = "" + Math.round((float) set.getMax() * 100D)/ 100D;
			}
			else if (set.getValue() instanceof Double) {
				displaymax = "" + Math.round((double) set.getMax() * 100D)/ 100D;
			} else {
				displaymax = "";
			}
			double textx = x + width - FontUtil.getStringWidth(setstrg) - FontUtil.getStringWidth(displaymax) - 4;
			if (textx < x) {
				width += x - textx + 1;
			}
		}else if (set.isBind()) {
			setstrg = sname.substring(0, 1).toUpperCase() + sname.substring(1, sname.length());
		}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {}
	
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		return isHovered(mouseX, mouseY);
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {}

	public boolean keyTyped(char typedChar, int keyCode) throws IOException { return false; }
	
	public boolean isHovered(int mouseX, int mouseY) 
	{
		
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
}
