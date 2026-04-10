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

local moduleTable = alya.modules.register("Reach", "Extends your attack range", "COMBAT")
local reachRange = moduleTable.addNumberSetting("Reach Range", "", 3, 3, 6, 0.1)
local chance = moduleTable.addNumberSetting("Chance", "Chance for reach to be applied", 70, 0, 100, 1)

local function getEffectiveMax()
    return reachRange.getSecondValue()
end

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end

    local effectiveMax = getEffectiveMax()
    local minR = reachRange.getValue()
    local reach = minR + math.random() * (effectiveMax - minR)

    if math.random() < chance.getValue() / 100 then
        alya.mc.setReach(reach)
    else
        alya.mc.resetReach()
    end
end)

moduleTable.onDisable(function()
    alya.mc.resetReach()
end)