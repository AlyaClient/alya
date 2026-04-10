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

local moduleTable = alya.modules.register("Blink", "Holds packets until disabled", "PLAYER")
local mode        = moduleTable.addModeSetting("Mode", "", "Manual", "Manual", "AutoRelease")
local delay       = moduleTable.addNumberSetting("Delay", "", 2000, 100, 10000, 100)
delay.setVisibility(function() return mode.is("AutoRelease") end)
local timer = alya.timer.create()

local function stop()
    alya.mc.holdPackets(false)
    alya.mc.flushPackets()
end

moduleTable.onEnable(function()
    timer.reset()
end)

moduleTable.onDisable(function()
    stop()
end)

alya.events.on("packetreceive", function(event)
    if not moduleTable.isEnabled() then return end
    if alya.mc.isPlayerNull() or alya.mc.isWorldNull() then
        stop()
        return
    end

    if not mode.is("AutoRelease") then return end
    if timer.hasElapsedAndReset(delay.getValueAsInt(), true) then
        alya.mc.holdPackets(false)
        alya.mc.flushPackets()
        alya.mc.holdPackets(true)
    else
        alya.mc.holdPackets(true)
    end
end)
