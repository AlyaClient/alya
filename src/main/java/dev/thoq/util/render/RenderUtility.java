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
        final float alpha = (color >> 24 & 0xFF) / 255.0F;
        final float red = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >> 8 & 0xFF) / 255.0F;
        final float blue = (color & 0xFF) / 255.0F;

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
        final float alpha = (color >> 24 & 0xFF) / 255.0F;
        final float red = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >> 8 & 0xFF) / 255.0F;
        final float blue = (color & 0xFF) / 255.0F;

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
        final float leftAlpha = (leftColor >> 24 & 0xFF) / 255.0F;
        final float leftRed = (leftColor >> 16 & 0xFF) / 255.0F;
        final float leftGreen = (leftColor >> 8 & 0xFF) / 255.0F;
        final float leftBlue = (leftColor & 0xFF) / 255.0F;

        final float rightAlpha = (rightColor >> 24 & 0xFF) / 255.0F;
        final float rightRed = (rightColor >> 16 & 0xFF) / 255.0F;
        final float rightGreen = (rightColor >> 8 & 0xFF) / 255.0F;
        final float rightBlue = (rightColor & 0xFF) / 255.0F;

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
        final float topAlpha = (topColor >> 24 & 0xFF) / 255.0F;
        final float topRed = (topColor >> 16 & 0xFF) / 255.0F;
        final float topGreen = (topColor >> 8 & 0xFF) / 255.0F;
        final float topBlue = (topColor & 0xFF) / 255.0F;

        final float bottomAlpha = (bottomColor >> 24 & 0xFF) / 255.0F;
        final float bottomRed = (bottomColor >> 16 & 0xFF) / 255.0F;
        final float bottomGreen = (bottomColor >> 8 & 0xFF) / 255.0F;
        final float bottomBlue = (bottomColor & 0xFF) / 255.0F;

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
        final float alpha = (color >> 24 & 0xFF) / 255.0F;
        final float red = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >> 8 & 0xFF) / 255.0F;
        final float blue = (color & 0xFF) / 255.0F;

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

    /**
     * Projects a world position to screen coordinates.
     * Returns null if the point is behind the camera.
     * Result is in display pixels (not scaled GUI coords).
     */
    public static float[] worldToScreen(double worldX, double worldY, double worldZ) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        net.minecraft.entity.Entity view = mc.getRenderViewEntity();
        if (view == null) return null;

        final float pt = mc.timer.renderPartialTicks;
        final double cx = view.lastTickPosX + (view.posX - view.lastTickPosX) * pt;
        final double cy = view.lastTickPosY + (view.posY - view.lastTickPosY) * pt;
        final double cz = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * pt;

        java.nio.FloatBuffer modelview   = net.minecraft.client.renderer.GLAllocation.createDirectFloatBuffer(16);
        java.nio.FloatBuffer projection  = net.minecraft.client.renderer.GLAllocation.createDirectFloatBuffer(16);
        java.nio.IntBuffer   viewport    = net.minecraft.client.renderer.GLAllocation.createDirectIntBuffer(16);
        java.nio.FloatBuffer screenCoords = net.minecraft.client.renderer.GLAllocation.createDirectFloatBuffer(3);

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

        float rx = (float)(worldX - cx);
        float ry = (float)(worldY - cy);
        float rz = (float)(worldZ - cz);

        boolean projected = org.lwjgl.util.glu.Project.gluProject(rx, ry, rz, modelview, projection, viewport, screenCoords);
        if (!projected || screenCoords.get(2) > 1.0f) return null; // behind camera

        net.minecraft.client.gui.ScaledResolution sr = new net.minecraft.client.gui.ScaledResolution(mc);
        float scale = sr.getScaleFactor();
        float sx = screenCoords.get(0) / scale;
        float sy = (viewport.get(3) - screenCoords.get(1)) / scale;
        return new float[]{ sx, sy };
    }

    public static void drawBox3D(final double worldX, final double worldY, final double worldZ,
                                 final double w, final double h,
                                 final int color, final float lineWidth) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        net.minecraft.entity.Entity view = mc.getRenderViewEntity();
        if (view == null) return;

        final float alpha = (color >> 24 & 0xFF) / 255.0F;
        final float red   = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >>  8 & 0xFF) / 255.0F;
        final float blue  = (color       & 0xFF) / 255.0F;

        final float pt = mc.timer.renderPartialTicks;
        final double cx = view.lastTickPosX + (view.posX - view.lastTickPosX) * pt;
        final double cy = view.lastTickPosY + (view.posY - view.lastTickPosY) * pt;
        final double cz = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * pt;

        final double ox = worldX - cx;
        final double oy = worldY - cy;
        final double oz = worldZ - cz;

        final double hw = w / 2.0;
        // corners: bottom y=oy, top y=oy+h
        final double x0 = ox - hw, x1 = ox + hw;
        final double y0 = oy,      y1 = oy + h;
        final double z0 = oz - hw, z1 = oz + hw;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableLighting();
        GlStateManager.color(red, green, blue, alpha);
        GL11.glLineWidth(lineWidth);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        wr.begin(GL11.GL_LINES, DefaultVertexFormats.field_181705_e);

        // bottom face
        wr.pos(x0, y0, z0).endVertex(); wr.pos(x1, y0, z0).endVertex();
        wr.pos(x1, y0, z0).endVertex(); wr.pos(x1, y0, z1).endVertex();
        wr.pos(x1, y0, z1).endVertex(); wr.pos(x0, y0, z1).endVertex();
        wr.pos(x0, y0, z1).endVertex(); wr.pos(x0, y0, z0).endVertex();
        // top face
        wr.pos(x0, y1, z0).endVertex(); wr.pos(x1, y1, z0).endVertex();
        wr.pos(x1, y1, z0).endVertex(); wr.pos(x1, y1, z1).endVertex();
        wr.pos(x1, y1, z1).endVertex(); wr.pos(x0, y1, z1).endVertex();
        wr.pos(x0, y1, z1).endVertex(); wr.pos(x0, y1, z0).endVertex();
        // verticals
        wr.pos(x0, y0, z0).endVertex(); wr.pos(x0, y1, z0).endVertex();
        wr.pos(x1, y0, z0).endVertex(); wr.pos(x1, y1, z0).endVertex();
        wr.pos(x1, y0, z1).endVertex(); wr.pos(x1, y1, z1).endVertex();
        wr.pos(x0, y0, z1).endVertex(); wr.pos(x0, y1, z1).endVertex();

        tess.draw();

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public static void drawCircle3D(final double worldX, final double worldY, final double worldZ,
                                    final double radius, final int segments, final int color, final float lineWidth) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        net.minecraft.entity.Entity view = mc.getRenderViewEntity();
        if (view == null) return;

        final float alpha = (color >> 24 & 0xFF) / 255.0F;
        final float red   = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >>  8 & 0xFF) / 255.0F;
        final float blue  = (color       & 0xFF) / 255.0F;

        final float pt = mc.timer.renderPartialTicks;
        double dx = worldX - (view.lastTickPosX + (view.posX - view.lastTickPosX) * pt);
        double dy = worldY - (view.lastTickPosY + (view.posY - view.lastTickPosY) * pt);
        double dz = worldZ - (view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * pt);

        GlStateManager.pushMatrix();
        GlStateManager.translate(dx, dy, dz);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableLighting();
        GlStateManager.color(red, green, blue, alpha);
        GL11.glLineWidth(lineWidth);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer wr = tessellator.getWorldRenderer();
        wr.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.field_181705_e);
        for (int i = 0; i < segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            wr.pos(Math.cos(angle) * radius, 0, Math.sin(angle) * radius).endVertex();
        }
        tessellator.draw();

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
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
