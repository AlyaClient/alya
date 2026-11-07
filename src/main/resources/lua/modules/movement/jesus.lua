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

local moduleTable = alya.modules.register("Jesus", "Walk on water", "MOVEMENT")

alya.events.on("update", function(event)
    alya.mc.setJesusActive(moduleTable.isEnabled())
end)

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    if alya.mc.isSneakPressed() then return end

    if alya.mc.isInWater() then
        if alya.mc.getFallDistance() <= 3.0 then
            alya.mc.setMotionY(0.11)
        end
        return
    end

    if alya.mc.isOnLiquid() then
        event.setOnGround(true)
    end
end)

moduleTable.onEnable(function()
    alya.mc.setJesusActive(true)
end)

moduleTable.onDisable(function()
    alya.mc.setJesusActive(false)
end)
