package dev.thoq.util.font;

import dev.thoq.Alya;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public final class AlyaFontRenderer {

    private static final float SCALE_FACTOR = 2.0f;

    private Font font;
    private final FontData fontData = new FontData();
    private float fontHeight;

    private static class FontData {
        private final CharData[] chars = new CharData[256];
        private DynamicTexture texture;
        private final int width = 1024;
        private int height;
    }

    private static class CharData {
        private float width;
        private int x, y, height;
    }

    public AlyaFontRenderer(final String resourcePath, final float size) {
        try {
            final InputStream inputStream = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation(resourcePath)).getInputStream();

            final Font baseFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            this.font = baseFont.deriveFont(Font.PLAIN, size * SCALE_FACTOR);
            inputStream.close();

            setupTexture();
        } catch(final Exception exception) {
            Alya.getInstance().getLogger().error("Failed to load font: {}", resourcePath, exception);
            this.font = new Font("SansSerif", Font.PLAIN, (int) (size * SCALE_FACTOR));
            setupTexture();
        }
    }

    public AlyaFontRenderer(final float size) {
        try {
            final InputStream inputStream = AlyaFontRenderer.class.getResourceAsStream(
                    "/assets/minecraft/Alya/Fonts/OpenSans-Medium.ttf");

            if(inputStream != null) {
                final Font baseFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
                this.font = baseFont.deriveFont(Font.PLAIN, size * SCALE_FACTOR);
                inputStream.close();
            } else {
                this.font = new Font("SansSerif", Font.PLAIN, (int) (size * SCALE_FACTOR));
            }

            setupTexture();
        } catch(final Exception exception) {
            Alya.getInstance().getLogger().error("Failed to load default font", exception);
            this.font = new Font("SansSerif", Font.PLAIN, (int) (size * SCALE_FACTOR));
            setupTexture();
        }
    }

    private void setupTexture() {
        final BufferedImage fakeImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics2D = (Graphics2D) fakeImage.getGraphics();

        graphics2D.setFont(font);

        handleSprites(fontData, font, graphics2D, false);
    }

    private void handleSprites(final FontData fontData, final Font currentFont, final Graphics2D graphics2D, final boolean drawString) {
        int x = 0, y = 0, height = 0, index = 0;

        final FontMetrics fontMetrics = graphics2D.getFontMetrics();

        if(drawString) {
            final BufferedImage image = new BufferedImage(fontData.width, fontData.height, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D graphics = (Graphics2D) image.getGraphics();

            graphics.setFont(currentFont);
            graphics.setColor(new Color(0, 0, 0, 0));
            graphics.fillRect(0, 0, fontData.width, fontData.height);
            graphics.setColor(Color.WHITE);

            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for(final CharData data : fontData.chars) {
                final char character = (char) index;
                final int baseline = data.y + data.height - fontMetrics.getDescent() - 1;
                graphics.drawString(String.valueOf(character), data.x + 2, baseline);
                index++;
            }

            fontData.texture = new DynamicTexture(image);
        } else {
            while(index < fontData.chars.length) {
                final char character = (char) index;
                final CharData charData = new CharData();
                final Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(character), graphics2D);

                charData.width = dimensions.getBounds().width + 8.2f;
                charData.height = dimensions.getBounds().height + 4;

                if(x + charData.width >= fontData.width) {
                    x = 0;
                    y += height;
                }

                if(charData.height > height)
                    height = charData.height;

                if(charData.height > fontHeight)
                    fontHeight = charData.height;

                charData.x = x;
                charData.y = y;

                fontData.chars[index] = charData;
                fontData.height = y + height;

                x += (int) charData.width;
                index++;
            }

            handleSprites(fontData, currentFont, graphics2D, true);
        }
    }

    public float drawString(final String text, final float x, final float y, final int color) {
        renderString(text, x, y, color);
        return getStringWidth(text);
    }

    public float drawStringWithShadow(final String text, final float x, final float y, final int color) {
        renderString(text, x + .5f, y + .5f, (color & 0xFCFCFC) >> 2 | color & 0xFF000000);
        renderString(text, x, y, color);
        return getStringWidth(text);
    }

    private void renderString(final String text, final float x, final float y, final int color) {
        if(text == null || text.isEmpty()) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(.5, .5, .5);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        final float red = ((color >> 16) & 0xFF) / 255.0f;
        final float green = ((color >> 8) & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;

        if(alpha == 0) {
            alpha = 1.0f;
        }

        GlStateManager.color(red, green, blue, alpha);
        GlStateManager.enableTexture2D();

        GlStateManager.bindTexture(fontData.texture.getGlTextureId());

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        float xOffset = x * 2;
        for(int i = 0; i < text.length(); i++) {
            final char character = text.charAt(i);
            if(character < fontData.chars.length) {
                drawLetter(xOffset, (y - 2) * 2, character);
                xOffset += roundToHalf(fontData.chars[character].width - 8.2f);
            }
        }

        GlStateManager.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GlStateManager.popMatrix();

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
    }

    private void drawLetter(final float x, final float y, final char character) {
        final CharData charData = fontData.chars[character];
        GlStateManager.glBegin(GL11.GL_TRIANGLES);
        drawQuad(x, y, charData.width, charData.height, charData.x, charData.y, fontData.width, fontData.height);
        GlStateManager.glEnd();
    }

    private void drawQuad(final float x, final float y, final float width, final float height, final float srcX, final float srcY, final float srcWidth, final float srcHeight) {
        final float renderSRCX = srcX / srcWidth;
        final float renderSRCY = srcY / srcHeight;
        final float renderSRCWidth = width / srcWidth;
        final float renderSRCHeight = height / srcHeight;

        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d(x + width, y);
        GL11.glTexCoord2f(renderSRCX, renderSRCY);
        GL11.glVertex2d(x, y);
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x + width, y + height);
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d(x + width, y);
    }

    private static float roundToHalf(final double value) {
        return (float) (Math.round(value * 2) / 2.0);
    }

    public float getStringWidth(final String text) {
        if(text == null || text.isEmpty()) {
            return 0;
        }

        float width = 0;
        for(int i = 0; i < text.length(); i++) {
            final char character = text.charAt(i);
            if(character < fontData.chars.length)
                width += roundToHalf(fontData.chars[character].width - 8.2f);
        }
        return width / 2;
    }

    public float getFontHeight() {
        return (fontHeight - 8) / 2;
    }

    public int getHeight() {
        return (int) ((fontHeight - 8) / 2);
    }


}
