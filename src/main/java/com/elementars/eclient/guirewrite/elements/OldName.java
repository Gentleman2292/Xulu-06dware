package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.Wrapper;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.Value;

public class OldName extends Element {

    private final Value<Boolean> rainbow = register(new Value<>("Rainbow", this, false));

    public OldName() {
        super("OldName");
    }

    @Override
    public void onEnable() {
        width = fontRenderer.getStringWidth("Elementars.com") + 2;
        height = fontRenderer.FONT_HEIGHT + 2;
        super.onEnable();
    }

    @Override
    public void onRender() {
        int color = ColorUtil.getClickGUIColor().getRGB();
        if (rainbow.getValue()) {
            color = Xulu.rgb;
        }
        if (Xulu.CustomFont) {
            Xulu.cFontRenderer.drawStringWithShadow("Elementars.com", x + 1, y + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
        } else {
            Wrapper.getMinecraft().fontRenderer.drawStringWithShadow("Elementars.com", (float) x + 1, (float) y + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
        }
    }
}
