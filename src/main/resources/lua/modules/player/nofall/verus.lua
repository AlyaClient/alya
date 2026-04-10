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

local function createVerusNoFallMode(moduleTable, modeSettings)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("Verus") then return end
        if alya.mc.getFallDistance() > 3.5 then
            alya.mc.setMotionY(0.0)
            event.setOnGround(true)
            alya.mc.setFallDistance(0)
        end
    end)
end
return createVerusNoFallMode
