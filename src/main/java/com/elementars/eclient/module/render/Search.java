package com.elementars.eclient.module.render;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.event.EventTarget;
import com.elementars.eclient.event.events.EventRenderBlock;
import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.*;
import dev.xulu.settings.Value;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Search extends Module {

    private static final List<Block> BLOCKS = new ArrayList<>(
            Arrays.asList(Blocks.PORTAL, Blocks.DIAMOND_ORE)
    );

    public final Map<BlockPos, Triplet<Integer, Integer, Integer>> posList = new HashMap<>();

    private final Value<String> mode = register(new Value<>("Mode", this, "Solid", new ArrayList<>(
            Arrays.asList("Solid", "Outline", "Full")
    )));
    private final Value<Float> renderDistance = register(new Value<>("RenderDistance", this, 50f, 1f, 100f));
    private final Value<Boolean> render = register(new Value<>("Render", this, true));
    private final Value<Boolean> tracers = register(new Value<>("Tracers", this, false));
    private final Value<Boolean> rainbow = register(new Value<>("Rainbow", this, false));
    private final Value<Integer> red = register(new Value<>("Default Red", this, 255, 0, 255));
    private final Value<Integer> green = register(new Value<>("Default Green", this, 130, 0, 255));
    private final Value<Integer> blue = register(new Value<>("Default Blue", this, 170, 0, 255));
    private final Value<Integer> alpha = register(new Value<>("Alpha", this, 70, 0, 255));
    private final Value<Integer> alphaF = register(new Value<>("Full Alpha", this, 100, 0, 255));

    public Search() {
        super("Search", "ESP for a certain block id", Keyboard.KEY_NONE, Category.RENDER, true);
    }

    @Override
    public void onEnable() {
        mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        posList.clear();
        mc.renderGlobal.loadRenderers();
    }

    public static List<Block> getBLOCKS() {
        return BLOCKS;
    }

    public static boolean addBlock(String string) {
        if (Block.getBlockFromName(string) != null) {
            BLOCKS.add(Block.getBlockFromName(string));
            return true;
        } else {
            return false;
        }
    }

    public static boolean delBlock(String string) {
        if (Block.getBlockFromName(string) != null) {
            BLOCKS.remove(Block.getBlockFromName(string));
            return true;
        } else {
            return false;
        }
    }

    ICamera camera = new Frustum();

    @Override
    public void onWorldRender(RenderEvent event) {
        double d3 = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double)event.getPartialTicks();
        double d4 = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double)event.getPartialTicks();
        double d5 = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double)event.getPartialTicks();

        camera.setPosition(d3,  d4,  d5);
        if (mc.player == null) return;
        if (render.getValue()) {
            if (this.mode.getValue().equalsIgnoreCase("Solid")) {
                XuluTessellator.prepare(7);
                synchronized (posList) {
                    posList.forEach((blockPos, triplet) -> {
                        if (blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= renderDistance.getValue() && camera.isBoundingBoxInFrustum(mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos))) {
                            this.drawBlock(blockPos, (this.rainbow.getValue() ? RainbowUtils.r : triplet.getFirst()), (this.rainbow.getValue() ? RainbowUtils.g : triplet.getSecond()), (this.rainbow.getValue() ? RainbowUtils.b : triplet.getThird()));
                        }
                    });
                }
                XuluTessellator.release();
            } else if (this.mode.getValue().equalsIgnoreCase("Outline")) {
                synchronized (posList) {
                    posList.forEach((blockPos, triplet) -> {
                        if (blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= renderDistance.getValue())
                            this.drawBlockO(blockPos, (this.rainbow.getValue() ? RainbowUtils.r : triplet.getFirst()), (this.rainbow.getValue() ? RainbowUtils.g : triplet.getSecond()), (this.rainbow.getValue() ? RainbowUtils.b : triplet.getThird()));
                    });
                }
            } else if (this.mode.getValue().equalsIgnoreCase("Full")) {
                synchronized (posList) {
                    posList.forEach((blockPos, triplet) -> {
                        XuluTessellator.prepare(7);
                        if (blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= renderDistance.getValue()) {
                            this.drawBlock(blockPos, (this.rainbow.getValue() ? RainbowUtils.r : triplet.getFirst()), (this.rainbow.getValue() ? RainbowUtils.g : triplet.getSecond()), (this.rainbow.getValue() ? RainbowUtils.b : triplet.getThird()));
                            this.drawBlockO(blockPos, (this.rainbow.getValue() ? RainbowUtils.r : triplet.getFirst()), (this.rainbow.getValue() ? RainbowUtils.g : triplet.getSecond()), (this.rainbow.getValue() ? RainbowUtils.b : triplet.getThird()));
                        }
                        XuluTessellator.release();
                    });
                }
            }
        }
        if (tracers.getValue()) {
            synchronized (posList) {
                posList.forEach((blockPos, triplet) -> {
                    if (blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= renderDistance.getValue()) {
                        drawLineToBlock(blockPos, (this.rainbow.getValue() ? RainbowUtils.r : triplet.getFirst()), (this.rainbow.getValue() ? RainbowUtils.g : triplet.getSecond()), (this.rainbow.getValue() ? RainbowUtils.b : triplet.getThird()), alpha.getValue());
                    }
                });
            }
        }
    }


    @EventTarget
    public void onRender(EventRenderBlock event) {
        if (BLOCKS.contains(event.getBlockState().getBlock())) {
            Vec3d vec3d = event.getBlockState().getOffset(event.getBlockAccess(), event.getBlockPos());
            double d0 = (double)event.getBlockPos().getX() + vec3d.x;
            double d1 = (double)event.getBlockPos().getY() + vec3d.y;
            double d2 = (double)event.getBlockPos().getZ() + vec3d.z;
            final BlockPos pos = new BlockPos(d0, d1, d2);
            synchronized (posList) {
                posList.put(pos, getColor(event.getBlockState().getBlock()));
            }
        }
    }

    private void drawBlock(final BlockPos blockPos, final int r, final int g, final int b) {
        final Color color = new Color(r, g, b, this.alpha.getValue());
        final IBlockState iBLOCKState3 = mc.world.getBlockState(blockPos);
        final Vec3d interp3 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
        XuluTessellator.drawBox2(iBLOCKState3.getSelectedBoundingBox(mc.world, blockPos).offset(-interp3.x, -interp3.y, -interp3.z), color.getRGB(), 63);
        //XuluTessellator.drawBox(blockPos, color.getRGB(), 63);
    }

    private void drawBlockO(final BlockPos blockPos, final int r, final int g, final int b) {
        final IBlockState iBLOCKState2 = mc.world.getBlockState(blockPos);
        final Vec3d interp2 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
        XuluTessellator.drawBoundingBox(iBLOCKState2.getSelectedBoundingBox(mc.world, blockPos).offset(-interp2.x, -interp2.y, -interp2.z), 1.5f, r, g, b, alphaF.getValue());
    }

    private Triplet<Integer, Integer, Integer> getColor(Block block) {
        if (block == Blocks.DIAMOND_ORE) return new Triplet<>(0, 255, 255);
        if (block == Blocks.IRON_ORE) return new Triplet<>(255, 226, 191);
        if (block == Blocks.GOLD_ORE) return new Triplet<>(255, 216, 0);
        if (block == Blocks.COAL_ORE) return new Triplet<>(35, 35, 35);
        if (block == Blocks.LAPIS_ORE) return new Triplet<>(0, 50, 255);
        if (block == Blocks.PORTAL) return new Triplet<>(170, 0, 255);
        if (block == Blocks.EMERALD_ORE) return new Triplet<>(0, 255, 0);
        if (block == Blocks.REDSTONE_ORE) return new Triplet<>(186, 0, 0);
        if (block == Blocks.END_PORTAL_FRAME) return new Triplet<>(255, 255, 150);
        return new Triplet<>(red.getValue(), green.getValue(), blue.getValue());
    }

    //
    //Tracers
    //
    public static void drawLineToBlock(BlockPos pos, float red, float green, float blue, float opacity){
        final Vec3d interp3 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
        drawLine(pos.x - interp3.x + 0.5D, pos.y - interp3.y + 0.5D, pos.z - interp3.z + 0.5D, 0, red, green, blue, opacity);
    }

    public static void drawLine(double posx, double posy, double posz, double up, float red, float green, float blue, float opacity)
    {
        Vec3d eyes = new Vec3d(0, 0, 1)
                .rotatePitch(-(float)Math
                        .toRadians(Minecraft.getMinecraft().player.rotationPitch))
                .rotateYaw(-(float)Math
                        .toRadians(Minecraft.getMinecraft().player.rotationYaw));

        drawLineFromPosToPos(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, posx, posy, posz, up, red, green, blue, opacity);
    }

    public static void drawLineFromPosToPos(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, float red, float green, float blue, float opacity){
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1.5f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, opacity);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.disableLighting();
        GL11.glLoadIdentity();
        final boolean bobbing = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());

        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex3d(posx, posy, posz);
            GL11.glVertex3d(posx2, posy2, posz2);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        GL11.glColor3d(1d,1d,1d);
        GlStateManager.enableLighting();
        mc.gameSettings.viewBobbing = bobbing;
    }
}
