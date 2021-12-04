package tech.mmmax.impl.components.setting;

import me.rina.turok.render.font.TurokFont;
import me.rina.turok.util.TurokRect;
import tech.mmmax.api.component.AbstractComponent;
import tech.mmmax.api.font.FontManager;

public class AbstractSettingComponent extends AbstractComponent {

    public TurokFont font = FontManager.SMALL.font;

    public AbstractSettingComponent(int x, int y, int width, int height){
        this.rect = new TurokRect(x, y, width, height);
        draggable = false;
    }
}
