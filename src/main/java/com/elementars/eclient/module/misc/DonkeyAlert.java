package com.elementars.eclient.module.misc;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.entity.passive.EntityDonkey;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 */
public class DonkeyAlert extends Module {
    public DonkeyAlert() {
        super("DonkeyAlert", "Alerts you when a donkey is in your render distance", Keyboard.KEY_NONE, Category.MISC, true);
    }

    int delay;

    @Override
    public void onEnable() {
        delay = 0;
    }

    @Override
    public void onUpdate() {
        if (delay > 0) --delay;
        mc.world.loadedEntityList.forEach(entity -> {
            if (entity instanceof EntityDonkey && delay == 0) {
                Command.sendChatMessage("Donkey spotted!");
                delay = 200;
            }
        });
    }
}
