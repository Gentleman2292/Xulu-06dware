package tech.mmmax.impl.components;

import me.rina.turok.hardware.mouse.TurokMouse;
import tech.mmmax.api.component.AbstractComponent;
import tech.mmmax.api.component.IComponent;

import java.util.ArrayList;
import java.util.Arrays;

public class ComponentManager implements IComponent {

    public static ComponentManager INSTANCE = new ComponentManager();

    ArrayList<AbstractComponent> components;

    public ComponentManager(){
        components = new ArrayList<>();
    }

    public ComponentManager addComponent(AbstractComponent component){
        components.add(component);
        return this;
    }

    public ComponentManager addComponents(AbstractComponent... component){
        components.addAll(Arrays.asList(component));
        return this;
    }

    public ArrayList<AbstractComponent> getComponents() {
        return components;
    }

    @Override
    public void draw(TurokMouse m, float partialTicks) {
        for (AbstractComponent component : components){

            if (component.exists) component.draw(m, partialTicks);
      //      if (component.solid) break;
        }
    }

    @Override
    public void click(TurokMouse m, int button) {
        for (AbstractComponent component : components){
            if (component.exists) component.click(m, button);
      //      if (component.solid) break;
        }
    }

    @Override
    public void key(int keyTyped, char character) {
        for (AbstractComponent component : components){
            if (component.exists) component.key(keyTyped, character);
       //     if (component.solid) break;
        }
    }

    @Override
    public void release(TurokMouse m, int state) {
        for (AbstractComponent component : components){
            if (component.exists) component.release(m, state);
       //     if (component.solid) break;
        }
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
