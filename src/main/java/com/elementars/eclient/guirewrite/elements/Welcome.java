package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.Wrapper;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.ScaledResolution;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

public class Welcome extends Element {

    private final Value<Boolean> rainbow = register(new Value<>("Rainbow", this, false));
    private final Value<Boolean> dynamic = register(new Value<>("Dynamic", this, false));
    private final Value<Boolean> holiday = register(new Value<>("Holiday", this, true));
    private final Value<Boolean> center = register(new Value<>("Center", this, false));

    public Welcome() {
        super("Welcome");
    }

    public static String text = "Welcome NAME";

    @Override
    public void onRender() {
        if (mc.player == null) return;
        int color = ColorUtil.getClickGUIColor().getRGB();
        if (rainbow.getValue()) {
            color = Xulu.rgb;
        }
        String display = text.replaceAll("NAME", mc.player.getName());
        if (dynamic.getValue()) display = getTimeMessage().replaceAll("NAME", mc.player.getName());
        if (holiday.getValue() && getHoliday() != null) display = getHoliday().replaceAll("NAME", mc.player.getName());
        width = fontRenderer.getStringWidth(display) + 2;
        height = fontRenderer.FONT_HEIGHT + 2;
        String test = display.replaceAll("&", String.valueOf(ChatFormatting.PREFIX_CODE));
        if (center.getValue()) {
            ScaledResolution sr = new ScaledResolution(mc);
            if (Xulu.CustomFont) {
                Xulu.cFontRenderer.drawStringWithShadow(test, (sr.getScaledWidth() / 2f) - (Xulu.cFontRenderer.getStringWidth(test) / 2f) + 1, y + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
            } else {
                Wrapper.getMinecraft().fontRenderer.drawStringWithShadow(test, (sr.getScaledWidth() / 2f) - (Xulu.cFontRenderer.getStringWidth(test) / 2f) + 1, (float) y + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
            }
        } else {
            if (Xulu.CustomFont) {
                Xulu.cFontRenderer.drawStringWithShadow(test, x + 1, y + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
            } else {
                Wrapper.getMinecraft().fontRenderer.drawStringWithShadow(test, (float) x + 1, (float) y + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
            }
        }
    }

    public static void handleWelcome(String stringIn) {
        text = stringIn;
    }

    @Override
    public void onMiddleClick() {
        WelcomeNotes.initTextBox();
    }

    private String getTimeMessage() {
        String date = new SimpleDateFormat("k").format(new Date());
        int hour = Integer.valueOf(date);
        if (hour < 6) {
            return "Good Night NAME";
        } else if (hour < 12) {
            return "Good Morning NAME";
        } else if (hour < 17) {
            return "Good Afternoon NAME";
        } else if (hour < 20) {
            return "Good Evening NAME";
        } else {
            return "Good Night NAME";
        }
    }

    private String getHoliday() {
        int month = Integer.valueOf(new SimpleDateFormat("MM").format(new Date()));
        int day = Integer.valueOf(new SimpleDateFormat("dd").format(new Date()));
        switch (month) {
            case 1:
                if (day == 1)
                    return "Happy New Years NAME!";
                break;
            case 2:
                if (day == 14)
                    return "Happy Valentines Day NAME!";
                break;
            case 10:
                if (day == 31)
                    return "Happy Halloween NAME! (spooky)";
                break;
            case 11:
                LocalDate thanksGiving = Year.of(Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()))).atMonth(Month.NOVEMBER).atDay(1)
                        .with(TemporalAdjusters.lastInMonth(DayOfWeek.WEDNESDAY));
                if (thanksGiving.getDayOfMonth() == day) {
                    return "Happy Thanksgiving NAME!";
                }
            case 12:
                if (day == 25)
                    return "Happy X-mas NAME!";
                break;
        }
        return null;
    }
}
