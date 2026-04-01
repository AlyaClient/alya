package net.minecraft.client.gui;

import dev.thoq.gui.UIConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unused")
public class GuiButton extends Gui {
  protected static final ResourceLocation buttonTextures =
      new ResourceLocation("textures/gui/widgets.png");

  private static final int BACKGROUND_COLOR = 0x6D181818;
  private static final int BACKGROUND_HOVER = 0x5A252525;
  private static final int BACKGROUND_DISABLED = 0x40101010;
  private static final int BORDER_COLOR = 0xFF303030;
  private static final int BORDER_HOVER = UIConstants.ACCENT_COLOR;
  private static final int TEXT_COLOR = 0xFFFFFFFF;
  private static final int TEXT_DISABLED = 0xFF666666;

  private float hoverAnimation = 0.0F;
  private static final float ANIMATION_SPEED = 0.15F;

  /** Button width in pixels */
  protected int width;

  /** Button height in pixels */
  protected int height;

  /** The x position of this control. */
  public int xPosition;

  /** The y position of this control. */
  public int yPosition;

  /** The string displayed on this control. */
  public String displayString;

  public int id;

  /** True if this control is enabled, false to disable. */
  public boolean enabled;

  /** Hides the button completely if false. */
  public boolean visible;

  protected boolean hovered;

  public GuiButton(int buttonId, int x, int y, String buttonText) {
    this(buttonId, x, y, 200, 20, buttonText);
  }

  public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
    this.width = 200;
    this.height = 20;
    this.enabled = true;
    this.visible = true;
    this.id = buttonId;
    this.xPosition = x;
    this.yPosition = y;
    this.width = widthIn;
    this.height = heightIn;
    this.displayString = buttonText;
  }

  /**
   * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if
   * it IS hovering over this button.
   */
  protected int getHoverState(boolean mouseOver) {
    int i = 1;

    if (!this.enabled) {
      i = 0;
    } else if (mouseOver) {
      i = 2;
    }

    return i;
  }

  private int interpolateColor(int color1, int color2, float factor) {
    int a1 = (color1 >> 24) & 0xFF;
    int r1 = (color1 >> 16) & 0xFF;
    int g1 = (color1 >> 8) & 0xFF;
    int b1 = color1 & 0xFF;

    int a2 = (color2 >> 24) & 0xFF;
    int r2 = (color2 >> 16) & 0xFF;
    int g2 = (color2 >> 8) & 0xFF;
    int b2 = color2 & 0xFF;

    int a = (int) (a1 + (a2 - a1) * factor);
    int r = (int) (r1 + (r2 - r1) * factor);
    int g = (int) (g1 + (g2 - g1) * factor);
    int b = (int) (b1 + (b2 - b1) * factor);

    return (a << 24) | (r << 16) | (g << 8) | b;
  }

  /** Draws this button to the screen. */
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    if (this.visible) {
      this.hovered =
          mouseX >= this.xPosition
              && mouseY >= this.yPosition
              && mouseX < this.xPosition + this.width
              && mouseY < this.yPosition + this.height;

      if (this.hovered && this.enabled) {
        hoverAnimation = Math.min(1.0F, hoverAnimation + ANIMATION_SPEED);
      } else {
        hoverAnimation = Math.max(0.0F, hoverAnimation - ANIMATION_SPEED);
      }

      int bgColor;
      int borderColor;
      int textColor;

      if (!this.enabled) {
        bgColor = BACKGROUND_DISABLED;
        borderColor = BORDER_COLOR;
        textColor = TEXT_DISABLED;
      } else {
        bgColor = interpolateColor(BACKGROUND_COLOR, BACKGROUND_HOVER, hoverAnimation);
        borderColor = interpolateColor(BORDER_COLOR, BORDER_HOVER, hoverAnimation);
        textColor = TEXT_COLOR;
      }

      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

      drawRect(
          this.xPosition,
          this.yPosition,
          this.xPosition + this.width,
          this.yPosition + this.height,
          bgColor);

      drawRect(
          this.xPosition,
          this.yPosition,
          this.xPosition + this.width,
          this.yPosition + 1,
          borderColor);
      drawRect(
          this.xPosition,
          this.yPosition + this.height - 1,
          this.xPosition + this.width,
          this.yPosition + this.height,
          borderColor);
      drawRect(
          this.xPosition,
          this.yPosition,
          this.xPosition + 1,
          this.yPosition + this.height,
          borderColor);
      drawRect(
          this.xPosition + this.width - 1,
          this.yPosition,
          this.xPosition + this.width,
          this.yPosition + this.height,
          borderColor);

      this.mouseDragged(mc, mouseX, mouseY);
      this.drawButtonText(mc, textColor);

      GlStateManager.disableBlend();
    }
  }

  /** Draws the button text. Override this method to use a custom font renderer. */
  protected void drawButtonText(Minecraft mc, int textColor) {
    FontRenderer fr = mc.fontRendererObj;
    String text = this.displayString;
    int maxWidth = this.width - 6;
    if (fr.getStringWidth(text) > maxWidth) {
      while (!text.isEmpty() && fr.getStringWidth(text + "...") > maxWidth) {
        text = text.substring(0, text.length() - 1);
      }
      text = text + "...";
    }
    drawCenteredString(
        fr,
        text,
        this.xPosition + this.width / 2,
        this.yPosition + (this.height - 8) / 2,
        textColor);
  }

  /**
   * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
   */
  protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {}

  /**
   * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent
   * e).
   */
  public void mouseReleased(int mouseX, int mouseY) {}

  /**
   * Returns true if the mouse has been pressed on this control. Equivalent of
   * MouseListener.mousePressed(MouseEvent e).
   */
  public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
    return this.enabled
        && this.visible
        && mouseX >= this.xPosition
        && mouseY >= this.yPosition
        && mouseX < this.xPosition + this.width
        && mouseY < this.yPosition + this.height;
  }

  /** Whether the mouse cursor is currently over the button. */
  public boolean isMouseOver() {
    return this.hovered;
  }

  public void drawButtonForegroundLayer(int mouseX, int mouseY) {}

  public void playPressSound(SoundHandler soundHandlerIn) {
    soundHandlerIn.playSound(
        PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
  }

  public int getButtonWidth() {
    return this.width;
  }

  public void setWidth(int width) {
    this.width = width;
  }
}
