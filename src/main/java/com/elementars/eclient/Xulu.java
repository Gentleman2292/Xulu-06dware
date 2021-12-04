package com.elementars.eclient;

import com.elementars.eclient.cape.Capes;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.command.CommandManager;
import com.elementars.eclient.event.EventManager;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.AnnouncerRegistry;
import com.elementars.eclient.event.events.EventKey;
import com.elementars.eclient.event.events.KeyRegistry;
import com.elementars.eclient.font.*;
import com.elementars.eclient.font.custom.CustomFont;
import com.elementars.eclient.font.rainbow.RainbowCycle;
import com.elementars.eclient.guirewrite.HUD;
import com.elementars.eclient.macro.MacroManager;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.module.ModuleManager;
import com.elementars.eclient.module.core.Global;
import com.elementars.eclient.util.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.xulu.newgui.NewGUI;
import dev.xulu.newgui.util.ColorUtil;
import dev.xulu.settings.ValueManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Optional;

/**
 * @author Elementars
 * @version v1.2.2
 * @since 12/26/2019 - --:-- AM/PM
 */
@Mod(modid = Xulu.id, name = Xulu.name, version = Xulu.version)
public class Xulu implements Helper {
    public static final String id = "eclient", name = "Xulu", version = "v1.5.2", creator = "Elementars";
    public Minecraft mc = Minecraft.getMinecraft();
    public static DecimalFormat df = new DecimalFormat("##,###,###,###,##0.00");

    public static RainbowCycle rainbowCycle = new RainbowCycle();

    public static GLSLSandboxShader backgroundShader;
    public static long initTime;

    @Mod.Instance
    public static Xulu INSTANCE;

    private DiscordRP discordRP = new DiscordRP();

    public static final TaskScheduler TASK_SCHEDULER = new TaskScheduler();
    public static final ValueManager VALUE_MANAGER = new ValueManager();
    public static final EventManager EVENT_MANAGER = new EventManager();
    public static final ModuleManager MODULE_MANAGER = new ModuleManager();
    public static final MacroManager MACRO_MANAGER = new MacroManager();
    public static final CommandManager COMMAND_MANAGER = new CommandManager();
    public static NewGUI newGUI;
    public static HUD hud;
    public static boolean CustomFont;
    public static int rgb;

    public static CFontManager cFontRenderer;

    //Server not responding stuff
    private boolean shownLag = false;
    private int beginY;
    private int endY;
    private int yCount;

