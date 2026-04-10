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

local moduleTable = alya.modules.register("NoRotate", "Prevents server from setting your rotation", "PLAYER")

local pitch = moduleTable.addBooleanSetting("Pitch", "Lock pitch", true)
local yaw = moduleTable.addBooleanSetting("Yaw", "Lock yaw", true)

local savedYaw = 0
local savedPitch = 0

moduleTable.onEnable(function()
    savedYaw = alya.mc.getYaw()
    savedPitch = alya.mc.getPitch()
end)

moduleTable.onDisable(function()
end)

alya.events.on("packetsend", function(event)
    if not moduleTable.isEnabled() then return end
end)

alya.events.on("packetreceive", function(event)
    if not moduleTable.isEnabled() then return end
    local packetClass = event.getPacketClass()
    
    if packetClass:find("S08") or packetClass:find("PlayerPosLook") then
        if yaw.isEnabled() then
            alya.mc.setCameraYaw(savedYaw)
            alya.combat.setClientRotation(savedYaw, savedPitch)
        end
        if pitch.isEnabled() then
            alya.mc.setCameraPitch(savedPitch)
        end
    end
end)

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    
    if yaw.isEnabled() then
        savedYaw = alya.mc.getYaw()
    end
    if pitch.isEnabled() then
        savedPitch = alya.mc.getPitch()
    end
end)