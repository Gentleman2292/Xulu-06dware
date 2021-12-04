package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.event.ArrayHelper;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.ListHelper;
import com.elementars.eclient.util.Wrapper;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.Value;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;

public class StickyNotes extends Element {

    private final Value<Boolean> rainbow = register(new Value<>("Rainbow", this, false));

    public StickyNotes() {
        super("StickyNotes");
    }

    public static String saveText;
    public static String[] text = new String[]{"Put text here"};

    @Override
    public void onRender() {
        width = fontRenderer.getStringWidth(ListHelper.longest(text)) + 2;
        height = ((fontRenderer.FONT_HEIGHT + 1) * text.length) + 1;
        int color = ColorUtil.getClickGUIColor().getRGB();
        if (rainbow.getValue()) {
            color = Xulu.rgb;
        }
        double yCount = y;
        for (String s : text) {
            String test = s.replaceAll("&", String.valueOf(ChatFormatting.PREFIX_CODE));
            if (Xulu.CustomFont) {
                Xulu.cFontRenderer.drawStringWithShadow(test, x + 1, yCount + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
            } else {
                fontRenderer.drawStringWithShadow(test, (float) x + 1, (float) yCount + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
            }
            yCount += 10;
        }
    }

    public static void processText(String stringIn) {
        text = stringIn.split("@");
        saveText = stringIn;
    }

    @Override
    public void onMiddleClick() {
        TextNotes.initTextBox();
    }
}
