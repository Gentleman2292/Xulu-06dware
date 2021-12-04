package com.elementars.eclient.module.render;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import com.elementars.eclient.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.*;

/**
 * Created by 086 on 25/01/2018.
 */
public class BossStack extends Module {
    public static Value<String> mode;
    private static Value<Float> scale;

    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");

    public BossStack() {
        super("BossStack", "Stacks boss bars", Keyboard.KEY_NONE, Category.RENDER, true);
        mode = register(new Value<>("Mode", this, "Stack", new ArrayList<>(
                Arrays.asList("Remove", "Stack", "Minimize")
        )));
        scale = register(new Value<>("Scale", this, 0.5f, 0f, 1f));
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
    public void render(RenderGameOverlayEvent.Pre event) {
        if (mode.getValue().equalsIgnoreCase("Minimize")) {
            Map<UUID, BossInfoClient> map = Minecraft.getMinecraft().ingameGUI.getBossOverlay().mapBossInfos;
            if (map == null) return;
            ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
            int i = scaledresolution.getScaledWidth();
            int j = 12;

            for (Map.Entry<UUID, BossInfoClient> entry : map.entrySet()) {
                BossInfoClient info = entry.getValue();
                String text = info.getName().getFormattedText();

                int k = (int) ((i / scale.getValue()) / 2 - 91);
                GL11.glScaled(scale.getValue(), scale.getValue(), 1);
                if (!event.isCanceled()) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_BARS_TEXTURES);
                    Minecraft.getMinecraft().ingameGUI.getBossOverlay().render(k, j, info);
                    Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, (float) ((i / scale.getValue()) / 2 - Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2), (float) (j - 9), 16777215);
                }
                GL11.glScaled(1d / scale.getValue(), 1d / scale.getValue(), 1);
                j += 10 + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
            }
        } else if (mode.getValue().equalsIgnoreCase("Stack")) {
            Map<UUID, BossInfoClient> map = Minecraft.getMinecraft().ingameGUI.getBossOverlay().mapBossInfos;
            HashMap<String, Pair<BossInfoClient, Integer>> to = new HashMap<>();

            for (Map.Entry<UUID, BossInfoClient> entry : map.entrySet()) {
                String s = entry.getValue().getName().getFormattedText();
                if (to.containsKey(s)) {
                    Pair<BossInfoClient, Integer> p = to.get(s);
                    p = new Pair<>(p.getKey(), p.getValue() + 1);
                    to.put(s, p);
                } else {
                    Pair<BossInfoClient, Integer> p = new Pair<>(entry.getValue(), 1);
                    to.put(s, p);
                }
            }

            ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
            int i = scaledresolution.getScaledWidth();
            int j = 12;

            for (Map.Entry<String, Pair<BossInfoClient, Integer>> entry : to.entrySet()) {
                String text = entry.getKey();
                BossInfoClient info = entry.getValue().getKey();
                int a = entry.getValue().getValue();
                text += " x" + a;

                int k = (int) ((i / scale.getValue()) / 2 - 91);
                GL11.glScaled(scale.getValue(), scale.getValue(), 1);
                if (!event.isCanceled()) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_BARS_TEXTURES);
                    Minecraft.getMinecraft().ingameGUI.getBossOverlay().render(k, j, info);
                    Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, (float) ((i / scale.getValue()) / 2 - Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2), (float) (j - 9), 16777215);
                }
                GL11.glScaled(1d / scale.getValue(), 1d / scale.getValue(), 1);
                j += 10 + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
            }
        }
        return;
    }
}
