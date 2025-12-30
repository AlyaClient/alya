package dev.thoq.util;

import dev.thoq.Alya;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public final class AlyaFontRenderer {

    private Font font;
    private final Map<Character, CharData> charDataMap = new HashMap<>();
    private int textureId = -1;
    private final int textureWidth = 2048;
    private final int textureHeight = 2048;
    private float fontHeight;
    private static final float SCALE_FACTOR = 4.0f;
    private static final int CHAR_START = 32;
    private static final int CHAR_END = 256;

    private static class CharData {
        public int x, y;
        public int width, height;
        public float advance;
    }

    public AlyaFontRenderer(final String resourcePath, final float size) {
        try {
            final InputStream inputStream = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation(resourcePath)).getInputStream();

            final Font baseFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);

            this.font = baseFont.deriveFont(size * SCALE_FACTOR);
            inputStream.close();

            createTexture();
        } catch(final Exception exception) {
            Alya.getInstance().getLogger().error("Failed to load font: {}", resourcePath, exception);
            this.font = new Font("OpenSans-Medium", Font.PLAIN, (int) (size * SCALE_FACTOR));
            createTexture();
        }
    }

    public AlyaFontRenderer(final float size) {
        try {
            final InputStream inputStream = AlyaFontRenderer.class.getResourceAsStream(
                    "/assets/minecraft/Alya/Fonts/OpenSans-Medium.ttf");

            if(inputStream != null) {
                final Font baseFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);

                this.font = baseFont.deriveFont(size * SCALE_FACTOR);
                inputStream.close();
            } else {
                this.font = new Font("SansSerif", Font.PLAIN, (int) (size * SCALE_FACTOR));
            }

            createTexture();
        } catch(final Exception exception) {
            Alya.getInstance().getLogger().error("Failed to load default font", exception);
            this.font = new Font("OpenSans-Medium", Font.PLAIN, (int) (size * SCALE_FACTOR));
            createTexture();
        }
    }

    private void createTexture() {
        final BufferedImage image = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setFont(font);
        FontMetrics fontMetrics = graphics.getFontMetrics();

        this.fontHeight = fontMetrics.getHeight() / SCALE_FACTOR;

        int x = 2;
        int y = 2;
        int rowHeight = 0;

        for(int i = CHAR_START; i < CHAR_END; i++) {
            final char c = (char) i;
            final CharData charData = new CharData();

            int charWidth = fontMetrics.charWidth(c);
            int charHeight = fontMetrics.getHeight();

            if(charWidth <= 0) {
                charWidth = 8;
            }

            if(x + charWidth + 4 > textureWidth) {
                x = 2;
                y += rowHeight + 2;
                rowHeight = 0;
            }

            charData.x = x;
            charData.y = y;
            charData.width = charWidth;
            charData.height = charHeight;
            charData.advance = charWidth;

            rowHeight = Math.max(rowHeight, charHeight);

            graphics.setColor(Color.WHITE);
            graphics.drawString(String.valueOf(c), x, y + fontMetrics.getAscent());

            charDataMap.put(c, charData);

            x += charWidth + 4;
        }

        graphics.dispose();

        final int[] pixels = new int[textureWidth * textureHeight];
        image.getRGB(0, 0, textureWidth, textureHeight, pixels, 0, textureWidth);

        final ByteBuffer buffer = ByteBuffer.allocateDirect(textureWidth * textureHeight * 4);
        buffer.order(ByteOrder.nativeOrder());

        for(final int pixel : pixels) {
            buffer.put((byte) ((pixel >> 16) & 0xFF));

            buffer.put((byte) ((pixel >> 8) & 0xFF));

            buffer.put((byte) (pixel & 0xFF));

            buffer.put((byte) ((pixel >> 24) & 0xFF));

        }

        buffer.flip();

        textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, textureWidth, textureHeight, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
    }

    public float drawString(final String text, final float x, final float y, final int color) {
        return drawString(text, x, y, color, false);
    }

    public float drawStringWithShadow(final String text, final float x, final float y, final int color) {
        drawString(text, x + 1, y + 1, 0x55000000, false);
        return drawString(text, x, y, color, false);
    }

    @SuppressWarnings({"unused", "SameParameterValue"})
    private float drawString(final String text, float x, final float y, final int color, final boolean shadow) {
        if(text == null || text.isEmpty()) {
            return 0;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        final float red = ((color >> 16) & 0xFF) / 255.0f;
        final float green = ((color >> 8) & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;

        if(alpha == 0) {
            alpha = 1.0f;
        }

        GlStateManager.color(red, green, blue, alpha);

        final float startX = x;

        GL11.glBegin(GL11.GL_QUADS);

        for(final char c : text.toCharArray()) {
            CharData charData = charDataMap.get(c);
            if(charData == null) {
                charData = charDataMap.get('?');
                if(charData == null) {
                    x += 8 / SCALE_FACTOR;
                    continue;
                }
            }

            final float u = (float) charData.x / textureWidth;
            final float v = (float) charData.y / textureHeight;
            final float u2 = (float) (charData.x + charData.width) / textureWidth;
            final float v2 = (float) (charData.y + charData.height) / textureHeight;

            final float w = charData.width / SCALE_FACTOR;
            final float h = charData.height / SCALE_FACTOR;

            GL11.glTexCoord2f(u, v);
            GL11.glVertex2f(x, y);

            GL11.glTexCoord2f(u, v2);
            GL11.glVertex2f(x, y + h);

            GL11.glTexCoord2f(u2, v2);
            GL11.glVertex2f(x + w, y + h);

            GL11.glTexCoord2f(u2, v);
            GL11.glVertex2f(x + w, y);

            x += charData.advance / SCALE_FACTOR;
        }

        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        return x - startX;
    }

    public float getStringWidth(final String text) {
        if(text == null || text.isEmpty()) {
            return 0;
        }

        float width = 0;
        for(final char c : text.toCharArray()) {
            final CharData charData = charDataMap.get(c);
            if(charData != null) {
                width += charData.advance;
            } else {
                width += 8;
            }
        }

        return width / SCALE_FACTOR;
    }

    public float getFontHeight() {
        return fontHeight;
    }

    public int getHeight() {
        return (int) fontHeight;
    }


}
