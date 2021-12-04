package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.combat.PopCounter;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.module.render.ExtraTab;
import com.elementars.eclient.util.ColorTextUtils;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.Wrapper;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.settings.Value;
import net.minecraft.entity.player.EntityPlayer;

import java.text.DecimalFormat;

public class TextRadar extends Element {

    private final Value<Boolean> pops = register(new Value<>("Pop Count", this, true));

    DecimalFormat decimalFormat = new DecimalFormat("#.#");

    public TextRadar() {
        super("TextRadar");
    }

    @Override
    public void onEnable() {
        width = 80;
        height = 80;
        super.onEnable();
    }

    @Override
    public void onRender() {
        float yCount = (float) y;
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.getName().equals(mc.player.getName())) continue;
            if (Xulu.CustomFont) {
                Xulu.cFontRenderer.drawStringWithShadow(ChatFormatting.GRAY + "- " + (Friends.isFriend(entityPlayer.getName()) ? Command.SECTIONSIGN() + ColorTextUtils.getColor(ExtraTab.INSTANCE.color.getValue()).substring(1) + entityPlayer.getName() : entityPlayer.getName()) + " " + ChatFormatting.RED + decimalFormat.format(entityPlayer.getHealth()) + " " + ChatFormatting.DARK_GREEN + (int) mc.player.getDistance(entityPlayer) + (PopCounter.INSTANCE.popMap.containsKey(entityPlayer) && pops.getValue() ? " " + ChatFormatting.DARK_RED + "-" + PopCounter.INSTANCE.popMap.get(entityPlayer) : ""), (float) x, yCount, ColorUtils.changeAlpha(ColorUtils.Colors.WHITE, Global.hudAlpha.getValue()));
            } else {
                Wrapper.getMinecraft().fontRenderer.drawStringWithShadow(ChatFormatting.GRAY + "- " + (Friends.isFriend(entityPlayer.getName()) ? Command.SECTIONSIGN() + ColorTextUtils.getColor(ExtraTab.INSTANCE.color.getValue()).substring(1) + entityPlayer.getName() : entityPlayer.getName()) + " " + ChatFormatting.RED + decimalFormat.format(entityPlayer.getHealth()) + " " + ChatFormatting.DARK_GREEN + (int) mc.player.getDistance(entityPlayer) + (PopCounter.INSTANCE.popMap.containsKey(entityPlayer) && pops.getValue() ? " " + ChatFormatting.DARK_RED + "-" + PopCounter.INSTANCE.popMap.get(entityPlayer) : ""), (float) x, yCount, ColorUtils.changeAlpha(ColorUtils.Colors.WHITE, Global.hudAlpha.getValue()));
            }
            yCount += 10;
        }
    }
}
