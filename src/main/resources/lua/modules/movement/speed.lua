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

local moduleTable = alya.modules.register("Speed", "F1 car", "MOVEMENT")
local mode = moduleTable.addModeSetting("Mode", "Speed mode", "BHop", "BHop", "Vanilla", "Verus", "NCP")
local createBHopSpeedMode = loadScript("/lua/modules/movement/speed/bhop.lua")
local createVanillaSpeedMode = loadScript("/lua/modules/movement/speed/vanilla.lua")
local createVerusSpeedMode = loadScript("/lua/modules/movement/speed/verus.lua")
local createNcpSpeedMode = loadScript("/lua/modules/movement/speed/ncp.lua")

createBHopSpeedMode(moduleTable, mode)
createVanillaSpeedMode(moduleTable, mode)
createVerusSpeedMode(moduleTable, mode)
createNcpSpeedMode(moduleTable, mode)

moduleTable.onDisable(function()
    alya.mc.setTimerSpeed(1)
end)
