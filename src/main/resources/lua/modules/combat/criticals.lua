--[[
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
--]]

local moduleTable = alya.modules.register("Criticals", "Crit attacks", "COMBAT")
local mode        = moduleTable.addModeSetting("Mode", "", "Packet", "Packet")
local delay       = moduleTable.addNumberSetting("Delay", "", 1, 0, 20, 1)
delay.setVisibility(function() return mode.is("Packet") end)
local createPacketMode = loadScript("/lua/modules/combat/criticals/packet.lua")
createPacketMode(moduleTable, mode, delay)
