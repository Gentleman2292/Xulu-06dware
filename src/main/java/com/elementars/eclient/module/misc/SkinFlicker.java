package com.elementars.eclient.module.misc;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.entity.player.EnumPlayerModelParts;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by 086 on 30/01/2018.
 */
public class SkinFlicker extends Module {
    private final Value<String> mode = register(new Value<>("Mode", this, "Horizontal", new ArrayList<>(
            Arrays.asList("Horizontal", "Vertical", "Random")
    )));
    private final Value<Integer> slowness = register(new Value<>("Slowness", this, 2, 1, 5));

    public SkinFlicker() {
        super("SkinFlicker", "Toggles skin layers", Keyboard.KEY_NONE, Category.MISC, true);
    }

    private final static EnumPlayerModelParts[] PARTS_HORIZONTAL = new EnumPlayerModelParts[]{
            EnumPlayerModelParts.LEFT_SLEEVE,
            EnumPlayerModelParts.JACKET,
            EnumPlayerModelParts.HAT,
            EnumPlayerModelParts.LEFT_PANTS_LEG,
            EnumPlayerModelParts.RIGHT_PANTS_LEG,
            EnumPlayerModelParts.RIGHT_SLEEVE
    };

    private final static EnumPlayerModelParts[] PARTS_VERTICAL = new EnumPlayerModelParts[]{
            EnumPlayerModelParts.HAT,
            EnumPlayerModelParts.JACKET,
            EnumPlayerModelParts.LEFT_SLEEVE,
            EnumPlayerModelParts.RIGHT_SLEEVE,
            EnumPlayerModelParts.LEFT_PANTS_LEG,
            EnumPlayerModelParts.RIGHT_PANTS_LEG,
    };

    private Random r = new Random();
    private int len = EnumPlayerModelParts.values().length;

    @Override
    public void onUpdate() {
        if (mode.getValue().equalsIgnoreCase("Random")) {
            if (mc.player.ticksExisted % slowness.getValue() != 0) return;
            mc.gameSettings.switchModelPartEnabled(EnumPlayerModelParts.values()[r.nextInt(len)]);
        }
        else {
            int i = (mc.player.ticksExisted / slowness.getValue()) % (PARTS_HORIZONTAL.length * 2); // *2 for on/off
            boolean on = false;
            if (i >= PARTS_HORIZONTAL.length) {
                on = true;
                i -= PARTS_HORIZONTAL.length;
            }
            mc.gameSettings.setModelPartEnabled(mode.getValue().equalsIgnoreCase("Vertical") ? PARTS_VERTICAL[i] : PARTS_HORIZONTAL[i], on);
        }
    }

}