    public static String[] getFonts() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    }

    public static String getCorrectFont(String stringIn) {
        for (String s : getFonts()) {
            if (s.equalsIgnoreCase(stringIn)) {
                return s;
            }
        }
        return null;
    }

    public static void setCFontRenderer(String stringIn, int size) {
        try{
            if (getCorrectFont(stringIn) == null) {
                Command.sendChatMessage("Invalid font!");
                return;
            }
            if (stringIn.equalsIgnoreCase("Comfortaa Regular")) {
                CFontManager.customFont = new CustomFont(new Font("Comfortaa Regular", Font.PLAIN, size), true, false);
                return;
            }
            CFontManager.customFont = new CustomFont(new Font(getCorrectFont(stringIn), Font.PLAIN, size), true, false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setcFontRendererDefault() {
        try{
            CFontManager.customFont = new CustomFont(new Font("Verdana", Font.PLAIN, 18), true, false);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setXdolfFontRenderer(String stringIn, int size) {
        try{
            if (getCorrectFont(stringIn) == null && !stringIn.equalsIgnoreCase("Xulu")) {
                Command.sendChatMessage("Invalid font!");
                return;
            }
            CFontManager.xFontRenderer = new XFontRenderer(new Font(getCorrectFont(stringIn), Font.PLAIN, size), true, 8);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setXdolfFontRendererDefault() {
        try{
            CFontManager.xFontRenderer = new XFontRenderer(new Font("Verdana", Font.PLAIN, 36), true, 8);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("[" + name + "] Starting client, " + version + ", created by " + creator);
        Display.setTitle(name + " " + version);
        discordRP.start();
        ColorTextUtils.initColors();
        Capes.getUsersCape();
        FontInit fontInit = new FontInit();
        fontInit.initFonts();

        MODULE_MANAGER.init();
        COMMAND_MANAGER.init();
        cFontRenderer = new CFontManager();
        fileManager.loadDummy();
        newGUI = new NewGUI();
        hud = new HUD();
        HUD.registerElements();
        hud.refreshPanel();
        LagCompensator.INSTANCE = new LagCompensator();
        LagUtil.INSTANCE = new LagUtil();
        ConnectionUtil.INSTANCE = new ConnectionUtil();
        fileManager.loadHacks();
        fileManager.loadDrawn();
        fileManager.loadSettingsList();
        fileManager.loadBinds();
        fileManager.loadMacros();
        fileManager.loadPrefix();
        fileManager.loadNewGui();
        fileManager.loadFriends();
        fileManager.loadEnemies();
        fileManager.loadHUD();
        fileManager.loadFont();
        fileManager.loadStickyNote();
        fileManager.loadWelcomeMessage();
        fileManager.loadXray();
        fileManager.loadSearch();
        fileManager.loadWaypoints();
        fileManager.loadNicks();
        EVENT_BUS.register(new KeyRegistry());
        EVENT_BUS.register(this);
        AnnouncerRegistry.initAnnouncer();
        EVENT_MANAGER.register(this);
        rgb = Color.HSBtoRGB(0.01f, Global.rainbowSaturation.getValue() / 255f, Global.rainbowLightness.getValue() / 255f);
    }

    @SubscribeEvent
    public void onRenderGui(final RenderGameOverlayEvent.Post event) {
        CustomFont = MODULE_MANAGER.getModule(com.elementars.eclient.module.core.CustomFont.class).isToggled();
        if (beginY != (CustomFont ? -cFontRenderer.getHeight() : -fontRenderer.FONT_HEIGHT)) beginY = CustomFont ? (int) -cFontRenderer.getHeight() : -fontRenderer.FONT_HEIGHT;
        if (endY != 3.0f) endY = 3;
        ScaledResolution s = new ScaledResolution(mc);
        Rainbow.updateRainbow();
        rgb = Rainbow.rgb;
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }
        if (!Xulu.VALUE_MANAGER.<Boolean>getValueByName("Rainbow Watermark").getValue()) {
            rgb = ColorUtil.getClickGUIColor().getRGB();
        }
        final String playername = mc.player.getName();

        int height = s.getScaledHeight() - 3;
        if (Global.coordinates.getValue()) {
            //String coords = mc.player.posX + ", " + mc.player.posY + ", " + mc.player.posZ + " (" + (mc.player.dimension == -1 ? mc.player.posX / 8 + ", " + mc.player.posY / 8 + ", " + mc.player.posZ / 8 : mc.player.posX * 8 + ", " + mc.player.posY * 8 + ", " + mc.player.posZ * 8 ) + ")";
            String coords = df.format(mc.player.posX) + ChatFormatting.GRAY +  ", " + ChatFormatting.RESET + df.format(mc.player.posY) + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + df.format(mc.player.posZ) + ChatFormatting.GRAY + " [" + ChatFormatting.RESET + (mc.player.dimension == -1 ? df.format(mc.player.posX * 8) + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + df.format(mc.player.posY) + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + df.format(mc.player.posZ * 8) : df.format(mc.player.posX / 8) + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + df.format(mc.player.posY) + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + df.format(mc.player.posZ / 8) ) + ChatFormatting.GRAY + "]";
            if (CustomFont) {
                cFontRenderer.drawStringWithShadow(coords, 3.0f, height - cFontRenderer.getHeight() - 1 - (mc.ingameGUI.getChatGUI().getChatOpen() ? 11 : 0), ColorUtils.changeAlpha(Color.white.getRGB(), Global.hudAlpha.getValue()));
            }else{
                fontRenderer.drawStringWithShadow(coords, 3.0f, height - fontRenderer.FONT_HEIGHT - (mc.ingameGUI.getChatGUI().getChatOpen() ? 11 : 0), ColorUtils.changeAlpha(Color.white.getRGB(), Global.hudAlpha.getValue()));
            }
        }
        if (Global.direction.getValue()) {
            if (CustomFont) {
                cFontRenderer.drawStringWithShadow(getFacing(mc.player.getHorizontalFacing().getName().toUpperCase()), 3.0f, height - cFontRenderer.getHeight() - (Global.coordinates.getValue() ? 11 : 1) - (mc.ingameGUI.getChatGUI().getChatOpen() ? 11 : 0), ColorUtils.changeAlpha(Color.white.getRGB(), Global.hudAlpha.getValue()));
            }else{
                fontRenderer.drawStringWithShadow(getFacing(mc.player.getHorizontalFacing().getName().toUpperCase()), 3.0f, height - fontRenderer.FONT_HEIGHT - (Global.coordinates.getValue() ? 10 : 0) - (mc.ingameGUI.getChatGUI().getChatOpen() ? 11 : 0), ColorUtils.changeAlpha(Color.white.getRGB(), Global.hudAlpha.getValue()));
            }
        }
        if (Global.showLag.getValue() && !mc.isIntegratedServerRunning()) {
            if (LagUtil.INSTANCE.getLastTimeDiff() != 0 && LagUtil.INSTANCE.getLastTimeDiff() > 5000) {
                String text = String.format("Server has been lagging for %01.1fs", ((float) (LagUtil.INSTANCE.getLastTimeDiff() - 1000)) / 1000);
                if (!shownLag)
                    yCount = beginY;
                if (CustomFont) {
                    cFontRenderer.drawStringWithShadow(text, s.getScaledWidth()/2 - (cFontRenderer.getStringWidth(text) / 2), yCount, (Global.lagColor.getValue().equalsIgnoreCase("Default") ? Color.LIGHT_GRAY.getRGB() : ColorUtil.getClickGUIColor().getRGB()));
                } else {
                    fontRenderer.drawStringWithShadow(text, s.getScaledWidth()/2 - (fontRenderer.getStringWidth(text) / 2), yCount, (Global.lagColor.getValue().equalsIgnoreCase("Default") ? Color.LIGHT_GRAY.getRGB() : ColorUtil.getClickGUIColor().getRGB()));
                }
                shownLag = true;
                if (yCount != endY) yCount++;
            } else {
                if (shownLag) {
                    String text = "Server has been lagging for 0.0s";
                    if (CustomFont) {
                        cFontRenderer.drawStringWithShadow(text, s.getScaledWidth()/2 - (cFontRenderer.getStringWidth(text) / 2), yCount, (Global.lagColor.getValue().equalsIgnoreCase("Default") ? Color.LIGHT_GRAY.getRGB() : ColorUtil.getClickGUIColor().getRGB()));
                    } else {
                        fontRenderer.drawStringWithShadow(text, s.getScaledWidth()/2 - (fontRenderer.getStringWidth(text) / 2), yCount, (Global.lagColor.getValue().equalsIgnoreCase("Default") ? Color.LIGHT_GRAY.getRGB() : ColorUtil.getClickGUIColor().getRGB()));
                    }
                    if (yCount != beginY) {
                        yCount--;
                    } else {
                        shownLag = false;
                    }
                }
            }
        }
    }

    public static void save() {
        fileManager.saveHacks();
        fileManager.saveBinds();
        fileManager.saveDrawn();
        fileManager.saveSettingsList();
        fileManager.saveMacros();
        fileManager.savePrefix();
        fileManager.saveNewGui();
        fileManager.saveFriends();
        fileManager.saveEnemies();
        fileManager.saveHUD();
        fileManager.saveFont();
        fileManager.saveStickyNote();
        fileManager.saveWelcomeMessage();
        fileManager.saveDummy();
        fileManager.saveXray();
        fileManager.saveSearch();
        fileManager.saveWaypoints();
        fileManager.saveNicks();
        System.out.println(Xulu.name + " Saved!");
    }

    public static void load() {
        fileManager.loadHacks();
        fileManager.loadBinds();
        fileManager.loadDrawn();
        fileManager.loadSettingsList();
        fileManager.loadMacros();
        fileManager.loadPrefix();
        fileManager.loadNewGui();
        fileManager.loadFriends();
        fileManager.loadEnemies();
        fileManager.loadHUD();
        fileManager.loadFont();
        fileManager.loadStickyNote();
        fileManager.loadWelcomeMessage();
        fileManager.loadDummy();
        fileManager.loadXray();
        fileManager.loadSearch();
        fileManager.loadWaypoints();
        fileManager.loadNicks();
        System.out.println(Xulu.name + " Loaded!");
    }

    public void stopClient() {
        save();
        MODULE_MANAGER.getModules().forEach(Module::destroy);
        EVENT_MANAGER.unregister(this);
    }

    private String getFacing(String in) {
        String facing = getTitle(in);
        String add;
        if (in.equalsIgnoreCase("North")) add = (" \u00A77[\u00A7r-Z\u00A77]");
        else if (in.equalsIgnoreCase("East")) add = (" \u00A77[\u00A7r+X\u00A77]");
        else if (in.equalsIgnoreCase("South")) add = (" \u00A77[\u00A7r+Z\u00A77]");
        else if (in.equalsIgnoreCase("West")) add = (" \u00A77[\u00A7r-X\u00A77]");
        else add = (" ERROR");
        return facing + add;
    }

    public static String getTitle(String in) {
        in = Character.toUpperCase(in.toLowerCase().charAt(0)) + in.toLowerCase().substring(1);
        return in;
    }

    @EventTarget
    public void onKey(EventKey eventKey) {
        MACRO_MANAGER.runMacros(eventKey.getKey());
        MODULE_MANAGER.getModules().stream().filter(module -> module.getKey() == eventKey.getKey()).forEach(Module::toggle);
    }

    public static class Rainbow {
        private static int rgb;
        public static int a;
        public static int r;
        public static int g;
        public static int b;
        static float hue = 0.01f;

        public static void updateRainbow() {
            rgb = Color.HSBtoRGB(hue, Global.rainbowSaturation.getValue() / 255f, Global.rainbowLightness.getValue() / 255f);
            a = (rgb >>> 24) & 0xFF;
            r = (rgb >>> 16) & 0xFF;
            g = (rgb >>> 8) & 0xFF;
            b = rgb & 0xFF;
            hue += Global.rainbowspeed2.getValue() / 100000f;
            if (hue > 1) hue -= 1;
        }
    }
}