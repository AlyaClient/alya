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

local moduleTable = alya.modules.register("LongJump", "Makes you jump farther/higher", "MOVEMENT")
local mode      = moduleTable.addModeSetting("Mode", "", "Mineland", "Mineland", "Verus", "Fireball", "Vulcan", "NCP", "Grim")
local verusType = moduleTable.addModeSetting("Type", "", "Fast", "Fast", "High")
verusType.setVisibility(function() return mode.is("Verus") end)
local createMineland = loadScript("/lua/modules/movement/longjump/mineland.lua")
local createVerus    = loadScript("/lua/modules/movement/longjump/verus.lua")
local createFireball = loadScript("/lua/modules/movement/longjump/fireball.lua")
local createVulcan   = loadScript("/lua/modules/movement/longjump/vulcan.lua")
local createNcp      = loadScript("/lua/modules/movement/longjump/ncp.lua")
local createGrim     = loadScript("/lua/modules/movement/longjump/grim.lua")
moduleTable.onDisable(function()
    alya.mc.setTimerSpeed(1)
    alya.combat.setPlayerPitch(0)
end)
createMineland(moduleTable, mode)
createVerus(moduleTable, mode, verusType)
createFireball(moduleTable, mode)
createVulcan(moduleTable, mode)
createNcp(moduleTable, mode)
createGrim(moduleTable, mode)
