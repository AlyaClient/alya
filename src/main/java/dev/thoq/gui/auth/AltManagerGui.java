package dev.thoq.gui.auth;

import dev.thoq.Alya;
import dev.thoq.gui.GUIPasswordField;
import dev.thoq.util.auth.SessionManager;
import dev.thoq.util.font.AlyaFontRenderer;
import dev.thoq.util.render.RenderUtility;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("CallToPrintStackTrace")
public final class AltManagerGui extends GuiScreen {

    private static final AlyaFontRenderer FONT = Alya.getInstance().getFontRendererBold();
    private static final AlyaFontRenderer FONT_SM = Alya.getInstance().getFontRendererSmall();
    private static final AlyaFontRenderer FONT_MD = Alya.getInstance().getFontRendererMedium();

    private static final ResourceLocation ICON_CRACKED =
            new ResourceLocation("client/icons/login/cracked.png");
    private static final ResourceLocation ICON_MICROSOFT =
            new ResourceLocation("client/icons/login/microsoft.png");
    private static final ResourceLocation ICON_FAVORITE =
            new ResourceLocation("client/icons/login/favorite.png");

    private static final Map<String, ResourceLocation> skinCache = new ConcurrentHashMap<>();
    private static final Set<String> skinLoading = Collections.synchronizedSet(new HashSet<>());
    private static final ExecutorService skinExecutor = Executors.newFixedThreadPool(2);
    private static final int PANEL_WIDTH = 260;
    private static final int ENTRY_HEIGHT = 28;
    private static final int ICON_SIZE = 12;
    private static final int HEAD_SIZE = 20;
    private int scrollOffset = 0;
    private int selectedIndex = -1;
    private int panelX, panelY, panelH;
    private long lastClickTime = 0;
    private int lastClickedIndex = -1;
    private boolean showAddCracked = false;
    private boolean showSetPassword = false;
    private boolean showConfirmLock = false;
    private GuiTextField inputField;
    private String promptMessage = "";

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        panelX = (this.width - PANEL_WIDTH) / 2;
        panelY = 40;
        panelH = this.height - 90;

        final AltStorage storage = AltStorage.getInstance();
        if(storage.hasPassword() && !storage.isUnlocked()) {
            mc.displayGuiScreen(new GUIUnlockScreen());
            return;
        }

        try {
            storage.load();
        } catch(final Exception ignored) {
        }

