package com.elementars.eclient.module.render;

import com.elementars.eclient.event.events.RenderEvent;
import com.elementars.eclient.module.Category;
import com.elementars.eclient.module.Module;
import com.elementars.eclient.util.*;
import dev.xulu.settings.Value;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by 086 on 10/12/2017.
 */
public class StorageESP extends Module {
    private final Value<String> mode = register(new Value<>("Mode", this, "Solid", new ArrayList<>(
            Arrays.asList("Solid", "Full", "Outline", "Shader")
    )));
    private final Value<Boolean> all = register(new Value<>("All Tile Entities", this, false));
    private static Value<Boolean> future;
    private final Value<Float> width = register(new Value<>("Line Width", this, 1f, 1f, 10f));
    private final Value<Boolean> save = register(new Value<>("Save coords above threshold", this, false));
    private final Value<Integer> threshold = register(new Value<>("Threshold", this, 200, 1, 2000));

    Random random = new Random();

    private int delay;
    private int count;

    public StorageESP() {
        super("StorageESP", "Highlights storage blocks", Keyboard.KEY_NONE, Category.RENDER, true);
        future = register(new Value<>("Future Colors", this, false));

        this.count = 1;
    }

    public static int getTileEntityColor(TileEntity tileEntity) {
        if(tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityDispenser)
            return ColorUtils.Colors.ORANGE;
        else if(tileEntity instanceof TileEntityShulkerBox)
            return ColorUtils.toRGBA(255 , 0, 95, 255);
        else if(tileEntity instanceof TileEntityEnderChest)
            return ColorUtils.Colors.PURPLE;
        else if(tileEntity instanceof TileEntityFurnace)
            return ColorUtils.Colors.GRAY;
        else if(tileEntity instanceof TileEntityHopper)
            return ColorUtils.Colors.DARK_RED;
        else
            return -1;
    }
    public static int getTileEntityColorF(TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityChest)
            return ColorUtils.toRGBA(200, 200, 101, 255);
        if (tileEntity instanceof TileEntityShulkerBox)
            return ColorUtils.toRGBA(180, 21, 99, 255);
        if (tileEntity instanceof TileEntityEnderChest)
            return ColorUtils.toRGBA(155, 0, 200, 255);
        else if(tileEntity instanceof TileEntityFurnace)
            return ColorUtils.Colors.GRAY;
        else if(tileEntity instanceof TileEntityHopper)
            return ColorUtils.Colors.GRAY;
        else
            return -1;
    }

    private int getEntityColor(Entity entity) {
        if(entity instanceof EntityMinecartChest)
            return ColorUtils.Colors.ORANGE;
        else if(entity instanceof EntityMinecartHopper)
            return ColorUtils.Colors.DARK_RED;
        else if(entity instanceof EntityItemFrame &&
                ((EntityItemFrame) entity).getDisplayedItem().getItem() instanceof ItemShulkerBox)
            return ColorUtils.Colors.YELLOW;
        else
            return -1;
    }

    @Override
    public String getHudInfo() {
        long chests = mc.world.loadedTileEntityList.stream().filter(tileEntity -> tileEntity instanceof TileEntityChest).count();
        if (this.save.getValue() && (int) chests >= this.threshold.getValue() && delay == 0) {
            Wrapper.getFileManager().saveStorageESP(LocalDate.now().toString() + "-" + random.nextInt(9999) + " - " + count, (int) mc.player.posX + " " + (int) mc.player.posY + " " + (int) mc.player.posZ , String.valueOf(chests));
            delay = 4000;
            count += 1;
        }
        return "Chests: " + chests;
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (delay > 0)
            delay -= 1;
        ArrayList<Triplet<TileEntity, Integer, Integer>> a = new ArrayList<>();
        GlStateManager.pushMatrix();
        if(mode.getValue().equalsIgnoreCase("Shader")) {
            OutlineUtils.checkSetupFBO();
        }
        for(TileEntity tileEntity : Wrapper.getWorld().loadedTileEntityList) {
            BlockPos pos = tileEntity.getPos();
            int color;
            if (future.getValue())
                color = getTileEntityColorF(tileEntity);
            else
                color = getTileEntityColor(tileEntity);
            int side = GeometryMasks.Quad.ALL;
            if (tileEntity instanceof TileEntityChest) {
                TileEntityChest chest = (TileEntityChest) tileEntity;
                // Leave only the colliding face and then flip the bits (~) to have ALL but that face
                if (chest.adjacentChestZNeg != null) side = ~(side & GeometryMasks.Quad.NORTH);
                if (chest.adjacentChestXPos != null) side = ~(side & GeometryMasks.Quad.EAST);
                if (chest.adjacentChestZPos != null) side = ~(side & GeometryMasks.Quad.SOUTH);
                if (chest.adjacentChestXNeg != null) side = ~(side & GeometryMasks.Quad.WEST);
            }
            if(color != -1) a.add(new Triplet<>(tileEntity, color, side)); //GeometryTessellator.drawCuboid(event.getBuffer(), pos, GeometryMasks.Line.ALL, color);
        }
        /*
        for(Entity entity : Wrapper.getWorld().loadedEntityList) {
            BlockPos pos = entity.getPosition();
            int color = getEntityColor(entity);
            if(color != -1) a.add(new Triplet<>(entity instanceof EntityItemFrame ? pos.add(0, -1, 0) : pos, color, GeometryMasks.Quad.ALL)); //GeometryTessellator.drawCuboid(event.getBuffer(), entity instanceof EntityItemFrame ? pos.add(0, -1, 0) : pos, GeometryMasks.Line.ALL, color);
        }
        */
        for (Triplet<TileEntity, Integer, Integer> pair : a)
            try {
                if (mode.getValue().equalsIgnoreCase("Solid")) {
                    XuluTessellator.prepare(GL11.GL_QUADS);
                    XuluTessellator.drawBox(pair.getFirst().getPos(), changeAlpha(pair.getSecond(), 100), pair.getThird());
                    XuluTessellator.release();
                } else if (mode.getValue().equalsIgnoreCase("Outline")) {
                    final IBlockState iBlockState2 = mc.world.getBlockState(pair.getFirst().getPos());
                    final Vec3d interp2 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                    XuluTessellator.prepare(GL11.GL_QUADS);
                    XuluTessellator.drawBoundingBox(iBlockState2.getSelectedBoundingBox(mc.world, pair.getFirst().getPos()).grow(0.0020000000949949026D).offset(-interp2.x, -interp2.y, -interp2.z), 1.5f, pair.getSecond());
                    XuluTessellator.release();
                } else if (mode.getValue().equalsIgnoreCase("Full")) {
                    final IBlockState iBlockState3 = mc.world.getBlockState(pair.getFirst().getPos());
                    final Vec3d interp3 = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                    XuluTessellator.drawFullBox2(iBlockState3.getSelectedBoundingBox(mc.world, pair.getFirst().getPos()).grow(0.0020000000949949026D).offset(-interp3.x, -interp3.y, -interp3.z), pair.getFirst().getPos(), 1.5f, changeAlpha(pair.getSecond(), 100));
                } else if (mode.getValue().equalsIgnoreCase("Shader")) {
                    //Empty for mixin
                } else {
                    XuluTessellator.prepare(GL11.GL_QUADS);
                    XuluTessellator.drawBox(pair.getFirst().getPos(), changeAlpha(pair.getSecond(), 100), pair.getThird());
                    XuluTessellator.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        /*
        XuluTessellator.prepare(GL11.GL_QUADS);
        for (Triplet<BlockPos, Integer, Integer> pair : a)
            XuluTessellator.drawBox(pair.getFirst(), changeAlpha(pair.getSecond(), 100), pair.getThird());
        XuluTessellator.release();
        */

        //GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
    }

    int changeAlpha(int origColor, int userInputedAlpha) {
        origColor = origColor & 0x00ffffff; //drop the previous alpha value
        return (userInputedAlpha << 24) | origColor; //add the one the user inputted
    }

    public class Triplet<T, U, V> {

        private final T first;
        private final U second;
        private final V third;

        public Triplet(T first, U second, V third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public T getFirst() { return first; }
        public U getSecond() { return second; }
        public V getThird() { return third; }
    }

    // Outline Util shit

    public static void renderNormal(final float n) {
        RenderHelper.enableStandardItemLighting();
        for (final TileEntity tileEntity : Wrapper.getMinecraft().world.loadedTileEntityList) {
            if (!(tileEntity instanceof TileEntityChest) && !(tileEntity instanceof TileEntityEnderChest) && !(tileEntity instanceof TileEntityShulkerBox)) {
                continue;
            } else {
                GL11.glPushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                TileEntityRendererDispatcher.instance.render(tileEntity, tileEntity.getPos().getX() - mc.renderManager.renderPosX, tileEntity.getPos().getY() - mc.renderManager.renderPosY, tileEntity.getPos().getZ() - mc.renderManager.renderPosZ, n);
                GL11.glPopMatrix();
            }
        }
    }
    public static void renderColor(final float n) {
        RenderHelper.enableStandardItemLighting();
        for (final TileEntity tileEntity : Wrapper.getMinecraft().world.loadedTileEntityList) {
            if (!(tileEntity instanceof TileEntityChest) && !(tileEntity instanceof TileEntityEnderChest) && !(tileEntity instanceof TileEntityShulkerBox) && !(tileEntity instanceof TileEntityFurnace) && !(tileEntity instanceof TileEntityHopper)) {
                continue;
            } else {
                if (future.getValue()) {
                    OutlineUtils2.setColor(new Color(getTileEntityColorF(tileEntity)));
                } else {
                    OutlineUtils2.setColor(new Color(getTileEntityColor(tileEntity)));
                }
                TileEntityRendererDispatcher.instance.render(tileEntity, tileEntity.getPos().getX() - mc.renderManager.renderPosX, tileEntity.getPos().getY() - mc.renderManager.renderPosY, tileEntity.getPos().getZ() - mc.renderManager.renderPosZ, n);
            }
        }
    }
}
