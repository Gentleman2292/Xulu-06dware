package com.elementars.eclient.module.misc;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.events.AnnouncerRegistry;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;

public class Announcer extends Module {
    private AnnouncerRegistry announcerRegistry;

    public static int delayy;

    public static Value<String> mode;
    public static Value<Integer> delay;
    public static Value<Boolean> walk;
    public static Value<Boolean> craftedItem;
    public static Value<Boolean> pickUpItem;
    public static Value<Boolean> smeltedItem;
    public static Value<Boolean> respawn;
    public static Value<Boolean> blockPlaced;
    public static Value<Boolean> blockBroke;
    public static Value<Boolean> itemDroped;
    public static Value<Boolean> openChat;
    public static Value<Boolean> pickBlock;
    public static Value<Boolean> command;
    public static Value<Boolean> fullScreen;
    public static Value<Boolean> pauseGame;
    public static Value<Boolean> openInv;
    public static Value<Boolean> playerList;
    public static Value<Boolean> screenShot;
    public static Value<Boolean> swapHand;
    public static Value<Boolean> sneak;
    public static Value<Boolean> Perspective;
    public static Value<Boolean> jump;
    public static Value<Boolean> attack;
    public static Value<Boolean> eatting;

    public Announcer() {
        super("Announcer", "Announce EVERYTHING in chat", Keyboard.KEY_NONE, Category.MISC, true);
        announcerRegistry = new AnnouncerRegistry();
        mode = register(new Value<>("Mode", this, "English", new ArrayList<>(
                Arrays.asList("English", "Hebrew")
        )));
        delay = register(new Value<Integer>("Delay", this, 10, 0, 60));
        walk = register(new Value<>("Walk", this, true));
        craftedItem = register(new Value<>("CraftedItem", this, true));
        pickUpItem = register(new Value<>("PickUpItem", this, true));
        smeltedItem = register(new Value<>("SmeltedItem", this, true));
        respawn = register(new Value<>("Respawn", this, true));
        blockPlaced = register(new Value<>("BlockPlaced", this, true));
        blockBroke = register(new Value<>("BlockBroke", this, true));
        itemDroped = register(new Value<>("ItemDropped", this, true));
        openChat = register(new Value<>("OpenChat", this, true));
        pickBlock = register(new Value<>("PickBlock", this, true));
        command = register(new Value<>("Command", this, true));
        fullScreen = register(new Value<>("FullScreen", this, true));
        pauseGame = register(new Value<>("PauseGame", this, true));
        openInv = register(new Value<>("OpenInv", this, true));
        playerList = register(new Value<>("PlayerList", this, true));
        screenShot = register(new Value<>("ScreenShot", this, true));
        swapHand = register(new Value<>("SwapHand", this, true));
        sneak = register(new Value<>("Sneak", this, true));
        Perspective = register(new Value<>("Perspective", this, true));
        jump = register(new Value<>("Jump", this, true));
        attack = register(new Value<>("Attack", this, true));
        eatting = register(new Value<>("Eating", this, true));
    }

    @Override
    public void onUpdate() {
        if (delayy > 0) --delayy;
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(announcerRegistry);
        Command.sendChatMessage("Announcer ON");
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(announcerRegistry);
        Command.sendChatMessage("Announcer OFF");
    }
}
