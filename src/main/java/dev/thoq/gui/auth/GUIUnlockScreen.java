package dev.thoq.gui.auth;

import dev.thoq.Alya;
import dev.thoq.gui.GUIPasswordField;
import dev.thoq.util.font.AlyaFontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GUIUnlockScreen extends GuiScreen {

    private static final AlyaFontRenderer FONT_MD = Alya.getInstance().getFontRendererMedium();
    private static final AlyaFontRenderer FONT_SM = Alya.getInstance().getFontRendererSmall();
    private GuiTextField inputField;
    private String message = "Enter password to unlock alts:";
    private boolean confirmingReset = false;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        if(confirmingReset) {
            this.buttonList.add(new GuiButton(3, width / 2 - 75, height / 2 + 6, 72, 20, "§cYes, erase"));
            this.buttonList.add(new GuiButton(4, width / 2 + 3, height / 2 + 6, 72, 20, "Cancel"));
            inputField = null;
        } else {
            inputField = new GUIPasswordField(200, fontRendererObj, width / 2 - 75, height / 2 - 10, 150, 20);
            inputField.setFocused(true);
            this.buttonList.add(new GuiButton(0, width / 2 - 75, height / 2 + 16, 72, 20, "Unlock"));
            this.buttonList.add(new GuiButton(1, width / 2 + 3, height / 2 + 16, 72, 20, "Back"));
            this.buttonList.add(new GuiButton(2, width / 2 - 75, height / 2 + 40, 150, 20, "§cForgot Password"));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        if(confirmingReset) {
            final String warn1 = "§cThis will erase ALL saved alts!";
            final String warn2 = "§cThis action cannot be undone.";
            final String warn3 = "Are you sure?";
            FONT_MD.drawString(warn1,
                    width / 2f - FONT_MD.getStringWidth(warn1) / 2, height / 2f - 50, 0xFFFFFFFF);
            FONT_SM.drawString(warn2,
                    width / 2f - FONT_SM.getStringWidth(warn2) / 2, height / 2f - 30, 0xFFFFFFFF);
            FONT_MD.drawString(warn3,
                    width / 2f - FONT_MD.getStringWidth(warn3) / 2, height / 2f - 12, 0xFFFFFFFF);
        } else {
            FONT_MD.drawString(message,
                    width / 2f - FONT_MD.getStringWidth(message) / 2, height / 2f - 40, 0xFFFFFFFF);
            inputField.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(final GuiButton button) {
        switch(button.id) {
            case 0:
                tryUnlock();
                break;
            case 1:
                mc.displayGuiScreen(new GuiMainMenu());
                break;
            case 2:
                confirmingReset = true;
                initGui();
                break;
            case 3:
                AltStorage.getInstance().resetAll();
                mc.displayGuiScreen(new AltManagerGui());
                break;
            case 4:
                confirmingReset = false;
                initGui();
                break;
        }
    }

    private void tryUnlock() {
        if(AltStorage.getInstance().unlock(inputField.getText())) {
            mc.displayGuiScreen(new AltManagerGui());
        } else {
            message = "§cWrong password, try again!";
        }
    }

    @Override
    protected void keyTyped(final char character, final int key) throws IOException {
        if(confirmingReset) {
            if(key == Keyboard.KEY_ESCAPE) {
                confirmingReset = false;
                initGui();
            }
            return;
        }
        if(key == Keyboard.KEY_RETURN) {
            tryUnlock();
            return;
        }
        if(key == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(new GuiMainMenu());
            return;
        }
        inputField.textboxKeyTyped(character, key);
    }

    @Override
    public void mouseClicked(final int x, final int y, final int button) {
        try {
            super.mouseClicked(x, y, button);
        } catch(final IOException ioException) {
            Alya.getInstance().getLogger().error("[UnlockScreen] mouseClicked error", ioException);
        }
        if(inputField != null) {
            inputField.mouseClicked(x, y, button);
        }
    }

    @Override
    public void updateScreen() {
        if(inputField != null) {
            inputField.updateCursorCounter();
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

}
