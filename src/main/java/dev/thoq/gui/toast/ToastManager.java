package dev.thoq.gui.toast;

import dev.thoq.Alya;
import dev.thoq.util.font.AlyaFontRenderer;
import dev.thoq.util.render.RenderUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public final class ToastManager {

    private static final ToastManager INSTANCE = new ToastManager();

    private static final int TOAST_WIDTH = 160;
    private static final int TOAST_HEIGHT = 36;
    private static final int ICON_SIZE = 12;
    private static final int PADDING = 6;
    private static final int GAP = 4;
    private static final int MARGIN_RIGHT = 6;
    private static final int MARGIN_BOTTOM = 6;
    private static final float SLIDE_SPEED = 18f; // pixels per ms

    private static final ResourceLocation ICON_INFO =
            new ResourceLocation("client/icons/contexts/INFO.png");
    private static final ResourceLocation ICON_SUCCESS =
            new ResourceLocation("client/icons/contexts/SUCCESS.png");
    private static final ResourceLocation ICON_WARNING =
            new ResourceLocation("client/icons/contexts/WARNING.png");
    private static final ResourceLocation ICON_DANGER =
            new ResourceLocation("client/icons/contexts/DANGER.png");

    private final List<Toast> toasts = new ArrayList<>();
    private long lastRenderTime = -1;

    private ToastManager() {}

    public static ToastManager getInstance() {
        return INSTANCE;
    }

    public void push(final Toast.Type type, final String title, final String message) {
        push(type, title, message, 4000);
    }

    public void push(final Toast.Type type, final String title, final String message, final long durationMs) {
        synchronized (toasts) {
            toasts.add(new Toast(type, title, message, durationMs));
        }
    }

    public void render() {
        final Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution sr = new ScaledResolution(mc);
        final int sw = sr.getScaledWidth();
        final int sh = sr.getScaledHeight();

        final long now = System.currentTimeMillis();
        final float delta = lastRenderTime < 0 ? 0 : Math.min(now - lastRenderTime, 100);
        lastRenderTime = now;

        final AlyaFontRenderer fontTitle = Alya.getInstance().getFontRendererMedium();
        final AlyaFontRenderer fontMsg  = Alya.getInstance().getFontRendererSmall();

        synchronized (toasts) {
            toasts.removeIf(Toast::isExpired);

            int stackIndex = 0;
            for (int i = toasts.size() - 1; i >= 0; i--) {
                final Toast toast = toasts.get(i);

                float offset = toast.getSlideOffset();
                if (offset > 0f) {
                    offset = Math.max(0f, offset - SLIDE_SPEED * delta);
                    toast.setSlideOffset(offset);
                }

                final float alpha = toast.getFadeAlpha();
                if (alpha <= 0f) continue;

                final float baseX = sw - MARGIN_RIGHT - TOAST_WIDTH;
                final float baseY = sh - MARGIN_BOTTOM - TOAST_HEIGHT - stackIndex * (TOAST_HEIGHT + GAP);
                final float x = baseX + offset;

                final int bg = colorWithAlpha(0x1A1A1A, (int)(alpha * 0xCC));
                RenderUtility.drawRoundedRect(x, baseY, TOAST_WIDTH, TOAST_HEIGHT, 3f, bg);

                final int accentColor = getAccentColor(toast.getType(), alpha);
                RenderUtility.drawRect(x, baseY + 3, 2, TOAST_HEIGHT - 6, accentColor);

                final ResourceLocation icon = getIcon(toast.getType());
                GlStateManager.color(1f, 1f, 1f, alpha);
                RenderUtility.drawImage(icon,
                        x + PADDING + 2,
                        baseY + (TOAST_HEIGHT - ICON_SIZE) / 2f,
                        ICON_SIZE, ICON_SIZE);
                GlStateManager.color(1f, 1f, 1f, 1f);

                final float textX = x + PADDING + 2 + ICON_SIZE + 4;
                fontTitle.drawString(toast.getTitle(), textX, baseY + 7,
                        colorWithAlpha(0xFFFFFF, (int)(alpha * 255)));

                fontMsg.drawString(toast.getMessage(), textX, baseY + 20,
                        colorWithAlpha(0xAAAAAA, (int)(alpha * 255)));

                stackIndex++;
            }
        }
    }

    private static ResourceLocation getIcon(final Toast.Type type) {
        return switch (type) {
            case SUCCESS -> ICON_SUCCESS;
            case WARNING -> ICON_WARNING;
            case DANGER  -> ICON_DANGER;
            default      -> ICON_INFO;
        };
    }

    private static int getAccentColor(final Toast.Type type, final float alpha) {
        final int a = (int)(alpha * 255);
        return switch (type) {
            case SUCCESS -> (a << 24) | 0x55FF55;
            case WARNING -> (a << 24) | 0xFFAA00;
            case DANGER  -> (a << 24) | 0xFF4444;
            default      -> (a << 24) | 0x5599FF;
        };
    }

    private static int colorWithAlpha(final int rgb, final int alpha) {
        return (Math.clamp(alpha, 0, 255) << 24) | (rgb & 0x00FFFFFF);
    }


}
