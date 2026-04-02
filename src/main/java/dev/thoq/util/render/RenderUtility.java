package dev.thoq.util.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public final class RenderUtility {
    public static void drawRect(
            final float x, final float y, final float width, final float height, final int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
    }

    public static void drawRectAbsolute(
            final float left, final float top, final float right, final float bottom, final int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }

    public static void drawRectOutline(
            final float x,
            final float y,
            final float width,
            final float height,
            final int color,
            final float thickness) {
        drawRect(x, y, width, thickness, color);
        drawRect(x, y + height - thickness, width, thickness, color);
        drawRect(x, y, thickness, height, color);
        drawRect(x + width - thickness, y, thickness, height, color);
    }

    public static void drawArc(
            final float centerX,
            final float centerY,
            final float radius,
            final int startAngle,
            final int endAngle,
            final int color) {
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
        worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(centerX, centerY, 0).endVertex();
        for(int i = startAngle; i <= endAngle; i++) {
            final double angle = Math.toRadians(i);
            worldRenderer
                    .pos(centerX + Math.cos(angle) * radius, centerY + Math.sin(angle) * radius, 0)
                    .endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawCircle(
            final float centerX, final float centerY, final float radius, final int color) {
        drawArc(centerX, centerY, radius, 0, 360, color);
    }

    public static void drawHorizontalGradient(
            final float x,
            final float y,
            final float width,
            final float height,
            final int leftColor,
            final int rightColor) {
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
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer
                .pos(x, y + height, 0)
                .color(leftRed, leftGreen, leftBlue, leftAlpha)
                .endVertex();
        worldRenderer
                .pos(x + width, y + height, 0)
                .color(rightRed, rightGreen, rightBlue, rightAlpha)
                .endVertex();
        worldRenderer
                .pos(x + width, y, 0)
                .color(rightRed, rightGreen, rightBlue, rightAlpha)
                .endVertex();
        worldRenderer.pos(x, y, 0).color(leftRed, leftGreen, leftBlue, leftAlpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawVerticalGradient(
            final float x,
            final float y,
            final float width,
            final float height,
            final int topColor,
            final int bottomColor) {
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
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer
                .pos(x, y + height, 0)
                .color(bottomRed, bottomGreen, bottomBlue, bottomAlpha)
                .endVertex();
        worldRenderer
                .pos(x + width, y + height, 0)
                .color(bottomRed, bottomGreen, bottomBlue, bottomAlpha)
                .endVertex();
        worldRenderer
                .pos(x + width, y, 0)
                .color(topRed, topGreen, topBlue, topAlpha)
                .endVertex();
        worldRenderer.pos(x, y, 0).color(topRed, topGreen, topBlue, topAlpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawLine(
            final float x1,
            final float y1,
            final float x2,
            final float y2,
            final float thickness,
            final int color) {
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
        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(x1, y1, 0).endVertex();
        worldRenderer.pos(x2, y2, 0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Projects a world position to screen coordinates. Returns null if the point is behind the
     * camera. Result is in display pixels (not scaled gui coords).
     */
    public static float[] worldToScreen(double worldX, double worldY, double worldZ) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        net.minecraft.entity.Entity view = mc.getRenderViewEntity();
        if(view == null) return null;
        final float pt = mc.timer.renderPartialTicks;
        final double cx = view.lastTickPosX + (view.posX - view.lastTickPosX) * pt;
        final double cy = view.lastTickPosY + (view.posY - view.lastTickPosY) * pt;
        final double cz = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * pt;
        java.nio.FloatBuffer modelview =
                net.minecraft.client.renderer.GLAllocation.createDirectFloatBuffer(16);
        java.nio.FloatBuffer projection =
                net.minecraft.client.renderer.GLAllocation.createDirectFloatBuffer(16);
        java.nio.IntBuffer viewport =
                net.minecraft.client.renderer.GLAllocation.createDirectIntBuffer(16);
        java.nio.FloatBuffer screenCoords =
                net.minecraft.client.renderer.GLAllocation.createDirectFloatBuffer(3);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        float rx = (float) (worldX - cx);
        float ry = (float) (worldY - cy);
        float rz = (float) (worldZ - cz);
        boolean projected =
                org.lwjgl.util.glu.Project.gluProject(
                        rx, ry, rz, modelview, projection, viewport, screenCoords);
        if(!projected || screenCoords.get(2) > 1.0f) return null;
        net.minecraft.client.gui.ScaledResolution sr =
                new net.minecraft.client.gui.ScaledResolution(mc);
        float scale = sr.getScaleFactor();
        float sx = screenCoords.get(0) / scale;
        float sy = (viewport.get(3) - screenCoords.get(1)) / scale;
        return new float[]{sx, sy};
    }

    public static void drawBox3D(
            final double worldX,
            final double worldY,
            final double worldZ,
            final double w,
            final double h,
            final int color,
            final float lineWidth) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        net.minecraft.entity.Entity view = mc.getRenderViewEntity();
        if(view == null) return;
        final float alpha = (color >> 24 & 0xFF) / 255.0F;
        final float red = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >> 8 & 0xFF) / 255.0F;
        final float blue = (color & 0xFF) / 255.0F;
        final float pt = mc.timer.renderPartialTicks;
        final double cx = view.lastTickPosX + (view.posX - view.lastTickPosX) * pt;
        final double cy = view.lastTickPosY + (view.posY - view.lastTickPosY) * pt;
        final double cz = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * pt;
        final double ox = worldX - cx;
        final double oy = worldY - cy;
        final double oz = worldZ - cz;
        final double hw = w / 2.0;
        final double x0 = ox - hw, x1 = ox + hw;
        final double y1 = oy + h;
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
        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_TEX);

        wr.pos(x0, oy, z0).endVertex();
        wr.pos(x1, oy, z0).endVertex();
        wr.pos(x1, oy, z0).endVertex();
        wr.pos(x1, oy, z1).endVertex();
        wr.pos(x1, oy, z1).endVertex();
        wr.pos(x0, oy, z1).endVertex();
        wr.pos(x0, oy, z1).endVertex();
        wr.pos(x0, oy, z0).endVertex();
        wr.pos(x0, y1, z0).endVertex();
        wr.pos(x1, y1, z0).endVertex();
        wr.pos(x1, y1, z0).endVertex();
        wr.pos(x1, y1, z1).endVertex();
        wr.pos(x1, y1, z1).endVertex();
        wr.pos(x0, y1, z1).endVertex();
        wr.pos(x0, y1, z1).endVertex();
        wr.pos(x0, y1, z0).endVertex();
        wr.pos(x0, oy, z0).endVertex();
        wr.pos(x0, y1, z0).endVertex();
        wr.pos(x1, oy, z0).endVertex();
        wr.pos(x1, y1, z0).endVertex();
        wr.pos(x1, oy, z1).endVertex();
        wr.pos(x1, y1, z1).endVertex();
        wr.pos(x0, oy, z1).endVertex();
        wr.pos(x0, y1, z1).endVertex();
        tess.draw();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public static void drawCircle3D(
            final double worldX,
            final double worldY,
            final double worldZ,
            final double radius,
            final int segments,
            final int color,
            final float lineWidth) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        net.minecraft.entity.Entity view = mc.getRenderViewEntity();
        if(view == null) return;
        final float alpha = (color >> 24 & 0xFF) / 255.0F;
        final float red = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >> 8 & 0xFF) / 255.0F;
        final float blue = (color & 0xFF) / 255.0F;
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
        wr.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_TEX);
        for(int i = 0; i < segments; i++) {
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

    public static void renderEntityChams(
            final int entityId, final float red, final float green, final float blue, final float alpha) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if(mc.theWorld == null || mc.thePlayer == null) return;
        net.minecraft.entity.Entity entity = mc.theWorld.getEntityByID(entityId);
        if(!(entity instanceof net.minecraft.entity.EntityLivingBase)) return;
        net.minecraft.entity.Entity view = mc.getRenderViewEntity();
        if(view == null) return;
        final float pt = mc.timer.renderPartialTicks;
        final double cx = view.lastTickPosX + (view.posX - view.lastTickPosX) * pt;
        final double cy = view.lastTickPosY + (view.posY - view.lastTickPosY) * pt;
        final double cz = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * pt;
        final double ix = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pt;
        final double iy = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pt;
        final double iz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pt;
        net.minecraft.util.AxisAlignedBB aabb = entity.getEntityBoundingBox();
        final double x0 = aabb.minX - entity.posX + ix - cx;
        final double y0 = aabb.minY - entity.posY + iy - cy;
        final double z0 = aabb.minZ - entity.posZ + iz - cz;
        final double x1 = aabb.maxX - entity.posX + ix - cx;
        final double y1 = aabb.maxY - entity.posY + iy - cy;
        final double z1 = aabb.maxZ - entity.posZ + iz - cz;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GlStateManager.color(red, green, blue, alpha);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(x0, y0, z0).endVertex();
        worldRenderer.pos(x1, y0, z0).endVertex();
        worldRenderer.pos(x1, y0, z1).endVertex();
        worldRenderer.pos(x0, y0, z1).endVertex();
        worldRenderer.pos(x0, y1, z0).endVertex();
        worldRenderer.pos(x0, y1, z1).endVertex();
        worldRenderer.pos(x1, y1, z1).endVertex();
        worldRenderer.pos(x1, y1, z0).endVertex();
        worldRenderer.pos(x0, y0, z0).endVertex();
        worldRenderer.pos(x0, y1, z0).endVertex();
        worldRenderer.pos(x1, y1, z0).endVertex();
        worldRenderer.pos(x1, y0, z0).endVertex();
        worldRenderer.pos(x1, y0, z0).endVertex();
        worldRenderer.pos(x1, y1, z0).endVertex();
        worldRenderer.pos(x1, y1, z1).endVertex();
        worldRenderer.pos(x1, y0, z1).endVertex();
        worldRenderer.pos(x1, y0, z1).endVertex();
        worldRenderer.pos(x1, y1, z1).endVertex();
        worldRenderer.pos(x0, y1, z1).endVertex();
        worldRenderer.pos(x0, y0, z1).endVertex();
        worldRenderer.pos(x0, y0, z1).endVertex();
        worldRenderer.pos(x0, y1, z1).endVertex();
        worldRenderer.pos(x0, y1, z0).endVertex();
        worldRenderer.pos(x0, y0, z0).endVertex();
        tessellator.draw();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void renderEntityOutline(
            final int entityId,
            final float red,
            final float green,
            final float blue,
            final float alpha,
            final float lineWidth) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if(mc.theWorld == null || mc.thePlayer == null) return;
        net.minecraft.entity.Entity entity = mc.theWorld.getEntityByID(entityId);
        if(!(entity instanceof net.minecraft.entity.EntityLivingBase)) return;
        net.minecraft.entity.Entity view = mc.getRenderViewEntity();
        if(view == null) return;
        final float pt = mc.timer.renderPartialTicks;
        final double cx = view.lastTickPosX + (view.posX - view.lastTickPosX) * pt;
        final double cy = view.lastTickPosY + (view.posY - view.lastTickPosY) * pt;
        final double cz = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * pt;
        final double ix = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pt;
        final double iy = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pt;
        final double iz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pt;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glLineWidth(lineWidth);
        GlStateManager.color(red, green, blue, alpha);
        mc.getRenderManager().renderEntityWithPosYaw(entity, ix - cx, iy - cy, iz - cz, entity.rotationYaw, pt);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawImage(
            final net.minecraft.util.ResourceLocation location,
            final float x,
            final float y,
            final float width,
            final float height) {
        net.minecraft.client.Minecraft.getMinecraft().getTextureManager().bindTexture(location);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(x, y + height, 0.0D).tex(0.0D, 1.0D).endVertex();
        worldRenderer.pos(x + width, y + height, 0.0D).tex(1.0D, 1.0D).endVertex();
        worldRenderer.pos(x + width, y, 0.0D).tex(1.0D, 0.0D).endVertex();
        worldRenderer.pos(x, y, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();

        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }
}
