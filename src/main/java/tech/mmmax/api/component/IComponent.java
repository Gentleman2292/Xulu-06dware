package tech.mmmax.api.component;

import me.rina.turok.hardware.mouse.TurokMouse;
import net.minecraft.client.Minecraft;

public interface IComponent {

    Minecraft mc = Minecraft.getMinecraft();

    boolean subcomp = false;

    void draw(TurokMouse m, float partialTicks);

    void click(TurokMouse m, int button);

    void key(int keyTyped, char character);

    void release(TurokMouse m, int state);

    int getHeight();

}
