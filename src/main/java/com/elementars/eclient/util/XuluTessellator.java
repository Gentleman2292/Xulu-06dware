package com.elementars.eclient.util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by 086 on 9/07/2017.
 */
public class XuluTessellator extends Tessellator {

    public static XuluTessellator INSTANCE = new XuluTessellator();

    public XuluTessellator() {
        super(0x200000);
    }

    public static void prepare(int mode) {
        prepareGL();
        begin(mode);
    }

    public static void prepareGL() {
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(1.5F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color(1,1,1);
    }

    public static void begin(int mode) {
        INSTANCE.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void release() {
        render();
        releaseGL();
    }

    public static void render() {
        INSTANCE.draw();
    }

    public static void releaseGL() {
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
    }

    public static void drawRectDouble(double left, double top, double right, double bottom, int color)
    {
        if (left < right)
        {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRectGradient(double left, double top, double right, double bottom, int color, int color2)
    {
        if (left < right)
        {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        float a = (float)(color2 >> 24 & 255) / 255.0F;
        float r = (float)(color2 >> 16 & 255) / 255.0F;
        float g = (float)(color2 >> 8 & 255) / 255.0F;
        float b = (float)(color2 & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        //GlStateManager.color(r, g, b, a);
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GL11.glShadeModel(7424);
    }

    public static void drawOutlineLine(double left, double top, double right, double bottom, float width, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(width);

        if (left < right)
        {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(left, bottom, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right, top, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(left, top, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(left, bottom, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        //GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawRectOutline(double left, double top, double right, double bottom, double width, int color) {
        drawRectOutline(left - width, top - width, right + width, bottom + width, left, top, right, bottom, color);
    }

    public static void drawRectOutline(double left, double top, double right, double bottom, double left2, double top2, double right2, double bottom2, int color) {
        //First 4 are the bigger box of the outline
        //Second 4 are the cutout box (should be the original box coordinates)
        drawRectDouble(left, top, right, top2, color);
        drawRectDouble(right2, top2, right, bottom, color);
        drawRectDouble(left, bottom2, right2, bottom, color);
        drawRectDouble(left, top2, left2, bottom2, color);
    }

    public static void drawBox(BlockPos blockPos, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawBox(blockPos, r, g, b, a, sides);
    }

    public static void drawBox(float x, float y, float z, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawBox(INSTANCE.getBuffer(), x, y, z, 1, 1, 1, r, g, b, a, sides);
    }

    public static void drawBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawBox(INSTANCE.getBuffer(), blockPos.x, blockPos.y, blockPos.z, 1, 1, 1, r, g, b, a, sides);
    }

    public static BufferBuilder getBufferBuilder() {
        return INSTANCE.getBuffer();
    }

    public static void drawBox(final BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & GeometryMasks.Quad.DOWN) != 0) {
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.UP) != 0) {
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.NORTH) != 0) {
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.SOUTH) != 0) {
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.WEST) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.EAST) != 0) {
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
        }
    }

    public static void drawFace(BlockPos blockPos, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawFace(blockPos, r, g, b, a, sides);
    }

    public static void drawFace(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawFace(INSTANCE.getBuffer(), blockPos.x, blockPos.y, blockPos.z, 1, 1, 1, r, g, b, a, sides);
    }

    public static void drawFace(final BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & GeometryMasks.Quad.DOWN) != 0) {

            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        }

    }

    public static void drawFaceOutline(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawFaceOutline(INSTANCE.getBuffer(), blockPos.x, blockPos.y, blockPos.z, 1, 1, 1, r, g, b, a, sides);
    }

