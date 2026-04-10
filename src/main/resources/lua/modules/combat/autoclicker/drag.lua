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

local function createDragMode(moduleTable, mode, cps, dragTime, dragDelay)
    local clickTimer = alya.timer.create()
    local dragTimer  = alya.timer.create()
    local delayTimer = alya.timer.create()
    local function getCpsTicks()
        local minVal = (20 - cps.getSecondValue()) * 20
        local maxVal = (20 - cps.getValue()) * 20
        if minVal > maxVal then minVal, maxVal = maxVal, minVal end
        return math.floor(minVal + alya.mathutil.makeRandom() * (maxVal - minVal))
    end
    alya.events.on("update", function(event)
        if not moduleTable.isEnabled() then return end
        if not mode.is("Drag") then return end
        if not alya.combat.isAttackKeyDown() then
            clickTimer.reset()
            dragTimer.reset()
            delayTimer.reset()
            return
        end
        local cps = getCpsTicks()
        if dragTimer.hasElapsed(dragTime.getValueAsInt()) then
            if delayTimer.hasElapsedAndReset(dragDelay.getValueAsInt(), true) then
                dragTimer.reset()
            end
        else
            delayTimer.reset()
            if clickTimer.hasElapsedAndReset(cps, true) then
                alya.combat.clickMouse()
            end
        end
    end)
end
return createDragMode
