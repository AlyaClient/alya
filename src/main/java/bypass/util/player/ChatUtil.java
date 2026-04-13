/*
 * Copyright (c) 2026 Alya Client.
 *
 * Alya Client is a free, open-source Minecraft hacked client.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package bypass.util.player;

import bypass.Alya;
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
        if(MC.thePlayer != null) {
            MC.thePlayer.addChatMessage(
                    new ChatComponentText(PREFIX + EnumChatFormatting.GRAY + message));
        }
    }

    public static void sendSuccess(final String message) {
        if(MC.thePlayer != null) {
            MC.thePlayer.addChatMessage(
                    new ChatComponentText(PREFIX + EnumChatFormatting.GREEN + message));
        }
    }

    public static void sendError(final String message) {
        if(MC.thePlayer != null) {
            MC.thePlayer.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.RED + message));
        }
    }

    public static void sendWarning(final String message) {
        if(MC.thePlayer != null) {
            MC.thePlayer.addChatMessage(
                    new ChatComponentText(PREFIX + EnumChatFormatting.YELLOW + message));
        }
    }

    public static void sendRaw(final String message) {
        if(MC.thePlayer != null) {
            MC.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }
}
