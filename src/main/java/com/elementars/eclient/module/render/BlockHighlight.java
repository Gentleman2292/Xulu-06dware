package com.elementars.eclient.module.render;

import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.GeometryMasks;
import com.elementars.eclient.util.XuluTessellator;
import com.elementars.eclient.util.MathUtil;
import com.elementars.eclient.util.RainbowUtils;
import dev.xulu.settings.Value;
import net.minecraft.block.material.Material;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.util.math.Vec3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;

public class BlockHighlight extends Module {

	private static BlockPos position;

	private final Value<String> mode = register(new Value<>("Mode", this, "Outline", new String[]{
			"Solid", "Outline", "Full"
	}));
	private final Value<Boolean> rainbow = register(new Value<>("Rainbow", this, false));
	private final Value<Integer> red = register(new Value<>("Red", this, 255, 0, 255));
	private final Value<Integer> green = register(new Value<>("Green", this, 255, 0, 255));
	private final Value<Integer> blue = register(new Value<>("Blue", this, 255, 0, 255));
	private final Value<Integer> alpha = register(new Value<>("Alpha", this, 255, 0, 255));
	private final Value<Integer> alphaF = register(new Value<>("Alpha Full", this, 255, 0, 255));
	private final Value<Float> width = register(new Value<>("Width", this, 1f, 1f, 10f));

	public BlockHighlight() {
		super("BlockHighlight", "Highlights block you're looking at", Keyboard.KEY_NONE, Category.RENDER, true);
	}

	@Override
    public void onEnable() {
    	MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
    	MinecraftForge.EVENT_BUS.unregister(this);
    	position = null;
    }

    @Override
	public void onWorldRender(RenderEvent event) {
		final Minecraft mc = Minecraft.getMinecraft();
		final RayTraceResult ray = mc.objectMouseOver;
		if (ray != null) {
			if (ray.typeOfHit == RayTraceResult.Type.BLOCK) {

				final BlockPos blockpos = ray.getBlockPos();
				final IBlockState iblockstate = mc.world.getBlockState(blockpos);

				if (iblockstate.getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
					final Vec3d interp = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
					int r = red.getValue();
					int g = green.getValue();
					int b = blue.getValue();
					if (rainbow.getValue()) {
						r = RainbowUtils.r;
						g = RainbowUtils.g;
						b = RainbowUtils.b;
					}
					if (mode.getValue().equalsIgnoreCase("Solid")) {
						XuluTessellator.prepare(GL11.GL_QUADS);
						XuluTessellator.drawBox(blockpos, r, g, b, alpha.getValue(), GeometryMasks.Quad.ALL);
						XuluTessellator.release();
					}
					else if (mode.getValue().equalsIgnoreCase("Outline")) {
						XuluTessellator.drawBoundingBox(iblockstate.getSelectedBoundingBox(mc.world, blockpos).grow(0.0020000000949949026D).offset(-interp.x, -interp.y, -interp.z), width.getValue(), r, g, b, alpha.getValue());
					}
					else if (mode.getValue().equalsIgnoreCase("Full")) {
						XuluTessellator.drawFullBox(iblockstate.getSelectedBoundingBox(mc.world, blockpos).grow(0.0020000000949949026D).offset(-interp.x, -interp.y, -interp.z), blockpos, width.getValue(), r, g, b, alpha.getValue(), alphaF.getValue());
					}
				}
			}
		}
	}
    
    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
    	if ((mc.player == null) || (mc.world == null)
    			|| ((!mc.playerController.getCurrentGameType().equals(GameType.SURVIVAL))
    					&& (!mc.playerController.getCurrentGameType().equals(GameType.CREATIVE)))) {
    		return;
    	}
    	event.setCanceled(true);
    }
    
}