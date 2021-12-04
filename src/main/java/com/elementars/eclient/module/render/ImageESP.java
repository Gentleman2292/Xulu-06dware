package com.elementars.eclient.module.render;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.EntityUtil;
import com.elementars.eclient.util.VectorUtils;
import dev.xulu.settings.Value;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.imageio.ImageIO;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class ImageESP extends Module {

    private final Value<Boolean> noRenderPlayers = register(new Value<>("No Render Players", this, false));
    private final Value<CachedImage> imageUrl = register(new Value<>("Image", this, CachedImage.LAUREN, CachedImage.values()))
            .onChanged(imagesOnChangedValue -> {
                this.waifu = null;
                onLoad();
            });

    private ResourceLocation waifu;

    public ImageESP() {
        super("ImageESP","overlay cute images over players", Keyboard.KEY_NONE, Category.RENDER, true);
        onLoad();
    }

    @Override
    public void onEnable() {
        EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        EVENT_BUS.unregister(this);
    }

    private <T> BufferedImage getImage(final T source, final ThrowingFunction<T, BufferedImage> readFunction) {
        try {
            return readFunction.apply(source);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private boolean shouldDraw(final EntityLivingBase entity) {

        return !entity.equals(mc.player) && entity.getHealth() > 0f && EntityUtil.isPlayer(entity);
    }

    private ICamera camera = new Frustum();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderGameOverlayEvent(final RenderGameOverlayEvent.Text event) {
        if (this.waifu == null) {
            return;
        }
        double d3 = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double)event.getPartialTicks();
        double d4 = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double)event.getPartialTicks();
        double d5 = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double)event.getPartialTicks();

        camera.setPosition(d3,  d4,  d5);
        final List<EntityPlayer> players = new ArrayList<>(mc.world.playerEntities);
        players.sort(Comparator.comparing(entityPlayer -> mc.player.getDistance((EntityPlayer)entityPlayer)).reversed());
        for (final EntityPlayer player : players) {
            if (player != mc.getRenderViewEntity() && player.isEntityAlive() && camera.isBoundingBoxInFrustum(player.getEntityBoundingBox())) {
                final EntityLivingBase living = (EntityLivingBase)player;
                final Vec3d bottomVec = EntityUtil.getInterpolatedPos((Entity)living, event.getPartialTicks());
                final Vec3d topVec = bottomVec.add(new Vec3d(0.0, player.getRenderBoundingBox().maxY - player.posY, 0.0));
                final VectorUtils.ScreenPos top = VectorUtils._toScreen(topVec.x, topVec.y, topVec.z);
                final VectorUtils.ScreenPos bot = VectorUtils._toScreen(bottomVec.x, bottomVec.y, bottomVec.z);
                if (!top.isVisible && !bot.isVisible) {
                    continue;
                }
                final int width;
                final int height = width = bot.y - top.y;
                final int x = (int)(top.x - width / 1.8);
                final int y = top.y;
                mc.renderEngine.bindTexture(this.waifu);
                GlStateManager.color(255.0f, 255.0f, 255.0f);
                Gui.drawScaledCustomSizeModalRect(x, y, 0.0f, 0.0f, width, height, width, height, (float)width, (float)height);
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(final RenderPlayerEvent.Pre event) {
        if (this.noRenderPlayers.getValue() && !event.getEntity().equals(mc.player)) {
            event.setCanceled(true);
        }
    }

    public void onLoad() {
        BufferedImage image = null;
        DynamicTexture dynamicTexture;
        try {
            if (getFile(imageUrl.getValue().getName()) != null) {
                image = this.getImage(getFile(imageUrl.getValue().getName()), ImageIO::read);
            }
            /*
            else {
                image = this.getImage(new URL(url.getUrl()), ImageIO::read);
                if (image != null) {
                    try {
                        ImageIO.write(image, "png", getCache(url));
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            */
            if (image == null) {
                LOGGER.warn("Failed to load image");
            }
            else {
                dynamicTexture = new DynamicTexture(image);
                dynamicTexture.loadTexture(mc.getResourceManager());
                this.waifu = mc.getTextureManager().getDynamicTextureLocation("XULU_" + imageUrl.getValue().name(), dynamicTexture);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface ThrowingFunction<T, R>
    {
        R apply(final T p0) throws IOException;
    }
    
    private InputStream getFile(String string) {
        return ImageESP.class.getResourceAsStream(string);
    }

    private enum CachedImage {
        LAUREN("/images/lauren.png"),
        DELTA("/images/delta.png"),
        OMEGA("/images/omega.png"),
        TRIANGLE("/images/triangle.png"),
        WAIFU("/images/waifu.png"),
        WAIFU2("/images/waifu2.png"),
        XULU("/images/xulutransparent.png"),
        PETER("/images/peter.png"),
        LOTUS("/images/lotus.png"),
        LOGAN("/images/logan.png"),
        ZHN("/images/zhn.png"),
        BIGOUNCE("/images/bigounce.png"),
        JOINT("/images/joint.png");

        String name;

        CachedImage(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
