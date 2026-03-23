package dev.thoq.gui;
import net.minecraft.client.gui.GuiButton;
public final class AlyaButton extends GuiButton {
    public AlyaButton(final int buttonId, final int x, final int y, final String buttonText) {
        super(buttonId, x, y, buttonText);
    }
    public AlyaButton(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }
}
