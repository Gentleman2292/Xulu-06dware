package tech.mmmax.impl;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokDisplay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import tech.mmmax.impl.components.ComponentManager;

import java.io.IOException;

public class ClickGuiCSGO extends GuiScreen {

    public static ClickGuiCSGO INSTANCE = new ClickGuiCSGO();

    public static MainFrame MAIN_FRAME = new MainFrame(100, 100, 600, 300);

    public TurokDisplay display;
    public TurokMouse mouse;

    public ClickGuiCSGO(){
        TurokRenderGL.init();
        MAIN_FRAME = new MainFrame(100, 100, 600, 300);
        ComponentManager.INSTANCE.addComponent(MAIN_FRAME);

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        TurokMouse mouse = new TurokMouse(mouseX, mouseY);

        this.display = new TurokDisplay(mc);
        this.mouse = new TurokMouse(mouseX, mouseY);

        TurokRenderGL.init(this.display);
        TurokRenderGL.init(this.mouse);

        TurokRenderGL.autoScale();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        //MAIN_FRAME.draw(mouse, partialTicks);
        ComponentManager.INSTANCE.draw(mouse, partialTicks);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.color(1, 1, 1);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        mouse = new TurokMouse(mouseX, mouseY);
        ComponentManager.INSTANCE.click(mouse, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        mouse = new TurokMouse(mouseX, mouseY);
        ComponentManager.INSTANCE.release(mouse, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        ComponentManager.INSTANCE.key(keyCode, typedChar);
    }
}
