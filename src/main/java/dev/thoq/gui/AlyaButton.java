package dev.thoq.gui;

import dev.thoq.Alya;
import dev.thoq.util.AlyaFontRenderer;
import dev.thoq.util.RenderUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public final class AlyaButton extends GuiButton {

    private static final int BACKGROUND_COLOR = 0xE0181818;
    private static final int BACKGROUND_HOVER = 0xE0252525;
    private static final int BACKGROUND_DISABLED = 0xE0101010;
    private static final int BORDER_COLOR = 0xFF303030;
    private static final int BORDER_HOVER = 0xFF505050;
    private static final int ACCENT_COLOR = 0xFF8B5CF6;

    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int TEXT_DISABLED = 0xFF666666;

    public AlyaButton(final int buttonId, final int x, final int y, final String buttonText) {
        super(buttonId, x, y, 200, 20, buttonText);
    }

    public AlyaButton(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    @Override
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
        if(this.visible) {
            final AlyaFontRenderer fontRenderer = Alya.getInstance().getFontRendererSmall();
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition
                    && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            int bgColor;
            int borderColor;
            int textColor;

            if(!this.enabled) {
                bgColor = BACKGROUND_DISABLED;
                borderColor = BORDER_COLOR;
                textColor = TEXT_DISABLED;
            } else if(this.hovered) {
                bgColor = BACKGROUND_HOVER;
                borderColor = BORDER_HOVER;
                textColor = TEXT_COLOR;
            } else {
                bgColor = BACKGROUND_COLOR;
                borderColor = BORDER_COLOR;
                textColor = TEXT_COLOR;
            }

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            RenderUtility.drawRect(this.xPosition, this.yPosition, this.width, this.height, bgColor);
            RenderUtility.drawRect(this.xPosition, this.yPosition, this.width, 1, borderColor);
            RenderUtility.drawRect(this.xPosition, this.yPosition + this.height - 1, this.width, 1, borderColor);
            RenderUtility.drawRect(this.xPosition, this.yPosition, 1, this.height, borderColor);
            RenderUtility.drawRect(this.xPosition + this.width - 1, this.yPosition, 1, this.height, borderColor);

            if(this.hovered && this.enabled) {
                RenderUtility.drawRect(this.xPosition, this.yPosition, 2, this.height, ACCENT_COLOR);
            }

            final float textWidth = fontRenderer.getStringWidth(this.displayString);
            final float textX = this.xPosition + (this.width - textWidth) / 2;
            final float textY = this.yPosition + (this.height - fontRenderer.getFontHeight()) / 2 + 1;
            fontRenderer.drawStringWithShadow(this.displayString, textX, textY, textColor);

            GlStateManager.disableBlend();
        }
    }


}
