package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.core.TitleScreenShader;
import com.elementars.eclient.util.GLSLSandboxShader;
import com.elementars.eclient.util.GLSLShaders;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Random;

/**
 * @author John200410 1/10/2020 for Elementars (buttons)
 * @author Elementars with the shaders
 */
@Mixin(GuiMainMenu.class)
public abstract class MixinGuiMainMenu extends GuiScreen {
	@Shadow protected abstract void renderSkybox(int mouseX, int mouseY, float partialTicks);

	@Inject(method = "initGui", at = @At(value = "RETURN"), cancellable = true)
	public void initGui(CallbackInfo info) {
		try {
			if (Xulu.MODULE_MANAGER.getModuleT(TitleScreenShader.class).mode.getValue().equalsIgnoreCase("Random")) {
				Random random = new Random();
				GLSLShaders[] shaders = GLSLShaders.values();
				Xulu.backgroundShader = new GLSLSandboxShader(shaders[random.nextInt(shaders.length)].get());
			} else {
				Xulu.backgroundShader = new GLSLSandboxShader(Xulu.MODULE_MANAGER.getModuleT(TitleScreenShader.class).shader.getValue().get());
			}
		} catch (IOException e) {
			throw new IllegalStateException("Failed to load background shader", e);
		}
		this.buttonList.add(new GuiButton(932, 5, 55, fontRenderer.getStringWidth("2b2tpvp.net") + 10, 20, "2b2tpvp.net"));
		this.buttonList.add(new GuiButton(284, 5, 75, fontRenderer.getStringWidth("2b2t.org") + 10, 20, "2b2t.org"));
		Xulu.initTime = System.currentTimeMillis();
	}

	@Inject(method = "actionPerformed", at = @At(value = "HEAD"), cancellable = true)
	public void actionPerformed(GuiButton button, CallbackInfo info) {
		if(button.id == 932) {
			this.mc.displayGuiScreen(new GuiConnecting(this, mc, "2b2tpvp.net", 25565)); //th﻿is n﻿umber is port﻿
		}
		if(button.id == 284) {
			this.mc.displayGuiScreen(new GuiConnecting(this, mc, "2b2t.org", 25565)); //th﻿is n﻿umber is port﻿
		}
	}

	@Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiMainMenu;renderSkybox(IIF)V"))
	private void voided(GuiMainMenu guiMainMenu, int mouseX, int mouseY, float partialTicks) {
		if (!Xulu.MODULE_MANAGER.getModule(TitleScreenShader.class).isToggled()) {
			renderSkybox(mouseX, mouseY, partialTicks);
		}
	}

	@Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiMainMenu;drawGradientRect(IIIIII)V", ordinal = 0))
	private void noRect1(GuiMainMenu guiMainMenu, int left, int top, int right, int bottom, int startColor, int endColor) {
		if (!Xulu.MODULE_MANAGER.getModule(TitleScreenShader.class).isToggled()) {
			drawGradientRect(left, top, right, bottom, startColor, endColor);
		}
	}

	@Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiMainMenu;drawGradientRect(IIIIII)V", ordinal = 1))
	private void noRect2(GuiMainMenu guiMainMenu, int left, int top, int right, int bottom, int startColor, int endColor) {
		if (!Xulu.MODULE_MANAGER.getModule(TitleScreenShader.class).isToggled()) {
			drawGradientRect(left, top, right, bottom, startColor, endColor);
		}
	}

	@Inject(method = "drawScreen", at = @At(value = "HEAD"), cancellable = true)
	public void drawScreenShader(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		if (Xulu.MODULE_MANAGER.getModule(TitleScreenShader.class).isToggled()) {
			GlStateManager.disableCull();
			Xulu.backgroundShader.useShader(this.width*2, this.height*2, mouseX*2, mouseY*2, (System.currentTimeMillis() - Xulu.initTime) / 1000f);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(-1f, -1f);
			GL11.glVertex2f(-1f, 1f);
			GL11.glVertex2f(1f, 1f);
			GL11.glVertex2f(1f, -1f);
			GL11.glEnd();
			GL20.glUseProgram(0);
		}
	}

}
