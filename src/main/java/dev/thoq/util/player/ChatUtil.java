package dev.thoq.util.player;

import dev.thoq.Alya;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@SuppressWarnings("unused")
public final class ChatUtil {
  private static final Minecraft MC = Minecraft.getMinecraft();
  private static final String PREFIX =
      EnumChatFormatting.DARK_PURPLE
          + "["
          + EnumChatFormatting.LIGHT_PURPLE
          + Alya.getName()
          + EnumChatFormatting.DARK_PURPLE
          + "] "
          + EnumChatFormatting.RESET;

  public static void sendInfo(final String message) {
    if (MC.thePlayer != null) {
      MC.thePlayer.addChatMessage(
          new ChatComponentText(PREFIX + EnumChatFormatting.GRAY + message));
    }
  }

  public static void sendSuccess(final String message) {
    if (MC.thePlayer != null) {
      MC.thePlayer.addChatMessage(
          new ChatComponentText(PREFIX + EnumChatFormatting.GREEN + message));
    }
  }

  public static void sendError(final String message) {
    if (MC.thePlayer != null) {
      MC.thePlayer.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.RED + message));
    }
  }

  public static void sendWarning(final String message) {
    if (MC.thePlayer != null) {
      MC.thePlayer.addChatMessage(
          new ChatComponentText(PREFIX + EnumChatFormatting.YELLOW + message));
    }
  }

  public static void sendRaw(final String message) {
    if (MC.thePlayer != null) {
      MC.thePlayer.addChatMessage(new ChatComponentText(message));
    }
  }
}
