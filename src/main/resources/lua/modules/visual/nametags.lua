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

local moduleTable = alya.modules.register("Nametags", "Draws player names above their heads", "VISUAL")
local ignoreFriends = moduleTable.addBooleanSetting("Ignore Friends", "", true)

local namePositions = {}

alya.events.on("render3d", function(event)
    if not moduleTable.isEnabled() then return end
    namePositions = {}
    local pt = alya.mc.getPartialTicks()
    local players = alya.combat.getAllPlayers()
    for _, player in ipairs(players) do
        if player.isInvisible then goto continue_loop end
        if ignoreFriends.isEnabled() and alya.combat.isFriend(player.name) then goto continue_loop end
        local ix = player.lastX + (player.x - player.lastX) * pt
        local iy = player.lastY + (player.y - player.lastY) * pt
        local iz = player.lastZ + (player.z - player.lastZ) * pt
        local screen = alya.visual.worldToScreen(ix, iy + player.height + 0.2, iz)
        if screen ~= nil then
            namePositions[#namePositions + 1] = {
                name = player.name,
                screenX = screen.x,
                screenY = screen.y,
            }
        end
        ::continue_loop::
    end
end)

alya.events.on("render2d", function(event)
    if not moduleTable.isEnabled() then return end
    local font = alya.getFontRendererMedium and alya.getFontRendererMedium() or nil
    if not font then return end
    for _, entry in ipairs(namePositions) do
        local width = font.getStringWidth(entry.name)
        font.drawStringWithShadow(entry.name, entry.screenX - width * 0.5, entry.screenY, 0xFFFFFFFF)
    end
end)

moduleTable.onDisable(function()
    namePositions = {}
end)
