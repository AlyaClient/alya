package dev.thoq.util.font;
import dev.thoq.Alya;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class AlyaFontRenderer {
    private static final float SCALE_FACTOR = 2.0f;
    private static final float CHAR_WIDTH_PAD = 2.05f * SCALE_FACTOR;
    private static final int CHAR_HEIGHT_PAD = (int) SCALE_FACTOR;
    private static final float FONT_HEIGHT_PAD = 2.0f * SCALE_FACTOR;
    private static final int DRAW_X_PAD = Math.max(1, (int) (SCALE_FACTOR / 2.0f));
    private static final float DRAW_Y_OFFSET = 2.0f;
    private Font font;
    private final FontData fontData = new FontData();
    private float fontHeight;
    private static class FontData {
        private final CharData[] chars = new CharData[256];
        private int textureId = -1;
        private final int width = 2048;
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
                    "/assets/minecraft/Alya/Fonts/SF-UI-Display-Regular.ttf");
            if(inputStream != null) {
                final Font baseFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
                this.font = baseFont.deriveFont(Font.PLAIN, size * SCALE_FACTOR);
                inputStream.close();
            } else {
                this.font = new Font("SF", Font.PLAIN, (int) (size * SCALE_FACTOR));
            }
            setupTexture();
        } catch(final Exception exception) {
            Alya.getInstance().getLogger().error("Failed to load default font", exception);
            this.font = new Font("SF", Font.PLAIN, (int) (size * SCALE_FACTOR));
            setupTexture();
        }
    }
    private void setupTexture() {
        BufferedImage scratch = new BufferedImage(fontData.width, 512, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2d = (Graphics2D) scratch.getGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2d.setFont(font);
        handleSprites(fontData, font, graphics2d, false);
        graphics2d.dispose();
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
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            for(final CharData data : fontData.chars) {
                final char character = (char) index;
                final int baseline = data.y + data.height - fontMetrics.getDescent() - 2;
                graphics.drawString(String.valueOf(character), data.x + DRAW_X_PAD, baseline);
                index++;
            }
            graphics.dispose();
            
            final int[] pixels = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
            final ByteBuffer buffer = ByteBuffer.allocateDirect(pixels.length * 4).order(ByteOrder.nativeOrder());
            for(final int pixel : pixels) {
                buffer.put((byte) ((pixel >> 16) & 0xFF)); 
                buffer.put((byte) ((pixel >> 8) & 0xFF));  
                buffer.put((byte) (pixel & 0xFF));          
                buffer.put((byte) ((pixel >> 24) & 0xFF)); 
            }
            buffer.flip();
            fontData.textureId = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontData.textureId);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(),
                    0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        } else {
            while(index < fontData.chars.length) {
                final char character = (char) index;
                final CharData charData = new CharData();
                final Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(character), graphics2D);
                charData.width = dimensions.getBounds().width + CHAR_WIDTH_PAD;
                charData.height = dimensions.getBounds().height + CHAR_HEIGHT_PAD;
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
    private static final int[] COLOR_CODES = {
        0x000000, 0x0000AA, 0x00AA00, 0x00AAAA,
        0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
        0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
        0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
    };
    private void renderString(final String text, final float x, final float y, final int color) {
        if(text == null || text.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0 / SCALE_FACTOR, 1.0 / SCALE_FACTOR, 1.0 / SCALE_FACTOR);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        if(alpha == 0) {
            alpha = 1.0f;
        }
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
        GlStateManager.bindTexture(fontData.textureId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        float xOffset = x * SCALE_FACTOR;
        for(int i = 0; i < text.length(); i++) {
            final char character = text.charAt(i);
            if(character == '\u00a7' && i + 1 < text.length()) {
                int colorIndex = "0123456789abcdefABCDEF".indexOf(text.charAt(i + 1));
                if(colorIndex >= 0) {
                    if(colorIndex >= 16) colorIndex -= 6;
                    int c = COLOR_CODES[colorIndex];
                    red = ((c >> 16) & 0xFF) / 255.0f;
                    green = ((c >> 8) & 0xFF) / 255.0f;
                    blue = (c & 0xFF) / 255.0f;
                    GlStateManager.color(red, green, blue, alpha);
                } else if(text.charAt(i + 1) == 'r' || text.charAt(i + 1) == 'R') {
                    red = ((color >> 16) & 0xFF) / 255.0f;
                    green = ((color >> 8) & 0xFF) / 255.0f;
                    blue = (color & 0xFF) / 255.0f;
                    GlStateManager.color(red, green, blue, alpha);
                }
                i++;
                continue;
            }
            if(character < fontData.chars.length) {
                drawLetter(xOffset, (y - DRAW_Y_OFFSET) * SCALE_FACTOR, character);
                xOffset += roundToHalf(fontData.chars[character].width - CHAR_WIDTH_PAD);
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
    }
    private void drawLetter(final float x, final float y, final char character) {
        final CharData charData = fontData.chars[character];
        GL11.glBegin(GL11.GL_TRIANGLES);
        drawQuad(x, y, charData.width, charData.height, charData.x, charData.y, fontData.width, fontData.height);
        GL11.glEnd();
    }
    private void drawQuad(final float x, final float y, final float width, final float height, final float srcX, final float srcY, final float srcWidth, final float srcHeight) {
        final float renderSRCX = srcX / srcWidth;
        final float renderSRCY = srcY / srcHeight;
        final float renderSRCWidth = width / srcWidth;
        final float renderSRCHeight = height / srcHeight;
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2f(x + width, y);
        GL11.glTexCoord2f(renderSRCX, renderSRCY);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2f(x + width, y);
    }
    private static float roundToHalf(final double value) {
        return (float) (Math.round(value * 2) / 2.0);
    }
    public float getStringWidth(final String text) {
        if(text == null || text.isEmpty()) return 0;
        float width = 0;
        for(int i = 0; i < text.length(); i++) {
            final char character = text.charAt(i);
            if(character == '\u00a7' && i + 1 < text.length()) {
                i++;
                continue;
            }
            if(character < fontData.chars.length)
                width += roundToHalf(fontData.chars[character].width - CHAR_WIDTH_PAD);
        }
        return width / SCALE_FACTOR;
    }
    public float getFontHeight() {
        return (fontHeight - FONT_HEIGHT_PAD) / SCALE_FACTOR;
    }
    public int getHeight() {
        return (int) ((fontHeight - FONT_HEIGHT_PAD) / SCALE_FACTOR);
    }
}
