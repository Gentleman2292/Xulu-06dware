package com.elementars.eclient.module.render;

import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import dev.xulu.settings.Value;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Elementars
 */
public class SecretShaders extends Module {

    private final Value<String> shader = register(new Value<>("Shader", this, "notch", new ArrayList<>(
            Arrays.asList(
            "antialias",
                        "art",
                                   "bits",
                                   "blobs",
                                   "blobs2",
                                   "blur",
                                   "bumpy",
                                   "color_convolve",
                                   "creeper",
                                   "deconverge",
                                   "desaturate",
                                   "entity_outline",
                                   "flip",
                                   "fxaa",
                                   "green",
                                   "invert",
                                   "notch",
                                   "ntsc",
                                   "outline",
                                   "pencil",
                                   "phosphor",
                                   "scan_pincusion",
                                   "sobel",
                                   "spider",
                                   "wobble"
            )
    )));

    public SecretShaders() {
        super("SecretShaders", "Brings back super secret settings shaders", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    @Override
    public void onEnable() {
        if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer) {
            if (mc.entityRenderer.getShaderGroup() != null) {
                mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }
            try {
                mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/" + this.shader.getValue() + ".json"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (mc.entityRenderer.getShaderGroup() != null && mc.currentScreen == null) {
                mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.entityRenderer.getShaderGroup() != null) {
            mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
    }
}
