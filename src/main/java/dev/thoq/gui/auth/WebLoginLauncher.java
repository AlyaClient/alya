package dev.thoq.gui.auth;

import dev.thoq.Alya;
import dev.thoq.util.auth.MicrosoftAuth;
import dev.thoq.util.auth.SessionManager;
import dev.thoq.util.font.AlyaFontRenderer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class WebLoginLauncher extends GuiScreen {
  private ExecutorService executor = null;
  private CompletableFuture<Void> task = null;
  private static final AlyaFontRenderer FONT_MD = Alya.getInstance().getFontRendererMedium();
  private final AtomicBoolean authComplete = new AtomicBoolean(false);
  private volatile String statusMessage = "Waiting for browser login...";

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.drawDefaultBackground();
    super.drawScreen(mouseX, mouseY, partialTicks);
    float cx = (float) this.width / 2;
    float cy = (float) this.height / 2;
    FONT_MD.drawString(
        statusMessage, cx - FONT_MD.getStringWidth(statusMessage) / 2, Math.round(cy - 70), -1);
  }

  @Override
  public void initGui() {
    final ScaledResolution sr = new ScaledResolution(this.mc);
    final int textY = sr.getScaledHeight() / 2 - 65;
    final int buttonY = textY + 30;
    final int buttonWidth = 120;
    final int buttonHeight = 20;
    final int buttonX = (this.width / 2) - (buttonWidth / 2);
    GuiButton doneBtn = new GuiButton(0, buttonX, buttonY, buttonWidth, buttonHeight, "Done");
    GuiButton cancelBtn =
        new GuiButton(1, buttonX, buttonY + buttonHeight + 5, buttonWidth, buttonHeight, "Cancel");
    doneBtn.enabled = false;
    this.buttonList.add(doneBtn);
    this.buttonList.add(cancelBtn);
    super.initGui();
    if (task == null) {
      if (executor == null) {
        executor = Executors.newSingleThreadExecutor();
      }
      Alya.getInstance().getLogger().info("[Auth] Starting Microsoft auth flow");
      try {
        task =
            MicrosoftAuth.acquireMSAuthCode(executor)
                .thenComposeAsync(
                    msAuthCode -> {
                      Alya.getInstance()
                          .getLogger()
                          .info("[Auth] Got MS auth code, requesting MS access token");
                      statusMessage = "Getting Microsoft token...";
                      return MicrosoftAuth.acquireMSAccessToken(msAuthCode, executor);
                    },
                    executor)
                .thenComposeAsync(
                    msAccessToken -> {
                      Alya.getInstance()
                          .getLogger()
                          .info(
                              "[Auth] Got MS access token (len={}), requesting Xbox token",
                              msAccessToken.length());
                      statusMessage = "Getting Xbox token...";
                      return MicrosoftAuth.acquireXboxAccessToken(msAccessToken, executor);
                    },
                    executor)
                .thenComposeAsync(
                    xboxAccessToken -> {
                      Alya.getInstance()
                          .getLogger()
                          .info(
                              "[Auth] Got Xbox token (len={}), requesting XSTS token",
                              xboxAccessToken.length());
                      statusMessage = "Getting XSTS token...";
                      return MicrosoftAuth.acquireXboxXstsToken(xboxAccessToken, executor);
                    },
                    executor)
                .thenComposeAsync(
                    xboxXstsData -> {
                      String xstsToken = xboxXstsData.get("Token");
                      String uhs = xboxXstsData.get("uhs");
                      Alya.getInstance()
                          .getLogger()
                          .info(
                              "[Auth] Got XSTS token (len={}), uhs present={}, requesting MC token",
                              xstsToken != null ? xstsToken.length() : 0,
                              uhs != null);
                      statusMessage = "Getting Minecraft token...";
                      return MicrosoftAuth.acquireMCAccessToken(xstsToken, uhs, executor);
                    },
                    executor)
                .thenComposeAsync(
                    mcToken -> {
                      Alya.getInstance()
                          .getLogger()
                          .info(
                              "[Auth] Got MC access token (len={}), fetching profile",
                              mcToken.length());
                      statusMessage = "Fetching profile...";
                      return MicrosoftAuth.login(mcToken, executor);
                    },
                    executor)
                .thenAccept(
                    session -> {
                      Alya.getInstance()
                          .getLogger()
                          .info(
                              "[Auth] Got session: username={}, uuid={}",
                              session.getUsername(),
                              session.getPlayerID());
                      authComplete.set(true);
                      Minecraft mc = Minecraft.getMinecraft();
                      mc.addScheduledTask(
                          () -> {
                            Alya.getInstance()
                                .getLogger()
                                .info("[Auth] Setting session on main thread");
                            SessionManager.setSession(session);
                            mc.displayGuiScreen(new GuiMainMenu());
                          });
                    })
                .exceptionally(
                    e -> {
                      Alya.getInstance()
                          .getLogger()
                          .error("[Auth] Auth chain failed: {}", e.getMessage(), e);
                      statusMessage = "Login failed: " + e.getCause().getMessage();
                      mc.addScheduledTask(
                          () -> {
                            for (Object btn : buttonList) {
                              ((GuiButton) btn).enabled = true;
                            }
                          });
                      return null;
                    });
      } catch (final Exception exception) {
        Alya.getInstance().getLogger().error("[Auth] Exception starting auth task", exception);
      }
    }
  }

  @Override
  protected void actionPerformed(final GuiButton button) {
    if (button.id == 0 && authComplete.get()) {
      mc.displayGuiScreen(new AltManagerGui());
    }
    if (button.id == 1) {
      cleanup();
      SessionChanger.getInstance().setUserOffline("Alya");
      mc.displayGuiScreen(new AltManagerGui());
    }
  }

  private void cleanup() {
    try {
      if (task != null) {
        task.cancel(true);
        task = null;
      }
      if (executor != null) {
        executor.shutdownNow();
        executor = null;
      }
    } catch (final Exception exception) {
      Alya.getInstance().getLogger().error("[Auth] Failed to close executor/task", exception);
    }
  }
}
