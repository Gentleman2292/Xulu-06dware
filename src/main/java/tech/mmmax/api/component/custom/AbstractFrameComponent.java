package tech.mmmax.api.component.custom;

import com.elementars.eclient.module.render.NewGui;
import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.TurokFont;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokRect;
import org.lwjgl.opengl.GL11;
import tech.mmmax.api.color.Colors;
import tech.mmmax.api.component.Metrics;
import tech.mmmax.api.font.FontManager;

import java.awt.*;

public class AbstractFrameComponent extends AbstractOutlined {


    public int height;




    Color outside;
    Color bgColor;

    public TurokFont font;

    public AbstractFrameComponent(int x, int y, int width, int height){
        this.rect = new TurokRect(x, y, width, height);
        header = new TurokRect(rect.getX(), rect.getY(), rect.getWidth(), Metrics.SPACER_SIZE);
        headerL = new TurokRect(rect.getX(), rect.getY(), Metrics.SPACER_SIZE, rect.getHeight());
        headerR = new TurokRect(rect.getX() + rect.getWidth() - Metrics.SPACER_SIZE, rect.getY(), Metrics.SPACER_SIZE, rect.getHeight());
        headerB = new TurokRect(rect.getX(), rect.getY() + rect.getHeight() - Metrics.SPACER_SIZE, rect.getWidth(), Metrics.SPACER_SIZE);
        insideRect = new TurokRect(rect.getX() + Metrics.SPACER_SIZE, rect.getY() + Metrics.SPACER_SIZE, rect.getWidth() - (Metrics.SPACER_SIZE * 2), rect.getHeight() - (Metrics.SPACER_SIZE * 2));

        background = new TurokRect(rect.getX(), rect.getY() + header.getHeight(), rect.getWidth(), rect.getHeight() - header.getHeight());


        font = FontManager.SMALL.font;

        this.height = height;

   //     updateAnchor = true;

   //     anchorPoint = this.rect;

        this.solid = true;
    }



    public void init(int x, int y, int width, int height){
        this.rect = new TurokRect(x, y, width, height);
    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {
        super.draw(m, partialTicks);

//        anchorPoint = this.rect;

        outside = Colors.HIGHLIGHT_COLOR;
        bgColor = Colors.BACKGROUND_COLOR;

        header = new TurokRect(rect.getX(), rect.getY(), rect.getWidth(), Metrics.SPACER_SIZE);
        headerL = new TurokRect(rect.getX(), rect.getY(), Metrics.SPACER_SIZE, rect.getHeight());
        headerR = new TurokRect(rect.getX() + rect.getWidth() - Metrics.SPACER_SIZE, rect.getY(), Metrics.SPACER_SIZE, rect.getHeight());
        headerB = new TurokRect(rect.getX(), rect.getY() + rect.getHeight() - Metrics.SPACER_SIZE, rect.getWidth(), Metrics.SPACER_SIZE);
        insideRect = new TurokRect(rect.getX() + Metrics.SPACER_SIZE, rect.getY() + Metrics.SPACER_SIZE, rect.getWidth() - (Metrics.SPACER_SIZE * 2), rect.getHeight() - (Metrics.SPACER_SIZE * 2));

        background = new TurokRect(rect.getX(), rect.getY() + header.getHeight(), rect.getWidth(), rect.getHeight() - header.getHeight());

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        TurokRenderGL.color(bgColor);
        TurokRenderGL.drawSolidRect(background);
        TurokRenderGL.color(outside);
        TurokRenderGL.drawSolidRect(header);
        TurokRenderGL.drawSolidRect(headerR);
        TurokRenderGL.drawSolidRect(headerL);
        TurokRenderGL.drawSolidRect(headerB);

        TurokRenderGL.color(Colors.LINE_COLOR);
        GL11.glLineWidth(Metrics.LINE_SIZE);
        TurokRenderGL.drawOutlineRect(rect);
        TurokRenderGL.drawOutlineRect(insideRect);

        if (NewGui.lineRainbow.getValue()) {
            renderRainbowLine(insideRect.getX() + 2, (int) (insideRect.getY() + Metrics.LINE_SIZE_BIG), insideRect.getWidth() - 4, Metrics.LINE_SIZE_BIG);
        } else {
            renderGradientLine(insideRect.getX() + 2, (int) (insideRect.getY() + Metrics.LINE_SIZE_BIG), insideRect.getWidth() - 4, Metrics.LINE_SIZE_BIG, new Color(NewGui.redL.getValue(), NewGui.greenL.getValue(), NewGui.blueL.getValue()), new Color(NewGui.redR.getValue(), NewGui.greenR.getValue(), NewGui.blueR.getValue()));
        }
        renderName();

    }

    public void renderName(){
        GL11.glEnable(GL11.GL_TEXTURE_2D);



        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    @Override
    public void click(TurokMouse m, int button) {
        if (rect.collideWithMouse(m)) {

            super.click(m, button);
        }

    }

    @Override
    public void release(TurokMouse m, int state) {
        super.release(m, state);
    }

    @Override
    public void key(int keyTyped, char character) {
        super.key(keyTyped, character);
    }

    public static void renderRainbowLine(int x, int y, int width, float linWidth){
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(linWidth);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        float sat = NewGui.rainbowSat.getValue();
        float bri = NewGui.rainbowBri.getValue();
        int steps = 20;
        for (int i = 0; i <= steps; i++){
            float hue = ((float) i) / ((float) steps);
            Color vertexColor = Color.getHSBColor(hue, sat, bri);
            TurokRenderGL.color(vertexColor);
            TurokRenderGL.addVertex(x +((((float) width) / ((float) steps)) * ((float) i)), y);
        }
        GL11.glEnd();
    }

    public static void renderGradientLine(int x, int y, int width, float linWidth, Color left, Color right){
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(linWidth);
        GL11.glBegin(GL11.GL_LINES);
        TurokRenderGL.color(left);
        TurokRenderGL.addVertex(x, y);
        TurokRenderGL.color(right);
        TurokRenderGL.addVertex(x + width, y);
        GL11.glEnd();
    }
}
