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

local function createJumpResetVelocityMode(moduleTable, modeSetting, mode, chance, miss)
    local needsJump = false

    alya.events.on("packetreceive", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSetting.is(mode) then return end
        local cls = event.getPacketClass()
        if cls ~= "S12PacketEntityVelocity" then return end
        if event.getEntityId() ~= alya.mc.getEntityId() then return end
        if not alya.mc.isOnGround() then return end

        if math.random() * 100 < chance.getValue() then
            needsJump = true
        end
    end)

    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSetting.is(mode) then return end
        if not event.isPre() then return end
        if not needsJump then return end
        needsJump = false

        if alya.mc.isOnGround() then
            if math.random() * 100 < miss.getValue() then
                alya.mc.jump()
            else
                alya.mc.jump()
            end
        end
    end)
end
return createJumpResetVelocityMode
