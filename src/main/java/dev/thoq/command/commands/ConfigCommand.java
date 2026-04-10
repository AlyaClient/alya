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

public final class ConfigCommand extends Command {
    public ConfigCommand() {
        super("config", "Save or load configurations", "cfg", "c");
    }

    @Override
    public void execute(final String[] args) {
        if(args.length < 1) {
            ChatUtil.sendInfo("Usage: .config save <name>");
            ChatUtil.sendInfo("Usage: .config load <name>");
            ChatUtil.sendInfo("Usage: .config list");
            return;
        }
        final String action = args[0].toLowerCase();
        if(action.equals("save")) {
            if(args.length < 2) {
                ChatUtil.sendError("Usage: .config save <name>");
                return;
            }
            Alya.getInstance().getConfigManager().save(args[1]);
            ChatUtil.sendSuccess("Saved config: " + args[1]);
        } else if(action.equals("load")) {
            if(args.length < 2) {
                ChatUtil.sendError("Usage: .config load <name>");
                return;
            }
            if(!Alya.getInstance().getConfigManager().configExists(args[1])) {
                ChatUtil.sendError("Config not found: " + args[1]);
                return;
            }
            Alya.getInstance().getConfigManager().load(args[1]);
            ChatUtil.sendSuccess("Loaded config: " + args[1]);
        } else if(action.equals("list")) {
            final String[] names = Alya.getInstance().getConfigManager().getConfigNames();
            if(names.length == 0) {
                ChatUtil.sendInfo("No configs found.");
                return;
            }
            ChatUtil.sendInfo("Available configs:");
            for(final String name : names) {
                ChatUtil.sendRaw("  - " + name);
            }
        } else {
            ChatUtil.sendError("Unknown action: " + action);
            ChatUtil.sendInfo("Available actions: save, load, list");
        }
    }
}
