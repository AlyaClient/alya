package dev.thoq.gui;

import dev.thoq.Alya;
import dev.thoq.util.font.AlyaFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public final class AlyaButton extends GuiButton {

    public AlyaButton(final int buttonId, final int x, final int y, final String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public AlyaButton(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    @Override
    protected void drawButtonText(Minecraft mc, int textColor) {
        final AlyaFontRenderer fontRenderer = Alya.getInstance().getFontRendererMedium();
        if(fontRenderer != null) {
            final float textWidth = fontRenderer.getStringWidth(this.displayString);
            final float textX = this.xPosition + (this.width - textWidth) / 2;
            final float textY = this.yPosition + (this.height - fontRenderer.getFontHeight()) / 2 + 1;
            fontRenderer.drawStringWithShadow(this.displayString, textX, textY, textColor);
        } else {
            super.drawButtonText(mc, textColor);
        }
    }


}
