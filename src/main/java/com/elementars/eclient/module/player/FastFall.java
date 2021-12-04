package com.elementars.eclient.module.player;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.movement.Strafe;
import com.elementars.eclient.util.EntityUtil;
import dev.xulu.settings.Value;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @since 7/28/2020 - 12:49 PM
 */
public class FastFall extends Module {

    public final Value<Boolean> falling = register(new Value<>("Fast Fall", this, true));
    public final Value<Boolean> webs = register(new Value<>("Fast Web", this, true));
    public final Value<String> fallMode = register(new Value<>("Fall Mode", this, "Normal", new String[]{
            "Normal", "2b2t"
    }));
    public final Value<String> webMode = register(new Value<>("Web Mode", this, "Normal", new String[]{
            "Normal", "2b2t"
    }));

    public FastFall() {
        super("FastFall", "Immediately hits terminal velocity when falling", Keyboard.KEY_NONE, Category.PLAYER, true);
    }

    int delay;

    @Override
    public void onUpdate() {
        if (delay > 0) delay--;
        if (webs.getValue()) {
            if (mc.player.isInWeb) {
                mc.player.motionY = webMode.getValue().equalsIgnoreCase("Normal") ? -3.9200038147008747 : -0.22000000000000003;
            }
        }
        if (falling.getValue()) {
            if (mc.player.motionY > -0.06f) {
                delay = 20;
            }
            if (mc.player.fallDistance > 0.0f && mc.player.fallDistance < 1.0f && delay == 0 && !Xulu.MODULE_MANAGER.getModule(Strafe.class).isToggled() && !EntityUtil.isInWater(mc.player)) {
                mc.player.motionY = fallMode.getValue().equalsIgnoreCase("Normal") ? -3.9200038147008747 : -0.22000000000000003;
            }
        }
    }
}
