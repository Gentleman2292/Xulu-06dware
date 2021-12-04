package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.Value;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.client.Minecraft.getDebugFPS;

/**
 * @author Elementars
 * Some maths by 086 and dominikaaa because im lazy
 */
public class Info extends Element {
    private final Value<Boolean> rainbow = register(new Value<>("Rainbow", this, false));
    private final Value<Boolean> t24 = register(new Value<>("24hr time", this, false));
    private final Value<String> mode = register(new Value<>("Aligned", this, "Right", new String[]{
            "Right",
            "Left"
    }));
    private final Value<String> order = register(new Value<>("Ordering", this, "Down", new String[]{
            "Up",
            "Down"
    }));
    private final Value<String> color2 = register(new Value<>("2nd Color", this, "LightGray", ColorTextUtils.colors));
    private final Value<Boolean> FPS = register(new Value<>("Fps", this, true));
    private final Value<Boolean> PING = register(new Value<>("Ping", this, true));
    private final Value<Boolean> TPS = register(new Value<>("Tps", this, true));
    private final Value<Boolean> SPEED = register(new Value<>("Speed", this, true));
    private final Value<Boolean> TIME = register(new Value<>("Time", this, true));
    private final Value<Boolean> DURABILITY = register(new Value<>("Durability", this, true));
    private final Value<Boolean> SERVER_IP = register(new Value<>("Server IP", this, true));
    private final Value<Boolean> POTIONS = register(new Value<>("Potions", this, true));
    private final Value<Boolean> ALPHABETICAL = register(new Value<>("ABC Potions", this, false));
    DecimalFormat df = new DecimalFormat("#.#");
    DecimalFormat df2 = new DecimalFormat("#.###");

    public Info() {
        super("Info");
    }

    public static double coordsDiff(char s) {
        switch (s) {
            case 'x': return mc.player.posX - mc.player.prevPosX;
            case 'z': return mc.player.posZ - mc.player.prevPosZ;
            default: return 0.0;
        }
    }

    public boolean isToolArmor(Item i) {
        if (
                i instanceof ItemArmor
                || i == Items.DIAMOND_SWORD
                || i == Items.DIAMOND_PICKAXE
                || i == Items.DIAMOND_AXE
                || i == Items.DIAMOND_SHOVEL
                || i == Items.DIAMOND_HOE
                || i == Items.IRON_SWORD
                || i == Items.IRON_PICKAXE
                || i == Items.IRON_AXE
                || i == Items.IRON_SHOVEL
                || i == Items.IRON_HOE
                || i == Items.GOLDEN_SWORD
                || i == Items.GOLDEN_PICKAXE
                || i == Items.GOLDEN_AXE
                || i == Items.GOLDEN_SHOVEL
                || i == Items.GOLDEN_HOE
                || i == Items.STONE_SWORD
                || i == Items.STONE_PICKAXE
                || i == Items.STONE_AXE
                || i == Items.STONE_SHOVEL
                || i == Items.STONE_HOE
                || i == Items.WOODEN_SWORD
                || i == Items.WOODEN_PICKAXE
                || i == Items.WOODEN_AXE
                || i == Items.WOODEN_SHOVEL
                || i == Items.WOODEN_HOE
        ) return true;
        return false;
    }

