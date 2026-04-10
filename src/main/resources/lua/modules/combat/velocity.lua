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

local moduleTable = alya.modules.register("Velocity", "nO KnoCkbaCK", "COMBAT")
local mode  = moduleTable.addModeSetting("Mode", "Velocity Mode", "Motion", "Motion", "Jump Reset")
local hVel  = moduleTable.addNumberSetting("Horizontal", "Horizontal velocity %", 0, 0, 100, 1)
local vVel  = moduleTable.addNumberSetting("Vertical", "Vertical velocity %", 0, 0, 100, 1)
local jumpChance = moduleTable.addNumberSetting("Jump Chance", "Chance to jump reset on hit", 100, 0, 100, 1)
local jumpMiss = moduleTable.addNumberSetting("Miss Chance", "Chance to jump without resetting motion", 0, 0, 100, 1)
hVel.setVisibility(function() return mode.is("Motion") end)
vVel.setVisibility(function() return mode.is("Motion") end)
jumpChance.setVisibility(function() return mode.is("Jump Reset") end)
jumpMiss.setVisibility(function() return mode.is("Jump Reset") end)

local createMotionVelocityMode = loadScript("/lua/modules/combat/velocity/motion.lua")
createMotionVelocityMode(moduleTable, mode, "Motion", hVel, vVel)

local createJumpResetVelocityMode = loadScript("/lua/modules/combat/velocity/jumpreset.lua")
createJumpResetVelocityMode(moduleTable, mode, "Jump Reset", jumpChance, jumpMiss)
