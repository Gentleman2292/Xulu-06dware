package com.elementars.eclient.module.render;

import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventRenderBlock;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import net.minecraft.block.Block;
import org.lwjgl.input.Keyboard;

import java.util.*;

public class Xray extends Module {
    private static final ArrayList<Block> BLOCKS = new ArrayList<>();

    public static Xray INSTANCE;

    public Xray() {
        super("Xray", "See through blocks!", Keyboard.KEY_NONE, Category.RENDER, true);
        initblocks();
        INSTANCE = this;
    }

    public static void initblocks() {
        Xray.BLOCKS.add(Block.getBlockFromName("coal_ore"));
        Xray.BLOCKS.add(Block.getBlockFromName("iron_ore"));
        Xray.BLOCKS.add(Block.getBlockFromName("gold_ore"));
        Xray.BLOCKS.add(Block.getBlockFromName("redstone_ore"));
        Xray.BLOCKS.add(Block.getBlockById(74));
        Xray.BLOCKS.add(Block.getBlockFromName("lapis_ore"));
        Xray.BLOCKS.add(Block.getBlockFromName("diamond_ore"));
        Xray.BLOCKS.add(Block.getBlockFromName("emerald_ore"));
        Xray.BLOCKS.add(Block.getBlockFromName("quartz_ore"));
        Xray.BLOCKS.add(Block.getBlockFromName("clay"));
        Xray.BLOCKS.add(Block.getBlockFromName("glowstone"));
        Xray.BLOCKS.add(Block.getBlockById(8));
        Xray.BLOCKS.add(Block.getBlockById(9));
        Xray.BLOCKS.add(Block.getBlockById(10));
        Xray.BLOCKS.add(Block.getBlockById(11));
        Xray.BLOCKS.add(Block.getBlockFromName("crafting_table"));
        Xray.BLOCKS.add(Block.getBlockById(61));
        Xray.BLOCKS.add(Block.getBlockById(62));
        Xray.BLOCKS.add(Block.getBlockFromName("torch"));
        Xray.BLOCKS.add(Block.getBlockFromName("ladder"));
        Xray.BLOCKS.add(Block.getBlockFromName("tnt"));
        Xray.BLOCKS.add(Block.getBlockFromName("coal_block"));
        Xray.BLOCKS.add(Block.getBlockFromName("iron_block"));
        Xray.BLOCKS.add(Block.getBlockFromName("gold_block"));
        Xray.BLOCKS.add(Block.getBlockFromName("diamond_block"));
        Xray.BLOCKS.add(Block.getBlockFromName("emerald_block"));
        Xray.BLOCKS.add(Block.getBlockFromName("redstone_block"));
        Xray.BLOCKS.add(Block.getBlockFromName("lapis_block"));
        Xray.BLOCKS.add(Block.getBlockFromName("fire"));
        Xray.BLOCKS.add(Block.getBlockFromName("mossy_cobblestone"));
        Xray.BLOCKS.add(Block.getBlockFromName("mob_spawner"));
        Xray.BLOCKS.add(Block.getBlockFromName("end_portal_frame"));
        Xray.BLOCKS.add(Block.getBlockFromName("enchanting_table"));
        Xray.BLOCKS.add(Block.getBlockFromName("bookshelf"));
        Xray.BLOCKS.add(Block.getBlockFromName("command_block"));
    }

    @EventTarget
    public void onRender(EventRenderBlock event) {
        final Block block = event.getBlockState().getBlock();
        if (shouldXray(block)) {
            if (mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelFlat(event.getBlockAccess(), event.getBakedModel(), event.getBlockState(), event.getBlockPos(), event.getBufferBuilder(), event.isCheckSides(), event.getRand())) {
                event.setRenderable(true);
            }
        }
        event.setCancelled(true);
    }

    public static ArrayList<Block> getBLOCKS() {
        return BLOCKS;
    }

    public static boolean shouldXray(Block block) {
        return BLOCKS.contains(block);
    }

    public static boolean addBlock(String string) {
        if (Block.getBlockFromName(string) != null) {
            Xray.BLOCKS.add(Block.getBlockFromName(string));
            return true;
        } else {
            return false;
        }
    }

    public static boolean delBlock(String string) {
        if (Block.getBlockFromName(string) != null) {
            Xray.BLOCKS.remove(Block.getBlockFromName(string));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onEnable() {
        mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        mc.renderGlobal.loadRenderers();
    }
}