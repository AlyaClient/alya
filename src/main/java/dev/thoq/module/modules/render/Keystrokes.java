package dev.thoq.module.modules.render;

import dev.thoq.Alya;
import dev.thoq.event.EventHandler;
import dev.thoq.event.events.Render2DEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.BooleanSetting;
import dev.thoq.module.setting.NumberSetting;
import dev.thoq.util.render.RenderUtility;
import dev.thoq.util.font.AlyaFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public final class Keystrokes extends Module {

    private static final Minecraft MC = Minecraft.getMinecraft();

    private final NumberSetting posX = new NumberSetting("PosX", "X position", 10, 0, 1920, 1);
    private final NumberSetting posY = new NumberSetting("PosY", "Y position", 100, 0, 1080, 1);
    private final BooleanSetting showMouse = new BooleanSetting("ShowMouse", "Show mouse buttons", true);
    private final BooleanSetting showSpace = new BooleanSetting("ShowSpace", "Show spacebar", true);
    private final NumberSetting gridSize = new NumberSetting("GridSize", "Grid snap size", 5, 1, 20, 1);

    private static final int KEY_SIZE = 22;
    private static final int KEY_SPACING = 2;
    private static final int SPACE_WIDTH = KEY_SIZE * 3 + KEY_SPACING * 2;
    private static final int MOUSE_WIDTH = (KEY_SIZE * 3 + KEY_SPACING * 2 - KEY_SPACING) / 2;

    private static final int BG_COLOR = 0xC0181818;
    private static final int BG_PRESSED = 0xC08B5CF6;

    private static final int BORDER_COLOR = 0xFF303030;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int TEXT_PRESSED = 0xFFFFFFFF;

    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public Keystrokes() {
        super("Keystrokes", "Shows pressed keys on screen", Category.RENDER);
        addSetting(posX);
        addSetting(posY);
        addSetting(showMouse);
        addSetting(showSpace);
        addSetting(gridSize);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onRender2D(final Render2DEvent event) {

        if(MC.currentScreen != null && !(MC.currentScreen instanceof GuiChat)) {
            return;

        }

        AlyaFontRenderer fontRenderer = Alya.getInstance().getFontRendererMedium();
        GameSettings settings = MC.gameSettings;

        final int x = posX.getValueAsInt();
        final int y = posY.getValueAsInt();

        final int wX = x + KEY_SIZE + KEY_SPACING;
        drawKey(fontRenderer, wX, y, KEY_SIZE, getKeyName(settings.keyBindForward), isKeyPressed(settings.keyBindForward));

        final int row2Y = y + KEY_SIZE + KEY_SPACING;
        drawKey(fontRenderer, x, row2Y, KEY_SIZE, getKeyName(settings.keyBindLeft), isKeyPressed(settings.keyBindLeft));
        drawKey(fontRenderer, x + KEY_SIZE + KEY_SPACING, row2Y, KEY_SIZE, getKeyName(settings.keyBindBack), isKeyPressed(settings.keyBindBack));
        drawKey(fontRenderer, x + (KEY_SIZE + KEY_SPACING) * 2, row2Y, KEY_SIZE, getKeyName(settings.keyBindRight), isKeyPressed(settings.keyBindRight));

        int currentY = row2Y + KEY_SIZE + KEY_SPACING;

        if(showMouse.isEnabled()) {
            boolean lmbPressed = Mouse.isButtonDown(0);
            boolean rmbPressed = Mouse.isButtonDown(1);

            drawKey(fontRenderer, x, currentY, MOUSE_WIDTH, "LMB", lmbPressed);
            drawKey(fontRenderer, x + MOUSE_WIDTH + KEY_SPACING, currentY, MOUSE_WIDTH, "RMB", rmbPressed);

            currentY += KEY_SIZE + KEY_SPACING;
        }

        if(showSpace.isEnabled()) {
            drawKey(fontRenderer, x, currentY, SPACE_WIDTH, "SPACE", isKeyPressed(settings.keyBindJump));
        }
    }

    private void drawKey(final AlyaFontRenderer fr, final int x, final int y, final int width, final String text, final boolean pressed) {
        final int bgColor = pressed ? BG_PRESSED : BG_COLOR;
        final int textColor = pressed ? TEXT_PRESSED : TEXT_COLOR;

        RenderUtility.drawRect(x, y, width, Keystrokes.KEY_SIZE, bgColor);

        RenderUtility.drawRect(x, y, width, 1, BORDER_COLOR);

        RenderUtility.drawRect(x, y + Keystrokes.KEY_SIZE - 1, width, 1, BORDER_COLOR);

        RenderUtility.drawRect(x, y, 1, Keystrokes.KEY_SIZE, BORDER_COLOR);

        RenderUtility.drawRect(x + width - 1, y, 1, Keystrokes.KEY_SIZE, BORDER_COLOR);

        final float textWidth = fr.getStringWidth(text);
        final float textX = x + (width - textWidth) / 2;
        final float textY = y + (Keystrokes.KEY_SIZE - fr.getFontHeight()) / 2;
        fr.drawString(text, textX, textY, textColor);
    }

    private boolean isKeyPressed(final KeyBinding keyBinding) {
        return Keyboard.isKeyDown(keyBinding.getKeyCode());
    }

    private String getKeyName(final KeyBinding keyBinding) {
        final int keyCode = keyBinding.getKeyCode();
        final String name = Keyboard.getKeyName(keyCode);
        if(name == null || name.isEmpty()) {
            return "?";
        }

        if(name.length() > 3) {
            return name.substring(0, 1);
        }
        return name;
    }

    public boolean mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if(mouseButton == 0 && isMouseOver(mouseX, mouseY)) {
            isDragging = true;
            dragOffsetX = mouseX - posX.getValueAsInt();
            dragOffsetY = mouseY - posY.getValueAsInt();
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    public void mouseReleased(final int mouseX, final int mouseY, final int mouseButton) {
        if(mouseButton == 0) {
            isDragging = false;
        }
    }

    public void mouseDragged(final int mouseX, final int mouseY) {
        if(isDragging) {
            int newX = mouseX - dragOffsetX;
            int newY = mouseY - dragOffsetY;

            final int grid = gridSize.getValueAsInt();
            newX = Math.round((float) newX / grid) * grid;
            newY = Math.round((float) newY / grid) * grid;

            final ScaledResolution scaledResolution = new ScaledResolution(MC);
            newX = Math.max(0, Math.min(newX, scaledResolution.getScaledWidth() - getTotalWidth()));
            newY = Math.max(0, Math.min(newY, scaledResolution.getScaledHeight() - getTotalHeight()));

            posX.setValue((double) newX);
            posY.setValue((double) newY);
        }
    }

    public boolean isMouseOver(final int mouseX, final int mouseY) {
        final int x = posX.getValueAsInt();
        final int y = posY.getValueAsInt();
        final int width = getTotalWidth();
        final int height = getTotalHeight();

        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public int getTotalWidth() {
        return KEY_SIZE * 3 + KEY_SPACING * 2;
    }

    public int getTotalHeight() {
        int height = KEY_SIZE * 2 + KEY_SPACING;

        if(showMouse.isEnabled()) {
            height += KEY_SIZE + KEY_SPACING;
        }
        if(showSpace.isEnabled()) {
            height += KEY_SIZE + KEY_SPACING;
        }
        return height;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public NumberSetting getPosX() {
        return posX;
    }

    public NumberSetting getPosY() {
        return posY;
    }


}
