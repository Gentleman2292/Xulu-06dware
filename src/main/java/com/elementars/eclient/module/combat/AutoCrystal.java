package com.elementars.eclient.module.combat;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.enemy.Enemies;
import com.elementars.eclient.event.ArrayHelper;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.*;
import com.elementars.eclient.friend.Friends;
import com.elementars.eclient.guirewrite.elements.PvPInfo;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.misc.FakePlayer;
import com.elementars.eclient.util.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.settings.Value;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AutoCrystal extends Module {
    public AutoCrystal() {
        super("AutoCrystal", "Xulu AutoCrystal", Keyboard.KEY_NONE, Category.COMBAT, true);
    }

    private final List<BlockPos> placedCrystals = new ArrayList<>();

    private BlockPos render;
    private BlockPos renderOld;
    private Entity renderEnt;
    // we need this cooldown to not place from old hotbar slot, before we have switched to crystals
    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    private int oldSlot = -1;
    private int newSlot;
    private int waitCounter;
    private int placeCounter;
    EnumFacing f;
    public static boolean isRand;

    private final Value<Boolean> explode = register(new Value<>("Hit", this, true));
    private final Value<String> explodeMode = register(new Value<>("Hit Mode", this, "All", new String[]{
            "All", "OnlyOwn"
    }));
    private final Value<Boolean> sync = register(new Value<>("Sync Break", this, true));
    private final Value<Integer> hitAttempts = register(new Value<>("Hit Attempts", this, 5, 0, 20));
    private final Value<Integer> hitRetryDelay = register(new Value<>("Retry Delay", this, 2, 0, 20));
    private final Value<Integer> waitTick = register(new Value<>("Tick Delay", this, 1, 0, 20));
    private final Value<Integer> placeTick = register(new Value<>("Place Delay", this, 1, 0, 20));
    private final Value<Float> range = register(new Value<>("Hit Range", this, 5.0f, 0.0f, 10.0f));
    private final Value<Float> walls = register(new Value<>("Walls Range", this, 3.5f, 0.0f, 10.0f));
    private final Value<Float> ER = register(new Value<>("Enemy Range", this, 5.0f, 0.0f, 10.0f));
    private final Value<Boolean> pre = register(new Value<>("Prioritize Enemies", this, false));
    private final Value<Boolean> antiWeakness = register(new Value<>("Anti Weakness", this, true));
    private final Value<Boolean> nodesync = register(new Value<>("No Desync", this, true));
    private final Value<Boolean> place = register(new Value<>("Place", this, true));
    private final Value<Boolean> oneHole = register(new Value<>("1.13 Mode", this, false));
    private final Value<Boolean> noSuicide = register(new Value<>("No Suicide", this, true));
    private final Value<Boolean> autoSwitch = register(new Value<>("Auto Switch", this, true));
    private final Value<Float> placeRange = register(new Value<>("Place Range", this, 5.0f, 0.0f, 10.0f));
    private final Value<Integer> minDmg = register(new Value<>("Min Damage", this, 5, 0, 40));
    private final Value<Integer> facePlace = register(new Value<>("Faceplace HP", this, 6, 0, 40));
    private final Value<Boolean> armor = register(new Value<>("Armor Place", this, true));
    private final Value<Integer> armorDmg = register(new Value<>("Armor %", this, 15, 0, 100));
    private final Value<Boolean> raytrace = register(new Value<>("Raytrace", this, false));
    private final Value<Boolean> rotate = register(new Value<>("Rotate", this, true));
    private final Value<String> rotateMode = register(new Value<>("Rot. Mode", this, "New", new String[]{
            "Old", "New"
    }))
            .visibleWhen(string -> rotate.getValue());
    private final Value<Boolean> randRotations = register(new Value<>("Random Rotations", this, true));
    private final Value<Boolean> lockOn = register(new Value<>("Lock On", this, true));
    private final Value<Boolean> fast = register(new Value<>("Fast Mode", this, false));
    private final Value<String> fastType = register(new Value<>("Fast Type", this, "Instant", new String[]{
            "Instant", "Ignore"
    }));
    private final Value<Boolean> toggleOff = register(new Value<>("Toggle Off", this, false));
    private final Value<Integer> toggleHealth = register(new Value<>("Toggle Off Health", this, 10, 0, 20));
    private final Value<Boolean> chat = register(new Value<>("Toggle Msgs", this, true));
    private final Value<Boolean> watermark = register(new Value<>("Watermark", this, true));
    private final Value<String> echatcolor = register(new Value<>("Enable Color", this, "White", ColorTextUtils.colors));
    private final Value<String> dchatcolor = register(new Value<>("Disable Color", this, "White", ColorTextUtils.colors));
    private final Value<Boolean> renderDamage = register(new Value<>("Render Damage", this, false));
    private final Value<Boolean> damageWhite = register(new Value<>("Damage Color White", this, false));
    private final Value<Boolean> renderBoolean = register(new Value<>("Render", this, true));
    private final Value<String> rendermode = register(new Value<>("RenderMode", this, "Solid", new ArrayList<>(
            Arrays.asList("Solid", "Outline", "Full")
    )));
    private final Value<Boolean> rainbow = register(new Value<>("Esp Rainbow", this, false));
    private final Value<Integer> espR = register(new Value<>("Esp Red", this, 200, 0, 255));
    private final Value<Integer> espG = register(new Value<>("Esp Green", this, 50, 0, 255));
    private final Value<Integer> espB = register(new Value<>("Esp Blue", this, 200, 0, 255));
    private final Value<Integer> espA = register(new Value<>("Esp Alpha", this, 50, 0, 255));
    private final Value<Integer> espF = register(new Value<>("Full Alpha", this, 80, 0, 255));
    private final Value<Integer> maxSelfDmg = register(new Value<>("Max Self Dmg", this, 10, 0, 36));
    private final Value<Boolean> noGappleSwitch = register(new Value<>("No Gap Switch", this, false));
    private final Value<Boolean> smoothEsp = register(new Value<>("Smooth ESP", this, false));
    private final Value<Integer> smoothSpeed = register(new Value<>("Smooth Speed", this, 5, 1, 20));

    public boolean isActive = false;

    ConcurrentHashMap<BlockPos, Integer> fadeList = new ConcurrentHashMap<>();

    private Map<EntityEnderCrystal, Integer> attemptMap = new WeakHashMap<>();
    private Map<EntityEnderCrystal, Integer> retryMap = new WeakHashMap<>();

    private final List<Entity> ignoreList = new ArrayList<>();

    public void onUpdate() {
        if (rotateMode.getValue().equalsIgnoreCase("Old")) {
            doAutoCrystal();
        }
    }

    private void doAutoCrystal() {
        isRand = randRotations.getValue();
        isActive = false;
        if(mc.player == null || mc.player.isDead) return; // bruh
        if (shouldPause()) {
            resetRotation();
            return;
        }
        if (mc.player.getHealth() <= this.toggleHealth.getValue() && this.toggleOff.getValue()) {
            toggle();
        }
        if (fast.getValue()) {
            if (waitTick.getValue() > 0) {
                if (waitCounter < waitTick.getValue()) {
                    waitCounter++;
                } else {
                    waitCounter = 0;
                }
            }
        }
        this.fadeList.forEach((pos, alpha) -> {
            if (alpha <= 0) {
                this.fadeList.remove(pos);
            }
            else {
                this.fadeList.put(pos, alpha - (int) smoothSpeed.getValue());
            }
        });
        int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                    crystalSlot = l;
                    break;
                }
            }
        }

        retryMap.forEach((entityEnderCrystal, integer) -> {
            if (entityEnderCrystal.isDead) {
                retryMap.remove(entityEnderCrystal);
            } else {
                if (retryMap.get(entityEnderCrystal) != 0) {
                    retryMap.put(entityEnderCrystal, integer - 1);
                }
            }
        });

        EntityEnderCrystal crystal = null;
        List<EntityEnderCrystal> crystals = new ArrayList<>();
        for (Entity e : mc.world.loadedEntityList) {
            if (e instanceof EntityEnderCrystal) {
                EntityEnderCrystal ec = (EntityEnderCrystal) e;
                if (!(mc.player.getDistance(ec) <= range.getValue())) continue;
                if (!checkCrystal(ec)) continue;
                if (attemptMap.containsKey(ec) && attemptMap.get(ec).equals(hitAttempts.getValue())) {
                    if (retryMap.containsKey(ec) && retryMap.get(ec) == 0) {
                        attemptMap.put(ec, 0);
                        retryMap.remove(ec);
                    } else if (!(retryMap.containsKey(ec))){
                        retryMap.put(ec, hitRetryDelay.getValue());
                        continue;
                    } else {
                        continue;
                    }
                }
                crystals.add(ec);
            }
        }
        if (!crystals.isEmpty()) {
            crystals.sort(Comparator.comparing(c -> mc.player.getDistance(c)));
            crystal = crystals.get(0);
        }
        if (explode.getValue() && crystal != null && (!fast.getValue() || waitCounter == 0)) {

            if (!mc.player.canEntityBeSeen(crystal) && mc.player.getDistance(crystal) > walls.getValue()) {
                PvPInfo.attack = false;
                return;
            }

            if (!fast.getValue()) {
                if (waitTick.getValue() > 0) {
                    if (waitCounter < waitTick.getValue()) {
                        waitCounter++;
                        PvPInfo.attack = false;
                        return;
                    } else {
                        waitCounter = 0;
                    }
                }
            }

            if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                if (!isAttacking) {
                    // save initial player hand
                    oldSlot = mc.player.inventory.currentItem;
                    PvPInfo.attack = true;
                    isAttacking = true;
                }
                // search for sword and tools in hotbar
                newSlot = -1;
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (stack == ItemStack.EMPTY) {
                        continue;
                    }
                    if ((stack.getItem() instanceof ItemSword)) {
                        newSlot = i;
                        break;
                    }
                    if ((stack.getItem() instanceof ItemTool)) {
                        newSlot = i;
                        break;
                    }
                }
                // check if any swords or tools were found
                if (newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                    switchCooldown = true;
                }
            }
            PvPInfo.attack = true;

            isActive = true;
            if (rotate.getValue()) {
                lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
            }
            if (sync.getValue())
                mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
            else
                mc.playerController.attackEntity(mc.player, crystal);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            isActive = false;
            if (fast.getValue()) {
                switch(fastType.getValue()) {
                    case "Instant":
                        crystal.setDead();
                        break;
                    case "Ignore":
                        ignoreList.add(crystal);
                        break;
                }
            }
            if (attemptMap.containsKey(crystal)) {
                attemptMap.put(crystal, attemptMap.get(crystal) + 1);
            } else {
                attemptMap.put(crystal, 1);
            }
            return;
        } else {
            resetRotation();
            if (oldSlot != -1) {
                mc.player.inventory.currentItem = oldSlot;
                oldSlot = -1;
            }
            isAttacking = false;
            isActive = false;
        }

        if (placeTick.getValue() > 0) {
            if (placeCounter < placeTick.getValue()) {
                placeCounter++;
                PvPInfo.place = false;
                return;
            } else {
                placeCounter = 0;
            }
        }

        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return;
        }

        List<BlockPos> blocks = findCrystalBlocks();
        final List<Entity> entities = new ArrayList<>();
        final List<Entity> enemies = new ArrayList<>();
        for (EntityPlayer player : mc.world.playerEntities) {
            if (Friends.isFriend(player.getName())) continue;
            entities.add(player);
        }
        entities.sort(Comparator.comparing(e -> mc.player.getDistance(e)));
        entities.removeIf(entity -> mc.player.getDistance(entity) > ER.getValue());
        if (pre.getValue()) {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (Friends.isFriend(player.getName()) || !Enemies.isEnemy(player.getName())) continue;
                enemies.add(player);
            }
            enemies.sort(Comparator.comparing(e -> mc.player.getDistance(e)));
            enemies.removeIf(entity -> mc.player.getDistance(entity) > ER.getValue());
            if (!enemies.isEmpty()) {
                entities.clear();
                entities.addAll(enemies);
            }
        }

        BlockPos q = null;
        double damage = .5;
        double dist = 69696969D;
        for (Entity entity : entities) {
            if(entity == mc.player) continue;
            if (((EntityLivingBase) entity).getHealth() <= 0 || entity.isDead || mc.player == null) {
                continue;
            }
            for (BlockPos blockPos : blocks) {
                double b = entity.getDistanceSq(blockPos);
                if (entity.isDead || ((EntityLivingBase) entity).getHealth() <= 0.0f) {
                    continue;
                }
                if (b >= 169) {
                    continue; // If this block if further than 13 (3.6^2, less calc) blocks, ignore it. It'll take no or very little damage
                }
                double d = calculateDamage(blockPos.getX() + .5, blockPos.getY() + 1, blockPos.getZ() + .5, entity);
                if(d < minDmg.getValue() && ((EntityLivingBase) entity).getHealth() + ((EntityLivingBase) entity).getAbsorptionAmount() > (AutoTotem.isFullArmor((EntityPlayer) entity) && !isArmorLow((EntityPlayer) entity) ? facePlace.getValue() : 36)) {
                    continue;
                }
                if (d > damage) {
                    double self = calculateDamage(blockPos.getX() + .5, blockPos.getY() + 1, blockPos.getZ() + .5, mc.player);
                    // If this deals more damage to ourselves than it does to our target, continue. This is only ignored if the crystal is sure to kill our target but not us.
                    // Also continue if our crystal is going to hurt us.. alot
                    if ((self > d && !(d < ((EntityLivingBase) entity).getHealth())) || self - .5 > mc.player.getHealth()) {
                        continue;
                    }
                    if(self > maxSelfDmg.getValue())
                        continue;
                    if (lockOn.getValue()) {
                        if ((q == null && d == .5) || dist > entity.getDistanceSq(blockPos)) {
                            damage = d;
                            dist = entity.getDistanceSq(blockPos);
                            q = blockPos;
                            renderEnt = entity;
                        }
                    } else {
                        damage = d;
                        q = blockPos;
                        renderEnt = entity;
                    }
                }
            }
        }
        if (damage == .5) {
            render = null;
            renderEnt = null;
            resetRotation();
            return;
        }
        render = q;

        if (place.getValue()) {
            if(mc.player == null) {
                PvPInfo.place = false;
                return;
            }
            isActive = true;
            if(rotate.getValue()){
                lookAtPacket(q.getX() + .5, q.getY() - .5, q.getZ() + .5, mc.player);
            }
            RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(q.getX() + .5, q.getY() - .5d, q.getZ() + .5));
            if(raytrace.getValue()) {
                if(result == null || result.sideHit == null) {
                    q = null;
                    f = null;
                    render = null;
                    resetRotation();
                    isActive = false;
                    PvPInfo.place = false;
                    return;
                } else {
                    f = result.sideHit;
                }
            }

            if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                if (autoSwitch.getValue()) {
                    if(noGappleSwitch.getValue() && isEatingGap()){
                        isActive = false;
                        resetRotation();
                        PvPInfo.place = false;
                        return;
                    } else {
                        isActive = true;
                        mc.player.inventory.currentItem = crystalSlot;
                        resetRotation();
                        switchCooldown = true;
                    }
                }
                PvPInfo.place = false;
                return;
            }
            // return after we did an autoswitch
            if (switchCooldown) {
                switchCooldown = false;
                PvPInfo.place = false;
                return;
            }
            //mc.playerController.processRightClickBlock(mc.player, mc.world, q, f, new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
            if(q != null && mc.player != null) {
                PvPInfo.place = true;
                isActive = true;
                if (raytrace.getValue() && f != null) {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                } else {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                }
                TargetPlayers.addTargetedPlayer(renderEnt.getName());
            }
            isActive = false;
        }
    }

    @Override
    public void onRender() {
        if (render == null || mc.player == null) return;
        if (!renderBoolean.getValue()) return;
        if (renderDamage.getValue()) {
            int color1 = espR.getValue();
            int color2 = espG.getValue();
            int color3 = espB.getValue();
            if (rainbow.getValue()) {
                color1 = RainbowUtils.r;
                color2 = RainbowUtils.g;
                color3 = RainbowUtils.b;
            }
            Plane pos = VectorUtils.toScreen(render.getX() + .5, render.getY() + .5, render.getZ() + .5);
            float damage = calculateDamage(render.getX() + .5, render.getY() + 1, render.getZ() + .5, renderEnt);
            String text = String.valueOf(MathUtil.round(damage, 1));
            fontRenderer.drawStringWithShadow(text, (float) pos.getX() - fontRenderer.getStringWidth(text)/2, (float) pos.getY() - fontRenderer.FONT_HEIGHT/2, damageWhite.getValue() ? -1 : new Color(color1, color2, color3).getRGB());
        }
    }

    public void onWorldRender(RenderEvent event) {
        if (render == null || mc.player == null) return;
        if (!renderBoolean.getValue()) return;
        if (renderOld != null && renderOld != render) {
            fadeList.put(renderOld, espA.getValue());
        }
        int color1 = espR.getValue();
        int color2 = espG.getValue();
        int color3 = espB.getValue();
        if (rainbow.getValue()) {
            color1 = RainbowUtils.r;
            color2 = RainbowUtils.g;
            color3 = RainbowUtils.b;
        }
        if (rendermode.getValue().equalsIgnoreCase("Solid")) {
            XuluTessellator.prepare(GL11.GL_QUADS);
            XuluTessellator.drawBox(render, color1, color2, color3, espA.getValue(), GeometryMasks.Quad.ALL);
            XuluTessellator.release();
        }
        else if (rendermode.getValue().equalsIgnoreCase("Outline")) {
            final IBlockState iBlockState2 = mc.world.getBlockState(render);
            final Vec3d interp2 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
            XuluTessellator.drawBoundingBox(iBlockState2.getSelectedBoundingBox(mc.world, render).grow(0.0020000000949949026D).offset(-interp2.x, -interp2.y, -interp2.z), 1.5f, color1, color2, color3, espA.getValue());
        }
        else if (rendermode.getValue().equalsIgnoreCase("Full")) {
            final IBlockState iBlockState3 = mc.world.getBlockState(render);
            final Vec3d interp3 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
            XuluTessellator.drawFullBox(iBlockState3.getSelectedBoundingBox(mc.world, render).grow(0.0020000000949949026D).offset(-interp3.x, -interp3.y, -interp3.z), render, 1.5f, color1, color2, color3, espA.getValue(), espF.getValue());
        }
        if (this.smoothEsp.getValue()) {
            for (BlockPos pos : fadeList.keySet()) {
                if (fadeList.get(pos) < 0) {
                    fadeList.remove(pos);
                    continue;
                }
                if (rendermode.getValue().equalsIgnoreCase("Solid")) {
                    XuluTessellator.prepare(GL11.GL_QUADS);
                    XuluTessellator.drawBox(pos, color1, color2, color3, fadeList.get(pos), GeometryMasks.Quad.ALL);
                    XuluTessellator.release();
                } else if (rendermode.getValue().equalsIgnoreCase("Outline")) {
                    final IBlockState iBlockState2 = mc.world.getBlockState(pos);
                    final Vec3d interp2 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                    XuluTessellator.drawBoundingBox(iBlockState2.getSelectedBoundingBox(mc.world, pos).grow(0.0020000000949949026D).offset(-interp2.x, -interp2.y, -interp2.z), 1.5f, color1, color2, color3, fadeList.get(pos));
                } else if (rendermode.getValue().equalsIgnoreCase("Full")) {
                    final IBlockState iBlockState3 = mc.world.getBlockState(pos);
                    final Vec3d interp3 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                    XuluTessellator.drawFullBox(iBlockState3.getSelectedBoundingBox(mc.world, pos).grow(0.0020000000949949026D).offset(-interp3.x, -interp3.y, -interp3.z), render, 1.5f, color1, color2, color3, fadeList.get(pos), espF.getValue());
                }
            }
        }
        renderOld = render;
    }

    @Override
    public String getHudInfo() {
        if (renderEnt != null) {
            if (mc.player.getDistance(renderEnt) <= range.getValue()) {
                return ChatFormatting.GREEN + renderEnt.getName();
            } else {
                return ChatFormatting.RED + renderEnt.getName();
            }
        } else {
            return null;
        }
    }

    private boolean checkCrystal(Entity e) {
        if (e == null) return false;
        if (noSuicide.getValue()) {
            double self = calculateDamage(e.posX, e.posY, e.posZ, mc.player);
            if (self - .5 > mc.player.getHealth()) {
                return false;
            }
        }
        switch(explodeMode.getValue()) {
            case "OnlyOwn":
                if (render != null && render.getDistance((int)e.posX, (int)e.posY, (int)e.posZ) <= 3.0)
                    return true;
                if (!placedCrystals.isEmpty()) {
                    synchronized (placedCrystals) {
                        try {
                            for (BlockPos pos : placedCrystals) {
                                if (pos.getDistance((int) e.posX, (int) e.posY, (int) e.posZ) <= 3.0) {
                                    return true;
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                break;
            case "All":
                return true;
        }
        return false;
    }

    private boolean isArmorLow(EntityPlayer player) {
        if (!armor.getValue()) return false;
        for (ItemStack stack : player.getArmorInventoryList()) {
            float green = ((float) stack.getMaxDamage() - (float) stack.getItemDamage()) / (float) stack.getMaxDamage();
            float red = 1 - green;
            int dmg = 100 - (int) (red * 100);
            if (dmg <= armorDmg.getValue()) {
                return true;
            }
        }
        return false;
    }

    private boolean isEatingGap(){
        return mc.player.getHeldItemMainhand().getItem() instanceof ItemAppleGold && mc.player.isHandActive();
    }


    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }

    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                && mc.world.getBlockState(boost).getBlock() == Blocks.AIR
                && (oneHole.getValue() || mc.world.getBlockState(boost2).getBlock() == Blocks.AIR)
                && isTrulyEmpty(mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)))
                && isTrulyEmpty(mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)));
    }

    private boolean isTrulyEmpty(List<Entity> list) {
        if (list.isEmpty()) {
            return true;
        } else {
            boolean isEmpty = true;
            for (Entity e : list) {
                if (!ignoreList.contains(e)) {
                    isEmpty = false;
                    break;
                }
            }
            return isEmpty;
        }
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    private List<BlockPos> findCrystalBlocks() {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), placeRange.getValue(), placeRange.getValue().intValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = (double) entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1.0D;
        /*if (entity instanceof EntityLivingBase)
            finald = getBlastReduction((EntityLivingBase) entity,getDamageMultiplied(damage));*/
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;

            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage = damage - (damage / 4);
            }
            //   damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0F);
            return damage;
        } else {
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            return damage;
        }
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    //Rotation System (No Spoof)

    private static float newYaw;
    private static float newPitch;

    public static void updateRotations() {
        newYaw = mc.player.rotationYaw;
        newPitch = mc.player.rotationPitch;
    }

    public static void restoreRotations() {
        mc.player.rotationYaw = newYaw;
        mc.player.rotationYawHead = newYaw;
        mc.player.rotationPitch = newPitch;
        resetRotation();
    }

    public static void setPlayerRotations(final float yaw, final float pitch) {
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    @EventTarget
    public void onMotionPre(MotionEvent event) {
        if (rotate.getValue() && rotateMode.getValue().equalsIgnoreCase("New")) {
            updateRotations();
            doAutoCrystal();
        }
    }

    @EventTarget
    public void onMotionPost(MotionEventPost event) {
        if (rotate.getValue() && rotateMode.getValue().equalsIgnoreCase("New")) {
            restoreRotations();
        }
    }

    //Better Rotation Spoofing System:

    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;

    //this modifies packets being sent so no extra ones are made. NCP used to flag with "too many packets"
    private static void setYawAndPitch(float yaw1, float pitch1) {
        Random rand = new Random(2);
        yaw = yaw1 + (isRand ? rand.nextFloat() / 100 : 0);
        pitch = pitch1 + (isRand ? rand.nextFloat() / 100 : 0);
        isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;

        double len = Math.sqrt(dirx*dirx + diry*diry + dirz*dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        //to degree
        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;

        yaw += 90f;

        return new double[]{yaw,pitch};
    }

    @EventTarget
    public void onSend(EventSendPacket event) {
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer && rotate.getValue()) {
            if (isSpoofingAngles) {
                ((CPacketPlayer) packet).yaw = (float) yaw;
                ((CPacketPlayer) packet).pitch = (float) pitch;
            }
        }
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            final CPacketPlayerTryUseItemOnBlock cpacket = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
            if (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                synchronized (placedCrystals) {
                    placedCrystals.add(cpacket.getPos());
                }
            }
        }
    }

    @EventTarget
    public void onRecieve(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketSoundEffect && nodesync.getValue()) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity e : Minecraft.getMinecraft().world.loadedEntityList) {
                    if (e instanceof EntityEnderCrystal) {
                        if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                            e.setDead();
                        }
                        placedCrystals.removeIf(p_Pos -> p_Pos.getDistance((int)packet.getX(), (int)packet.getY(), (int)packet.getZ()) <= 6.0);
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        Xulu.EVENT_MANAGER.register(this);
        isActive = false;
        if(chat.getValue() && mc.player != null) {
            if (this.watermark.getValue())
                Command.sendChatMessage(ColorTextUtils.getColor(echatcolor.getValue()) + "AutoCrystal ON");
            else
                Command.sendRawChatMessage(ColorTextUtils.getColor(echatcolor.getValue()) + "AutoCrystal ON");
        }
    }

    @Override
    public void onDisable() {
        Xulu.EVENT_MANAGER.unregister(this);
        PvPInfo.place = false;
        PvPInfo.attack = false;
        render = null;
        renderEnt = null;
        resetRotation();
        isActive = false;
        ignoreList.clear();
        attemptMap.clear();
        retryMap.clear();
        if(chat.getValue()) {
            if (this.watermark.getValue())
                Command.sendChatMessage(ColorTextUtils.getColor(dchatcolor.getValue()) + "AutoCrystal OFF");
            else
                Command.sendRawChatMessage(ColorTextUtils.getColor(dchatcolor.getValue()) + "AutoCrystal OFF");
        }
    }

    private boolean shouldPause() {
        if (Xulu.MODULE_MANAGER.getModule(Surround.class).isToggled() && Surround.isExposed() && Xulu.MODULE_MANAGER.getModuleT(Surround.class).findObiInHotbar() != -1) {
            return true;
        }
        if (Xulu.MODULE_MANAGER.getModule(AutoTrap.class).isToggled()) {
            return true;
        }
        if (Xulu.MODULE_MANAGER.getModule(HoleFill.class).isToggled()) {
            return true;
        }
        if (Xulu.MODULE_MANAGER.getModule(HoleBlocker.class).isToggled() && HoleBlocker.isExposed() && Xulu.MODULE_MANAGER.getModuleT(Surround.class).findObiInHotbar() != -1) {
            return true;
        }
        return false;
    }
}