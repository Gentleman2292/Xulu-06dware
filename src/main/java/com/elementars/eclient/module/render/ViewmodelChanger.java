package com.elementars.eclient.module.render;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @version Xulu v1.2.0
 * @since 6/27/2020 - 7:28 PM
 */
public class ViewmodelChanger extends Module {

    public final Value<HandMode> mode = register(new Value<>("Mode", this, HandMode.MAINHAND, HandMode.values()));
    public final Value<Boolean> pause = register(new Value<>("Pause On Eat", this, false));
    public final Value<Boolean> hand = register(new Value<>("Hand", this, true));
    public final Value<Boolean> item = register(new Value<>("Item", this, true));
    public final Value<Float> sizex = register(new Value<>("Size X", this, 1f, 0f, 2f));
    public final Value<Float> sizey = register(new Value<>("Size Y", this, 1f, 0f, 2f));
    public final Value<Float> sizez = register(new Value<>("Size Z", this, 1f, 0f, 2f));
    public final Value<Float> x = register(new Value<>("x", this, 1f, 0f, 1f));
    public final Value<Float> y = register(new Value<>("y", this, 1f, 0f, 1f));
    public final Value<Float> z = register(new Value<>("z", this, 1f, 0f, 1f));
    public final Value<Float> posX = register(new Value<>("X offset", this, 0f, -2f, 2f));
    public final Value<Float> posY = register(new Value<>("Y offset", this, 0f, -2f, 2f));
    public final Value<Float> posZ = register(new Value<>("Z offset", this, 0f, -2f, 2f));
    public final Value<Float> alpha = register(new Value<>("Alpha", this, 1f, 0f, 1f));

    public ViewmodelChanger() {
        super("ViewmodelChanger", "Tweaks the players hand", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    @Override
    public void onEnable() {
        EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        EVENT_BUS.unregister(this);
    }

    public enum HandMode {
        MAINHAND,
        OFFHAND,
        BOTH;

        HandMode() {}

        public boolean isOK(boolean isOffhand) {
            switch (this) {
                case MAINHAND:
                    return !isOffhand;
                case OFFHAND:
                    return isOffhand;
                case BOTH:
                    return true;
            }
            return false;
        }
    }
}
