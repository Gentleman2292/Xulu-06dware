package com.elementars.eclient.module.combat;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.EntityUtil;
import com.elementars.eclient.util.TargetPlayers;
import dev.xulu.settings.Value;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class AutoEz extends Module {
    private ArrayList<String> dummy = new ArrayList<>(
            Arrays.asList("Change this in the settings")
    );
    private final Value<String> message = register(new Value<>("Message", this, "NAME has been put in the montage", dummy));
    private final Value<Boolean> mode = register(new Value<>("Names", this, true));
    private final Value<Boolean> autocityboss = register(new Value<>("AutoCityboss", this, false));
    private final Value<String> acmessage = register(new Value<>("AC message", this, "NAME ez pop", dummy));
    private final Value<Boolean> acmode = register(new Value<>("AC Names", this, true));
    private final Value<Boolean> autoezhelmet = register(new Value<>("AutoEZHelmet", this, false));
    private final Value<String> ahmessage = register(new Value<>("AH message", this, "NAME ez helmet", dummy));
    private final Value<Boolean> ahmode = register(new Value<>("AH Names", this, true));
    //private /* synthetic */ EntityPlayer target;
    private /* synthetic */ EntityPlayer target;
    private ConcurrentHashMap<String, Integer> targettedplayers = new ConcurrentHashMap<>();
    private ArrayList<EntityPlayer> targets;
    private EntityEnderCrystal crystal;
    private /* synthetic */ int hasBeenCombat;
    private ConcurrentHashMap<String, Integer> totemplayers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> helmetplayers = new ConcurrentHashMap<>();

    private int acdelay;
    private int ahdelay;

    public static AutoEz INSTANCE;

    public AutoEz() {
        super("AutoEZ", "Sends toxic messages for you (use NAME like in Welcome)", Keyboard.KEY_NONE, Category.COMBAT, true);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        if (this.isToggled() && event.getTarget() instanceof EntityEnderCrystal) {
            this.crystal = (EntityEnderCrystal) event.getTarget();
        }
        if (this.isToggled() && event.getTarget() instanceof EntityPlayer) {
            final EntityPlayer target = (EntityPlayer) event.getTarget();
            //  || !mc.world.playerEntities.contains(target)
            if (target.getHealth() <= 0.0f || target.isDead) {
                if (mode.getValue()) {
                    sendChatMessage(message.getValue(), target.getName());
                }else{
                    sendChatMessage(message.getValue(), null);
                }

            }

        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (mc.player != null) {
            EntityLivingBase entity = event.getEntityLiving();
            if (entity != null) {
                if (EntityUtil.isPlayer(entity)) {
                    final EntityPlayer target = (EntityPlayer) entity;
                    if (target.getHealth() <= 0.0f) {
                        String name = target.getName();
                        if (TargetPlayers.targettedplayers.containsKey(name)) {
                            if (mode.getValue()) {
                                sendChatMessage(message.getValue(), name);
                            }else{
                                sendChatMessage(message.getValue(), null);
                            }
                            TargetPlayers.targettedplayers.remove(name);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (mc.player.isDead) {
            this.hasBeenCombat = 0;
        }
        for (final Entity entity : mc.world.getLoadedEntityList()) {
            if (!EntityUtil.isPlayer(entity)) {
                continue;
            }
            final EntityPlayer player = (EntityPlayer)entity;
            if (player.getHealth() > 0.0f) {
                continue;
            }
            final String name2 = player.getName();
            if (TargetPlayers.targettedplayers.containsKey(name2)) {
                if (mode.getValue()) {
                    sendChatMessage(message.getValue(), name2);
                }else{
                    sendChatMessage(message.getValue(), null);
                }
                TargetPlayers.targettedplayers.remove(name2);
            }
        }
        if (this.autocityboss.getValue() && acdelay == 0) {
            for (final Entity entity : mc.world.getLoadedEntityList()) {
                if (!EntityUtil.isPlayer(entity)) {
                    continue;
                }
                final EntityPlayer player = (EntityPlayer) entity;
                if (player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
                    this.totemplayers.put(player.getName(), 20);
                    continue;
                }
                final String name2 = player.getName();
                if (TargetPlayers.targettedplayers.containsKey(name2) && this.totemplayers.containsKey(name2) && mc.world.playerEntities.contains(player)) {
                    if (acmode.getValue()) {
                        sendChatMessage(acmessage.getValue(), name2);
                    } else {
                        sendChatMessage(acmessage.getValue(), null);
                    }
                    TargetPlayers.targettedplayers.remove(name2);
                    this.totemplayers.remove(name2);
                    acdelay = 1500;
                    break;
                }
            }
        }
        if (this.autoezhelmet.getValue() && ahdelay == 0) {
            for (final Entity entity : mc.world.getLoadedEntityList()) {
                if (!EntityUtil.isPlayer(entity)) {
                    continue;
                }
                final EntityPlayer player = (EntityPlayer) entity;
                boolean helmet = false;
                for (ItemStack itemStack : player.getArmorInventoryList()) {
                    if (itemStack != null && itemStack.getItem() == Items.DIAMOND_HELMET)
                        helmet = true;
                }
                if (helmet) {
                    helmetplayers.put(player.getName(), 20);
                    continue;
                }
                final String name2 = player.getName();
                if (TargetPlayers.targettedplayers.containsKey(name2) && helmetplayers.containsKey(name2) && mc.world.playerEntities.contains(player)) {
                    if (acmode.getValue()) {
                        sendChatMessage(ahmessage.getValue(), name2);
                    } else {
                        sendChatMessage(ahmessage.getValue(), null);
                    }
                    TargetPlayers.targettedplayers.remove(name2);
                    this.helmetplayers.remove(name2);
                    ahdelay = 1500;
                    break;
                }
            }
        }

        this.totemplayers.forEach((name, timeout) -> {
            if (timeout <= 0) {
                this.totemplayers.remove(name);
            }
            else {
                this.totemplayers.put(name, timeout - 1);
            }
        });
        this.helmetplayers.forEach((name, timeout) -> {
            if (timeout <= 0) {
                this.helmetplayers.remove(name);
            }
            else {
                this.helmetplayers.put(name, timeout - 1);
            }
        });
        if (acdelay > 0) {
            acdelay -= 1;
        }
        if (ahdelay > 0) {
            ahdelay -= 1;
        }
    }
    
    private void sendChatMessage(String message, @Nullable String name) {
        String text = (name == null ? message : message.replaceAll("NAME", name));
        mc.player.sendChatMessage(text);
    }
}