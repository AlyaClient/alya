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

local moduleTable = alya.modules.register("Chams", "Highlights player models through walls", "VISUAL")
local red = moduleTable.addNumberSetting("Red", "Red channel", 255, 0, 255, 1)
local green = moduleTable.addNumberSetting("Green", "Green channel", 0, 0, 255, 1)
local blue = moduleTable.addNumberSetting("Blue", "Blue channel", 255, 0, 255, 1)
local alpha = moduleTable.addNumberSetting("Alpha", "Opacity", 180, 0, 255, 1)
local ignoreFriends = moduleTable.addBooleanSetting("Ignore Friends", "Skip friends", true)

alya.events.on("render3d", function(event)
    if not moduleTable.isEnabled() then return end

    local players = alya.combat.getAllPlayers()
    local colorRed = red.getValue() / 255.0
    local colorGreen = green.getValue() / 255.0
    local colorBlue = blue.getValue() / 255.0
    local colorAlpha = alpha.getValue() / 255.0

    for _, player in ipairs(players) do
        if player.isInvisible then goto continue_loop end
        if ignoreFriends.isEnabled() and alya.combat.isFriend(player.name) then goto continue_loop end
        alya.visual.renderEntityChams(player.id, colorRed, colorGreen, colorBlue, colorAlpha)
        ::continue_loop::
    end
end)
