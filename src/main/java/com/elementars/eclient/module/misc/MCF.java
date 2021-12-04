package com.elementars.eclient.module.misc;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventMiddleClick;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.math.RayTraceResult;

import org.lwjgl.input.Keyboard;

public class MCF extends Module {
    private final Value<Boolean> message = register(new Value<>("Send Message", this, true));

    public MCF() {
        super("MCF", "Middle Click Friends", Keyboard.KEY_NONE, Category.MISC, true);
    }

    @EventTarget
    public void onMiddleClick(EventMiddleClick event) {
        final RayTraceResult ray = MCF.mc.objectMouseOver;
        if(ray.typeOfHit == RayTraceResult.Type.ENTITY) {
            final Entity entity = ray.entityHit;
            if (entity instanceof EntityPlayer){
                String name = ((EntityPlayer) entity).getDisplayNameString();
                if (Friends.isFriend(name)) {
                    Friends.delFriend(name);
                    if (message.getValue())
                        Command.sendChatMessage("&b" + name + "&r has been unfriended.");
                }else{
                    Friends.addFriend(name);
                    if (message.getValue())
                        Command.sendChatMessage("&b" + name + "&r has been friended.");
                }
            }
        }
    }
}