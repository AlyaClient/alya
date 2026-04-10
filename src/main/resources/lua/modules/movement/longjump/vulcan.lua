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

local function createVulcanMode(moduleTable, mode)
    local timer = alya.timer.create()
    local function findFireballSlot()
        for i = 0, 8 do
            local name = alya.combat.getHotbarItemName(i)
            if name == "item.fireball" or name == "item.firecharge" then
                return i
            end
        end
        return -1
    end
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not mode.is("Vulcan") then return end
        if not event.isPre() then return end
        if alya.mc.isOnGround() then
            alya.combat.setPlayerPitch(90)
        end
        if timer.hasElapsedAndReset(10, true) then
            local slot = findFireballSlot()
            if slot ~= -1 then
                alya.combat.setHotbarSlot(slot)
                alya.combat.useHeldItem()
                alya.combat.setPlayerPitch(0)
            end
        end
        if alya.combat.getHurtTime() > 8 then
            alya.mc.setTimerSpeed(0.1)
            alya.mc.setMotionY(4)
            alya.movement.setSpeed(2.6)
            moduleTable.toggle()
        end
    end)
end
return createVulcanMode
