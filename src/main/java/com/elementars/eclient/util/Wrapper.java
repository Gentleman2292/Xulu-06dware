package com.elementars.eclient.util;

import dev.xulu.settings.FileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;

public class Wrapper {
    public static FileManager fileManager;

    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }
    public static EntityPlayerSP getPlayer() {
        return getMinecraft().player;
    }
    public static World getWorld() {
        return getMinecraft().world;
    }

    public static FileManager getFileManager()
    {
        if (fileManager == null) {
            fileManager = new FileManager();
        }
        return fileManager;
    }
}
