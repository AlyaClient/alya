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

local function createPacketMode(moduleTable, mode, delay)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not mode.is("Packet") then return end
        if not event.isPre() then return end
        if not alya.mc.isOnGround() then return end
        if alya.combat.getHurtTime() <= delay.getValueAsInt() then return end
        for _, offset in ipairs({ 0.006253453, 0.002253453, 0.001253453 }) do
            alya.combat.sendPositionPacket(offset, false)
        end
    end)
end
return createPacketMode
