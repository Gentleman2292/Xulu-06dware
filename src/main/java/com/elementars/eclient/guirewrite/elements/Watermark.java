package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.Wrapper;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.Value;

public class Watermark extends Element {

    private final Value<String> text = register(new Value<>("Mode", this, "Xulu", new String[]{
            "Xulu", "PK Client", "WideHack"
    }))
            .onChanged(onChangedValue -> width = fontRenderer.getStringWidth(onChangedValue.getNew() + " " + Xulu.version) + 2);
    private final Value<Boolean> rainbow = register(new Value<>("Rainbow", this, false));

    public Watermark() {
        super("Watermark");
    }

    @Override
    public void onEnable() {
        width = fontRenderer.getStringWidth(text.getValue() + " " + Xulu.version) + 2;
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
            Xulu.cFontRenderer.drawStringWithShadow(text.getValue() + " " + Xulu.version, x + 1, y + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
        } else {
            Wrapper.getMinecraft().fontRenderer.drawStringWithShadow(text.getValue() + " " + Xulu.version, (float) x + 1, (float) y + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
        }
    }
}
