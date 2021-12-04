//
// Decompiled by Procyon v0.5.36
//

package com.elementars.eclient.module.render;

import java.awt.Color;

import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.combat.AutoCrystal;
import com.elementars.eclient.util.BlockInteractionHelper;
import com.elementars.eclient.util.XuluTessellator;
import dev.xulu.settings.Value;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import io.netty.util.internal.ConcurrentSet;
import org.lwjgl.input.Keyboard;

public class VoidESP extends Module {
    private final Value<Integer> range = register(new Value<>("Range", this, 8, 1, 32));
    private final Value<Integer> activateAtY = register(new Value<>("ActivateAtY", this, 32, 1, 512));
    private final Value<String> holeMode = register(new Value<>("HoleMode", this, "Sides", new ArrayList<>(
            Arrays.asList("Sides", "Above")
    )));
    private final Value<String> renderMode = register(new Value<>("RenderMode", this, "Down", new ArrayList<>(
            Arrays.asList("Down", "Block")
    )));
    private final Value<Integer> red = register(new Value<>("Red", this, 255, 0, 255));
    private final Value<Integer> green = register(new Value<>("Green", this, 0, 0, 255));
    private final Value<Integer> blue = register(new Value<>("Blue", this, 0, 0, 255));
    private final Value<Integer> alpha = register(new Value<>("Alpha", this, 128, 0, 255));
    private ConcurrentSet<BlockPos> voidHoles;

    public VoidESP() {
        super("VoidESP", "Highlights possible void holes", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    @Override
    public void onUpdate() {
        if (VoidESP.mc.player.getPosition().y > this.activateAtY.getValue()) {
            return;
        }
        if (this.voidHoles == null) {
            this.voidHoles = (ConcurrentSet<BlockPos>)new ConcurrentSet();
        }
        else {
            this.voidHoles.clear();
        }
        final List<BlockPos> blockPosList = BlockInteractionHelper.getCircle(AutoCrystal.getPlayerPos(), 0, (float) this.range.getValue(), false);
        for (final BlockPos pos : blockPosList) {
            if (VoidESP.mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK)) {
                continue;
            }
            if (this.isAnyBedrock(pos, Offsets.center)) {
                continue;
            }
            boolean aboveFree = false;
            if (!this.isAnyBedrock(pos, Offsets.above)) {
                aboveFree = true;
            }
            if (this.holeMode.getValue().equalsIgnoreCase("Above")) {
                if (!aboveFree) {
                    continue;
                }
                this.voidHoles.add(pos);
            }
            else {
                boolean sidesFree = false;
                if (!this.isAnyBedrock(pos, Offsets.north)) {
                    sidesFree = true;
                }
                if (!this.isAnyBedrock(pos, Offsets.east)) {
                    sidesFree = true;
                }
                if (!this.isAnyBedrock(pos, Offsets.south)) {
                    sidesFree = true;
                }
                if (!this.isAnyBedrock(pos, Offsets.west)) {
                    sidesFree = true;
                }
                if (!this.holeMode.getValue().equalsIgnoreCase("Sides") || (!aboveFree && !sidesFree)) {
                    continue;
                }
                this.voidHoles.add(pos);
            }
        }
    }

    private boolean isAnyBedrock(final BlockPos origin, final BlockPos[] offset) {
        for (final BlockPos pos : offset) {
            if (VoidESP.mc.world.getBlockState(origin.add((Vec3i)pos)).getBlock().equals(Blocks.BEDROCK)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onWorldRender(final RenderEvent event) {
        if (VoidESP.mc.player == null || this.voidHoles == null || this.voidHoles.isEmpty()) {
            return;
        }
        XuluTessellator.prepare(7);
        this.voidHoles.forEach(blockPos -> this.drawBlock(blockPos, (int) this.red.getValue(), (int) this.green.getValue(), (int) this.blue.getValue()));
        XuluTessellator.release();
    }

    private void drawBlock(final BlockPos blockPos, final int r, final int g, final int b) {
        final Color color = new Color(r, g, b, (int) this.alpha.getValue());
        int mask = 0;
        if (this.renderMode.getValue().equalsIgnoreCase("Block")) {
            mask = 63;
        }
        if (this.renderMode.getValue().equalsIgnoreCase("Down")) {
            mask = 1;
        }
        XuluTessellator.drawBox(blockPos, color.getRGB(), mask);
    }

    public String getHudInfo() {
        return this.holeMode.getValue();
    }

    private enum RenderMode
    {
        DOWN,
        BLOCK;
    }

    private enum HoleMode
    {
        SIDES,
        ABOVE;
    }

    private static class Offsets
    {
        static final BlockPos[] center;
        static final BlockPos[] above;
        static final BlockPos[] aboveStep1;
        static final BlockPos[] aboveStep2;
        static final BlockPos[] north;
        static final BlockPos[] east;
        static final BlockPos[] south;
        static final BlockPos[] west;

        static {
            center = new BlockPos[] { new BlockPos(0, 1, 0), new BlockPos(0, 2, 0) };
            above = new BlockPos[] { new BlockPos(0, 3, 0), new BlockPos(0, 4, 0) };
            aboveStep1 = new BlockPos[] { new BlockPos(0, 3, 0) };
            aboveStep2 = new BlockPos[] { new BlockPos(0, 4, 0) };
            north = new BlockPos[] { new BlockPos(0, 1, -1), new BlockPos(0, 2, -1) };
            east = new BlockPos[] { new BlockPos(1, 1, 0), new BlockPos(1, 2, 0) };
            south = new BlockPos[] { new BlockPos(0, 1, 1), new BlockPos(0, 2, 1) };
            west = new BlockPos[] { new BlockPos(-1, 1, 0), new BlockPos(-1, 2, 0) };
        }
    }
}
