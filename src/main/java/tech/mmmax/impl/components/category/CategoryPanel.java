package tech.mmmax.impl.components.category;

import com.elementars.eclient.module.Category;
import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.util.TurokRect;
import tech.mmmax.api.component.AbstractComponent;
import tech.mmmax.api.component.Metrics;
import tech.mmmax.impl.MainFrame;
import tech.mmmax.impl.components.ComponentManager;
import tech.mmmax.impl.components.category.CategoryButton;
import tech.mmmax.impl.components.category.TitleComponent;

import java.util.ArrayList;

public class CategoryPanel extends AbstractComponent {

    MainFrame parent;

    TitleComponent title;

    ArrayList<CategoryButton> categoryButtons;

    public CategoryPanel(int x, int y, int width, MainFrame parent){
        title = new TitleComponent(x, y, parent);
        rect = new TurokRect(x, y, width, title.getHeight());
        this.parent = parent;
        anchorPoint = parent.anchorPoint;

        categoryButtons = new ArrayList<>();
       // ComponentManager.INSTANCE.addComponent(title);
        int offset = 0;
        for (Category category : Category.values()){
            CategoryButton button = new CategoryButton(x + title.rect.getWidth() + offset, y, title.getHeight(), category, parent, this);
            categoryButtons.add(button);
           // ComponentManager.INSTANCE.addComponent(button);
            offset += button.rect.getWidth();
        }
        rect.width = title.rect.getWidth() + offset;


        this.parent.rect.setWidth(rect.getWidth());
    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {
        super.draw(m, partialTicks);
        title.draw(m, partialTicks);
        this.anchorPoint = parent.anchorPoint;


        int offset = 0;
        for (CategoryButton button : categoryButtons){
            offset += button.rect.getWidth();
           // button.rect.setX(rect.getX() + title.rect.getWidth() + offset);
           // button.rect.setY(rect.y);
            button.draw(m, partialTicks);
        }
        rect.width = title.rect.getWidth() + offset;

        this.parent.rect.setWidth(rect.getWidth() + (Metrics.BASIC_SPACING * 4));

    }

    @Override
    public void click(TurokMouse m, int button) {
        for (CategoryButton categoryButton : categoryButtons){
         //   if (rect.collideWithMouse(m)){
            //    categoryButton.selected = false;
                categoryButton.click(m, button);
        //    }
        }
        super.click(m, button);
        title.click(m, button);

    }

    @Override
    public void release(TurokMouse m, int state) {
        super.release(m, state);
        title.release(m, state);
        for (CategoryButton categoryButton : categoryButtons){
            categoryButton.release(m, state);
        }
    }

    public CategoryButton getSelectedCategoryButton(){
        for (CategoryButton categoryButton : categoryButtons){
            if (categoryButton.selected){
                return categoryButton;
            }
        }
        return null;
    }

    public void disableAll(){
        for (CategoryButton categoryButton : categoryButtons){
            categoryButton.selected = false;
        }
    }
}
