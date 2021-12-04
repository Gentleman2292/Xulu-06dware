package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.ListHelper;
import com.elementars.eclient.util.PotionUtil;
import com.elementars.eclient.util.Wrapper;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.settings.Value;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class Potions extends Element {

    DecimalFormat df = new DecimalFormat("00");
    Value<Boolean> pc;
    Value<Integer> red;
    Value<Integer> green;
    Value<Integer> blue;
    Value<Boolean> onlysw;
    Value<String> align;
    Value<String> order;

    public Potions() {
        super("Potions");
        this.pc = register(new Value<>("Use Potion Color", this, true));
        this.red = register(new Value<>("Red", this, 0, 0, 255));
        this.green = register(new Value<>("Green", this, 0, 0, 255));
        this.blue = register(new Value<>("Blue", this, 0, 0, 255));
        this.onlysw = register(new Value<>("Only Str & Weak", this, false));
        this.align = register(new Value<>("Align", this, "Left", new ArrayList<>(
                Arrays.asList("Left", "Right")
        )));
        this.order = register(new Value<>("Order", this, "Up", new ArrayList<>(
                Arrays.asList("Up", "Down")
        )));
    }

    @Override
    public void onEnable() {
        width = 50;
        height = 50;
    }

    @Override
    public void onRender() {
        float yCount = (float) y;
        float right = (float) x;
        if (mc.player == null) return;
        List<String> potions = new ArrayList<>();
        if (order.getValue().equalsIgnoreCase("Down")) {
            yCount += height - 10;
        }
        for (PotionEffect potionEffect : mc.player.getActivePotionEffects()) {
            if (this.onlysw.getValue() && potionEffect.getPotion() != MobEffects.STRENGTH && potionEffect.getPotion() != MobEffects.WEAKNESS) continue;
            String text = PotionUtil.getPotionName(potionEffect.getPotion()) + " " + (potionEffect.getAmplifier() + 1 != 1 ? (potionEffect.getAmplifier() + 1) + " " : "") + ChatFormatting.GRAY + (getTime(potionEffect.getDuration()/20).length() == 1 ? "0" : "") + getTime(potionEffect.getDuration()/20);
            int color = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB();
            if (this.pc.getValue()) color = potionEffect.getPotion().getLiquidColor();
            if (Xulu.CustomFont) {
                Xulu.cFontRenderer.drawStringWithShadow(text, (this.align.getValue().equalsIgnoreCase("Right") ? right - Xulu.cFontRenderer.getStringWidth(text) + getFrame().width : right) + 1, yCount + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
            } else {
                Wrapper.getMinecraft().fontRenderer.drawStringWithShadow(text, (float) (this.align.getValue().equalsIgnoreCase("Right") ? right - Wrapper.getMinecraft().fontRenderer.getStringWidth(text) + getFrame().width : right) + 1, yCount + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
            }
            potions.add(text);
            yCount += (order.getValue().equalsIgnoreCase("Up") ? 10 : -10);
        }
    }

    public String getTime(int duration) {
        int min = 0;
        while (duration > 59) {
            duration -= 60;
            min += 1;
        }
        return min + ":" + df.format(duration);
    }
}
