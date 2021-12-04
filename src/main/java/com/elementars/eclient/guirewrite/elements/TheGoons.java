package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.ListHelper;
import com.elementars.eclient.util.Wrapper;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.Value;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class TheGoons extends Element {

    private final Value<Boolean> rainbow = register(new Value<>("Rainbow", this, false));
    private final Value<Integer> red = register(new Value<>("Friend Red", this, 255, 0 ,255));
    private final Value<Integer> green = register(new Value<>("Friend Green", this, 255, 0 ,255));
    private final Value<Integer> blue = register(new Value<>("Friend Blue", this, 255, 0 ,255));

    public TheGoons() {
        super("TheGoons");
    }

    @Override
    public void onRender() {
        int color = ColorUtil.getClickGUIColor().getRGB();
        if (this.rainbow.getValue()) {
            color = Xulu.rgb;
        }
        List<String> friends = mc.world.playerEntities.stream().filter(player -> Friends.isFriend(player.getName())).map(EntityPlayer::getName).collect(Collectors.toList());
        friends.add("The Goons");
        width = fontRenderer.getStringWidth(ListHelper.longest(friends)) + 2;
        height = ((fontRenderer.FONT_HEIGHT + 1) * friends.size()) + 1;
        float yCount = (float) y;
        if (Xulu.CustomFont) {
            Xulu.cFontRenderer.drawStringWithShadow("The Goons", (float) x + 1, yCount + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
        } else {
            Wrapper.getMinecraft().fontRenderer.drawStringWithShadow("The Goons", (float) x + 1, yCount + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
        }
        yCount += 10;
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.getName().equals(mc.player.getName())) continue;
            if (!Friends.isFriend(entityPlayer.getName())) continue;
            if (Xulu.CustomFont) {
                Xulu.cFontRenderer.drawStringWithShadow(entityPlayer.getName(), (float) x + 1, yCount + 1, new Color(red.getValue(), green.getValue(), blue.getValue(), Global.hudAlpha.getValue()).getRGB());
            } else {
                Wrapper.getMinecraft().fontRenderer.drawStringWithShadow(entityPlayer.getName(), (float) x + 1, yCount + 1, new Color(red.getValue(), green.getValue(), blue.getValue(), Global.hudAlpha.getValue()).getRGB());
            }
            yCount += 10;
        }
    }
}
