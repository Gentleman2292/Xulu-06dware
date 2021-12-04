package tech.mmmax.api.component;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.util.TurokRect;
import net.minecraft.client.gui.ScaledResolution;

public class AbstractComponent implements IComponent {

    public AbstractComponent parent = null;

    public TurokRect anchorPoint;

    public TurokRect rect;
    public boolean dragging;
    public boolean draggable = true;

    public boolean updateAnchor;


    public int dragX = 0, dragY = 0;

    public boolean solid = false;

    public boolean exists = true;

    public ScaledResolution sr = new ScaledResolution(mc);


    @Override
    public void draw(TurokMouse m, float partialTicks) {
        sr = new ScaledResolution(mc);
        if (draggable) {
            if (this.dragging) {
                this.rect.setX(m.getX() - this.dragX);
                this.rect.setY(m.getY() - this.dragY);
                if (updateAnchor){
                //    this.anchorPoint = rect;
                }
            }
        }
    }

    @Override
    public void click(TurokMouse m, int button) {
        if (button == 0) {
            if (draggable) {
                if (anchorPoint.collideWithMouse(m)) {
                    this.dragging = true;
                    this.dragX = m.getX() - this.rect.x;
                    this.dragY = m.getY() - (this.rect.y);
                }
            }
        }
    }

    @Override
    public void key(int keyTyped, char character) {

    }

    @Override
    public void release(TurokMouse m, int state) {
        this.dragging = false;
    }

    @Override
    public int getHeight() {
        return rect.getHeight();
    }

}
