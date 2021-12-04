package com.elementars.eclient;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class DiscordRP {
    private boolean running = true;
    private long created = 0;
    public void start() {
        Minecraft mc = Minecraft.getMinecraft();
        ServerData svr;
        DiscordRPC lib = DiscordRPC.INSTANCE;
        String applicationId = "671154973274275850";
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Ready!");
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        DiscordRichPresence presence = new DiscordRichPresence();

        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details = "Playing epicly";
        presence.state = "lol";
        presence.largeImageKey = "xulu2";
        lib.Discord_UpdatePresence(presence);
        // in a worker thread
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try{
                    presence.largeImageKey = "xulurevamp3";
                    presence.largeImageText = "Xulu " + Xulu.version;
                    if (mc.isIntegratedServerRunning()) {
                        presence.details = "Singleplayer";
                        presence.state = "In Game";
                    }
                    else if (mc.getCurrentServerData() != null) {
                        if (!mc.getCurrentServerData().serverIP.equals(presence.state)) {
                            presence.details = "Playing a server";
                            presence.state = mc.getCurrentServerData().serverIP;
                        }
                    }
                    else {
                        presence.details = "Menu";
                        presence.state = "Idle";
                    }
                    lib.Discord_UpdatePresence(presence);
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
    }
    public void shutdown() {
        DiscordRPC lib = DiscordRPC.INSTANCE;
        lib.Discord_Shutdown();
    }
}
