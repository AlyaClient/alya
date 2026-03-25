package dev.thoq.gui.auth;

import dev.thoq.Alya;
import dev.thoq.util.font.AlyaFontRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@SuppressWarnings({"CallToPrintStackTrace", "RedundantArrayCreation"})
public class LoginGui extends GuiScreen {
    private GuiTextField username;
    private static final AlyaFontRenderer FONT_MD = Alya.getInstance().getFontRendererMedium();

    @Override
    protected void actionPerformed(final GuiButton button) {
        if(button.id == 0) {
            if(this.username.getText().isEmpty()) {
                this.mc.displayGuiScreen(new LoginGui());
            } else {
                SessionChanger.getInstance().setUserOffline(this.username.getText());
                this.mc.displayGuiScreen(new GuiMainMenu());
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.username.drawTextBox();
        FONT_MD.drawString(
                "Alya Portal - Cracked",
                (float) this.width / 2 - FONT_MD.getStringWidth("Alya Portal - Cracked") / 2,
                Math.round((float) this.height / 2 - 80),
                -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        final ScaledResolution sr = new ScaledResolution(this.mc);
        this.buttonList.clear();
        this.buttonList.add(
                new GuiButton(
                        0,
                        this.width / 2 - 50 - 10,
                        this.height / 2 - 20,
                        120,
                        20,
                        I18n.format("Login", new Object[0])));
        (this.username =
                new GuiTextField(
                        100,
                        this.fontRendererObj,
                        this.width / 2 - 50 - 10,
                        sr.getScaledHeight() / 2 - 50,
                        120,
                        20))
                .setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(final char character, final int key) {
        try {
            super.keyTyped(character, key);
        } catch(final IOException ioException) {
            ioException.printStackTrace();
        }
        if(character == '\t' && !this.username.isFocused()) {
            this.username.setFocused(true);
        }
        if(character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
    }

    @Override
    public void mouseClicked(final int x2, final int y2, final int button) {
        try {
            super.mouseClicked(x2, y2, button);
        } catch(IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(x2, y2, button);
    }

    @Override
    public void onGuiClosed() {
        mc.entityRenderer.loadEntityShader(null);
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
    }
}
