package tech.mmmax.impl.components.category;

import com.elementars.eclient.Xulu;
import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokRect;
import org.lwjgl.opengl.GL11;
import tech.mmmax.api.color.Colors;
import tech.mmmax.api.component.AbstractComponent;
import tech.mmmax.api.component.Metrics;
import tech.mmmax.api.font.FontManager;
import tech.mmmax.impl.MainFrame;

public class TitleComponent extends AbstractComponent {

    MainFrame parent;

    public TitleComponent(int x, int y, MainFrame parent){
        rect = new TurokRect(x, y, TurokFontManager.getStringWidth(FontManager.XLARGE.font, Xulu.name) + (Metrics.BASIC_SPACING * 2), TurokFontManager.getStringHeight(FontManager.XLARGE.font, Xulu.name) + Metrics.BASIC_SPACING);
        anchorPoint = parent.anchorPoint;
        this.parent = parent;
    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {
        super.draw(m, partialTicks);
        this.anchorPoint = parent.anchorPoint;


        TurokRenderGL.color(Colors.SECONDARY_COLOR);
        TurokRenderGL.drawSolidRect(rect);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        TurokFontManager.render(FontManager.XLARGE.font, Xulu.name, rect.getX() + Metrics.BASIC_SPACING, rect.getY() + (Metrics.BASIC_SPACING / 2), true, Colors.FONT_COLOR);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
}
