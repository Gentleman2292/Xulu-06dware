package com.elementars.eclient.guirewrite.elements;

import com.elementars.eclient.guirewrite.Element;
import com.elementars.eclient.module.render.ImageESP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Elementars
 * @since 8/10/2020 - 4:07 PM
 */
public class Logo extends Element {

    private ResourceLocation logo;

    public Logo() {
        super("Logo");
        onLoad();
    }

    @Override
    public void onEnable() {
        width = 32;
        height = 32;
    }

    @Override
    public void onRender() {
        mc.renderEngine.bindTexture(this.logo);
        GlStateManager.color(255.0f, 255.0f, 255.0f);
        Gui.drawScaledCustomSizeModalRect((int) x + 4, (int) y + 4, 7.0f, 7.0f, (int) width - 7, (int) height - 7, (int) width, (int) height, (float)width, (float)height);
    }

    private void onLoad() {
        BufferedImage image = null;
        DynamicTexture dynamicTexture;
        try {
            if (getFile("/images/xulutransparent.png") != null) {
                image = this.getImage(getFile("/images/xulutransparent.png"), ImageIO::read);
            }
            if (image == null) {
                LOGGER.warn("Failed to load image");
            }
            else {
                dynamicTexture = new DynamicTexture(image);
                dynamicTexture.loadTexture(mc.getResourceManager());
                this.logo = mc.getTextureManager().getDynamicTextureLocation("XULU_LOGO_TRANSPARENT", dynamicTexture);
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

    private <T> BufferedImage getImage(final T source, final ThrowingFunction<T, BufferedImage> readFunction) {
        try {
            return readFunction.apply(source);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
