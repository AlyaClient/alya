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

local moduleTable = alya.modules.register("NoSlowDown", "Prevents slowdown when using items", "PLAYER")
local eating    = moduleTable.addBooleanSetting("Eating", "", true)
local drinking  = moduleTable.addBooleanSetting("Drinking", "", true)
local blocking  = moduleTable.addBooleanSetting("Blocking", "", true)
local bow       = moduleTable.addBooleanSetting("Bow", "", true)
alya.events.on("slowdown", function(event)
    if not moduleTable.isEnabled() then return end
    local reason = event.getReason()
    if reason == "eat"   and eating.isEnabled()   then event.cancel() end
    if reason == "drink" and drinking.isEnabled()  then event.cancel() end
    if reason == "block" and blocking.isEnabled()  then event.cancel() end
    if reason == "bow"   and bow.isEnabled()       then event.cancel() end
end)