    @Override
    public void onRender() {
        float yCount = (float) y;
        int color = ColorUtil.getClickGUIColor().getRGB();
        if (this.rainbow.getValue()) {
            color = Xulu.rgb;
        }
        float currentTps = mc.timer.tickLength / 1000.0f;
        ItemStack itemStack = mc.player.getHeldItemMainhand();
        List<String> infolist = new ArrayList<>();
        Map<String, PotionEffect> potionMap = new HashMap<>();
        List<String> potionList = new ArrayList<>();
        if (FPS.getValue()) infolist.add("FPS: " + Command.SECTIONSIGN() + ColorTextUtils.getColor(color2.getValue()).substring(1) + getDebugFPS());
        if (PING.getValue()) infolist.add("Ping: " + Command.SECTIONSIGN() + ColorTextUtils.getColor(color2.getValue()).substring(1) + (mc.getConnection() != null && mc.player != null && mc.getConnection().getPlayerInfo(mc.player.entityUniqueID) != null ? mc.getConnection().getPlayerInfo(mc.player.entityUniqueID).getResponseTime() : "-1") + "ms");
        if (TPS.getValue()) infolist.add("TPS: " + Command.SECTIONSIGN() + ColorTextUtils.getColor(color2.getValue()).substring(1) + df2.format(LagCompensator.INSTANCE.getTickRate()));
        if (SPEED.getValue()) infolist.add("Speed: " + Command.SECTIONSIGN() + ColorTextUtils.getColor(color2.getValue()).substring(1) + df.format(((MathHelper.sqrt(Math.pow(coordsDiff('x'), 2) + Math.pow(coordsDiff('z'), 2)) / currentTps)) * 3.6) + " hm/h");
        if (TIME.getValue()) infolist.add("Time: " + Command.SECTIONSIGN() + ColorTextUtils.getColor(color2.getValue()).substring(1)  + (this.t24.getValue() ? new SimpleDateFormat("k:mm").format(new Date()) : new SimpleDateFormat("h:mm a").format(new Date())));
        if (DURABILITY.getValue() && isToolArmor(itemStack.getItem())) infolist.add("Durability: " + Command.SECTIONSIGN() + ColorTextUtils.getColor(color2.getValue()).substring(1) + (itemStack.getMaxDamage() - itemStack.getItemDamage()));
        if (SERVER_IP.getValue()) infolist.add("Server IP: " + Command.SECTIONSIGN() + ColorTextUtils.getColor(color2.getValue()).substring(1) + (mc.getCurrentServerData() == null ? "None" : mc.getCurrentServerData().serverIP));
        if (POTIONS.getValue()) {
            for (PotionEffect potionEffect : mc.player.getActivePotionEffects()) {
                String text = PotionUtil.getPotionName(potionEffect.getPotion()) + " " + (potionEffect.getAmplifier() + 1 != 1 ? (potionEffect.getAmplifier() + 1) + " " : "") + Command.SECTIONSIGN() + ColorTextUtils.getColor(color2.getValue()).substring(1) + (getTime(potionEffect.getDuration()/20).length() == 1 ? "0" : "") + getTime(potionEffect.getDuration()/20);
                potionMap.put(text, potionEffect);
            }
            if (Xulu.CustomFont) {
                potionList = potionMap.keySet().stream().sorted(Comparator.comparing(s -> Xulu.cFontRenderer.getStringWidth(s))).collect(Collectors.toList());
            } else {
                potionList = potionMap.keySet().stream().sorted(Comparator.comparing(fontRenderer::getStringWidth)).collect(Collectors.toList());
            }
            if (this.ALPHABETICAL.getValue()) {
                String[] names = potionList.toArray(new String[0]);
                int count = potionList.size();
                String temp;
                for (int i = 0; i < count; i++) {
                    for (int j = i + 1; j < count; j++) {
                        if (names[i].compareTo(names[j]) > 0) {
                            temp = names[i];
                            names[i] = names[j];
                            names[j] = temp;
                        }
                    }
                }
                potionList.clear();
                for (String modname : names) {
                    try {
                        potionList.add(modname);
                    } catch (Exception e) {
                        //empty
                    }
                }
                if (order.getValue().equalsIgnoreCase("Down")) Collections.reverse(potionList);
            }
        }
        width = 50;
        height = 50;
        if (Xulu.CustomFont) {
            infolist.sort(Comparator.comparing(s -> Xulu.cFontRenderer.getStringWidth(s)));
            Collections.reverse(infolist);
            if (order.getValue().equalsIgnoreCase("Down")) {
                yCount += 39;
            }
            for (String s : potionList) {
                Xulu.cFontRenderer.drawStringWithShadow(s, x - (mode.getValue().equalsIgnoreCase("Right") ? 0 : Xulu.cFontRenderer.getStringWidth(s)) + (mode.getValue().equalsIgnoreCase("Right") ? 1 : getFrame().width - 2), yCount + 1, ColorUtils.changeAlpha(potionMap.get(s).getPotion().getLiquidColor(), Global.hudAlpha.getValue()));
                yCount += (order.getValue().equalsIgnoreCase("Up") ? 10 : -10);
            }
            for (String s : infolist) {
                Xulu.cFontRenderer.drawStringWithShadow(s, x - (mode.getValue().equalsIgnoreCase("Right") ? 0 : Xulu.cFontRenderer.getStringWidth(s)) + (mode.getValue().equalsIgnoreCase("Right") ? 1 : getFrame().width - 2), yCount + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
                yCount += (order.getValue().equalsIgnoreCase("Up") ? 10 : -10);
            }
        } else {
            infolist.sort(Comparator.comparing(fontRenderer::getStringWidth));
            Collections.reverse(infolist);
            if (order.getValue().equalsIgnoreCase("Down")) {
                yCount += 39;
            }
            for (String s : potionList) {
                fontRenderer.drawStringWithShadow(s, (float) x - (float) (mode.getValue().equalsIgnoreCase("Right") ? 0 : fontRenderer.getStringWidth(s)) + (float) (mode.getValue().equalsIgnoreCase("Right") ? 1 : getFrame().width - 2), yCount + 1, ColorUtils.changeAlpha(potionMap.get(s).getPotion().getLiquidColor(), Global.hudAlpha.getValue()));
                yCount += (order.getValue().equalsIgnoreCase("Up") ? 10 : -10);
            }
            for (String s : infolist) {
                fontRenderer.drawStringWithShadow(s, (float) x - (float) (mode.getValue().equalsIgnoreCase("Right") ? 0 : fontRenderer.getStringWidth(s)) + (float) (mode.getValue().equalsIgnoreCase("Right") ? 1 : getFrame().width - 2), yCount + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
                yCount += (order.getValue().equalsIgnoreCase("Up") ? 10 : -10);
            }
        }
    }

    public String getTime(int duration) {
        int min = 0;
        while (duration > 59) {
            duration -= 60;
            min += 1;
        }
        return min + ":" + (df.format(duration).length() == 1 ? "0" + df.format(duration) : df.format(duration));
    }
}
