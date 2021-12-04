package com.elementars.eclient.module.movement;

import com.elementars.eclient.event.Event;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.PlayerMoveEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.MovementUtils;
import dev.xulu.settings.Value;
import org.lwjgl.input.Keyboard;

/**
 * Created by 086 on 23/08/2017.
 */
public class Sprint extends Module {

    private final Value<String> mode = register(new Value<String>("Mode", this, "Legit", new String[]{
            "Legit", "Rage"
    }));

    public Sprint() {
        super("Sprint", "Sprints", Keyboard.KEY_NONE, Category.MOVEMENT, true);
    }

    @Override
    public void onUpdate() {
        try {
            if (mode.getValue().equalsIgnoreCase("Legit")) {
                if ((mc.gameSettings.keyBindForward.isKeyDown()) && !(mc.player.isSneaking()) && !(mc.player.isHandActive()) && !(mc.player.collidedHorizontally) && mc.currentScreen == null && !(mc.player.getFoodStats().getFoodLevel() <= 6f)) {
                    mc.player.setSprinting(true);
                }
            }
        } catch (Exception ignored) {}
    }

    @EventTarget
    public void onMove(PlayerMoveEvent event) {
        if (event.getEventState() != Event.State.PRE) return;
        if ((mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) && !(mc.player.isSneaking()) && !(mc.player.collidedHorizontally) && !(mc.player.getFoodStats().getFoodLevel() <= 6f)) {
            mc.player.setSprinting(true);
            double[] dir = MovementUtils.forward2(0.017453292F);
            event.setX(dir[0] * 0.2f);
            event.setZ(dir[1] * 0.2f);
        }
    }

}
