package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.ColourHolder;
import dev.xulu.settings.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

/**
 * @author Elementars
 * @since 6/29/2020 - 2:50 PM
 */
public class Armor extends Element {

    private final Value<String> aligned = register(new Value<>("Aligned", this, "X-axis", new String[]{
            "X-axis", "Y-axis"
    }))
            .onChanged(onChangedValue -> {
                switch (onChangedValue.getNew()) {
                    case "X-axis":
                        height = 16;
                        width = 79;
                        break;
                    case "Y-axis":
                        height = 72;
                        width = 16;
                        break;
                }
            });
    private final Value<Boolean> progress = register(new Value<>("Durab. Bar", this, false));
    private final Value<Integer> w = register(new Value<>("Width", this, 0, 0, 100));
    private final Value<Integer> h = register(new Value<>("Height", this, 0, 0, 100));
    private final Value<Boolean> cf = register(new Value<>("Custom Font", this, false));
    private final Value<Boolean> damage = register(new Value<>("Damage", this, false));
    private final Value<Boolean> fixed = register(new Value<>("Fixed", this, false));

    public Armor() {
        super("Armor");
    }

    @Override
    public void onEnable() {
        switch (aligned.getValue()) {
            case "X-axis":
                height = 16;
                width = 79;
                break;
            case "Y-axis":
                height = 72;
                width = 16;
                break;
        }
    }

    private static RenderItem itemRender = Minecraft.getMinecraft()
            .getRenderItem();

    @Override
    public void onRender() {
        GlStateManager.enableTexture2D();

        ScaledResolution resolution = new ScaledResolution(mc);
        int i = resolution.getScaledWidth() / 2;
        int iteration = 0;
        int l = resolution.getScaledHeight() - 55 - (mc.player.isInWater() ? 10 : 0);
        for (ItemStack is : mc.player.inventory.armorInventory) {
            iteration++;
            int x_2 = 0;
            int y_2 = 0;
            switch (aligned.getValue()) {
                case "X-axis":
                    x_2 = (int) x - 90 + (9 - iteration) * 20 + 2 - 12;
                    y_2 = (int) y;
                    if (fixed.getValue()) {
                        x_2 = i - 90 + (9 - iteration) * 20 + 2;
                        y_2 = l;
                    }
                    break;
                case "Y-axis":
                    x_2 = (int) x;
                    y_2 = (int) y - (iteration - 1) * 18 + 54;
                    if (fixed.getValue()) {
                        x_2 = i;
                        y_2 = l - (iteration - 1) * 18 + 54;
                    }
                    break;
            }
            if (progress.getValue()) {
                switch (aligned.getValue()) {
                    case "X-axis":
                        Gui.drawRect(x_2 - 1, y_2 + 16, (x_2 + 19), (y_2 - 51) + 16, ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 60));
                        break;
                    case "Y-axis":
                        Gui.drawRect(x_2, y_2, (x_2 + 69), (y_2 + 18), ColorUtils.changeAlpha(ColorUtils.Colors.BLACK, 60));
                        break;
                }
                if (is.isEmpty()) continue;
                float percentBar = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                float red = 1 - percentBar;
                switch (aligned.getValue()) {
                    case "X-axis":
                        Gui.drawRect(x_2 - 1, y_2 + 16, (x_2 + 19), (int) (y_2 - (percentBar * 51)) + 16, ColorUtils.changeAlpha(ColourHolder.toHex((int) (red * 255), (int) (percentBar * 255), 0), 150));
                        break;
                    case "Y-axis":
                        Gui.drawRect(x_2, y_2, (int) (x_2 + (percentBar * 69)), (y_2 + 18), ColorUtils.changeAlpha(ColourHolder.toHex((int) (red * 255), (int) (percentBar * 255), 0), 150));
                        break;
                }
            } else {
                if (is.isEmpty()) continue;
            }
            GlStateManager.enableDepth();

            itemRender.zLevel = 200F;
            itemRender.renderItemAndEffectIntoGUI(is, x_2, y_2);
            itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x_2, y_2, "");
            itemRender.zLevel = 0F;

            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            mc.fontRenderer.drawStringWithShadow(s, x_2 + 19 - 2 - mc.fontRenderer.getStringWidth(s), (int) y + 9, 0xffffff);
            if (damage.getValue()) {
                float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                float red = 1 - green;
                int dmg = 100 - (int) (red * 100);
                switch (aligned.getValue()) {
                    case "X-axis":
                        if (cf.getValue()) {
                            Xulu.cFontRenderer.drawStringWithShadow(dmg + "", x_2 + 8 - Xulu.cFontRenderer.getStringWidth(dmg + "") / 2, y_2 - 11, ColourHolder.toHex((int) (red * 255), (int) (green * 255), 0));
                        } else {
                            fontRenderer.drawStringWithShadow(dmg + "", x_2 + 9 - fontRenderer.getStringWidth(dmg + "") / 2, y_2 - 11, ColourHolder.toHex((int) (red * 255), (int) (green * 255), 0));
                        }
                        break;
                    case "Y-axis":
                        if (cf.getValue()) {
                            Xulu.cFontRenderer.drawStringWithShadow(dmg + "", x_2 + 18, y_2 + 5, ColourHolder.toHex((int) (red * 255), (int) (green * 255), 0));
                        } else {
                            fontRenderer.drawStringWithShadow(dmg + "", x_2 + 18, y_2 + 5, ColourHolder.toHex((int) (red * 255), (int) (green * 255), 0));
                        }
                        break;
                }
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }
}
