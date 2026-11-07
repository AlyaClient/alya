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

local function createSpartanSpeedMode(moduleTable, modeSettings)
    local ticks = 0

    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() or not modeSettings.is("Spartan") then
            ticks = 0
            return
        end
        if not event.isPre() then return end
        if alya.mc.isPlayerNull() then return end

        if alya.mc.isOnGround() then
            ticks = 0
        else
            ticks = ticks + 1

            if ticks == 1 then
                local currentSpeed = alya.movement.getSpeed()
                if currentSpeed < 0.8 then
                    alya.movement.setSpeedStrafe(0.8, 1)
                else
                    alya.movement.setSpeedStrafe(currentSpeed * 2.5, 1)
                end
            end
        end
    end)
end
return createSpartanSpeedMode
