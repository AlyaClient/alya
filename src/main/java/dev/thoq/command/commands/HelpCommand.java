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

package dev.thoq.command.commands;

import dev.thoq.Alya;
import dev.thoq.command.Command;
import dev.thoq.util.player.ChatUtil;

public final class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "Displays a list of available commands", "h", "?");
    }

    @Override
    public void execute(final String[] args) {
        final String prefix = Alya.getInstance().getCommandManager().prefix();
        ChatUtil.sendRaw("\u00a7e=== Available Commands ===");
        for(final Command command : Alya.getInstance().getCommandManager().getCommands()) {
            final String[] aliases = command.getAliases();
            final String aliasText =
                    aliases.length > 0 ? " \u00a7f(" + String.join(", ", aliases) + ")" : "";
            ChatUtil.sendRaw(
                    "\u00a7b"
                            + prefix
                            + command.getName()
                            + aliasText
                            + "\u00a7f - "
                            + command.getDescription());
        }
    }
}
