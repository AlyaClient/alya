package dev.thoq.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GUIPasswordField extends GuiTextField {

    private String realText = "";

    public GUIPasswordField(final int componentId, final FontRenderer fontRenderer, final int x, final int y, final int width, final int height) {
        super(componentId, fontRenderer, x, y, width, height);
    }

    @Override
    public void setText(final String text) {
        this.realText = text;
        super.setText(text);
    }

    @Override
    public void writeText(final String text) {
        super.writeText(text);
        this.realText = super.getText();
    }

    @Override
    public void deleteFromCursor(final int num) {
        super.deleteFromCursor(num);
        this.realText = super.getText();
    }

    @Override
    public void deleteWords(final int num) {
        super.deleteWords(num);
        this.realText = super.getText();
    }

    @Override
    public String getText() {
        return this.realText;
    }

    @Override
    public void drawTextBox() {
        final int cursor = getCursorPosition();
        final int selection = getSelectionEnd();

        super.setText("*".repeat(realText.length()));
        setCursorPosition(cursor);
        setSelectionPos(selection);

        super.drawTextBox();

        super.setText(realText);
        setCursorPosition(cursor);
        setSelectionPos(selection);
    }


}
