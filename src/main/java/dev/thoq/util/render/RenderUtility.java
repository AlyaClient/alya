package dev.thoq.util.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public final class RenderUtility {

    public static void drawRect(final float x, final float y, final float width, final float height, final int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
    }

    public static void drawRectAbsolute(final float left, final float top, final float right, final float bottom, final int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }

    public static void drawRectOutline(final float x, final float y, final float width, final float height, final int color, final float thickness) {

        drawRect(x, y, width, thickness, color);

        drawRect(x, y + height - thickness, width, thickness, color);

        drawRect(x, y, thickness, height, color);

        drawRect(x + width - thickness, y, thickness, height, color);
    }

    public static void drawRoundedRect(final float x, final float y, final float width, final float height, final float radius, final int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.pos(x + radius, y + height, 0).endVertex();
        worldRenderer.pos(x + width - radius, y + height, 0).endVertex();
        worldRenderer.pos(x + width - radius, y, 0).endVertex();
        worldRenderer.pos(x + radius, y, 0).endVertex();
        tessellator.draw();

        worldRenderer.begin(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.pos(x, y + height - radius, 0).endVertex();
        worldRenderer.pos(x + radius, y + height - radius, 0).endVertex();
        worldRenderer.pos(x + radius, y + radius, 0).endVertex();
        worldRenderer.pos(x, y + radius, 0).endVertex();
        tessellator.draw();

        worldRenderer.begin(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.pos(x + width - radius, y + height - radius, 0).endVertex();
        worldRenderer.pos(x + width, y + height - radius, 0).endVertex();
        worldRenderer.pos(x + width, y + radius, 0).endVertex();
        worldRenderer.pos(x + width - radius, y + radius, 0).endVertex();
        tessellator.draw();

        drawArc(x + radius, y + radius, radius, 180, 270, color);
        drawArc(x + width - radius, y + radius, radius, 270, 360, color);
        drawArc(x + width - radius, y + height - radius, radius, 0, 90, color);
        drawArc(x + radius, y + height - radius, radius, 90, 180, color);

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawArc(final float centerX, final float centerY, final float radius, final int startAngle, final int endAngle, final int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.field_181705_e);
        worldRenderer.pos(centerX, centerY, 0).endVertex();

        for(int i = startAngle; i <= endAngle; i++) {
            final double angle = Math.toRadians(i);
            worldRenderer.pos(centerX + Math.cos(angle) * radius, centerY + Math.sin(angle) * radius, 0).endVertex();
        }

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawCircle(final float centerX, final float centerY, final float radius, final int color) {
        drawArc(centerX, centerY, radius, 0, 360, color);
    }

    public static void drawHorizontalGradient(final float x, final float y, final float width, final float height, final int leftColor, final int rightColor) {
        float leftAlpha = (leftColor >> 24 & 0xFF) / 255.0F;
        float leftRed = (leftColor >> 16 & 0xFF) / 255.0F;
        float leftGreen = (leftColor >> 8 & 0xFF) / 255.0F;
        float leftBlue = (leftColor & 0xFF) / 255.0F;

        float rightAlpha = (rightColor >> 24 & 0xFF) / 255.0F;
        float rightRed = (rightColor >> 16 & 0xFF) / 255.0F;
        float rightGreen = (rightColor >> 8 & 0xFF) / 255.0F;
        float rightBlue = (rightColor & 0xFF) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(7, DefaultVertexFormats.field_181706_f);
        worldRenderer.pos(x, y + height, 0).func_181666_a(leftRed, leftGreen, leftBlue, leftAlpha).endVertex();
        worldRenderer.pos(x + width, y + height, 0).func_181666_a(rightRed, rightGreen, rightBlue, rightAlpha).endVertex();
        worldRenderer.pos(x + width, y, 0).func_181666_a(rightRed, rightGreen, rightBlue, rightAlpha).endVertex();
        worldRenderer.pos(x, y, 0).func_181666_a(leftRed, leftGreen, leftBlue, leftAlpha).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawVerticalGradient(final float x, final float y, final float width, final float height, final int topColor, final int bottomColor) {
        float topAlpha = (topColor >> 24 & 0xFF) / 255.0F;
        float topRed = (topColor >> 16 & 0xFF) / 255.0F;
        float topGreen = (topColor >> 8 & 0xFF) / 255.0F;
        float topBlue = (topColor & 0xFF) / 255.0F;

        float bottomAlpha = (bottomColor >> 24 & 0xFF) / 255.0F;
        float bottomRed = (bottomColor >> 16 & 0xFF) / 255.0F;
        float bottomGreen = (bottomColor >> 8 & 0xFF) / 255.0F;
        float bottomBlue = (bottomColor & 0xFF) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(7, DefaultVertexFormats.field_181706_f);
        worldRenderer.pos(x, y + height, 0).func_181666_a(bottomRed, bottomGreen, bottomBlue, bottomAlpha).endVertex();
        worldRenderer.pos(x + width, y + height, 0).func_181666_a(bottomRed, bottomGreen, bottomBlue, bottomAlpha).endVertex();
        worldRenderer.pos(x + width, y, 0).func_181666_a(topRed, topGreen, topBlue, topAlpha).endVertex();
        worldRenderer.pos(x, y, 0).func_181666_a(topRed, topGreen, topBlue, topAlpha).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawLine(final float x1, final float y1, final float x2, final float y2, final float thickness, final int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);
        GL11.glLineWidth(thickness);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.field_181705_e);
        worldRenderer.pos(x1, y1, 0).endVertex();
        worldRenderer.pos(x2, y2, 0).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static int toARGB(final int alpha, final int red, final int green, final int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int toRGB(final int red, final int green, final int blue) {
        return toARGB(255, red, green, blue);
    }

    public static int withAlpha(final int color, final int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }


}
