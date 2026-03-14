package dev.thoq.module.modules.render;

import dev.thoq.Alya;
import dev.thoq.event.EventHandler;
import dev.thoq.event.events.Render2DEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.BooleanSetting;
import dev.thoq.util.font.AlyaFontRenderer;
import net.minecraft.client.Minecraft;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class HUDModule extends Module {

    private final BooleanSetting showFPS = new BooleanSetting("Show FPS", "Display FPS counter", true);
    private final BooleanSetting showBPS = new BooleanSetting("Show BPS", "Display blocks per second", false);
    private final BooleanSetting showTime = new BooleanSetting("Show Time", "Display current time", false);
    private final BooleanSetting smoothChat = new BooleanSetting("Smooth Chat", "Animate messages in chat to smoothly enter", true);
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private double lastX, lastY, lastZ;
    private double blocksPerSecond = 0;
    private long lastBPSUpdate = 0;

    public HUDModule() {
        super("HUD", "Displays client information on screen", Category.RENDER);
        initializeSettings(showFPS, showBPS, showTime, smoothChat);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(MC.thePlayer != null) {
            lastX = MC.thePlayer.posX;
            lastY = MC.thePlayer.posY;
            lastZ = MC.thePlayer.posZ;
        }
    }

    @EventHandler
    public void onRender2D(final Render2DEvent event) {
        final AlyaFontRenderer fontRenderer = Alya.getInstance().getFontRendererMedium();
        float x = 4;
        final float y = 4;
        final int purpleColor = 0xFF8B5CF6;
        final int whiteColor = 0xFFFFFFFF;

        fontRenderer.drawStringWithShadow("A", x, y, purpleColor);
        x += fontRenderer.getStringWidth("A");
        fontRenderer.drawStringWithShadow("lya", x, y, whiteColor);
        x += fontRenderer.getStringWidth("lya");

        final StringBuilder info = new StringBuilder();

        if(showFPS.isEnabled()) {
            info.append(" [").append(Minecraft.getDebugFPS()).append(" FPS]");
        }
        if(showBPS.isEnabled()) {
            updateBPS();
            info.append(" [").append(String.format("%.1f", blocksPerSecond)).append(" BPS]");
        }
        if(showTime.isEnabled()) {
            info.append(" [").append(timeFormat.format(new Date())).append("]");
        }

        fontRenderer.drawStringWithShadow(info.toString(), x, y, whiteColor);
    }

    private void updateBPS() {
        if(MC.thePlayer == null) { blocksPerSecond = 0; return; }
        final long currentTime = System.currentTimeMillis();
        if(currentTime - lastBPSUpdate >= 50) {
            final double deltaX = MC.thePlayer.posX - lastX;
            final double deltaY = MC.thePlayer.posY - lastY;
            final double deltaZ = MC.thePlayer.posZ - lastZ;
            final double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            final double timeDelta = (currentTime - lastBPSUpdate) / 1000.0;
            if(timeDelta > 0) blocksPerSecond = blocksPerSecond * 0.8 + (distance / timeDelta) * 0.2;
            lastX = MC.thePlayer.posX;
            lastY = MC.thePlayer.posY;
            lastZ = MC.thePlayer.posZ;
            lastBPSUpdate = currentTime;
        }
    }

    public boolean getSmoothChatEnabled() {
        return smoothChat.isEnabled();
    }
}
