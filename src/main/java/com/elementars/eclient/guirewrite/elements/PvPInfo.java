package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.ListHelper;
import com.elementars.eclient.util.Wrapper;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.Value;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class PvPInfo extends Element {
    private final Value<Boolean> rainbow = register(new Value<>("Rainbow", this, false));

    public static boolean attack = false;
    public static boolean place = false;
    public static boolean surround = false;

    private Vec3d[] offset = new Vec3d[]{
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, 1),
            new Vec3d(-1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, -1, 0)
    };

    public PvPInfo() {
        super("PvPInfo");
    }

    public void checkSurround() {
        if (mc.player == null || mc.world == null) return;
        boolean isSafe = true;
        for (final Vec3d pos : offset) {
            if (mc.world.getBlockState(new BlockPos(mc.player.getPositionVector()).add(pos.x, pos.y, pos.z)).getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(new BlockPos(mc.player.getPositionVector()).add(pos.x, pos.y, pos.z)).getBlock() != Blocks.BEDROCK) {
                isSafe = false;
            }
        }
        surround = isSafe;
    }

    @Override
    public void onRender() {
        checkSurround();
        float yCount = (float) y;
        int color = ColorUtil.getClickGUIColor().getRGB();
        int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) totems++;
        String[] pvpinfo = new String[] {
                "ATT: " + getFromBoolean(attack),
                "PLC: " + getFromBoolean(place),
                "FOB: " + getFromBoolean(surround),
                "PING: " + getPing((mc.getConnection() != null && mc.player != null && mc.getConnection().getPlayerInfo(mc.player.entityUniqueID) != null ? mc.getConnection().getPlayerInfo(mc.player.entityUniqueID).getResponseTime() : -1)),
                "TOTEMS: " + getTotems(totems),
                "AT: " + getAutoTrap(),
                "SU: " + getSurround(),
                "CA: " + getCaura()
        };
        width = fontRenderer.getStringWidth(ListHelper.longest(pvpinfo)) + 2;
        height = ((fontRenderer.FONT_HEIGHT + 1) * pvpinfo.length) + 1;
        if (this.rainbow.getValue()) {
            color = Xulu.rgb;
        }
        if (Xulu.CustomFont) {
            for (String s : pvpinfo) {
                Xulu.cFontRenderer.drawStringWithShadow(s, x + 1, yCount + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
                yCount += 10;
            }
        } else {
            for (String s : pvpinfo) {
                fontRenderer.drawStringWithShadow(s, (float) x + 1, yCount + 1, ColorUtils.changeAlpha(color, Global.hudAlpha.getValue()));
                yCount += 10;
            }
        }
    }

    private String getTotems(int totems) {
        if (totems > 0) {
            return "" + ChatFormatting.GREEN + totems;
        } else {
            return "" + ChatFormatting.RED + totems;
        }
    }

    private String getPing(long ping) {
        if (ping > 100) {
            return "" + ChatFormatting.RED + ping;
        } else {
            return "" + ChatFormatting.GREEN + ping;
        }
    }

    private String getAutoTrap() {

        if (Xulu.MODULE_MANAGER.getModuleByName("AutoTrap") != null) {
            if (Xulu.MODULE_MANAGER.getModuleByName("AutoTrap").isToggled()) {
                return ChatFormatting.GREEN + "ON";
            } else {
                return ChatFormatting.RED + "OFF";
            }
        }
        return "NULL";
    }

    private String getSurround() {
        if (Xulu.MODULE_MANAGER.getModuleByName("Surround") != null) {
            if (Xulu.MODULE_MANAGER.getModuleByName("Surround").isToggled()) {
                return ChatFormatting.GREEN + "ON";
            } else {
                return ChatFormatting.RED + "OFF";
            }
        }
        return "NULL";
    }

    private String getCaura() {
        boolean caura = false;
        if (Xulu.MODULE_MANAGER.getModuleByName("AutoCrystal") != null) {
            caura = Xulu.MODULE_MANAGER.getModuleByName("AutoCrystal").isToggled();
            if (caura) return ChatFormatting.GREEN + "ON";
        }
        if (Xulu.MODULE_MANAGER.getModuleByName("AutoCrystalO") != null) {
            caura = Xulu.MODULE_MANAGER.getModuleByName("AutoCrystalO").isToggled();
            if (caura) return ChatFormatting.GREEN + "ON";
        }
        if (Xulu.MODULE_MANAGER.getModuleByName("AutoCrystalX") != null) {
            caura = Xulu.MODULE_MANAGER.getModuleByName("AutoCrystalX").isToggled();
            if (caura) return ChatFormatting.GREEN + "ON";
        }
        return ChatFormatting.RED + "OFF";
    }

    public String getFromBoolean(boolean b) {
        if (b) {
            return ChatFormatting.GREEN + "TRUE";
        } else {
            return ChatFormatting.RED + "FALSE";
        }
    }
}
