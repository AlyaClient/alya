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

local moduleTable = alya.modules.register("Sprint", "Auto-Sprint", "MOVEMENT")
local omniSprint = moduleTable.addBooleanSetting("OmniSprint", "Sprint in all directions", false)

alya.events.on("playerinput", function(event)
    if not moduleTable.isEnabled() then return end
    if not alya.movement.isMoving() then 
        alya.mc.setSprinting(false)
        return 
    end

    local scaffoldModule = alya.modules.get("Scaffold")
    if scaffoldModule and scaffoldModule.isEnabled() then
        local scaffoldSprint = scaffoldModule.getSetting("Sprint")
        if scaffoldSprint and not scaffoldSprint.isEnabled() then
            alya.mc.setSprinting(false)
            return
        end
    end

    if omniSprint.isEnabled() then
        alya.mc.setSprinting(true)
    else
        local forwardKey = alya.mc.getKeyCode("forward")
        local backwardKey = alya.mc.getKeyCode("back")
        if alya.mc.isKeyDown(forwardKey) and not alya.mc.isKeyDown(backwardKey) then
            alya.mc.setSprinting(true)
        else
            alya.mc.setSprinting(false)
        end
    end
end)

moduleTable.onDisable(function()
    alya.mc.setSprinting(false)
end)