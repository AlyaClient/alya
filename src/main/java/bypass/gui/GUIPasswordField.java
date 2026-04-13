/*
 * Copyright (c) 2026 Alya Client.
 *
 * Alya Client is a free, open-source Minecraft hacked client.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package bypass.gui;

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
