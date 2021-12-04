package tech.mmmax.api.component.custom;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.TurokFont;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.util.TurokRect;

import org.lwjgl.opengl.GL11;
import tech.mmmax.api.color.Colors;
import tech.mmmax.api.component.AbstractComponent;
import tech.mmmax.api.font.FontManager;

public class AbstractButton extends AbstractComponent {

    public String disabledText;
    public String enabledText;
    public boolean enabled;

    public TurokFont font;


    public AbstractButton(int x, int y, int width, int height, String disabledText, String enabledText){
        this.rect = new TurokRect(x, y, width, height);
        this.disabledText = disabledText;
        this.enabledText = enabledText;
        this.enabled = false;

        font = FontManager.MEDIUM_LARGE.font;

    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {

        renderButton();
        super.draw(m, partialTicks);

    }

    public void renderButton(){
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        if (enabled) {
            int centerY = (rect.getHeight() - TurokFontManager.getStringHeight(font, enabledText)) / 2;
            int centerX = (rect.getWidth() - TurokFontManager.getStringWidth(font, enabledText)) / 2;

            TurokFontManager.render(font, enabledText, rect.getX() + centerX, rect.getY() + centerY, true, Colors.FONT_COLOR);
        } else {
            int centerY = (rect.getHeight() - TurokFontManager.getStringHeight(font, disabledText)) / 2;
            int centerX = (rect.getWidth() - TurokFontManager.getStringWidth(font, disabledText)) / 2;

            TurokFontManager.render(font, disabledText, rect.getX() + centerX, rect.getY() + centerY, true, Colors.FONT_COLOR);
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    @Override
    public void click(TurokMouse m, int button) {
        super.click(m, button);
        if (rect.collideWithMouse(m)){
            enabled = !enabled;
        }
    }
}
