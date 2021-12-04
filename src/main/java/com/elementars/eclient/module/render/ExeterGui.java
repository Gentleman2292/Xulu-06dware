package com.elementars.eclient.module.render;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.clickgui.ClickGui;
import dev.xulu.settings.Value;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @since 6/2/2020 - 9:41 AM
 */
public class ExeterGui extends Module {

    private final Value<Boolean> customFont = register(new Value<>("Custom Font", this, false));
    private final Value<Boolean> sound = register(new Value<>("Sound", this, true));
    private final Value<Boolean> rainbow = register(new Value<>("Rainbow", this, false));
    private final Value<Integer> rainbowspeed = register(new Value<>("Rainbow Speed", this, 20, 1, 50));

    public ExeterGui() {
        super("ExeterGui", "Exeter Gui", Keyboard.KEY_NONE, Category.CORE, true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(ClickGui.getClickGui());
        toggle();
    }

    public static boolean getCF() {
        return Xulu.MODULE_MANAGER.getModuleT(ExeterGui.class).customFont.getValue();
    }
    public static boolean getSound() {
        return Xulu.MODULE_MANAGER.getModuleT(ExeterGui.class).sound.getValue();
    }
    public static boolean getRainbow() {
        return Xulu.MODULE_MANAGER.getModuleT(ExeterGui.class).rainbow.getValue();
    }
    public static int getSpeed() {
        return Xulu.MODULE_MANAGER.getModuleT(ExeterGui.class).rainbowspeed.getValue();
    }
}
