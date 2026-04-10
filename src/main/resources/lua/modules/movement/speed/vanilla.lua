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

local function createVanillaSpeedMode(moduleTable, modeSettings)
    local speedAmount = moduleTable.addNumberSetting("Speed", "How fast", 0.6, 0.1, 10.0)
    local autoJump = moduleTable.addBooleanSetting("Auto Jump", "Automatically jump", true)
    local strafe = moduleTable.addBooleanSetting("Strafe", "Move mid-air", true)
    speedAmount.setVisibility(function() return modeSettings.is("Vanilla") end)
    autoJump.setVisibility(function() return modeSettings.is("Vanilla") end)
    strafe.setVisibility(function() return modeSettings.is("Vanilla") end)
    alya.events.on("playermove", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("Vanilla") then return end
        if not alya.movement.isMoving() then return end
        if autoJump.isEnabled() and alya.mc.isOnGround() then
            alya.mc.jump()
        end
        local strafePercentage = strafe.isEnabled() and 1 or 0
        alya.movement.setSpeedStrafe(speedAmount.getValue(), strafePercentage)
    end)
end
return createVanillaSpeedMode
