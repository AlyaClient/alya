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

local function createBHopSpeedMode(moduleTable, modeSettings)
    local strafe = moduleTable.addBooleanSetting("Strafe", "Strafe while jumping", true)
    local omniSprint = moduleTable.addBooleanSetting("OmniSprint", "Sprint constantly in all directions", true)
    strafe.setVisibility(function() return modeSettings.is("BHop") end)
    omniSprint.setVisibility(function() return modeSettings.is("BHop") end)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("BHop") then return end
        if not alya.movement.isMoving() then return end
        if alya.mc.isOnGround() then
            alya.mc.jump()
        end
        local strafePercentage = strafe.isEnabled() and 1 or 0
        alya.mc.setSprinting(true)
        if omniSprint.isEnabled() then
            alya.movement.setSpeedStrafe(0.377, strafePercentage)
        else
            if alya.mc.isForwardDown() then
                alya.movement.setSpeedStrafe(0.377, strafePercentage)
            else
                alya.movement.setSpeedStrafe(0.32, strafePercentage)
            end
        end
    end)
    alya.events.on("playerinput", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("BHop") then return end
        if alya.mc.isJumpDown() then
            event.cancel()
        end
    end)
end
return createBHopSpeedMode
