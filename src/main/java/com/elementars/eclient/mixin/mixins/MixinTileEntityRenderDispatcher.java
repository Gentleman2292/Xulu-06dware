package com.elementars.eclient.mixin.mixins;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.module.render.StorageESP;
import com.elementars.eclient.util.OutlineUtils;
import com.google.common.collect.Maps;
import dev.xulu.settings.Value;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.ReportedException;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Map;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRenderDispatcher {
    @Shadow
    public final Map<Class<? extends TileEntity>, TileEntitySpecialRenderer<? extends TileEntity>> renderers = Maps.<Class<? extends TileEntity>, TileEntitySpecialRenderer<? extends TileEntity>>newHashMap();

    @Shadow
    @Nullable
    public <T extends TileEntity> TileEntitySpecialRenderer<T> getRenderer(@Nullable TileEntity tileEntityIn) {
        return tileEntityIn == null || tileEntityIn.isInvalid() ? null : this.getRenderer(tileEntityIn.getClass()); // Forge: fix MC-123363
    }

    @Shadow
    public <T extends TileEntity> TileEntitySpecialRenderer<T> getRenderer(Class<? extends TileEntity> teClass) {
        TileEntitySpecialRenderer<T> tileentityspecialrenderer = (TileEntitySpecialRenderer) this.renderers.get(teClass);

        if (tileentityspecialrenderer == null && teClass != TileEntity.class) {
            tileentityspecialrenderer = this.getRenderer((Class<? extends TileEntity>) teClass.getSuperclass());
            this.renderers.put(teClass, tileentityspecialrenderer);
        }

        return tileentityspecialrenderer;
    }

    @Shadow
    private net.minecraft.client.renderer.Tessellator batchBuffer = new net.minecraft.client.renderer.Tessellator(0x200000);
    @Shadow
    private boolean drawingBatch = false;


    @Inject(method = "render(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V", at = @At("HEAD"), cancellable = true)
    public void Irender(TileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage, float p_192854_10, CallbackInfo callback) {
        {
            TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = this.<TileEntity>getRenderer(tileEntityIn);

            if (tileentityspecialrenderer != null) {
                try {
                    StorageESP storageESP = (StorageESP) Xulu.MODULE_MANAGER.getModuleByName("StorageESP");
                    Value all = Xulu.VALUE_MANAGER.getValueByMod(storageESP, "All Tile Entities");
                    if (all != null && !(boolean) all.getValue()) {
                        if (!(tileEntityIn instanceof TileEntityChest || tileEntityIn instanceof TileEntityEnderChest || tileEntityIn instanceof TileEntityShulkerBox)) {
                            return;
                        }
                    }
                    if (storageESP != null && storageESP.isToggled() && (Xulu.VALUE_MANAGER.<String>getValueByMod(storageESP, "Mode").getValue()).equalsIgnoreCase("Shader") && tileEntityIn.hasWorld()) {
                        Color n;
                        if (Xulu.VALUE_MANAGER.<Boolean>getValueByMod(storageESP, "Future Colors").getValue())
                            n = new Color(StorageESP.getTileEntityColorF(tileEntityIn));
                        else
                            n = new Color(StorageESP.getTileEntityColor(tileEntityIn));
                        if (drawingBatch && tileEntityIn.hasFastRenderer()) {
                            GL11.glLineWidth(5.0F);
                            tileentityspecialrenderer.renderTileEntityFast(tileEntityIn, x, y, z, partialTicks, destroyStage, p_192854_10, batchBuffer.getBuffer());
                            OutlineUtils.renderOne(Xulu.VALUE_MANAGER.<Float>getValueByMod(storageESP, "Line Width").getValue());
                            tileentityspecialrenderer.renderTileEntityFast(tileEntityIn, x, y, z, partialTicks, destroyStage, p_192854_10, batchBuffer.getBuffer());
                            OutlineUtils.renderTwo();
                            tileentityspecialrenderer.renderTileEntityFast(tileEntityIn, x, y, z, partialTicks, destroyStage, p_192854_10, batchBuffer.getBuffer());
                            OutlineUtils.renderThree();
                            OutlineUtils.renderFour(n);
                            tileentityspecialrenderer.renderTileEntityFast(tileEntityIn, x, y, z, partialTicks, destroyStage, p_192854_10, batchBuffer.getBuffer());
                            OutlineUtils.renderFive();
                        } else {
                            GL11.glLineWidth(5.0F);
                            tileentityspecialrenderer.render(tileEntityIn, x, y, z, partialTicks, destroyStage, p_192854_10);
                            OutlineUtils.renderOne(Xulu.VALUE_MANAGER.<Float>getValueByMod(storageESP, "Line Width").getValue());
                            tileentityspecialrenderer.render(tileEntityIn, x, y, z, partialTicks, destroyStage, p_192854_10);
                            OutlineUtils.renderTwo();
                            tileentityspecialrenderer.render(tileEntityIn, x, y, z, partialTicks, destroyStage, p_192854_10);
                            OutlineUtils.renderThree();
                            OutlineUtils.renderFour(n);
                            tileentityspecialrenderer.render(tileEntityIn, x, y, z, partialTicks, destroyStage, p_192854_10);
                            OutlineUtils.renderFive();
                        }
                    }
                    if (drawingBatch && tileEntityIn.hasFastRenderer())
                        tileentityspecialrenderer.renderTileEntityFast(tileEntityIn, x, y, z, partialTicks, destroyStage, p_192854_10, batchBuffer.getBuffer());
                    else
                        tileentityspecialrenderer.render(tileEntityIn, x, y, z, partialTicks, destroyStage, p_192854_10);
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Block Entity");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Block Entity Details");
                    tileEntityIn.addInfoToCrashReport(crashreportcategory);
                    throw new ReportedException(crashreport);
                }
            }
        }
    }
}