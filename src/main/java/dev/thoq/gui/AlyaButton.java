package dev.thoq.gui;

import dev.thoq.Alya;
import dev.thoq.util.font.AlyaFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("SameParameterValue")
public final class AlyaButton extends GuiButton {

    private static final int BACKGROUND_COLOR = 0x80000000;
    private static final int BACKGROUND_HOVER = 0x85000000;
    private static final int BACKGROUND_DISABLED = 0x60000000;
    private static final int BORDER_COLOR = 0x60FFFFFF;
    private static final int BORDER_HOVER = 0x80FFFFFF;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int TEXT_DISABLED = 0xFF666666;

    private static final int CORNER_RADIUS = 6;

    public AlyaButton(final int buttonId, final int x, final int y, final String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public AlyaButton(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    @Override
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
        if(!this.visible) {
            return;
        }

        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        int bgColor;
        int borderColor;
        int textColor;

        if(!this.enabled) {
            bgColor = BACKGROUND_DISABLED;
            borderColor = BORDER_COLOR;
            textColor = TEXT_DISABLED;
        } else {
            bgColor = this.hovered ? BACKGROUND_HOVER : BACKGROUND_COLOR;
            borderColor = this.hovered ? BORDER_HOVER : BORDER_COLOR;
            textColor = TEXT_COLOR;
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();

        GlStateManager.disableTexture2D();
        drawRoundedRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, CORNER_RADIUS, bgColor);

        drawRoundedRectOutline(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, CORNER_RADIUS, borderColor);

        GlStateManager.enableTexture2D();

        this.mouseDragged(mc, mouseX, mouseY);
        this.drawButtonText(mc, textColor);

        GlStateManager.disableBlend();
    }

    @Override
    protected void drawButtonText(final Minecraft mc, final int textColor) {
        final AlyaFontRenderer fontRenderer = Alya.getInstance().getFontRendererMedium();
        if(fontRenderer != null) {
            final float textWidth = fontRenderer.getStringWidth(this.displayString);
            final float textX = this.xPosition + (this.width - textWidth) / 2;
            final float textY = this.yPosition + (this.height - fontRenderer.getFontHeight()) / 2 + 1;
            fontRenderer.drawString(this.displayString, textX, textY, textColor);
        } else {
            drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, textColor);
        }
    }


    private void drawRoundedRect(int left, int top, int right, int bottom, int radius, int color) {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        GL11.glColor4f(r, g, b, a);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glBegin(GL11.GL_POLYGON);

        drawCorner(left + radius, top + radius, radius, 180, 270);
        drawCorner(right - radius, top + radius, radius, 270, 360);
        drawCorner(right - radius, bottom - radius, radius, 0, 90);
        drawCorner(left + radius, bottom - radius, radius, 90, 180);

        GL11.glEnd();
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
    }

    private void drawRoundedRectOutline(int left, int top, int right, int bottom, int radius, int color) {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        GL11.glColor4f(r, g, b, a);
        GL11.glLineWidth(1.0F);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glBegin(GL11.GL_LINE_LOOP);

        drawCorner(left + radius, top + radius, radius, 180, 270);
        drawCorner(right - radius, top + radius, radius, 270, 360);
        drawCorner(right - radius, bottom - radius, radius, 0, 90);
        drawCorner(left + radius, bottom - radius, radius, 90, 180);

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    private void drawCorner(int cx, int cy, int radius, int startAngle, int endAngle) {
        for(float angle = startAngle; angle <= endAngle; angle += 0.5f) {
            double rad = Math.toRadians(angle);
            GL11.glVertex2d(cx + Math.cos(rad) * radius, cy + Math.sin(rad) * radius);
        }
    }


}
