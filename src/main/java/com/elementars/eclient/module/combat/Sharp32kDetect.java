package com.elementars.eclient.module.combat;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagList;
import org.lwjgl.input.Keyboard;

public class Sharp32kDetect extends Module {
    private final Value<Boolean> watermark = register(new Value<>("Watermark", this, true));
    private final Value<Boolean> color = register(new Value<>("Color", this, false));
    
    public Sharp32kDetect() {
        super("32kDetect", "Detects held 32ks", Keyboard.KEY_NONE, Category.COMBAT, true);
    }

    private Set<EntityPlayer> sword = Collections.newSetFromMap(new WeakHashMap());
    public static final Minecraft mc = Minecraft.getMinecraft();

    private boolean is32k(EntityPlayer player, ItemStack stack) {
        if(stack.getItem() instanceof ItemSword) {
            NBTTagList enchants = stack.getEnchantmentTagList();
            if(enchants != null) {
                for (int i = 0; i < enchants.tagCount(); i++) {
                    if(enchants.getCompoundTagAt(i).getShort("lvl") >= 32767) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void onUpdate() {
        for (EntityPlayer player : Sharp32kDetect.mc.world.playerEntities) {
            if (player.equals(Sharp32kDetect.mc.player)) continue;
            if (is32k(player, player.itemStackMainHand) && !this.sword.contains(player)) {
                if (watermark.getValue())
                    if(color.getValue())
                        Command.sendChatMessage("&4" + player.getDisplayNameString() + " is holding a 32k");
                    else
                        Command.sendChatMessage(player.getDisplayNameString() + " is holding a 32k");
                else
                    if(color.getValue())
                        Command.sendRawChatMessage("&4" + player.getDisplayNameString() + " is holding a 32k");
                    else
                        Command.sendRawChatMessage(player.getDisplayNameString() + " is holding a 32k");
                this.sword.add(player);
            }
            if (!this.sword.contains(player) || is32k(player, player.itemStackMainHand)) continue;
            if(watermark.getValue())
                if(color.getValue())
                    Command.sendChatMessage("&2" + player.getDisplayNameString() + " is no longer holding a 32k");
                else
                    Command.sendChatMessage(player.getDisplayNameString() + " is no longer holding a 32k");
            else
                if(color.getValue())
                    Command.sendRawChatMessage("&2" + player.getDisplayNameString() + " is no longer holding a 32k");
                else
                    Command.sendRawChatMessage(player.getDisplayNameString() + " is no longer holding a 32k");
            this.sword.remove(player);
        }
    }
}