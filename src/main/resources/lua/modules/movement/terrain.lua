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

local moduleTable = alya.modules.register("Terrain", "Move faster on terrain surfaces", "MOVEMENT")
local fastIce = moduleTable.addBooleanSetting("Fast-Ice", "Move faster on ice", true)
local fastLadder = moduleTable.addBooleanSetting("Fast-Ladder", "Climb ladders faster", true)

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end

    if fastIce.isEnabled() and alya.mc.isOnIce() then
        local motionX = alya.mc.getMotionX()
        local motionZ = alya.mc.getMotionZ()
        if math.abs(motionX) > 0.001 or math.abs(motionZ) > 0.001 then
            alya.mc.setMotionX(motionX * 1.06)
            alya.mc.setMotionZ(motionZ * 1.06)
        end
    end

    if fastLadder.isEnabled() and alya.mc.isOnLadder() then
        if alya.mc.isJumpPressed() then
            alya.mc.setMotionY(0.25)
        elseif alya.mc.isSneakPressed() then
            alya.mc.setMotionY(-0.2)
        elseif alya.movement.isMoving() then
            alya.mc.setMotionY(0.2)
        end
    end
end)
