package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.combat.AutoCrystal;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.ColorUtils;
import com.elementars.eclient.util.ListHelper;
import com.elementars.eclient.util.Wrapper;
import dev.xulu.settings.Value;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GodInfo extends Element {

    private final Value<Boolean> cf = register(new Value<>("Custom Font", this, false));

    public GodInfo() {
        super("GodInfo");
    }

    @Override
    public void onRender() {
        int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) totems++;
        List<String> info = new ArrayList<>(
                Arrays.asList(
                        "HTR",
                        "PLR",
                        String.valueOf(totems),
                        "PING " + (mc.getConnection() != null && mc.player != null && mc.getConnection().getPlayerInfo(mc.player.entityUniqueID) != null ? mc.getConnection().getPlayerInfo(mc.player.entityUniqueID).getResponseTime() : "-1")
                )
        );
        EntityPlayer enemy = mc.world.playerEntities.stream()
                .filter(player -> !player.getName().equals(mc.player.getName()))
                .filter(player -> !Friends.isFriend(player.getName()))
                .min(Comparator.comparing(player -> mc.player.getDistance(player)))
                .orElse(null);
        if (enemy != null) info.add("LBY");
        width = fontRenderer.getStringWidth(ListHelper.longest(info)) + 2;
        height = ((fontRenderer.FONT_HEIGHT + 1) * info.size()) + 1;
        float yCount = (float) y;
        for (String s : info) {
            if (cf.getValue()) {
                Xulu.cFontRenderer.drawStringWithShadow(s, (float) x + 1, yCount + 1, ColorUtils.changeAlpha(getColor(s, enemy), Global.hudAlpha.getValue()));
            } else {
                Wrapper.getMinecraft().fontRenderer.drawStringWithShadow(s, (float) x + 1, yCount + 1, ColorUtils.changeAlpha(getColor(s, enemy), Global.hudAlpha.getValue()));
            }
            yCount += 10;
        }
    }

    private Vec3d[] offset = new Vec3d[]{
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, 1),
            new Vec3d(-1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, -1, 0)
    };

    public boolean checkSurround(EntityPlayer player) {
        if (mc.player == null || mc.world == null) return false;
        boolean isSafe = true;
        for (final Vec3d pos : offset) {
            if (mc.world.getBlockState(new BlockPos(player.getPositionVector()).add(pos.x, pos.y, pos.z)).getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(new BlockPos(player.getPositionVector()).add(pos.x, pos.y, pos.z)).getBlock() != Blocks.BEDROCK) {
                isSafe = false;
            }
        }
        return isSafe;
    }

    private int getColor(String s, EntityPlayer e) {
        switch (s) {
            case "HTR":
                if (e != null && mc.player.getDistance(e) <= Xulu.VALUE_MANAGER.<Float>getValueT("Hit Range", AutoCrystal.class).getValue()) {
                    return new Color(0, 255, 0).getRGB();
                } else {
                    return new Color(255, 0, 0).getRGB();
                }
            case "PLR":
                if (e != null && mc.player.getDistance(e) <= Xulu.VALUE_MANAGER.<Float>getValueT("Hit Range", AutoCrystal.class).getValue()) {
                    return new Color(0, 255, 0).getRGB();
                } else {
                    return new Color(255, 0, 0).getRGB();
                }
            case "LBY":
                if (e != null && checkSurround(e)) {
                    return new Color(0, 255, 0).getRGB();
                } else {
                    return new Color(255, 0, 0).getRGB();
                }
        }
        if (s.startsWith("PING")) {
            int num = Integer.valueOf(s.substring(5));
            if (num > 100) {
                return new Color(255, 0, 0).getRGB();
            } else {
                return new Color(0, 255, 0).getRGB();
            }
        } else {
            try {
                int num = Integer.valueOf(s);
                if (num > 0) {
                    return new Color(0, 255, 0).getRGB();
                } else {
                    return new Color(255, 0, 0).getRGB();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return -1;
            }
        }
    }
}