        buildButtons();
    }

    private void buildButtons() {
        this.buttonList.clear();
        final int btnY = this.height - 42;
        final int btnW = 56;
        final int gap = 4;
        final int totalW = btnW * 4 + gap * 3;
        final int startX = (this.width - totalW) / 2;

        this.buttonList.add(new GuiButton(0, startX, btnY, btnW, 20, "Cracked"));
        this.buttonList.add(new GuiButton(1, startX + (btnW + gap), btnY, btnW, 20, "Microsoft"));
        this.buttonList.add(new GuiButton(2, startX + (btnW + gap) * 2, btnY, btnW, 20, "Remove"));
        this.buttonList.add(new GuiButton(3, startX + (btnW + gap) * 3, btnY, btnW, 20, "Login"));

        final int btnY2 = btnY - 24;
        final int totalW2 = btnW * 3 + gap * 2;
        final int startX2 = (this.width - totalW2) / 2;
        this.buttonList.add(new GuiButton(4, startX2, btnY2, btnW, 20, "Favorite"));
        this.buttonList.add(new GuiButton(5, startX2 + (btnW + gap), btnY2, btnW, 20, "§" + (isPasswordSet() ? "cLock" : "aSet Lock")));
        this.buttonList.add(new GuiButton(6, startX2 + (btnW + gap) * 2, btnY2, btnW, 20, "Back"));
    }

    private boolean isPasswordSet() {
        return AltStorage.getInstance().hasPassword();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        if(showSetPassword) {
            drawSetPasswordPrompt(mouseX, mouseY, partialTicks);
            return;
        }
        if(showConfirmLock) {
            drawConfirmLockPrompt(mouseX, mouseY, partialTicks);
            return;
        }
        if(showAddCracked) {
            drawAddCrackedPrompt(mouseX, mouseY, partialTicks);
            return;
        }

        FONT.drawString("Alt Manager", panelX, panelY - 18, 0xFFFFFFFF);
        final String sessionInfo = "Session: " + mc.getSession().getUsername();
        FONT_SM.drawString(sessionInfo,
                panelX + PANEL_WIDTH - FONT_SM.getStringWidth(sessionInfo),
                panelY - 14, 0xFFAAAAAA);

        RenderUtility.drawRect(panelX - 2, panelY - 2, PANEL_WIDTH + 4, panelH + 4, 0x000000);
        RenderUtility.drawRect(panelX, panelY, PANEL_WIDTH, panelH, 0x000000);

        final List<AltEntry> alts = getSortedAlts();
        final int visibleCount = panelH / ENTRY_HEIGHT;
        scrollOffset = Math.clamp(scrollOffset, 0, Math.max(0, alts.size() - visibleCount));

        for(int i = 0; i < visibleCount && (i + scrollOffset) < alts.size(); i++) {
            final int index = i + scrollOffset;
            final AltEntry alt = alts.get(index);
            final int entryY = panelY + i * ENTRY_HEIGHT;

            final boolean selected = index == selectedIndex;
            final boolean hovered = mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH
                    && mouseY >= entryY && mouseY < entryY + ENTRY_HEIGHT;

            if(selected) {
                RenderUtility.drawRect(panelX, entryY, PANEL_WIDTH, ENTRY_HEIGHT, 0xFF2A2A5E);
            } else if(hovered) {
                RenderUtility.drawRect(panelX, entryY, PANEL_WIDTH, ENTRY_HEIGHT, 0xFF1E1E4A);
            }

            int cx = panelX + 4;

            final ResourceLocation headTex = getSkinHead(alt);
            if(headTex != null) {
                GlStateManager.color(1, 1, 1, 1);
                RenderUtility.drawImage(headTex, cx, entryY + (ENTRY_HEIGHT - HEAD_SIZE) / 2f, HEAD_SIZE, HEAD_SIZE);
            } else {
                RenderUtility.drawRect(cx, entryY + (ENTRY_HEIGHT - HEAD_SIZE) / 2f, HEAD_SIZE, HEAD_SIZE, 0xFF555555);
            }
            cx += HEAD_SIZE + 4;

            final ResourceLocation typeIcon = alt.getType() == AltEntry.Type.MICROSOFT ? ICON_MICROSOFT : ICON_CRACKED;
            GlStateManager.color(1, 1, 1, 1);
            RenderUtility.drawImage(typeIcon, cx, entryY + (ENTRY_HEIGHT - ICON_SIZE) / 2f, ICON_SIZE, ICON_SIZE);
            cx += ICON_SIZE + 4;

            final String displayName = alt.getName();
            FONT_MD.drawString(displayName, cx, entryY + 5, 0xFFFFFFFF);

            final String typeLabel = alt.getType() == AltEntry.Type.MICROSOFT ? "§bMicrosoft" : "§7Cracked";
            FONT_SM.drawString(typeLabel, cx, entryY + 17, 0xFFAAAAAA);

            if(alt.isFavorite()) {
                GlStateManager.color(1, 1, 1, 1);
                RenderUtility.drawImage(ICON_FAVORITE,
                        panelX + PANEL_WIDTH - ICON_SIZE - 6,
                        entryY + (ENTRY_HEIGHT - ICON_SIZE) / 2f,
                        ICON_SIZE, ICON_SIZE);
            }

            RenderUtility.drawRect(panelX, entryY + ENTRY_HEIGHT - 1, PANEL_WIDTH, 1, 0xFF2A2A4A);
        }

        if(alts.size() > visibleCount) {
            final float ratio = (float) visibleCount / alts.size();
            final float barH = Math.max(20, panelH * ratio);
            final float barY = panelY + ((float) scrollOffset / alts.size()) * panelH;
            RenderUtility.drawRect(panelX + PANEL_WIDTH - 3, barY, 3, barH, 0xFF4A4A8A);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawAddCrackedPrompt(final int mouseX, final int mouseY, final float pt) {
        FONT_MD.drawString("Enter username for cracked account:",
                width / 2f - FONT_MD.getStringWidth("Enter username for cracked account:") / 2, height / 2f - 40, 0xFFFFFFFF);
        inputField.drawTextBox();
        super.drawScreen(mouseX, mouseY, pt);
    }

    private void drawSetPasswordPrompt(final int mouseX, final int mouseY, final float pt) {
        final String msg = "Set encryption password (empty to remove):";
        FONT_MD.drawString(msg,
                width / 2f - FONT_MD.getStringWidth(msg) / 2, height / 2f - 40, 0xFFFFFFFF);
        inputField.drawTextBox();
        super.drawScreen(mouseX, mouseY, pt);
    }

    private void drawConfirmLockPrompt(final int mouseX, final int mouseY, final float pt) {
        FONT_MD.drawString(promptMessage,
                width / 2f - FONT_MD.getStringWidth(promptMessage) / 2, height / 2f - 40, 0xFFFFFFFF);
        inputField.drawTextBox();
        super.drawScreen(mouseX, mouseY, pt);
    }

    @Override
    protected void actionPerformed(final GuiButton button) {
        final AltStorage storage = AltStorage.getInstance();
        final List<AltEntry> alts = getSortedAlts();

        switch(button.id) {
            case 0:
                mc.displayGuiScreen(new LoginGui());
                break;

            case 1:
                mc.displayGuiScreen(new WebLoginLauncher() {
                });
                break;

            case 2:
                if(selectedIndex >= 0 && selectedIndex < alts.size()) {
                    storage.removeAlt(alts.get(selectedIndex));
                    selectedIndex = -1;
                }
                break;

            case 3:
                if(selectedIndex >= 0 && selectedIndex < alts.size()) {
                    AltEntry alt = alts.get(selectedIndex);
                    loginWithAlt(alt);
                }
                break;

            case 4:
                if(selectedIndex >= 0 && selectedIndex < alts.size()) {
                    AltEntry alt = alts.get(selectedIndex);
                    alt.setFavorite(!alt.isFavorite());
                    storage.save();
                }
                break;

            case 5:
                if(isPasswordSet()) {
                    showConfirmLock = true;
                    promptMessage = "Enter password to confirm lock:";
                } else {
                    showSetPassword = true;
                }
                inputField = new GUIPasswordField(200, fontRendererObj, width / 2 - 75, height / 2 - 10, 150, 20);
                inputField.setFocused(true);
                this.buttonList.clear();
                this.buttonList.add(new GuiButton(10, width / 2 - 75, height / 2 + 16, 150, 20, "Submit"));
                break;

            case 6:
                mc.displayGuiScreen(new GuiMainMenu());
                break;

            case 10:
                submitPrompt();
                break;
        }
    }

    private void submitPrompt() {
        if(showConfirmLock) {
            final String entered = inputField.getText();
            if(AltStorage.getInstance().unlock(entered)) {
                AltStorage.getInstance().save();
                showConfirmLock = false;
                inputField = null;
                mc.displayGuiScreen(new GuiMainMenu());
            } else {
                promptMessage = "§cWrong password! Try again:";
            }
        } else if(showSetPassword) {
            final String password = inputField.getText();
            if(password.isEmpty()) {
                AltStorage.getInstance().clearPassword();
            } else {
                AltStorage.getInstance().setPassword(password);
            }
            showSetPassword = false;
            inputField = null;
            buildButtons();
        }
    }

    private void loginWithAlt(final AltEntry alt) {
        if(alt.getType() == AltEntry.Type.CRACKED) {
            SessionChanger.getInstance().setUserOffline(alt.getName());
        } else {
            final Session session = new Session(alt.getName(), alt.getUuid(), alt.getAccessToken(),
                    Session.Type.MOJANG.toString());
            SessionManager.setSession(session);
        }
    }

    @Override
    protected void keyTyped(final char character, final int key) throws IOException {
        if(showAddCracked) {
            if(key == Keyboard.KEY_RETURN && !inputField.getText().isEmpty()) {
                AltStorage.getInstance().addAlt(AltEntry.cracked(inputField.getText()));
                showAddCracked = false;
                inputField = null;
                buildButtons();
                return;
            }
            if(key == Keyboard.KEY_ESCAPE) {
                showAddCracked = false;
                inputField = null;
                buildButtons();
                return;
            }
            inputField.textboxKeyTyped(character, key);
            return;
        }
        if(showConfirmLock || showSetPassword) {
            if(key == Keyboard.KEY_RETURN) {
                submitPrompt();
                return;
            }
            if(key == Keyboard.KEY_ESCAPE) {
                showConfirmLock = false;
                showSetPassword = false;
                inputField = null;
                buildButtons();
                return;
            }
            inputField.textboxKeyTyped(character, key);
            return;
        }
        if(key == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(new GuiMainMenu());
            return;
        }
        super.keyTyped(character, key);
    }

    @Override
    public void mouseClicked(final int x, final int y, final int button) {
        try {
            super.mouseClicked(x, y, button);
        } catch(final IOException ioException) {
            ioException.printStackTrace();
        }
        if(inputField != null) {
            inputField.mouseClicked(x, y, button);
        }
        if(showAddCracked || showSetPassword || showConfirmLock) return;

        if(x >= panelX && x <= panelX + PANEL_WIDTH && y >= panelY && y < panelY + panelH) {
            final int clickedIndex = (y - panelY) / ENTRY_HEIGHT + scrollOffset;
            final List<AltEntry> alts = getSortedAlts();
            if(clickedIndex >= 0 && clickedIndex < alts.size()) {
                final long now = System.currentTimeMillis();
                if(clickedIndex == lastClickedIndex && (now - lastClickTime) < 500) {
                    loginWithAlt(alts.get(clickedIndex));
                    lastClickTime = 0;
                    lastClickedIndex = -1;
                } else {
                    selectedIndex = clickedIndex;
                    lastClickTime = now;
                    lastClickedIndex = clickedIndex;
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        final int scroll = Mouse.getEventDWheel();
        if(scroll != 0) {
            scrollOffset -= Integer.signum(scroll);
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

    private List<AltEntry> getSortedAlts() {
        final List<AltEntry> sorted = new ArrayList<>(AltStorage.getInstance().getAlts());
        sorted.sort((a, b) -> Boolean.compare(b.isFavorite(), a.isFavorite()));
        return sorted;
    }

    private ResourceLocation getSkinHead(final AltEntry alt) {
        final String key = alt.getUuid();
        if(key == null || key.isEmpty() || alt.getType() == AltEntry.Type.CRACKED) {
            return null;
        }
        if(skinCache.containsKey(key)) {
            return skinCache.get(key);
        }
        if(!skinLoading.contains(key)) {
            skinLoading.add(key);
            skinExecutor.submit(() -> {
                try {
                    final String url = alt.getSkinUrl();
                    if(url == null) {
                        skinLoading.remove(key);
                        return;
                    }
                    final java.net.HttpURLConnection conn =
                            (java.net.HttpURLConnection) new URI(url).toURL().openConnection();
                    conn.setRequestProperty("User-Agent", "Alya-Client/1.0");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    final int responseCode = conn.getResponseCode();
                    if(responseCode != 200) {
                        Alya.getInstance().getLogger().warn(
                                "[AltManager] Skin API returned {} for {}", responseCode, key);
                        skinLoading.remove(key);
                        return;
                    }

                    final BufferedImage img = ImageIO.read(conn.getInputStream());
                    conn.disconnect();

                    if(img != null) {
                        mc.addScheduledTask(() -> {
                            DynamicTexture tex = new DynamicTexture(img);
                            ResourceLocation loc = mc.getTextureManager()
                                    .getDynamicTextureLocation("althead_" + key, tex);
                            skinCache.put(key, loc);
                            skinLoading.remove(key);
                        });
                    } else {
                        skinLoading.remove(key);
                    }
                } catch(final Exception exception) {
                    skinLoading.remove(key);
                    Alya.getInstance().getLogger().warn("[AltManager] Failed to load skin for {}: {}",
                            key, exception.getMessage());
                }
            });
        }
        return null;
    }


}
