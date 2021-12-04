package tech.mmmax.api.component.features;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokRect;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import tech.mmmax.api.component.AbstractComponent;
import tech.mmmax.api.component.Metrics;

public class AbstractScrollable extends AbstractComponent {

    int scroll;
    public TurokRect scrollableContainer;
    public TurokRect scissor;

    int speed = 13;

    public AbstractScrollable(int x, int y, int width, int height, int scrollableHeight){
        rect = new TurokRect(x, y, width, height);
        scissor = new TurokRect(rect.getX(), rect.getY() + 1, rect.getWidth() - 2, rect.getHeight() - Metrics.BASIC_SPACING);
        scroll = 0;
        scrollableContainer = new TurokRect(x, y + scroll, width, scrollableHeight);
    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {
        super.draw(m, partialTicks);
        scissor = new TurokRect(rect.getX(), rect.getY() + 1, rect.getWidth() - 2, rect.getHeight() - Metrics.BASIC_SPACING * 2);

        GlStateManager.pushMatrix();

        TurokRenderGL.drawScissor(scissor);

        int dWheel = 0;

        dWheel = Mouse.getDWheel();

        int scrollCalc = dWheel > 0 ? speed : dWheel < 0 ? -speed : 0;

        if (rect.collideWithMouse(m)) {
            scroll += scrollCalc;

            if (scrollableContainer.getY() + scrollableContainer.getHeight() + scrollCalc > rect.getY() + rect.getHeight() && (scrollableContainer.getY() + scrollCalc <= rect.getY())) {

                scroll += scrollCalc;
            }


        }

        scrollableContainer.setY(rect.getY() + scroll);

        scrollableContainer.setWidth(rect.getWidth());
        scrollableContainer.setX(rect.getX());

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public void resetScroll(){
        scroll = 0;
    }
}
