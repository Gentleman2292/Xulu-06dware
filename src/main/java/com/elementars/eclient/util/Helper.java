package com.elementars.eclient.util;

import dev.xulu.settings.FileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public interface Helper {
    Minecraft mc = Minecraft.getMinecraft();
    World world = Minecraft.getMinecraft().world;
    EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    FileManager fileManager = Wrapper.getFileManager();
    EventBus EVENT_BUS = MinecraftForge.EVENT_BUS;
}
