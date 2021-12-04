package tech.mmmax.api.component.custom;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokRect;
import org.lwjgl.opengl.GL11;
import tech.mmmax.api.color.Colors;
import tech.mmmax.api.component.AbstractComponent;
import tech.mmmax.api.component.Metrics;

public class AbstractOutlined extends AbstractComponent {

    public TurokRect header;
    public TurokRect headerL;
    public TurokRect headerR;
    public TurokRect headerB;
    public TurokRect background;
    public TurokRect insideRect;
    @Override
    public void draw(TurokMouse m, float partialTicks) {
        super.draw(m, partialTicks);


        header = new TurokRect(rect.getX(), rect.getY(), rect.getWidth(), Metrics.SPACER_SIZE);
        headerL = new TurokRect(rect.getX(), rect.getY(), Metrics.SPACER_SIZE, rect.getHeight());
        headerR = new TurokRect(rect.getX() + rect.getWidth() - Metrics.SPACER_SIZE, rect.getY(), Metrics.SPACER_SIZE, rect.getHeight());
        headerB = new TurokRect(rect.getX(), rect.getY() + rect.getHeight() - Metrics.SPACER_SIZE, rect.getWidth(), Metrics.SPACER_SIZE);
        insideRect = new TurokRect(rect.getX() + Metrics.SPACER_SIZE, rect.getY() + Metrics.SPACER_SIZE, rect.getWidth() - (Metrics.SPACER_SIZE * 2), rect.getHeight() - (Metrics.SPACER_SIZE * 2));

        background = new TurokRect(rect.getX(), rect.getY() + header.getHeight(), rect.getWidth(), rect.getHeight() - header.getHeight());

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        TurokRenderGL.color(Colors.BACKGROUND_COLOR);
        TurokRenderGL.drawSolidRect(background);
        TurokRenderGL.color(Colors.HIGHLIGHT_COLOR);
        TurokRenderGL.drawSolidRect(header);
        TurokRenderGL.drawSolidRect(headerR);
        TurokRenderGL.drawSolidRect(headerL);
        TurokRenderGL.drawSolidRect(headerB);

        TurokRenderGL.color(Colors.LINE_COLOR);
        GL11.glLineWidth(Metrics.LINE_SIZE);
        TurokRenderGL.drawOutlineRect(rect);
        TurokRenderGL.drawOutlineRect(insideRect);
    }
}
