package com.elementars.eclient.module.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.GeometryMasks;
import com.elementars.eclient.util.XuluTessellator;
import com.elementars.eclient.util.MathUtil;
import dev.xulu.settings.Value;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.BlockPos;

/**
 * @author Elementars
 */
public class BreakESP extends Module {
    private ArrayList<String> options;
    public ConcurrentSet<BlockPos> breaking = new ConcurrentSet<>();
    private ConcurrentSet<BlockPos> test = new ConcurrentSet<>();

    private Map<Integer, Integer> alphaMap = new HashMap<>();

    BlockPos pos;

    private final Value<String> mode = register(new Value<>("Mode", this, "Solid", new ArrayList<>(
            Arrays.asList("Solid", "Outline", "Full")
    )));
    private final Value<Boolean> ignoreSelf = register(new Value<>("Ignore Self", this, true));
    private final Value<Boolean> onlyObby = register(new Value<>("Only Obsidian", this, true));
    private final Value<Boolean> fade = register(new Value<>("Fade Progress", this, true));
    private final Value<Integer> red = register(new Value<>("Red", this, 255, 0, 255));
    private final Value<Integer> green = register(new Value<>("Green", this, 0, 0, 255));
    private final Value<Integer> blue = register(new Value<>("Blue", this, 0, 0, 255));
    private final Value<Integer> alpha = register(new Value<>("Alpha", this, 70, 0, 255));
    private final Value<Integer> alphaF = register(new Value<>("Full Alpha", this, 100, 0, 255));

    float inc;

    public BreakESP() {
        super("BreakESP", "Highlights blocks being broken", Keyboard.KEY_NONE, Category.RENDER, true);
        alphaMap.put(0, 28);
        alphaMap.put(1, 56);
        alphaMap.put(2, 84);
        alphaMap.put(3, 112);
        alphaMap.put(4, 140);
        alphaMap.put(5, 168);
        alphaMap.put(6, 196);
        alphaMap.put(7, 224);
        alphaMap.put(8, 255);
        alphaMap.put(9, 255);
    }

    public static BreakESP INSTANCE;

    @Override
    public void onWorldRender(RenderEvent event) {
        mc.renderGlobal.damagedBlocks.forEach((integer, destroyBlockProgress) -> {
            if (destroyBlockProgress == null) return;
            if (ignoreSelf.getValue() && mc.world.getEntityByID(integer) == mc.player) return;
            if (onlyObby.getValue() && mc.world.getBlockState(destroyBlockProgress.getPosition()).getBlock() != Blocks.OBSIDIAN) return;
            int transparency = fade.getValue() ? alphaMap.get(destroyBlockProgress.getPartialBlockDamage()) : alpha.getValue();
            if (mode.getValue().equalsIgnoreCase("Solid")) {
                XuluTessellator.prepare(GL11.GL_QUADS);
                XuluTessellator.drawBox(destroyBlockProgress.getPosition(), red.getValue(), green.getValue(), blue.getValue(), transparency, GeometryMasks.Quad.ALL);
                XuluTessellator.release();
            }
            else if (mode.getValue().equalsIgnoreCase("Full")) {
                final IBlockState iBlockState3 = mc.world.getBlockState(destroyBlockProgress.getPosition());
                final Vec3d interp3 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                XuluTessellator.drawFullBox(iBlockState3.getSelectedBoundingBox(mc.world, destroyBlockProgress.getPosition()).grow(0.0020000000949949026D).offset(-interp3.x, -interp3.y, -interp3.z), destroyBlockProgress.getPosition(), 1.5f, red.getValue(), green.getValue(), blue.getValue(), transparency, alphaF.getValue());
            }
            else if (mode.getValue().equalsIgnoreCase("Outline")) {
                final IBlockState iBlockState2 = mc.world.getBlockState(destroyBlockProgress.getPosition());
                final Vec3d interp2 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                XuluTessellator.drawBoundingBox(iBlockState2.getSelectedBoundingBox(mc.world, destroyBlockProgress.getPosition()).grow(0.0020000000949949026D).offset(-interp2.x, -interp2.y, -interp2.z), 1.5f, red.getValue(), green.getValue(), blue.getValue(), transparency);
            }
            else {
                XuluTessellator.prepare(GL11.GL_QUADS);
                XuluTessellator.drawBox(destroyBlockProgress.getPosition(), red.getValue(), green.getValue(), blue.getValue(), transparency, GeometryMasks.Quad.ALL);
                XuluTessellator.release();
            }
        });
    }

}
