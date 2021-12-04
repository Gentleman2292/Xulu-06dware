package com.elementars.eclient.module.misc;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.init.Items;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by 086 on 8/04/2018.
 */
public class NoEntityTrace extends Module {
    private ArrayList<String> options;
    private final Value<String> mode = register(new Value<>("Mode", this, "Dynamic", new ArrayList<>(
            Arrays.asList("Dynamic", "Static")
    )));
    private final Value<Boolean> pickaxe = register(new Value<>("Pickaxe Only", this, true));
    private static NoEntityTrace INSTANCE;

    public NoEntityTrace() {
        super("NoEntityTrace", "Keeps mining through an entity", Keyboard.KEY_NONE, Category.MISC, true);
        NoEntityTrace.INSTANCE = this;
    }

    public static boolean shouldBlock() {
        return INSTANCE.isToggled() && (INSTANCE.mode.getValue().equalsIgnoreCase("Static") || mc.playerController.isHittingBlock) && (!INSTANCE.pickaxe.getValue() || mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE);
    }
}
