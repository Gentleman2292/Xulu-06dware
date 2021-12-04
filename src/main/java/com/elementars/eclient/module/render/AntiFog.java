package com.elementars.eclient.module.render;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by 086 on 9/04/2018.
 */
public class AntiFog extends Module {
    private ArrayList<String> options;
    private final Value<Boolean> rainbow = register(new Value<>("Rainbow", this, false));
    private final Value<Float> r = register(new Value<>("Red", this, 1f, 0f, 1f));
    private final Value<Float> g = register(new Value<>("Green", this, 1f, 0f, 1f));
    private final Value<Float> b = register(new Value<>("Blue", this, 1f, 0f, 1f));
    private final Value<Float> r1 = register(new Value<>("Nether Red", this, 1f, 0f, 1f));
    private final Value<Float> g1 = register(new Value<>("Nether Green", this, 1f, 0f, 1f));
    private final Value<Float> b1 = register(new Value<>("Nether Blue", this, 1f, 0f, 1f));
    private final Value<Float> r2 = register(new Value<>("End Red", this, 1f, 0f, 1f));
    private final Value<Float> g2 = register(new Value<>("End Green", this, 1f, 0f, 1f));
    private final Value<Float> b2 = register(new Value<>("End Blue", this, 1f, 0f, 1f));
    private final Value<Boolean> clear = register(new Value<>("Remove fog", this, true));
    private final Value<Boolean> color = register(new Value<>("Color fog", this, true));

    public AntiFog() {
        super("AntiFog", "Prevents fog", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (clear.getValue()) {
            event.setDensity(0);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onFogColor(EntityViewRenderEvent.FogColors event) {
        if (color.getValue()) {
            if (rainbow.getValue()) {
                event.setRed(new Color(Xulu.rgb).getRed() / 255f);
                event.setGreen(new Color(Xulu.rgb).getGreen() / 255f);
                event.setBlue(new Color(Xulu.rgb).getBlue() / 255f);
            } else {
                if (mc.player.dimension == 0) {
                    event.setRed(r.getValue());
                    event.setGreen(g.getValue());
                    event.setBlue(b.getValue());
                } else if (mc.player.dimension == -1) {
                    event.setRed(r1.getValue());
                    event.setGreen(g1.getValue());
                    event.setBlue(b1.getValue());
                } else if (mc.player.dimension == 1) {
                    event.setRed(r2.getValue());
                    event.setGreen(g2.getValue());
                    event.setBlue(b2.getValue());
                }
            }
        }
    }

}
