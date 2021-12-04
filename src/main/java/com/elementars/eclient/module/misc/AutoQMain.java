package com.elementars.eclient.module.misc;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import org.lwjgl.input.Keyboard;

public class AutoQMain extends Module {
    private final Value<Boolean> message = register(new Value<>("Debug Messages", this, false));
    private final Value<Integer> delay = register(new Value<>("Seconds", this, 120, 10, 600));
    private int wait;

    public AutoQMain() {
        super("AutoQMain", "Automatically sends /queue main", Keyboard.KEY_NONE, Category.MISC, true);
    }

    @Override
    public void onEnable() {
        wait = 12000;
    }

    @Override
    public void onUpdate() {
        if (AutoQMain.mc.getCurrentServerData() == null || (AutoQMain.mc.getCurrentServerData() != null && !AutoQMain.mc.getCurrentServerData().serverIP.equals("2b2t.org"))) {
            wait = 0;
            Command.sendChatMessage("Server not 2b2t.org, disabling...");
            this.disable();
        }else {
            if (wait <= 0) {
                AutoQMain.mc.player.sendChatMessage("/queue main");
                if (message.getValue()) {
                    Command.sendChatMessage("Did /queue main");
                }
                wait = (int) this.delay.getValue();
            }
            --wait;
        }
    }
}
