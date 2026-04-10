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

local moduleTable = alya.modules.register("AutoClicker", "Clicks for you.", "COMBAT")
local mode        = moduleTable.addModeSetting("Mode", "", "Normal", "Normal", "Drag")
local cps         = moduleTable.addNumberSetting("CPS", "", 9, 0, 20, 0.01)

cps.setRangeEnabled(true)
cps.setSecondValue(11)
local dragTime  = moduleTable.addNumberSetting("Duration (MS)", "", 1200, 100, 2000, 100)
local dragDelay = moduleTable.addNumberSetting("Delay (MS)", "", 300, 100, 1000, 100)
cps.setVisibility(function() return mode.is("Normal") end)
dragTime.setVisibility(function() return mode.is("Drag") end)
dragDelay.setVisibility(function() return mode.is("Drag") end)
local createNormalMode = loadScript("/lua/modules/combat/autoclicker/normal.lua")
local createDragMode   = loadScript("/lua/modules/combat/autoclicker/drag.lua")
createNormalMode(moduleTable, mode, cps)
createDragMode(moduleTable, mode, cps, dragTime, dragDelay)
