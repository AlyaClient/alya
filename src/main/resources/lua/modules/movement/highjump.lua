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

local moduleTable = alya.modules.register("HighJump", "Jump high", "MOVEMENT")
local amount = moduleTable.addNumberSetting("Speed", "How fast to jump", 1.0, 0.1, 10.0, 0.1)
alya.events.on("playermove", function(event)
    if not moduleTable.isEnabled() then return end
    if not alya.mc.isOnGround() then return end
    if not alya.mc.isJumpPressed() then return end
    alya.mc.jump()
    alya.mc.setJumpTicks(10)
    alya.mc.setMotionY(amount.getValue())
end)
