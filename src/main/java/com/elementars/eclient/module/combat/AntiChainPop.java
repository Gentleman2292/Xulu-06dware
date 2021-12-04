package com.elementars.eclient.module.combat;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 */
public class AntiChainPop extends Module {
    public AntiChainPop() {
        super("AntiChainPop", "Does exactly what you think it does", Keyboard.KEY_NONE, Category.COMBAT, true);
    }
}
