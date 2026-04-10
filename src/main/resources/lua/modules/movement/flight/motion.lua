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

local function createMotionFlightMode(moduleTable, modeSettings, speedSetting)
    local glide = moduleTable.addBooleanSetting("Glide", "Slowly fall", false)
    local smooth = moduleTable.addBooleanSetting("Smooth", "Smooth movement", true)
    glide.setVisibility(function() return modeSettings.is("Motion") end)
    smooth.setVisibility(function() return modeSettings.is("Motion") end)
    alya.events.on("playermove", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("Motion") then return end
        if not smooth.isEnabled() then
            alya.mc.setCameraYaw(0.1)
        end
        if alya.mc.isJumpPressed() then
            alya.mc.setMotionY(alya.mc.getMotionY() + speedSetting.getValue() / 4.0)
        elseif alya.mc.isSneakPressed() then
            alya.mc.setMotionY(alya.mc.getMotionY() - speedSetting.getValue() / 4.0)
        else
            alya.mc.setMotionY(glide.isEnabled() and -0.05 or 0)
        end
        alya.movement.setSpeed(speedSetting.getValue())
    end)
end
return createMotionFlightMode
