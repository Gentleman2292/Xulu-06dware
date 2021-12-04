package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.*;
import com.google.common.collect.Maps;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.Value;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class FeatureList extends Element {
    private Value<Boolean> corner;
    public static Value<Boolean> animation;
    public static Value<String> type;
    private Value<Boolean> alphab;
    private Value<Boolean> box;
    private Value<String> boxMode;
    private Value<String> mode;
    private Value<String> order;
    private Value<String> rlist;
    private Value<Integer> rainbowspeed;
    private Value<Integer> rspeed;
    private Value<Integer> rsaturation;
    private Value<Integer> rlightness;
    private Value<String> prefix;
    private Value<String> suffix;
    private Value<String> categoryProfile;

    private Rainbow rainbow = new Rainbow();
    String comp;

    private Map<Module, Triplet<Double, Double, Pair<Double, Integer>>> animationMap = Maps.newHashMap();
    private List<Module> removal = new ArrayList<>();

    public FeatureList() {
        super("FeatureList");
        this.corner = register(new Value<>("List In Corner", this, false));
        animation = register(new Value<>("Animation", this, false));
        type = register(new Value<>("Type", this, "Both", new String[]{"Both", "Enable", "Disable"}));
        this.alphab = register(new Value<>("Alphabetical", this, false));
        this.box = register(new Value<>("Boxes", this, false));
        this.boxMode = register(new Value<>("Box Mode", this, "Tag", new String[]{
                "Black", "Tag", "Outline"
        }));
        this.prefix = register(new Value<>("Prefix", this, "None", new String[]{
                "None", ">", ")", "]", "}", ">(space)", "->", "-", "=", "<", "(", "[", "{"
        }));
        this.suffix = register(new Value<>("Suffix", this, "None", new String[]{
                "None", ">", ")", "]", "}", "(space)<", "<-", "-", "=", "<", "(", "[", "{"
        }));
        this.mode = register(new Value<>("Aligned", this, "Left", new ArrayList<>(
                Arrays.asList("Left", "Right")
        )));
        this.order = register(new Value<>("Ordering", this, "Up", new ArrayList<>(
                Arrays.asList("Up", "Down")
        )));
        this.rlist = register(new Value<>("Color Mode", this, "ClickGui", new String[]{
                "ClickGui", "Rainbow", "Category"
        }));
        this.categoryProfile = register(new Value<>("Category Mode", this, "Xulu", new String[]{
                "Xulu", "Impact", "DotGod"
        }));
        this.rainbowspeed = register(new Value<>("Rainbow Speed", this, 5, 1, 100));
        this.rspeed = register(new Value<>("Rainbow Size", this, 2, 0, 20));
        this.rsaturation = register(new Value<>("Rainbow Sat.", this, 255, 0, 255));
        this.rlightness = register(new Value<>("Rainbow Light.", this, 255, 0, 255));
    }

    @Override
    public void onEnable() {
        width = 80;
        height = 80;
    }

    private int betterCompare(Module mod, String stringIn) {
        int returnOut = 0;
        returnOut = mod.getName().compareTo(stringIn);
        comp = stringIn;
        return returnOut;
    }

    @Override
    public void onRender() {
        ScaledResolution s = new ScaledResolution(mc);
        double yCount = 3;
        double right = s.getScaledWidth() - 3 - getFrame().width;
        if (!this.corner.getValue()) {
            yCount = y + 1;
            right = x + 1;
        }
        rainbow.updateRainbow();
        if (Xulu.CustomFont) {
            List<Module> mods = Xulu.MODULE_MANAGER.getModules().stream()
                    .filter(Module::isToggledAnim)
                    .filter(Module::isDrawn)
                    .sorted(Comparator.comparing(module -> Xulu.cFontRenderer.getStringWidth(module.getName() + (module.getHudInfo() == null ? "" : Command.SECTIONSIGN() + "7 [" + module.getHudInfo() + "]")) * -1))
                    .collect(Collectors.toList());
            if (this.alphab.getValue()) {
                String[] names = mods.stream().map(Module::getName).toArray(String[]::new);
                int count = mods.size();
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
                mods.clear();
                for (String modname : names) {
                    try {
                        mods.add(Xulu.MODULE_MANAGER.getModuleByName(modname));
                    } catch (Exception e) {
                        //empty
                    }
                }
            }
            boolean start = true;
            if (order.getValue().equalsIgnoreCase("Down")) {
                yCount += 69;
            }
            float hue = rainbow.hue;
            for (Module module : mods) {
                int rgb2 = Color.HSBtoRGB(hue, rsaturation.getValue() / 255f, rlightness.getValue() / 255f);
                switch (rlist.getValue()) {
                    case "ClickGui":
                        rgb2 = ColorUtil.getClickGUIColor().getRGB();
                        break;
                    case "Category":
                        rgb2 = getCategoryColor(module);
                        break;
                }
                String pre;
                String suf;
                switch (prefix.getValue()) {
                    case "None":
                        pre = "";
                        break;
                    case ">(space)":
                        pre = "> ";
                        break;
                    default:
                        pre = prefix.getValue();
                }
                switch (suffix.getValue()) {
                    case "None":
                        suf = "";
                        break;
                    case "(space)<":
                        suf = " <";
                        break;
                    default:
                        suf = suffix.getValue();
                }
                String display = pre + module.getName() + (module.getHudInfo() == null ? "" : Command.SECTIONSIGN() + "7 [" + Command.SECTIONSIGN() + "f" + module.getHudInfo() + Command.SECTIONSIGN() + "7]" + ChatFormatting.RESET) + suf;
                double right3 = (this.mode.getValue().equalsIgnoreCase("Right") ? right - Xulu.cFontRenderer.getStringWidth(display) + getFrame().width - 3 : right);
                double width = Xulu.cFontRenderer.getStringWidth(display);
                if (this.box.getValue()) {
                    switch (boxMode.getValue()) {
                        case "Black":
                            Gui.drawRect((int) right3 - 1, (int) yCount - 1, (int) right3 + (int) width + 3, (int) yCount + (int) Xulu.cFontRenderer.getHeight(), 0x55111111);
                            break;
                        case "Tag":
                            Gui.drawRect((int) right3 - 1, (int) yCount - 1, (int) right3 + (int) width + 3, (int) yCount + (int) Xulu.cFontRenderer.getHeight(), 0x55111111);
                            Gui.drawRect((int) right3 - 1, (int) yCount - 1, (int) right3 + 1, (int) yCount + (int) Xulu.cFontRenderer.getHeight(), rgb2);
                            break;
                        case "Outline":
                            Gui.drawRect((int) right3 - 1, (int) yCount - 1, (int) right3 + (int) width + 3, (int) yCount + (int) Xulu.cFontRenderer.getHeight(), 0x55111111);
                            XuluTessellator.drawRectOutline((int) right3 - 2, (int) yCount - 1, (int) right3 + (int) width + 4, (int) yCount + (int) Xulu.cFontRenderer.getHeight(), (int) right3 - 1, (int) yCount - 1, (int) right3 + (int) width + 3, (int) yCount + (int) Xulu.cFontRenderer.getHeight(), rgb2);
                            if (mods.indexOf(module) == 0) {
                                XuluTessellator.drawRectOutline((int) right3 - 2, (int) yCount - 2, (int) right3 + (int) width + 4, (int) yCount + (int) Xulu.cFontRenderer.getHeight(), (int) right3 - 2, (int) yCount - 1, (int) right3 + (int) width + 4, (int) yCount + (int) Xulu.cFontRenderer.getHeight(), rgb2);
                                if (mods.indexOf(module) + 1 < mods.size()) {
                                    Module mod = mods.get(mods.indexOf(module) + 1);
                                    String next = pre + mod.getName() + (mod.getHudInfo() == null ? "" : Command.SECTIONSIGN() + "7 [" + Command.SECTIONSIGN() + "f" + mod.getHudInfo() + Command.SECTIONSIGN() + "7]" + ChatFormatting.RESET) + suf;
                                    double nextWidth = Xulu.cFontRenderer.getStringWidth(next);
                                    XuluTessellator.drawRectOutline((int) right3 - 2, (int) yCount - 1, (int) right3 + (int) width - nextWidth - 1, (int) yCount + (int) Xulu.cFontRenderer.getHeight() + 1, (int) right3 - 2, (int) yCount - 1, (int) right3 + (int) width - nextWidth - 1, (int) yCount + (int) Xulu.cFontRenderer.getHeight(), rgb2);
                                }
                            } else {
                                if (mods.indexOf(module) + 1 == mods.size()) {
                                    XuluTessellator.drawRectOutline((int) right3 - 2, (int) yCount - 1, (int) right3 + (int) width + 4, (int) yCount + (int) Xulu.cFontRenderer.getHeight() + 1, (int) right3 - 2, (int) yCount - 1, (int) right3 + (int) width + 4, (int) yCount + (int) Xulu.cFontRenderer.getHeight(), rgb2);
                                } else {
                                    Module mod = mods.get(mods.indexOf(module) + 1);
                                    String next = pre + mod.getName() + (mod.getHudInfo() == null ? "" : Command.SECTIONSIGN() + "7 [" + Command.SECTIONSIGN() + "f" + mod.getHudInfo() + Command.SECTIONSIGN() + "7]" + ChatFormatting.RESET) + suf;
                                    double nextWidth = Xulu.cFontRenderer.getStringWidth(next);
                                    XuluTessellator.drawRectOutline((int) right3 - 2, (int) yCount - 1, (int) right3 + (int) width - nextWidth - 1, (int) yCount + (int) Xulu.cFontRenderer.getHeight() + 1, (int) right3 - 2, (int) yCount - 1, (int) right3 + (int) width - nextWidth - 1, (int) yCount + (int) Xulu.cFontRenderer.getHeight(), rgb2);
                                }
                            }
                            break;
                    }
                    start = false;
                }
                Xulu.cFontRenderer.drawStringWithShadow((module.inAnimation.getValue() != Animation.NONE ? "" : display), right3 + 1, yCount, ColorUtils.changeAlpha(rgb2, Global.hudAlpha.getValue()), true);
                if (!animationMap.containsKey(module)) {
                    if (module.inAnimation.getValue() != Animation.NONE) {
                        if (mode.getValue().equalsIgnoreCase("Right")) {
                            if (module.inAnimation.getValue() == Animation.ENABLE) {
                                animationMap.put(module, new Triplet<>(right3 + width, yCount, new Pair<>(right3, rgb2)));
                            } else {
                                animationMap.put(module, new Triplet<>(right3, yCount, new Pair<>(right3 + width, rgb2)));
                            }
                        } else if (mode.getValue().equalsIgnoreCase("Left")) {
                            if (module.inAnimation.getValue() == Animation.ENABLE) {
                                animationMap.put(module, new Triplet<>(right3 - width, yCount, new Pair<>(right3, rgb2)));
                            } else {
                                animationMap.put(module, new Triplet<>(right3, yCount, new Pair<>(right3 - width, rgb2)));
                            }
                        }
                    }
                } else {
                    animationMap.get(module).getThird().setValue(rgb2);
                }
                yCount += (order.getValue().equalsIgnoreCase("Up") ? 10 : -10);
                double speed = this.rspeed.getValue();
                hue += (speed / 100);
            }
            for (Module m : animationMap.keySet()) {
                Triplet<Double, Double, Pair<Double, Integer>> triplet = animationMap.get(m);
                String pre;
                String suf;
                switch (prefix.getValue()) {
                    case "None":
                        pre = "";
                        break;
                    case ">(space)":
                        pre = "> ";
                        break;
                    default:
                        pre = prefix.getValue();
                }
                switch (suffix.getValue()) {
                    case "None":
                        suf = "";
                        break;
                    case "(space)<":
                        suf = " <";
                        break;
                    default:
                        suf = suffix.getValue();
                }
                String display = pre + m.getName() + (m.getHudInfo() == null ? "" : Command.SECTIONSIGN() + "7 [" + Command.SECTIONSIGN() + "f" + m.getHudInfo() + Command.SECTIONSIGN() + "7]" + ChatFormatting.RESET) + suf;
                Xulu.cFontRenderer.drawStringWithShadow(display, triplet.getFirst(), triplet.getSecond(), ColorUtils.changeAlpha(triplet.getThird().getValue(), Global.hudAlpha.getValue()), true);
                if (!triplet.getFirst().equals(triplet.getThird().getKey())) {
                    if (triplet.getFirst() > triplet.getThird().getKey()) {
                        if (mode.getValue().equalsIgnoreCase("Left")) {
                            triplet.setFirst(triplet.getThird().getKey());
                        }
                        triplet.setFirst(triplet.getFirst() - 1);
                    }
                    if (triplet.getFirst() < triplet.getThird().getKey()) {
                        if (mode.getValue().equalsIgnoreCase("Right")) {
                            triplet.setFirst(triplet.getThird().getKey());
                        }
                        triplet.setFirst(triplet.getFirst() + 1);
                    }
                } else {
                    m.inAnimation.setEnumValue("NONE");
                    removal.add(m);
                }
            }
            removal.forEach(module -> {
                if (module.inAnimation.getValue() == Animation.NONE) animationMap.remove(module);
            });
            removal.clear();
        }else{
            List<Module> mods = Xulu.MODULE_MANAGER.getModules().stream()
                    .filter(Module::isToggledAnim)
                    .filter(Module::isDrawn)
                    .sorted(Comparator.comparing(module -> Wrapper.getMinecraft().fontRenderer.getStringWidth(module.getName()+(module.getHudInfo()==null?"":Command.SECTIONSIGN() + "7 [" + module.getHudInfo() +"]")) * -1))
                    .collect(Collectors.toList());
            if (this.alphab.getValue()) {
                String[] names = (mods.stream().map(Module::getName).collect(Collectors.toList())).toArray(new String[0]);
                int count = mods.size();
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
                mods.clear();
                for (String modname : names) {
                    try {
                        mods.add(Xulu.MODULE_MANAGER.getModuleByName(modname));
                    } catch (Exception e) {
                        //empty
                    }
                }
            }
            float hue2 = rainbow.hue;
            if (order.getValue().equalsIgnoreCase("Down")) {
                yCount += 69;
            }
            for (Module module : mods) {
                int rgb2 = Color.HSBtoRGB(hue2, rsaturation.getValue() / 255f, rlightness.getValue() / 255f);
                switch (rlist.getValue()) {
                    case "ClickGui":
                        rgb2 = ColorUtil.getClickGUIColor().getRGB();
                        break;
                    case "Category":
                        rgb2 = getCategoryColor(module);
                        break;
                }
                String pre;
                String suf;
                switch (prefix.getValue()) {
                    case "None":
                        pre = "";
                        break;
                    case ">(space)":
                        pre = "> ";
                        break;
                    default:
                        pre = prefix.getValue();
                }
                switch (suffix.getValue()) {
                    case "None":
                        suf = "";
                        break;
                    case "(space)<":
                        suf = " <";
                        break;
                    default:
                        suf = suffix.getValue();
                }
                boolean start = true;
                /*
                if (this.box.getValue()) {
                    double right2 = (this.mode.getValue().equalsIgnoreCase("Right") ? right - Wrapper.getMinecraft().fontRenderer.getStringWidth(pre + module.getName() + (module.getHudInfo() == null ? "" : Command.SECTIONSIGN() + "7 [" + module.getHudInfo() + "]" + suf)) + getFrame().width - 3 : right);
                    Gui.drawRect((int) right2 - 10, (int) yCount - (start ? 3 : 0) + 2, (int) right2 + Wrapper.getMinecraft().fontRenderer.getStringWidth(pre + module.getName() + (module.getHudInfo() == null ? "" : Command.SECTIONSIGN() + "7 [" + module.getHudInfo() + "]" + suf)) + 3, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, 0x55111111);
                    Gui.drawRect((int) right2 - 10, (int) yCount - (start ? 3 : 0) + 2, (int) right2 - 3, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, rgb2);
                    start = false;
                }
                 */
                String display = pre + module.getName() + (module.getHudInfo() == null ? "" : Command.SECTIONSIGN() + "7 [" + Command.SECTIONSIGN() + "f" + module.getHudInfo() + Command.SECTIONSIGN() + "7]" + ChatFormatting.RESET) + suf;
                double right3 = (int) (this.mode.getValue().equalsIgnoreCase("Right") ? right - Wrapper.getMinecraft().fontRenderer.getStringWidth(display) + getFrame().width - 3 : right);
                double width = Wrapper.getMinecraft().fontRenderer.getStringWidth(display);
                if (this.box.getValue()) {
                    switch (boxMode.getValue()) {
                        case "Black":
                            Gui.drawRect((int) right3 + 2, (int) yCount - 1, (int) right3 + (int) width + 3, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, 0x55111111);
                            break;
                        case "Tag":
                            Gui.drawRect((int) right3 - 2, (int) yCount - 1, (int) right3 + (int) width + 3, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, 0x55111111);
                            Gui.drawRect((int) right3 - 2, (int) yCount - 1, (int) right3 + 1, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, rgb2);
                            break;
                        case "Outline":
                            Gui.drawRect((int) right3 + 2, (int) yCount - 1, (int) right3 + (int) width + 3, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, 0x55111111);
                            XuluTessellator.drawRectOutline((int) right3 + 1, (int) yCount - 1, (int) right3 + (int) width + 4, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, (int) right3 + 2, (int) yCount - 1, (int) right3 + (int) width + 3, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, rgb2);
                            if (mods.indexOf(module) == 0) {
                                XuluTessellator.drawRectOutline((int) right3 + 1, (int) yCount - 2, (int) right3 + (int) width + 4, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, (int) right3 + 1, (int) yCount - 1, (int) right3 + (int) width + 4, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, rgb2);
                                if (mods.indexOf(module) + 1 < mods.size()) {
                                    Module mod = mods.get(mods.indexOf(module) + 1);
                                    String next = pre + mod.getName() + (mod.getHudInfo() == null ? "" : Command.SECTIONSIGN() + "7 [" + Command.SECTIONSIGN() + "f" + mod.getHudInfo() + Command.SECTIONSIGN() + "7]" + ChatFormatting.RESET) + suf;
                                    double nextWidth = Wrapper.getMinecraft().fontRenderer.getStringWidth(next);
                                    XuluTessellator.drawRectOutline((int) right3 + 1, (int) yCount - 1, (int) right3 + (int) width - nextWidth + 2, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT + 1, (int) right3 + 1, (int) yCount - 1, (int) right3 + (int) width - nextWidth + 2, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, rgb2);
                                }
                            } else {
                                if (mods.indexOf(module) + 1 == mods.size()) {
                                    XuluTessellator.drawRectOutline((int) right3 + 1, (int) yCount - 1, (int) right3 + (int) width + 4, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT + 1, (int) right3 + 1, (int) yCount - 1, (int) right3 + (int) width + 4, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, rgb2);
                                } else {
                                    Module mod = mods.get(mods.indexOf(module) + 1);
                                    String next = pre + mod.getName() + (mod.getHudInfo() == null ? "" : Command.SECTIONSIGN() + "7 [" + Command.SECTIONSIGN() + "f" + mod.getHudInfo() + Command.SECTIONSIGN() + "7]" + ChatFormatting.RESET) + suf;
                                    double nextWidth = Wrapper.getMinecraft().fontRenderer.getStringWidth(next);
                                    XuluTessellator.drawRectOutline((int) right3 + 1, (int) yCount - 1, (int) right3 + (int) width - nextWidth + 2, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT + 1, (int) right3 + 1, (int) yCount - 1, (int) right3 + (int) width - nextWidth + 2, (int) yCount + Wrapper.getMinecraft().fontRenderer.FONT_HEIGHT, rgb2);
                                }
                            }
                            break;
                    }
                    start = false;
                }
                Wrapper.getMinecraft().fontRenderer.drawStringWithShadow((module.inAnimation.getValue() != Animation.NONE ? "" : display), (int) (this.mode.getValue().equalsIgnoreCase("Right") ? right - Wrapper.getMinecraft().fontRenderer.getStringWidth(display) + getFrame().width : right), (int) yCount, ColorUtils.changeAlpha(rgb2, Global.hudAlpha.getValue()));
                if (!animationMap.containsKey(module)) {
                    if (module.inAnimation.getValue() != Animation.NONE) {
                        if (mode.getValue().equalsIgnoreCase("Right")) {
                            if (module.inAnimation.getValue() == Animation.ENABLE) {
                                animationMap.put(module, new Triplet<>(right3 + width, yCount, new Pair<>(right3, rgb2)));
                            } else {
                                animationMap.put(module, new Triplet<>(right3, yCount, new Pair<>(right3 + width, rgb2)));
                            }
                        } else if (mode.getValue().equalsIgnoreCase("Left")) {
                            if (module.inAnimation.getValue() == Animation.ENABLE) {
                                animationMap.put(module, new Triplet<>(right3 - width, yCount, new Pair<>(right3, rgb2)));
                            } else {
                                animationMap.put(module, new Triplet<>(right3, yCount, new Pair<>(right3 - width, rgb2)));
                            }
                        }
                    }
                } else {
                    animationMap.get(module).getThird().setValue(rgb2);
                }
                yCount += (order.getValue().equalsIgnoreCase("Up") ? 10 : -10);
                double speed = this.rspeed.getValue();
                hue2 += (speed / 100);
            }
            for (Module m : animationMap.keySet()) {
                Triplet<Double, Double, Pair<Double, Integer>> triplet = animationMap.get(m);
                String pre;
                String suf;
                switch (prefix.getValue()) {
                    case "None":
                        pre = "";
                        break;
                    case ">(space)":
                        pre = "> ";
                        break;
                    default:
                        pre = prefix.getValue();
                }
                switch (suffix.getValue()) {
                    case "None":
                        suf = "";
                        break;
                    case "(space)<":
                        suf = " <";
                        break;
                    default:
                        suf = suffix.getValue();
                }
                String display = pre + m.getName() + (m.getHudInfo() == null ? "" : Command.SECTIONSIGN() + "7 [" + Command.SECTIONSIGN() + "f" + m.getHudInfo() + Command.SECTIONSIGN() + "7]" + ChatFormatting.RESET) + suf;
                Wrapper.getMinecraft().fontRenderer.drawStringWithShadow(display, triplet.getFirst().floatValue(), triplet.getSecond().floatValue(), ColorUtils.changeAlpha(triplet.getThird().getValue(), Global.hudAlpha.getValue()));
                if (!triplet.getFirst().equals(triplet.getThird().getKey())) {
                    if (triplet.getFirst() > triplet.getThird().getKey()) {
                        if (mode.getValue().equalsIgnoreCase("Left")) {
                            triplet.setFirst(triplet.getThird().getKey());
                        }
                        triplet.setFirst(triplet.getFirst() - 1);
                    }
                    if (triplet.getFirst() < triplet.getThird().getKey()) {
                        if (mode.getValue().equalsIgnoreCase("Right")) {
                            triplet.setFirst(triplet.getThird().getKey());
                        }
                        triplet.setFirst(triplet.getFirst() + 1);
                    }
                } else {
                    m.inAnimation.setEnumValue("NONE");
                    removal.add(m);
                }
            }
            removal.forEach(module -> {
                if (module.inAnimation.getValue() == Animation.NONE) animationMap.remove(module);
            });
            removal.clear();
        }
    }

    public String getTitle(String in) {
        in = Character.toUpperCase(in.toLowerCase().charAt(0)) + in.toLowerCase().substring(1);
        return in;
    }

    private int getCategoryColor(Module m) {
        switch (categoryProfile.getValue()) {
            case "Xulu":
                switch (m.getCategory()) {
                    case CORE:
                        return new Color(0, 218, 242).getRGB();
                    case COMBAT:
                        return new Color(222, 57, 11).getRGB();
                    case MOVEMENT:
                        return new Color(189, 28, 173).getRGB();
                    case PLAYER:
                        return new Color(83, 219, 41).getRGB();
                    case RENDER:
                        return new Color(255, 242, 62).getRGB();
                    case MISC:
                        return new Color(255, 143, 15).getRGB();
                    case DUMMY:
                        return new Color(222, 57, 209).getRGB();
                    case HUD:
                        return new Color(255, 0, 123).getRGB();
                    case HIDDEN:
                        return -1;
                }
            case "Impact":
                switch (m.getCategory()) {
                    case CORE:
                        return new Color(0, 218, 242).getRGB();
                    case COMBAT:
                        return new Color(229, 30, 16).getRGB();
                    case MOVEMENT:
                        return new Color(8, 116, 227).getRGB();
                    case PLAYER:
                        return new Color(43, 203, 55).getRGB();
                    case RENDER:
                        return new Color(227, 162, 50).getRGB();
                    case MISC:
                        return new Color(97, 30, 212).getRGB();
                    case DUMMY:
                        return new Color(241, 243, 90).getRGB();
                    case HUD:
                        return new Color(255, 0, 123).getRGB();
                    case HIDDEN:
                        return -1;
                }
            case "DotGod":
                switch (m.getCategory()) {
                    case CORE:
                        return new Color(0, 218, 242).getRGB();
                    case COMBAT:
                        return new Color(39, 181, 171).getRGB();
                    case MOVEMENT:
                        return new Color(26, 84, 219).getRGB();
                    case PLAYER:
                        return new Color(219, 184, 190).getRGB();
                    case RENDER:
                        return new Color(169, 204, 83).getRGB();
                    case MISC:
                        return new Color(215, 214, 216).getRGB();
                    case DUMMY:
                        return new Color(222, 57, 209).getRGB();
                    case HUD:
                        return new Color(255, 0, 123).getRGB();
                    case HIDDEN:
                        return -1;
                }
        }
        return -1;
    }

    public class Rainbow {
        public int rgb;
        public int a;
        public int r;
        public int g;
        public int b;
        float hue = 0.01f;

        public void updateRainbow() {
            rgb = Color.HSBtoRGB(hue, Xulu.MODULE_MANAGER.getModuleT(FeatureList.class).rsaturation.getValue() / 255f, Xulu.MODULE_MANAGER.getModuleT(FeatureList.class).rlightness.getValue() / 255f);
            a = (rgb >>> 24) & 0xFF;
            r = (rgb >>> 16) & 0xFF;
            g = (rgb >>> 8) & 0xFF;
            b = rgb & 0xFF;
            hue += Xulu.MODULE_MANAGER.getModuleT(FeatureList.class).rainbowspeed.getValue() / 10000f;
            if (hue > 1) hue -= 1;
        }
    }
}
