package tech.mmmax.impl.components.category;

import com.elementars.eclient.module.Category;
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

import java.awt.*;

public class CategoryButton extends AbstractComponent {

    public Category category;
    MainFrame mainFrame;
    boolean selected;
    CategoryPanel categoryPanel;

    public CategoryButton(int x, int y, int height, Category category, MainFrame mainFrame, CategoryPanel categoryPanel){
        this.rect = new TurokRect(x, y, TurokFontManager.getStringWidth(FontManager.MEDIUM_LARGE.font, category.name()) + 6, height);
        this.category = category;
        this.parent = mainFrame;
        this.mainFrame = mainFrame;
        this.anchorPoint = mainFrame.anchorPoint;
        selected = false;
        this.categoryPanel = categoryPanel;
    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {
        super.draw(m, partialTicks);
        //anchorPoint = parent.rect;
        this.anchorPoint = mainFrame.anchorPoint;


        Color backgroundColor = selected ? Colors.TERTIARY_COLOR : Colors.SECONDARY_COLOR;
        TurokRenderGL.color(backgroundColor);
        TurokRenderGL.drawSolidRect(rect);

        TurokRect top = new TurokRect(rect.getX(), rect.getY(), rect.getWidth(), Metrics.SPACER_SIZE);
        TurokRect left = new TurokRect(rect.getX(), rect.getY(), Metrics.SPACER_SIZE, rect.getHeight());
        TurokRect right = new TurokRect(rect.getX() + rect.getWidth() - Metrics.SPACER_SIZE, rect.getY(), Metrics.SPACER_SIZE, rect.getHeight());
        TurokRect inside = new TurokRect(rect.getX() + Metrics.SPACER_SIZE, rect.getY() + Metrics.SPACER_SIZE, rect.getWidth() - (Metrics.SPACER_SIZE * 2), rect.getHeight() - Metrics.SPACER_SIZE);

        if (selected){
            TurokRenderGL.color(Colors.HIGHLIGHT_COLOR);
            TurokRenderGL.drawSolidRect(top);
            TurokRenderGL.drawSolidRect(left);
            TurokRenderGL.drawSolidRect(right);
            TurokRenderGL.color(Colors.LINE_COLOR);
            renderLines(inside, Metrics.LINE_SIZE);
            renderLines(rect, Metrics.LINE_SIZE);
        }

        int centerY = (rect.getHeight() - TurokFontManager.getStringHeight(FontManager.MEDIUM_LARGE.font, category.name())) / 2;
        int centerX = (rect.getWidth() - TurokFontManager.getStringWidth(FontManager.MEDIUM_LARGE.font, category.name())) / 2;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        TurokFontManager.render(FontManager.MEDIUM_LARGE.font, category.name(), rect.getX() + centerX, rect.getY() + centerY, true, Colors.FONT_COLOR);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    @Override
    public void click(TurokMouse m, int button) {
        super.click(m, button);
        TurokRect clickRect = new TurokRect(rect.getX() + 1, rect.getY(), rect.getWidth() - 2, rect.getHeight());
        if (categoryPanel.rect.collideWithMouse(m)) {

            if (clickRect.collideWithMouse(m)) {
                categoryPanel.disableAll();
                selected = true;
            }
        }

    }

    public void renderLines(TurokRect rect, float line){
        GL11.glLineWidth(line);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        TurokRenderGL.addVertex(rect.getX(), rect.getY() + rect.getHeight());
        TurokRenderGL.addVertex(rect.getX(), rect.getY());
        TurokRenderGL.addVertex(rect.getX() + rect.getWidth(), rect.getY());
        TurokRenderGL.addVertex(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
        GL11.glEnd();
    }
}
