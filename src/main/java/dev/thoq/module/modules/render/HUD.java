package dev.thoq.module.modules.render;

import dev.thoq.Alya;
import dev.thoq.event.EventHandler;
import dev.thoq.event.events.Render2DEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.BooleanSetting;
import dev.thoq.util.AlyaFontRenderer;
import net.minecraft.client.Minecraft;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class HUD extends Module {

    private final BooleanSetting showFPS = new BooleanSetting("ShowFPS", "Display FPS counter", true);
    private final BooleanSetting showBPS = new BooleanSetting("ShowBPS", "Display blocks per second", false);
    private final BooleanSetting showTime = new BooleanSetting("ShowTime", "Display current time", false);
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private double lastX, lastY, lastZ;
    private double blocksPerSecond = 0;
    private long lastBPSUpdate = 0;

    public HUD() {
        super("HUD", "Displays client information on screen", Category.RENDER);
        addSetting(showFPS);
        addSetting(showBPS);
        addSetting(showTime);
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
        AlyaFontRenderer fontRenderer = Alya.getInstance().getFontRenderer();

        float x = 4;
        final float y = 4;

        final String firstChar = "A";
        final String restChars = "lya";

        final int purpleColor = 0xFF8B5CF6;
        final int whiteColor = 0xFFFFFFFF;

        fontRenderer.drawStringWithShadow(firstChar, x, y, purpleColor);
        x += fontRenderer.getStringWidth(firstChar);

        fontRenderer.drawStringWithShadow(restChars, x, y, whiteColor);
        x += fontRenderer.getStringWidth(restChars);

        final StringBuilder info = new StringBuilder();

        if(showFPS.isEnabled()) {
            final int fps = Minecraft.getDebugFPS();
            info.append(" [").append(fps).append(" FPS]");
        }

        if(showBPS.isEnabled()) {
            updateBPS();
            info.append(" [").append(String.format("%.1f", blocksPerSecond)).append(" BPS]");
        }

        if(showTime.isEnabled()) {
            final String time = timeFormat.format(new Date());
            info.append(" [").append(time).append("]");
        }

        if(info.length() > 0) {
            fontRenderer.drawStringWithShadow(info.toString(), x, y, whiteColor);
        }
    }

    private void updateBPS() {
        if(MC.thePlayer == null) {
            blocksPerSecond = 0;
            return;
        }

        final long currentTime = System.currentTimeMillis();

        if(currentTime - lastBPSUpdate >= 50) {

            final double deltaX = MC.thePlayer.posX - lastX;
            final double deltaY = MC.thePlayer.posY - lastY;
            final double deltaZ = MC.thePlayer.posZ - lastZ;

            final double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            final double timeDelta = (currentTime - lastBPSUpdate) / 1000.0;

            if(timeDelta > 0) {

                final double newBPS = distance / timeDelta;
                blocksPerSecond = blocksPerSecond * 0.8 + newBPS * 0.2;
            }

            lastX = MC.thePlayer.posX;
            lastY = MC.thePlayer.posY;
            lastZ = MC.thePlayer.posZ;
            lastBPSUpdate = currentTime;
        }
    }


}
