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

local function createMotionVelocityMode(moduleTable, modeSettings, mode, hVel, vVel)
    alya.events.on("packetreceive", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is(mode) then return end
        local cls = event.getPacketClass()
        if cls == "S12PacketEntityVelocity" then
            if event.getEntityId() ~= alya.mc.getEntityId() then return end
            local h = hVel.getValue()
            local v = vVel.getValue()
            if h == 0 and v == 0 then
                event.cancel()
                return
            end
            event.setMotionX(math.floor(event.getMotionX() * (h / 100)))
            event.setMotionY(math.floor(event.getMotionY() * (v / 100)))
            event.setMotionZ(math.floor(event.getMotionZ() * (h / 100)))
        elseif cls == "S27PacketExplosion" then
            local h = hVel.getValue()
            local v = vVel.getValue()
            if h == 0 and v == 0 then
                event.cancel()
                return
            end
            event.setMotionX(event.getMotionX() * (h / 100))
            event.setMotionY(event.getMotionY() * (v / 100))
            event.setMotionZ(event.getMotionZ() * (h / 100))
        end
    end)
end
return createMotionVelocityMode
