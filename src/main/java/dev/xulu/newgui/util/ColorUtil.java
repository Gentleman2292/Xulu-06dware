package dev.xulu.newgui.util;

import com.elementars.eclient.module.render.OldGui;

import java.awt.*;

//Deine Imports

/**
 *  Made by HeroCode
 *  it's free to use
 *  but you have to credit me
 *
 *  @author HeroCode
 */
public class ColorUtil {
	
	public static Color getClickGUIColor(){
		return new Color((int) OldGui.red.getValue(), (int)OldGui.green.getValue(), (int)OldGui.blue.getValue());
	}
}
