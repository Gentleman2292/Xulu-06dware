package tech.mmmax.api.font;

import me.rina.turok.render.font.TurokFont;

import java.awt.*;

public enum FontManager {

    SMALL(new TurokFont(new Font("Arial", Font.PLAIN, 13), true, true)),
    MEDIUM_LARGE(new TurokFont(new Font("Arial", Font.PLAIN, 18), true, true)),
    LARGE(new TurokFont(new Font("Arial", Font.PLAIN, 22), true, true)),
    XLARGE(new TurokFont(new Font("Arial", Font.PLAIN, 30), true, true));

    public TurokFont font;
    FontManager(TurokFont font){
        this.font = font;
    }

}
