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

local moduleTable = alya.modules.register("Scoreboard", "Move the scoreboard vertically", "VISUAL")
local yOffset = moduleTable.addNumberSetting("Y Offset", "Vertical offset in pixels (negative moves up)", 0, -500, 500, 1)

alya.events.on("render2d", function(event)
    if moduleTable.isEnabled() then
        alya.mc.setScoreboardYOffset(yOffset.getValue())
    end
end)

moduleTable.onDisable(function()
    alya.mc.resetScoreboardYOffset()
end)