    public static void drawFaceOutline(final BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & GeometryMasks.Quad.DOWN) != 0) {

            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+0.02).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+0.02).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();

            buffer.pos(x+0.02, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+0.02, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();

            buffer.pos(x+w, y, z+d-0.02).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d-0.02).color(r, g, b, a).endVertex();

            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w-0.02, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w-0.02, y, z).color(r, g, b, a).endVertex();
        }

    }

    public static void drawBoxOutline(BlockPos blockPos, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawBoxOutline(blockPos, r, g, b, a, sides);
    }

    public static void drawBoxOutline(float x, float y, float z, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawBoxOutline(INSTANCE.getBuffer(), x, y, z, 1, 1, 1, r, g, b, a, sides);
    }
    public static void drawBoxOutline(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawBoxOutline(INSTANCE.getBuffer(), blockPos.x, blockPos.y, blockPos.z, 1, 1, 1, r, g, b, a, sides);
    }

    public static void drawBoxOutline(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & GeometryMasks.Quad.DOWN) != 0) {

            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+0.02).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+0.02).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();

            buffer.pos(x+0.02, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+0.02, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();

            buffer.pos(x+w, y, z+d-0.02).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d-0.02).color(r, g, b, a).endVertex();

            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w-0.02, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w-0.02, y, z).color(r, g, b, a).endVertex();
        }
    }

    public static void drawLines(BlockPos blockPos, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawLines(blockPos, r, g, b, a, sides);
    }

    public static void drawLines(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawLines(INSTANCE.getBuffer(), blockPos.x, blockPos.y, blockPos.z, 1, 1, 1, r, g, b, a, sides);
    }

    public static void drawLines(final BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & GeometryMasks.Line.DOWN_WEST) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.UP_WEST) != 0) {
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.DOWN_EAST) != 0) {
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.UP_EAST) != 0) {
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.DOWN_NORTH) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.UP_NORTH) != 0) {
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.DOWN_SOUTH) != 0) {
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.UP_SOUTH) != 0) {
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.NORTH_WEST) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.NORTH_EAST) != 0) {
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.SOUTH_WEST) != 0) {
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.SOUTH_EAST) != 0) {
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
        }
    }

    public static void drawBox2(AxisAlignedBB bb, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawBox2(bb, r, g, b, a, sides);
    }

    public static void drawBox2(AxisAlignedBB bb, int r, int g, int b, int a, int sides) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        if ((sides & GeometryMasks.Quad.DOWN) != 0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.UP) != 0) {
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.NORTH) != 0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.SOUTH) != 0) {
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.WEST) != 0) {
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.EAST) != 0) {
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, a).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, a).endVertex();
        }
        tessellator.draw();
    }


    public static void drawFullBox2(AxisAlignedBB bb, BlockPos blockPos, float width, int argb) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawFullBox2(bb, blockPos, width, r, g, b, a, 255);
    }

    public static void drawFullBox2(AxisAlignedBB bb, BlockPos blockPos, float width, int argb, int alpha2) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawFullBox2(bb, blockPos, width, r, g, b, a, alpha2);
    }

    public static void drawFullBox2(AxisAlignedBB bb, BlockPos blockPos, float width, int red, int green, int blue, int alpha, int alpha2) {
        XuluTessellator.prepare(GL11.GL_QUADS);
        drawBox2(bb, red, green, blue, alpha, GeometryMasks.Quad.ALL);
        XuluTessellator.release();
        drawBoundingBox(bb, width, red, green, blue, alpha2);
    }
    public static void drawFullBox(AxisAlignedBB bb, BlockPos blockPos, float width, int argb, int alpha2) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawFullBox(bb, blockPos, width, r, g, b, a, alpha2);
    }

    public static void drawFullBox(AxisAlignedBB bb, BlockPos blockPos, float width, int red, int green, int blue, int alpha, int alpha2) {
        XuluTessellator.prepare(GL11.GL_QUADS);
        drawBox(blockPos, red, green, blue, alpha, GeometryMasks.Quad.ALL);
        XuluTessellator.release();
        drawBoundingBox(bb, width, red, green, blue, alpha2);
    }

    public static void drawFullBoxAA(AxisAlignedBB bb, float width, int argb, int alpha2) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawFullBoxAA(bb, width, r, g, b, a, alpha2);
    }

    public static void drawFullBoxAA(AxisAlignedBB bb, float width, int red, int green, int blue, int alpha, int alpha2) {
        //XuluTessellator.prepare(GL11.GL_QUADS);
        drawBox2(bb, red, green, blue, alpha, GeometryMasks.Quad.ALL);
        //XuluTessellator.release();
        drawBoundingBox(bb, width, red, green, blue, alpha2);
    }

    public static void drawFullFace(AxisAlignedBB bb, BlockPos blockPos, float width, int argb, int alpha2) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawFullFace(bb, blockPos, width, r, g, b, a, alpha2);
    }

    public static void drawFullFace(AxisAlignedBB bb, BlockPos blockPos, float width, int red, int green, int blue, int alpha, int alpha2) {
        XuluTessellator.prepare(GL11.GL_QUADS);
        drawFace(blockPos, red, green, blue, alpha, GeometryMasks.Quad.ALL);
        XuluTessellator.release();
        drawBoundingBoxFace(bb, width, red, green, blue, alpha2);
    }
    public static void drawBoundingBox(AxisAlignedBB bb, float width, int argb) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawBoundingBox(bb, width, r, g, b, a);
    }
    public static void drawBoundingBox(AxisAlignedBB bb, float width, int red, int green, int blue, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(width);

        //final float alpha = (color >> 24 & 0xFF) / 255.0F;
        //final float red = (color >> 16 & 0xFF) / 255.0F;
        //final float green = (color >> 8 & 0xFF) / 255.0F;
        //final float blue = (color & 0xFF) / 255.0F;

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();


        //laptop
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        //L1
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        //L2
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        //Line
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();


        glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawIndicator(AxisAlignedBB bb, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawIndicator(bb, r, g, b, a, sides);
    }
    public static void drawIndicator(AxisAlignedBB bb, int r, int g, int b, int a, int sides) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        if ((sides & GeometryMasks.Quad.DOWN) != 0) {
            bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.NORTH) != 0) {
            bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, 0).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, 0).endVertex();
        }

        if ((sides & GeometryMasks.Quad.SOUTH) != 0) {
            bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, 0).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, 0).endVertex();
        }

        if ((sides & GeometryMasks.Quad.WEST) != 0) {
            bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, 0).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, 0).endVertex();
        }

        if ((sides & GeometryMasks.Quad.EAST) != 0) {
            bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, 0).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, 0).endVertex();
        }
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxFace(AxisAlignedBB bb, float width, int red, int green, int blue, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(width);

        //final float alpha = (color >> 24 & 0xFF) / 255.0F;
        //final float red = (color >> 16 & 0xFF) / 255.0F;
        //final float green = (color >> 8 & 0xFF) / 255.0F;
        //final float blue = (color & 0xFF) / 255.0F;

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();

        tessellator.draw();
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawForgehaxLines(
            final BufferBuilder buffer,
            final double x0,
            final double y0,
            final double z0,
            final double x1,
            final double y1,
            final double z1,
            final int sides,
            final int argb) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;

        drawForgehaxLines(buffer, x0, y0, z0, x1, y1, z1, sides, a, r, g, b);
    }

    public static void drawForgehaxLines(
            final BufferBuilder buffer,
            final double x0,
            final double y0,
            final double z0,
            final double x1,
            final double y1,
            final double z1,
            final int sides,
            final int a,
            final int r,
            final int g,
            final int b) {
        if ((sides & GeometryMasks.Line.DOWN_WEST) != 0) {
            buffer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
            buffer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.UP_WEST) != 0) {
            buffer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
            buffer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.DOWN_EAST) != 0) {
            buffer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
            buffer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.UP_EAST) != 0) {
            buffer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
            buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.DOWN_NORTH) != 0) {
            buffer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
            buffer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.UP_NORTH) != 0) {
            buffer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
            buffer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.DOWN_SOUTH) != 0) {
            buffer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
            buffer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.UP_SOUTH) != 0) {
            buffer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
            buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.NORTH_WEST) != 0) {
            buffer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
            buffer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.NORTH_EAST) != 0) {
            buffer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
            buffer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.SOUTH_WEST) != 0) {
            buffer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
            buffer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.SOUTH_EAST) != 0) {
            buffer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
            buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
        }
    }

}
