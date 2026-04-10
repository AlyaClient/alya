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

local moduleTable = alya.modules.register("Click Assist", "Gives you two mice", "COMBAT")
local extraCps = moduleTable.addNumberSetting("Added CPS", "", 1, 0, 3, 1)
local chance = moduleTable.addNumberSetting("Chance", "", 100, 0, 100, 1)
local rightClick = moduleTable.addBooleanSetting("Right Click", "", false)
local leftClick = moduleTable.addBooleanSetting("Left Click", "", true)
local recursing = false
local wasAttacking = false
local wasUsing = false

alya.events.on("tick", function(event)
    if not moduleTable.isEnabled() then return end
    if recursing then return end

    local attacking = alya.combat.isAttackKeyDown()
    local using = alya.combat.isUseKeyDown()
    local justAttacked = attacking and not wasAttacking
    local justUsed = using and not wasUsing
    wasAttacking = attacking
    wasUsing = using

    if not justAttacked and not justUsed then return end
    if math.random(100) > chance.getValue() then return end

    local clicks = extraCps.getValueAsInt()
    recursing = true
    for i = 1, clicks do
        if leftClick.isEnabled() and justAttacked and not using then
            alya.combat.clickMouse()
        end
        if rightClick.isEnabled() and justUsed and not attacking then
            alya.combat.rightClickMouse()
        end
    end
    recursing = false
end)
moduleTable.onEnable(function()
    recursing = false
    wasAttacking = false
    wasUsing = false
end)
moduleTable.onDisable(function()
    recursing = false
    wasAttacking = false
    wasUsing = false
end)
