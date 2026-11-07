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

local mc = alya.mc

local function createAirJumpMode(moduleTable, modeSettings)
    local timer = alya.timer.create()
    local hoverY = nil
    local sprint = moduleTable.addBooleanSetting("Sprint", "Sprint while air jumping", true)
    sprint.setVisibility(function() return modeSettings.is("Air Jump") end)

    alya.events.on("playermove", function(_)
        if not moduleTable.isEnabled() then
            hoverY = nil
            return
        end
        if not modeSettings.is("Air Jump") then
            hoverY = nil
            return
        end

        mc.setSprinting(sprint.isEnabled() and alya.movement.isMoving())

        local y = mc.getPlayerY()
        if hoverY == nil then hoverY = y end

        if mc.isJumpPressed() then
            if timer.hasElapsed(150) then
                mc.jump()
                timer.reset()
            end
            hoverY = y
        elseif mc.isSneakPressed() then
            hoverY = y
        elseif y <= hoverY and mc.getMotionY() <= 0 and timer.hasElapsed(150) then
            mc.jump()
            timer.reset()
        end
    end)
end
return createAirJumpMode
