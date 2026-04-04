package dev.thoq.module.modules.render;

import dev.thoq.Alya;
import dev.thoq.event.EventHandler;
import dev.thoq.event.events.Render2DEvent;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.BooleanSetting;
import dev.thoq.util.font.AlyaFontRenderer;
import dev.thoq.util.render.RenderUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class HUDModule extends Module {

    private final BooleanSetting showFPS =
            new BooleanSetting("Show FPS", "Display FPS counter", true);
    private final BooleanSetting showBPS =
            new BooleanSetting("Show BPS", "Display blocks per second", false);
    private final BooleanSetting showTime =
            new BooleanSetting("Show Time", "Display current time", false);
    private final BooleanSetting smoothChat =
            new BooleanSetting("Smooth Chat", "Animate messages in chat to smoothly enter", true);
    private final BooleanSetting showMenuImage =
            new BooleanSetting("Femboy", "Display the main menu image in the bottom right", false);
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private double lastX, lastY, lastZ;
    private double blocksPerSecond = 0;
    private long lastBPSUpdate = 0;

    public HUDModule() {
        super("HUD", "Displays client information on screen", Category.VISUAL);
        initializeSettings(showFPS, showBPS, showTime, smoothChat, showMenuImage);
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
        final int purpleColor = 0xFF8B5CF6;
        final int whiteColor = 0xFFFFFFFF;
        float x = 4;
        final float y = 4;
        final String name = Alya.getInstance().getClientName();
        final String firstChar = name.substring(0, 1);
        final String rest = name.substring(1);
        fontRenderer.drawStringWithShadow(firstChar, x, y, purpleColor);
        x += fontRenderer.getStringWidth(firstChar);
        fontRenderer.drawStringWithShadow(rest, x, y, whiteColor);
        final List<String> infoLines = new ArrayList<>();
        if(showTime.isEnabled()) {
            infoLines.add(timeFormat.format(new Date()));
        }
        if(showBPS.isEnabled()) {
            updateBPS();
            infoLines.add("BPS: " + String.format("%.1f", blocksPerSecond));
        }
        if(showFPS.isEnabled()) {
            infoLines.add("FPS: " + Minecraft.getDebugFPS());
        }
        if(!infoLines.isEmpty()) {
            final int screenHeight = event.scaledResolution().getScaledHeight();
            final float lineHeight = fontRenderer.getFontHeight() + 2;
            float infoY = screenHeight - 4 - lineHeight;
            for(int i = infoLines.size() - 1; i >= 0; i--) {
                fontRenderer.drawStringWithShadow(infoLines.get(i), 4, infoY, whiteColor);
                infoY -= lineHeight;
            }
        }
        if(showMenuImage.isEnabled()) {
            final ResourceLocation image = GuiMainMenu.getRandomImage();
            if(image != null) {
                final int screenWidth = event.scaledResolution().getScaledWidth();
                final int screenHeight = event.scaledResolution().getScaledHeight();
                final int maxSize = 100;
                MC.getTextureManager().bindTexture(image);
                final int texW = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
                final int texH = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
                if(texW > 0 && texH > 0) {
                    final float scale = Math.min((float) maxSize / texW, (float) maxSize / texH);
                    final int drawW = (int) (texW * scale);
                    final int drawH = (int) (texH * scale);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderUtility.drawImage(image, screenWidth - drawW, screenHeight - drawH, drawW, drawH);
                }
            }
        }
    }

    private void updateBPS() {
        if (MC.thePlayer == null) {
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
            if(timeDelta > 0) blocksPerSecond = blocksPerSecond * 0.8 + (distance / timeDelta) * 0.2;
            lastX = MC.thePlayer.posX;
            lastY = MC.thePlayer.posY;
            lastZ = MC.thePlayer.posZ;
            lastBPSUpdate = currentTime;
        }
    }

    @SuppressWarnings("unused")
    public boolean getSmoothChatEnabled() {
        return smoothChat.isEnabled();
    }


}
