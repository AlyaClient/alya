package dev.thoq.gui.auth;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

public class AltManagerGui extends GuiScreen {
    @Override
    public void initGui() {
        System.currentTimeMillis();
        final int buttonWidth = 100;
        final int buttonHeight = 20;
        final int buttonSpacing = 10;
        final int totalHeight = 4 * buttonHeight + 3 * buttonSpacing;
        final int startY = (this.height / 2) - (totalHeight / 2);
        final int buttonX = (this.width / 2) - (buttonWidth / 2);
        this.buttonList.add(new GuiButton(3, buttonX, startY, buttonWidth, buttonHeight, "Browser"));
        this.buttonList.add(
                new GuiButton(
                        1,
                        buttonX,
                        startY + buttonHeight + buttonSpacing,
                        buttonWidth,
                        buttonHeight,
                        "Cracked"));
        this.buttonList.add(
                new GuiButton(
                        0,
                        buttonX,
                        startY + (buttonHeight + buttonSpacing) * 2,
                        buttonWidth,
                        buttonHeight,
                        "Exit"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch(button.id) {
            case 0:
                mc.displayGuiScreen(new GuiMainMenu());
                break;
            case 1:
                this.buttonList.clear();
                mc.displayGuiScreen(new LoginGui());
                break;
            case 3:
                mc.displayGuiScreen(new WebLoginLauncher());
                break;
        }
    }
}
