package com.elementars.eclient.font;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontInit {
    public void initFonts() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/MODERN SPACE.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, FontInit.class.getResourceAsStream("/fonts/Comfortaa-Regular.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, FontInit.class.getResourceAsStream("/fonts/GOTHIC.TTF")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, FontInit.class.getResourceAsStream("/fonts/MODERN SPACE.ttf")));
        }catch (IOException | FontFormatException e){
            e.printStackTrace();
        }
    }
}