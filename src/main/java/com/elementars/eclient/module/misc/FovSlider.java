package com.elementars.eclient.module.misc;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class FovSlider extends Module {

    public FovSlider() {
        super("FovSlider", "Better FOV slider", Keyboard.KEY_NONE, Category.MISC, true);
    }

    private final Value<Integer> FOV = register(new Value<>("FOV", this, 110, 90, 200));
    private final Value<String> mode = register(new Value<>("Mode", this, "Fov Changer", new String[]{
            "Fov Changer", "Hand Changer"
    }));
    private float fov;

    @SubscribeEvent
    public void fovOn(final EntityViewRenderEvent.FOVModifier e) {
        if (mode.getValue().equalsIgnoreCase("Hand Changer")) {
            e.setFOV((float)this.FOV.getValue());
        }
    }

    @Override
    public void onEnable() {
        EVENT_BUS.register(this);
        this.fov = mc.gameSettings.fovSetting;
    }

    @Override
    public void onDisable() {
        EVENT_BUS.unregister(this);
        mc.gameSettings.fovSetting = this.fov;
    }

    @Override
    public void onUpdate() {
        if (!this.isToggled() || mc.world == null) {
            return;
        }
        if (mode.getValue().equalsIgnoreCase("Fov Changer"))
            mc.gameSettings.fovSetting = (float) this.FOV.getValue();
    }

}