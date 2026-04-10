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

local moduleTable = alya.modules.register("Timer", "Change the tick speed client side", "PLAYER")
local timerSpeed = moduleTable.addNumberSetting("Speed", "Multiplier", 1.0, 0.1, 10.0, 0.1)
alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if event.isPre and event.isPre() then
        alya.mc.setTimerSpeed(timerSpeed.getValueAsFloat())
    end
end)
moduleTable.onDisable(function()
    alya.mc.setTimerSpeed(1.0)
end)
