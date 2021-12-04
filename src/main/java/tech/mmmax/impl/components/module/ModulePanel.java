package tech.mmmax.impl.components.module;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.ModuleManager;
import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokRect;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import tech.mmmax.api.color.Colors;
import tech.mmmax.api.component.AbstractComponent;
import tech.mmmax.api.component.Metrics;
import tech.mmmax.api.component.features.AbstractScrollable;
import tech.mmmax.impl.MainFrame;
import tech.mmmax.impl.components.ComponentManager;
import tech.mmmax.impl.components.category.CategoryButton;

import java.util.ArrayList;

public class ModulePanel extends AbstractScrollable {

    CategoryButton categoryButton;
    MainFrame parent;

    ArrayList<ModuleButton> moduleButtons;

    public ModulePanel(int x, int y, int width, int height, MainFrame parent){
        super(x, y, width, height, 0);
        //rect = new TurokRect(x, y, width, height);
        this.parent = parent;
        this.anchorPoint = parent.anchorPoint;
        categoryButton = parent.categoryPanel.getSelectedCategoryButton();
    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {


        super.draw(m, partialTicks);
        this.anchorPoint = parent.anchorPoint;
        if (categoryButton != null){
            drawOutline(rect);
        }
        if (parent.categoryPanel.getSelectedCategoryButton() != null && categoryButton != null) {

            if (parent.categoryPanel.getSelectedCategoryButton().category != categoryButton.category) {
                if (parent.categoryPanel.getSelectedCategoryButton() != null) {
                    categoryButton = parent.categoryPanel.getSelectedCategoryButton();
                    onSwitchCategories(categoryButton);
                }
            }
        } else {
            categoryButton = parent.categoryPanel.getSelectedCategoryButton();
            onSwitchCategories(categoryButton);
        }



        if (moduleButtons != null){
            int currentXIndex = 0;
            int currentY1 = 0;
            int currentY2 = 0;
            for (ModuleButton mb : moduleButtons){
                boolean newLine = currentXIndex >= Metrics.MODULE_COLUMNS;
                int modWidth = (rect.getWidth() - Metrics.BASIC_SPACING * 2) / Metrics.MODULE_COLUMNS;
                if (newLine) {
                   // currentY1 += Metrics.MODULE_HEIGHT;
                    currentXIndex = 0;
                }
                mb.rect.setX(rect.getX() + (currentXIndex * modWidth) + Metrics.BASIC_SPACING);
                mb.rect.setY(scrollableContainer.getY() + (currentXIndex > 0 ? currentY2 : currentY1));
                if (currentXIndex > 0){
                    currentY2 += mb.getHeight();
                } else {
                    currentY1 += mb.getHeight();

                }

                mb.draw(m, partialTicks);
                currentXIndex ++;
            }
           // scrollableContainer.setHeight(currentY + Metrics.MODULE_HEIGHT);

        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();

        for (ModuleButton moduleButton : moduleButtons){
            moduleButton.renderSettings(m, partialTicks);
        }
    }

    public void onSwitchCategories(CategoryButton newCat){
        moduleButtons = new ArrayList<>();
        int currentXIndex = 0;
        int currentY1 = 0;
        int currentY2 = 0;


        for (Module module : Xulu.MODULE_MANAGER.getModules()) {
            if (newCat != null) {
                if (module.getCategory() == newCat.category) {
                    boolean newLine = currentXIndex >= Metrics.MODULE_COLUMNS;
                    int modWidth = (rect.getWidth() - Metrics.BASIC_SPACING * 2) / Metrics.MODULE_COLUMNS;
                    if (newLine) {
                        currentXIndex = 0;
                    }
                    ModuleButton mb = new ModuleButton(rect.getX() + (currentXIndex * modWidth) + Metrics.BASIC_SPACING, rect.getY() + (currentXIndex > 0 ? currentY2 : currentY1), modWidth, Metrics.MODULE_HEIGHT, module, parent);
                    if (currentXIndex > 0){
                        currentY2 += mb.getHeight();
                    } else {
                        currentY1 += mb.getHeight();

                    }
                    moduleButtons.add(mb);
                    currentXIndex++;

                }
            }
        }
        int heighest = Math.max(currentY2, currentY1);
        scrollableContainer.setHeight(heighest);
        resetScroll();

    }

    public void drawOutline(TurokRect panel){
        TurokRect left = new TurokRect(panel.getX(), panel.getY(), Metrics.SPACER_SIZE, panel.getHeight());
        TurokRect right = new TurokRect(panel.getX() + panel.getWidth() - Metrics.BASIC_SPACING, panel.getY(), Metrics.SPACER_SIZE, panel.getHeight());
        TurokRect bottom = new TurokRect(panel.getX(), panel.getY() + panel.getHeight() - Metrics.BASIC_SPACING, panel.getWidth(), Metrics.BASIC_SPACING);
        int l = categoryButton.rect.getX() - panel.getX();
        TurokRect topLeft = new TurokRect(panel.getX(), panel.getY(), l + Metrics.BASIC_SPACING, Metrics.BASIC_SPACING);
        int l2 = (panel.getX() + panel.getWidth()) - (topLeft.getX() + l + categoryButton.rect.getWidth());
        TurokRect topRight = new TurokRect(categoryButton.rect.getX() + categoryButton.rect.getWidth() - Metrics.BASIC_SPACING, panel.getY(), l2 + Metrics.BASIC_SPACING, Metrics.BASIC_SPACING);
        TurokRenderGL.color(Colors.HIGHLIGHT_COLOR);
        TurokRenderGL.drawSolidRect(left);
        TurokRenderGL.drawSolidRect(right);
        TurokRenderGL.drawSolidRect(bottom);
        TurokRenderGL.drawSolidRect(topLeft);
        TurokRenderGL.drawSolidRect(topRight);

        GL11.glLineWidth(Metrics.LINE_SIZE);
        TurokRenderGL.color(Colors.LINE_COLOR);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        TurokRenderGL.addVertex(topLeft.getX() + topLeft.getWidth(), topLeft.getY());
        TurokRenderGL.addVertex(topLeft.getX() + topLeft.getWidth(), topLeft.getY() + topLeft.getHeight());
        TurokRenderGL.addVertex(topLeft.getX() + Metrics.BASIC_SPACING, topLeft.getY() + topLeft.getHeight());
        TurokRenderGL.addVertex(topLeft.getX() + Metrics.BASIC_SPACING, left.getY() + left.getHeight() - Metrics.BASIC_SPACING);
        TurokRenderGL.addVertex(bottom.getX() + bottom.getWidth() - Metrics.BASIC_SPACING, left.getY() + left.getHeight() - Metrics.BASIC_SPACING);
        TurokRenderGL.addVertex(bottom.getX() + bottom.getWidth() - Metrics.BASIC_SPACING, left.getY() + Metrics.BASIC_SPACING);
        TurokRenderGL.addVertex(topRight.getX(), left.getY() + topRight.getHeight());
        TurokRenderGL.addVertex(topRight.getX(), left.getY());
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_STRIP);
        TurokRenderGL.addVertex(topLeft.getX() + topLeft.getWidth() - Metrics.BASIC_SPACING, topLeft.getY());
        TurokRenderGL.addVertex(topLeft.getX(), topLeft.getY());
        TurokRenderGL.addVertex(topLeft.getX(), left.getY() + left.getHeight());
        TurokRenderGL.addVertex(bottom.getX() + bottom.getWidth(), left.getY() + left.getHeight());
        TurokRenderGL.addVertex(bottom.getX() + bottom.getWidth(), left.getY());
        TurokRenderGL.addVertex(topRight.getX() + Metrics.BASIC_SPACING, left.getY());
        GL11.glEnd();
    }

    @Override
    public void click(TurokMouse m, int button) {
        super.click(m, button);
        if (moduleButtons != null){
            if (rect.collideWithMouse(m)) {
                for (ModuleButton mb : moduleButtons) {
                    mb.click(m, button);
                }
            }
            for (ModuleButton mb : moduleButtons) {
                mb.clickSettings(m, button);
            }
        }
    }

    @Override
    public void release(TurokMouse m, int state) {
        super.release(m, state);
        if (moduleButtons != null){
            for (ModuleButton mb : moduleButtons){
                mb.release(m, state);
            }
        }
    }

    @Override
    public void key(int keyTyped, char character) {
        super.key(keyTyped, character);
        if (moduleButtons != null){
            for (ModuleButton mb : moduleButtons){
                mb.key(keyTyped, character);
            }
        }
    }
}
