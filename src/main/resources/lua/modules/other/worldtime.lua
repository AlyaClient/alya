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

local moduleTable = alya.modules.register("WorldTime", "Sets the time of day client side", "OTHER")
local timeOfDay = moduleTable.addNumberSetting("Time", "Time of day", 18.0, 0.0, 24.0, 1)
local originalTime = 0

moduleTable.onEnable(function()
    if alya.mc.isPlayerNull() then return end
    originalTime = alya.mc.getWorldTime()
end)

moduleTable.onDisable(function()
    if alya.mc.isPlayerNull() then return end
    alya.mc.setWorldTime(originalTime)
end)

alya.events.on("timeupdate", function(event)
    if not moduleTable.isEnabled() then return end
    local mcTime = timeOfDay.getValueAsInt() * 1000

    event.setTime(mcTime)
end)
