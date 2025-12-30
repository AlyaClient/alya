package dev.thoq.gui.auth;

import dev.thoq.Alya;
import dev.thoq.gui.AltManagerGui;
import dev.thoq.gui.SessionChanger;
import dev.thoq.util.auth.MicrosoftAuth;
import dev.thoq.util.auth.SessionManager;
import dev.thoq.util.font.AlyaFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebLoginLauncher extends GuiScreen {

    private ExecutorService executor = null;
    private CompletableFuture<Void> task = null;
    private static final AlyaFontRenderer FONT_MD = Alya.getInstance().getFontRendererMedium();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
        FONT_MD.drawString("Please continue in your web browser", (float) this.width / 2 - FONT_MD.getStringWidth("Please continue in your web browser") / 2, Math.round((float) this.height / 2 - 70), -1);
    }

    @Override
    public void initGui() {
        Display.setTitle(String.format("%s %s - Alt Manager", Alya.getName(), Alya.getVersion()));
        final ScaledResolution sr = new ScaledResolution(this.mc);
        final int textY = sr.getScaledHeight() / 2 - 65;
        final int buttonY = textY + 30;
        final int buttonWidth = 120;
        final int buttonHeight = 20;
        final int buttonX = (this.width / 2) - (buttonWidth / 2);

        this.buttonList.add(new GuiButton(0, buttonX, buttonY, buttonWidth, buttonHeight, "Done"));
        this.buttonList.add(new GuiButton(1, buttonX, buttonY + buttonHeight + 5, buttonWidth, buttonHeight, "Cancel"));
        super.initGui();

        if(task == null) {
            if(executor == null) {
                executor = Executors.newSingleThreadExecutor();
            }

            try {
                task = MicrosoftAuth.acquireMSAuthCode(executor)
                        .thenComposeAsync(msAuthCode -> MicrosoftAuth.acquireMSAccessToken(msAuthCode, executor))
                        .thenComposeAsync(msAccessToken -> MicrosoftAuth.acquireXboxAccessToken(msAccessToken, executor))
                        .thenComposeAsync(xboxAccessToken -> MicrosoftAuth.acquireXboxXstsToken(xboxAccessToken, executor))
                        .thenComposeAsync(xboxXstsData -> MicrosoftAuth.acquireMCAccessToken(
                                xboxXstsData.get("Token"), xboxXstsData.get("uhs"), executor
                        ))
                        .thenComposeAsync(mcToken -> MicrosoftAuth.login(mcToken, executor))
                        .thenAccept(session -> {
                            SessionManager.setSession(session);
                            Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
                        })
                        .exceptionally(error -> null);
            } catch(final Exception exception) {
                Alya.getInstance().getLogger().error("Failed to acquire Microsoft access token", exception);
            }
        }
    }

    @Override
    protected void actionPerformed(final GuiButton button) {
        if(button.id == 0) {
            mc.displayGuiScreen(new AltManagerGui());
        }
        if(button.id == 1) {
            SessionChanger.getInstance().setUserOffline("Alya");
            mc.displayGuiScreen(new AltManagerGui());
        }
    }


}
