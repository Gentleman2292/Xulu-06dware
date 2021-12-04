package com.elementars.eclient.module.misc;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.ColorTextUtils;
import com.elementars.eclient.util.EntityUtil;
import dev.xulu.settings.Value;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class VisualRange extends Module {
    private final Value<Boolean> mode = register(new Value<>("Send Message", this, false));
    private final Value<Boolean> sf = register(new Value<>("No Friend Send", this, false));
    private final Value<String> message = register(new Value<>("Message", this, "hello uwu", new ArrayList<>(
            Arrays.asList("Change this in the settings")
    )));
    private final Value<Integer> delayN = register(new Value<>("Delay", this, 15, 1, 30));
    private final Value<Boolean> ignoreFriends = register(new Value<>("Ignore Friends", this, false));
    private final Value<Boolean> vr = register(new Value<>("VisualRange", this, true));
    private final Value<Boolean> watermark = register(new Value<>("Watermark", this, true));
    private final Value<Boolean> color = register(new Value<>("Color", this, false));
    private final Value<String> selectcolor = register(new Value<>("Color Pick", this, "LightGreen", ColorTextUtils.colors));
    public ArrayList<String> names = new ArrayList<>();
    public ArrayList<String> names2 = new ArrayList<>();
    public ArrayList<String> removal = new ArrayList<>();

    private int delay;

    public VisualRange() {
        super("VisualRange", "Alerts people appearing in your visual range", Keyboard.KEY_NONE, Category.MISC, true);
    }

    @Override
    public void onUpdate() {
        if (delay > 0) {
            delay -= 1;
        }
    }

    @Override
    public void onWorldRender(final RenderEvent event) {
        names2.clear();
        Minecraft.getMinecraft().world.loadedEntityList.stream().filter(EntityUtil::isLiving).filter(entity -> !EntityUtil.isFakeLocalPlayer(entity)).filter(entity -> entity instanceof EntityPlayer).filter(entity -> !(entity instanceof EntityPlayerSP)).forEach(this::testName);
        testLeave();
    }
    private void testName(final Entity entityIn) {
        names2.add(entityIn.getName());
        if(!names.contains(entityIn.getName())) {
            sendMessage(entityIn);
            names.add(entityIn.getName());
        }
    }
    private void testLeave() {
        names.forEach(name ->{
            if(!names2.contains(name)) {
                removal.add(name);
            }
        });
        removal.forEach(name -> names.remove(name));
        removal.clear();
    }
    private void sendMessage(final Entity entityIn) {
        if (this.mode.getValue() && delay == 0) {
            if (sf.getValue() && Friends.isFriend(entityIn.getName())) return;
            mc.player.sendChatMessage("/msg " + entityIn.getName() + " " + message.getValue());
            delay = this.delayN.getValue() * 20;
        }
        if (this.vr.getValue()) {
            if (ignoreFriends.getValue() && Friends.isFriend(entityIn.getName())) return;
            if (watermark.getValue()) {
                if (color.getValue()) {
                    Command.sendChatMessage(ColorTextUtils.getColor(selectcolor.getValue()) + entityIn.getName() + " entered visual range");
                } else {
                    Command.sendChatMessage(entityIn.getName() + " entered visual range");
                }
            } else {
                if (color.getValue()) {
                    Command.sendRawChatMessage(ColorTextUtils.getColor(selectcolor.getValue()) + entityIn.getName() + " entered visual range");
                } else {
                    Command.sendRawChatMessage(entityIn.getName() + " entered visual range");
                }
            }
        }
    }
}