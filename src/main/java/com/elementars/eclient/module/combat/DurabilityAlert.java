package com.elementars.eclient.module.combat;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.ColorTextUtils;
import com.elementars.eclient.util.ColorUtils;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Elementars
 */
public class DurabilityAlert extends Module {

    ConcurrentHashMap<String, Integer> players = new ConcurrentHashMap<>();
    private final Value<String> mode = register(new Value<>("Mode", this, "Chat", new String[]{
            "Chat", "Notification"
    }));
    private final Value<Boolean> ignoreself = register(new Value<>("Ignore Self", this, false));
    private final Value<Boolean> ignorefriends = register(new Value<>("Ignore Friends", this, false));
    private final Value<Boolean> watermark = register(new Value<>("Watermark", this, true));
    private final Value<String> color = register(new Value<>("Color", this, "White", ColorTextUtils.colors));

    public DurabilityAlert() {
        super("DurabilityAlert", "Alerts when someone has low durability", Keyboard.KEY_NONE, Category.COMBAT, true);
    }

    @Override
    public void onUpdate() {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (this.ignoreself.getValue() && player.getName().equalsIgnoreCase(mc.player.getName())) return;
            if (this.ignorefriends.getValue() && Friends.isFriend(player.getName())) return;
            for (ItemStack itemStack : player.getArmorInventoryList()) {
                if (itemStack != null && itemStack.getItem().getDurabilityForDisplay(itemStack) > 0.75)
                    if (!this.players.containsKey(player.getName())) {
                        if (this.mode.getValue().equalsIgnoreCase("Chat")) {
                            if (this.watermark.getValue())
                                Command.sendChatMessage(ColorTextUtils.getColor(this.color.getValue()) + player.getName() + " has low durability!");
                            else
                                Command.sendRawChatMessage(ColorTextUtils.getColor(this.color.getValue()) + player.getName() + " has low durability!");
                        }
                        this.players.put(player.getName(), 1500);
                    }
            }
        }
        this.players.forEach((name, timeout) -> {
            if (timeout <= 0) {
                this.players.remove(name);
            }
            else {
                this.players.put(name, timeout - 1);
            }
        });
    }

    @Override
    public void onRender() {
        if (this.mode.getValue().equalsIgnoreCase("Notification")) {
            ScaledResolution sr = new ScaledResolution(mc);
            int yCount = (int) (sr.getScaledHeight() / 2 - (sr.getScaledHeight() / 2 * 0.9)) - (mc.fontRenderer.FONT_HEIGHT / 2);
            for (String name : players.keySet()) {
                if (players.get(name) > 1000) {
                    mc.fontRenderer.drawStringWithShadow(Command.SECTIONSIGN() + ColorTextUtils.getColor(this.color.getValue()).substring(1) + name + " has low durability!", (sr.getScaledWidth() / 2) - (mc.fontRenderer.getStringWidth(name + " has low durability!") / 2), yCount, ColorUtils.Colors.RED);
                    yCount += 10;
                }
            }
        }
    }
}
