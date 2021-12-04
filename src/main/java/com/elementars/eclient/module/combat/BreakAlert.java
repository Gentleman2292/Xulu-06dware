package com.elementars.eclient.module.combat;

import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.ColorTextUtils;
import com.elementars.eclient.util.ColorUtils;
import dev.xulu.settings.Value;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Elementars
 */
public class BreakAlert extends Module {

    private final Value<String> mode = register(new Value<>("Mode", this, "Chat", new ArrayList<>(
            Arrays.asList("Chat", "Text", "Dot")
        )));
    private final Value<Boolean> ignoreSelf = register(new Value<>("Ignore Self", this, true));
    private final Value<Boolean> watermark = register(new Value<>("Watermark", this, true));
    private final Value<String> color = register(new Value<>("Color", this, "White", ColorTextUtils.colors));

    private ConcurrentSet<BlockPos> breaking = new ConcurrentSet<>();
    private ConcurrentSet<BlockPos> test = new ConcurrentSet<>();

    public BlockPos[] xd = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};

    public BreakAlert() {
        super("BreakAlert", "Alerts you when your feet blocks are being broken", Keyboard.KEY_NONE, Category.COMBAT, true);
    }

    boolean testy;
    int delay;

    @Override
    public void onUpdate() {
        if (delay > 0) --delay;
        test.clear();
        if (mc.world == null) return;
        mc.world.playerEntities.forEach(entityPlayer -> {
            if (this.ignoreSelf.getValue()) {
                if (!entityPlayer.getName().equalsIgnoreCase(mc.player.getName())) {
                    if (entityPlayer.isSwingInProgress && entityPlayer.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE) {
                        final RayTraceResult ray = entityPlayer.rayTrace(5, mc.getRenderPartialTicks());
                        if (ray != null) {
                            if (ray.typeOfHit == RayTraceResult.Type.BLOCK && mc.world.getBlockState(ray.getBlockPos()).getBlock() != Blocks.BEDROCK) {
                                this.test.add(ray.getBlockPos());
                            }
                        }
                    }
                }
            } else {
                if (entityPlayer.isSwingInProgress && entityPlayer.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE) {
                    final RayTraceResult ray = entityPlayer.rayTrace(5, mc.getRenderPartialTicks());
                    if (ray != null) {
                        if (ray.typeOfHit == RayTraceResult.Type.BLOCK && mc.world.getBlockState(ray.getBlockPos()).getBlock() != Blocks.BEDROCK) {
                            this.test.add(ray.getBlockPos());
                        }
                    }
                }
            }
        });
        breaking.removeIf(blockPos -> !test.contains(blockPos));
        breaking.addAll(test);
        testy = false;
        for (BlockPos pos : xd) {
            BlockPos pos1 = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ).add(pos.x, pos.y, pos.z);
            if (!breaking.isEmpty()) {
                if (breaking.contains(pos1)) testy = true;
            }
        }
        if (testy) {
            if (this.mode.getValue().equalsIgnoreCase("Chat")) {
                if (delay == 0) {
                    if (this.watermark.getValue()) {
                        Command.sendChatMessage(ColorTextUtils.getColor(this.color.getValue()) + "Your feet are being mined!");
                    } else {
                        Command.sendRawChatMessage(ColorTextUtils.getColor(this.color.getValue()) + "Your feet are being mined!");
                    }
                }
                delay = 100;
            }
        }
    }

    @Override
    public void onRender() {
        if (testy && this.mode.getValue().equalsIgnoreCase("dot")) {
            GlStateManager.pushMatrix();
            Gui.drawRect((mc.displayWidth / 4) - 3, (mc.displayHeight / 4) - 3, (mc.displayWidth / 4) + 4, (mc.displayHeight / 4) + 4, new Color(255, 0, 0, 255).getRGB());
            GlStateManager.popMatrix();
        }
        else if (testy && this.mode.getValue().equalsIgnoreCase("Text")) {
            GlStateManager.pushMatrix();
            mc.fontRenderer.drawStringWithShadow(Command.SECTIONSIGN() + ColorTextUtils.getColor(this.color.getValue()).substring(1) + "Your feet are being mined!", (mc.displayWidth / 4) - (mc.fontRenderer.getStringWidth("Your feet are being mined!")/2), (mc.displayHeight / 4) - (mc.fontRenderer.FONT_HEIGHT/2), ColorUtils.Colors.RED);
            GlStateManager.popMatrix();
        }
    }
}
