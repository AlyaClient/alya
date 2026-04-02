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

@SuppressWarnings("unused")
public final class ToastManager {

    private static final ToastManager INSTANCE = new ToastManager();

    private static final int TOAST_WIDTH = 170;
    private static final int TOAST_HEIGHT = 38;
    private static final int ICON_SIZE = 14;
    private static final int PADDING = 7;
    private static final int GAP = 4;
    private static final int MARGIN_RIGHT = 6;
    private static final int MARGIN_BOTTOM = 6;
    private static final int PROGRESS_BAR_H = 2;

    private static final ResourceLocation ICON_INFO = new ResourceLocation("client/icons/contexts/INFO.png");
    private static final ResourceLocation ICON_SUCCESS = new ResourceLocation("client/icons/contexts/SUCCESS.png");
    private static final ResourceLocation ICON_WARNING = new ResourceLocation("client/icons/contexts/WARNING.png");
    private static final ResourceLocation ICON_DANGER = new ResourceLocation("client/icons/contexts/DANGER.png");

    private final List<Toast> toasts = new ArrayList<>();

    private ToastManager() {
    }

    public static ToastManager getInstance() {
        return INSTANCE;
    }

    public void push(final Toast.Type type, final String title, final String message) {
        push(type, title, message, 4000);
    }

    public void push(final Toast.Type type, final String title, final String message, final long durationMs) {
        synchronized(toasts) {
            toasts.add(new Toast(type, title, message, durationMs));
        }
    }

    public void info(final String title, final String message) {
        push(Toast.Type.INFO, title, message);
    }

    public void success(final String title, final String message) {
        push(Toast.Type.SUCCESS, title, message);
    }

    public void warning(final String title, final String message) {
        push(Toast.Type.WARNING, title, message);
    }

    public void danger(final String title, final String message) {
        push(Toast.Type.DANGER, title, message);
    }

    public void render() {
        final Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution sr = new ScaledResolution(mc);
        final int sw = sr.getScaledWidth();
        final int sh = sr.getScaledHeight();

        final AlyaFontRenderer fontTitle = Alya.getInstance().getFontRendererMedium();
        final AlyaFontRenderer fontMsg = Alya.getInstance().getFontRendererSmall();

        synchronized(toasts) {
            toasts.removeIf(Toast::isExpired);

            int stackIndex = 0;
            for(int i = toasts.size() - 1; i >= 0; i--) {
                final Toast toast = toasts.get(i);
                final float slide = toast.getSlide();

                final float targetX = sw - MARGIN_RIGHT - TOAST_WIDTH;
                final float targetY = sh - MARGIN_BOTTOM - TOAST_HEIGHT - stackIndex * (TOAST_HEIGHT + GAP);

                final float offScreenY = sh + 4;
                final float y = offScreenY - slide * (offScreenY - targetY);

                RenderUtility.drawRect(targetX, y, TOAST_WIDTH, TOAST_HEIGHT, 0x55777777);

                GlStateManager.color(1f, 1f, 1f, 1f);
                RenderUtility.drawImage(getIcon(toast.getType()),
                        targetX + PADDING,
                        y + (TOAST_HEIGHT - ICON_SIZE) / 2f,
                        ICON_SIZE, ICON_SIZE);

                final float textX = targetX + PADDING + ICON_SIZE + PADDING - 2;
                fontTitle.drawString(toast.getTitle(), textX, y + 7, 0xFFFFFFFF);
                fontMsg.drawString(toast.getMessage(), textX, y + 20, 0xFFAAAAAA);

                final float barY = y + TOAST_HEIGHT - PROGRESS_BAR_H;
                RenderUtility.drawRect(targetX, barY, TOAST_WIDTH, PROGRESS_BAR_H, getAccentColor(toast.getType()));

                final float progress = toast.getProgress();
                RenderUtility.drawRect(targetX, barY, TOAST_WIDTH * progress, PROGRESS_BAR_H, getProgressColor(toast.getType()));

                stackIndex++;
            }
        }
    }

    private static ResourceLocation getIcon(final Toast.Type type) {
        return switch(type) {
            case SUCCESS -> ICON_SUCCESS;
            case WARNING -> ICON_WARNING;
            case DANGER -> ICON_DANGER;
            default -> ICON_INFO;
        };
    }

    private static int getAccentColor(final Toast.Type type) {
        return switch(type) {
            case SUCCESS -> 0xFF1A331A;
            case WARNING -> 0xFF332200;
            case DANGER -> 0xFF330000;
            default -> 0xFF001133;
        };
    }

    private static int getProgressColor(final Toast.Type type) {
        return switch(type) {
            case SUCCESS -> 0xFF55CC77;
            case WARNING -> 0xFFFFAA33;
            case DANGER -> 0xFFFF4444;
            default -> 0xFF5599FF;
        };
    }


}
